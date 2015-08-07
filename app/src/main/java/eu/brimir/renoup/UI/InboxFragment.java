package eu.brimir.renoup.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import eu.brimir.renoup.Adapters.MessageAdapter;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.R;

/**
 * Created by Patrik on 2015-07-17.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected String routeNumber;
    private ProgressDialog pDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipeRefresh5, R.color.swipeRefresh4, R.color.swipeRefresh3, R.color.swipeRefresh5);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.swipeRefresh2);




        return rootView;
    }

public void getSharedPreferences(){

    SharedPreferences prefs = getActivity().getSharedPreferences(ParseConstants.PREF_FILE_NAME, 0);

    routeNumber = prefs.getString(ParseConstants.KEY_ROUTE, null);
}
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);
        getSharedPreferences();

        retrieveMessages();


    }

    private void retrieveMessages() {


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_ROUTE, routeNumber);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
               getActivity().setProgressBarIndeterminateVisibility(false);

                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);


                }
                if(e == null){

                    mMessages = messages;



                    String[] usernames = new String[mMessages.size()];
                    String[] address = new String[mMessages.size()];

                    String[] felkod = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_FIRSTNAME);
                        address[i] = message.getString(ParseConstants.KEY_ADDRESS);
                        felkod[i] = message.getString(ParseConstants.KEY_FELKOD);
                        i++;
                    }

                    if(getListView().getAdapter() == null){
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);
                    }else{

                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }

                    }


                }
            });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());
        String address = message.getString(ParseConstants.KEY_ADDRESS);
        String felkod = message.getString(ParseConstants.KEY_ADDRESS);
        String route = routeNumber;

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            Intent intent = new Intent(getActivity(),ViewImageActivity.class);
            intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_ADDRESS, address);
            intent.putExtra(ParseConstants.KEY_FELKOD, felkod);
            intent.putExtra(ParseConstants.KEY_ROUTE, route);
            intent.putExtra(ParseConstants.KEY_POSTNR, message.getString(ParseConstants.KEY_POSTNR));


            startActivity(intent);
        }else{

            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);


        }






    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };


}
