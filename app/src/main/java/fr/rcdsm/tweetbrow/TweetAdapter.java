package fr.rcdsm.tweetbrow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class TweetAdapter extends BaseAdapter {
    Context context;
    ArrayList<Tweet> tweets;

    LayoutInflater inflater;

    public TweetAdapter(Context context, ArrayList<Tweet> notes) {
        this.context = context;
        this.tweets = notes;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tweets.size();
    }

    @Override
    public Object getItem(int position) {
        return tweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tweets.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM kk:mm", Locale.FRENCH);
        format.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.layout_tweet, null);
            holder = new ViewHolder();
            holder.pseudo = (TextView) convertView.findViewById(R.id.pseudoTweet);
            holder.login = (TextView) convertView.findViewById(R.id.loginTweet);
            holder.date = (TextView) convertView.findViewById(R.id.dateTweet);
            holder.message = (TextView) convertView.findViewById(R.id.messageTweet);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tweet note = tweets.get(position);

        holder.pseudo.setText(note.getPseudo());
        holder.login.setText("@"+note.getLogin());
        holder.date.setText(format.format(note.getDate_create()));
        holder.message.setText(note.getMessage());

        return convertView;
    }

    class ViewHolder {
        public TextView pseudo;
        public TextView login;
        public TextView date;
        public TextView message;
    }
}
