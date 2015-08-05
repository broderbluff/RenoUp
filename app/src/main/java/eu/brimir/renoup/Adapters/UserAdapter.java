package eu.brimir.renoup.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import eu.brimir.renoup.Utils.MD5Util;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.R;

/**
 * Created by brode on 2015-08-01.
 */
public class UserAdapter extends ArrayAdapter<ParseUser>{

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users){
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
           holder.userImageView = (ImageView)convertView.findViewById(R.id.userImageView);
           holder.checkedImageview = (ImageView)convertView.findViewById(R.id.checkImageView);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if(email.equals("")){
            holder.userImageView.setImageResource(R.drawable.ic_user);
        }else{
            String hash = MD5Util.md5Hex(email);

            String gravatarUrl ="http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";

            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.ic_user).into(holder.userImageView);
        }


        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)){
            holder.checkedImageview.setVisibility(View.VISIBLE);
        }else{
            holder.checkedImageview.setVisibility(View.INVISIBLE);
        }

        holder.nameLabel.setText(user.getString("firstName") + " " + user.getString("lastName"));

        return convertView;
    }

    private static class ViewHolder{
          ImageView userImageView;
        TextView nameLabel;
        ImageView checkedImageview;

    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
