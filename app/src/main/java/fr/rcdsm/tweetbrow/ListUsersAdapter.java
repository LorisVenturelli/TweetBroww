package fr.rcdsm.tweetbrow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by rcdsm on 20/05/15.
 */
public class ListUsersAdapter extends BaseAdapter {
    Context context;
    ArrayList<User> allUsers;

    LayoutInflater inflater;

    Realm realm;
    TweetManager manager;

    public ListUsersAdapter(Context context, ArrayList<User> notes) {
        this.context = context;
        this.allUsers = notes;

        inflater = LayoutInflater.from(context);
        realm = Realm.getInstance(context);
        manager = new TweetManager(context);
    }

    @Override
    public int getCount() {
        return allUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return allUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return allUsers.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.layout_item_user, null);
            holder = new ViewHolder();

            holder.pseudo = (TextView) convertView.findViewById(R.id.listPseudo);
            holder.login = (TextView) convertView.findViewById(R.id.listLogin);
            holder.follow = (Button) convertView.findViewById(R.id.listFollow);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User user = allUsers.get(position);

        holder.pseudo.setText(user.getPseudo());
        holder.login.setText("@" + user.getLogin());

        if(user.getId() == User.getInstance().getId())
            holder.follow.setVisibility(View.INVISIBLE);
        else if(user.isFollowed())
            holder.follow.setText("UNFOLLOW");

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager.deleteAll();

                if(!user.isFollowed()){

                    ClientAPI.getInstance().follow(String.valueOf(user.getId()), new ClientAPI.APIListener() {
                        @Override
                        public void callback() {
                            realm.beginTransaction();
                                holder.follow.setText("UNFOLLOW");
                                user.setFollowed(true);
                            realm.commitTransaction();
                        }
                    });

                } else {

                    ClientAPI.getInstance().unfollow(String.valueOf(user.getId()), new ClientAPI.APIListener() {
                        @Override
                        public void callback() {
                            realm.beginTransaction();
                                holder.follow.setText("FOLLOW");
                                user.setFollowed(false);
                            realm.commitTransaction();
                        }
                    });

                }

            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView pseudo;
        public TextView login;
        public Button follow;
    }
}
