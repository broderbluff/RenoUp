package eu.brimir.renoup.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import eu.brimir.renoup.Adapters.UserAdapter;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.R;

/**
 * Created by Patrik on 2015-07-17.
 */
public class FriendsFragment extends Fragment {
    public static final String TAG = FriendsFragment.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;
    protected Button mSendButton;
    protected String pushMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mSendButton = (Button) rootView.findViewById(R.id.profile_button);
        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);
        mSendButton.setOnClickListener(mOnClickListener);

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();


        ParseQuery<ParseUser> query = mCurrentUser.getQuery().whereEqualTo("accepted", true);
        getActivity().setProgressBarIndeterminateVisibility(true);


        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null) {


                    mFriends = list;


                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getString("firstName") + " " + user.getString("lastName");
                        i++;
                    }

                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

    }


    public int getCheckedItemCount() {
        GridView gridView = mGridView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return gridView.getCheckedItemCount();
        }

        SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();
        int count = 0;
        for (int i = 0, size = checkedItems.size(); i < size; ++i) {
            if (checkedItems.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (mGridView.getCheckedItemCount() > 0) {
                mSendButton.setVisibility(View.VISIBLE);
            } else {
                mSendButton.setVisibility(View.INVISIBLE);
            }
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
            if (mGridView.isItemChecked(position)) {
                checkImageView.setVisibility(View.VISIBLE);

            } else {
                checkImageView.setVisibility(View.INVISIBLE);
            }


        }
    };

    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }


    protected void sendPushNotification(String message) {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USERID, getRecipientIds());
        message = message.trim();
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(message + " - " + ParseUser.getCurrentUser().getString(ParseConstants.KEY_FIRSTNAME));
        push.sendInBackground();

    }


    protected View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final Dialog dialog = new Dialog(getActivity(),
                    R.style.Theme_D1NoTitleDim);
            Window window = dialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);

            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.dialog);



            Button okButton = (Button) dialog.findViewById(R.id.sendDialogButton);
            Button noButton = (Button) dialog.findViewById(R.id.cancelDialogButton);


            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText reciverInput = (EditText) dialog.findViewById(R.id.etReciver);
                    pushMessage = reciverInput.getText().toString();
                    sendPushNotification(pushMessage);
                    dialog.dismiss();

                }
            }

        );


            dialog.show();

        }


    };

}



/*  mFriends.get(position).getUsername();

            String hej = mFriends.toString();



            Toast.makeText(getActivity(), hej, Toast.LENGTH_LONG).show();

            ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
            query.whereContainedIn(ParseConstants.KEY_USERID, mFriends );

            ParsePush push = new ParsePush();
            push.setQuery(query);
            push.setMessage(getString(R.string.push_message,ParseUser.getCurrentUser().getString("firstName")));
            push.sendInBackground();*/