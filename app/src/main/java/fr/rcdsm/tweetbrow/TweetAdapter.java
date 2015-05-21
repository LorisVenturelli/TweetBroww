package fr.rcdsm.tweetbrow;

import android.content.Context;
import android.graphics.Color;
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

    public TweetAdapter(Context context, ArrayList<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;

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
            holder.retweets=(TextView)convertView.findViewById(R.id.infoRetweet);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tweet tweet = tweets.get(position);

        if(tweet.getRetweet()){
            holder.retweets.setVisibility(View.VISIBLE);
            holder.retweets.setText(tweet.getPseudo() + " a retweeté");
            holder.retweets.setTextColor(Color.rgb(0, 102, 204));
        }
        else
        {
            holder.retweets.setVisibility(View.GONE);
        }

        holder.pseudo.setText(tweet.getPseudo());
        holder.login.setText("@" + tweet.getLogin());
        holder.date.setText(format.format(tweet.getDate_create()));
        holder.message.setText(tweet.getMessage());

        return convertView;
    }

    class ViewHolder {
        public TextView pseudo;
        public TextView login;
        public TextView date;
        public TextView message;
        public TextView retweets;
    }
}
