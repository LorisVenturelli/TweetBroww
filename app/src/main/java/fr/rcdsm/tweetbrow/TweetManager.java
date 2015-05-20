package fr.rcdsm.tweetbrow;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by rcdsm on 09/04/15.
 */
public class TweetManager {

    Realm realm;

    public TweetManager(Context context){
        realm = Realm.getInstance(context);
    }

    public ArrayList<Tweet> listTweets() {
        RealmResults<Tweet> query = realm.where(Tweet.class).findAll();
        query.sort("date_create", RealmResults.SORT_ORDER_DESCENDING);

        ArrayList<Tweet> allNotes = new ArrayList<>();

        for (Tweet tweet : query){
            allNotes.add(tweet);
        }

        return allNotes;
    }

    public boolean isPopulated(){
        return realm.where(Tweet.class).findAll().size() > 0;
    }

    public void deleteAll(){
        realm.beginTransaction();
        RealmResults<Tweet> query = realm.where(Tweet.class).findAll();
        query.clear();
        realm.commitTransaction();

        Log.d("Tweets", "All tweets deleted.");
    }

    public void searchAndUpdate(String search){

        RealmResults<Tweet> results = realm.where(Tweet.class).contains("message", search).findAll();

        realm.beginTransaction();
        for (int i = 0; i < results.size(); i++) {
            Tweet tweet = results.get(i);
            tweet.setMessage(tweet.getMessage() + " - MODIFIED");
            tweet.setDate_create(new Date());
        }
        realm.commitTransaction();
    }

    public Tweet getTweetWithId(long id){

        return realm.where(Tweet.class).equalTo("id", id).findFirst();

    }

    public void addTweet(String newContent) {

        ClientAPI.getInstance().createTweet( newContent, new ClientAPI.APIListener() {
            @Override
            public void callback() {
                Log.d("Send tweet api", "Envoi du tweet terminée.");
            }
        });
    }

    public ArrayList<Tweet> searchTweetOnList(String search) {

        RealmResults<Tweet> query = realm.where(Tweet.class)
                                        .beginGroup()
                                            .contains("pseudo", search, false)
                                            .or()
                                            .contains("message", search, false)
                                        .endGroup()
                                        .findAll();

        query.sort("date_create", RealmResults.SORT_ORDER_DESCENDING);

        ArrayList<Tweet> allTweets = new ArrayList<>();

        for (Tweet tweet : query){
            allTweets.add(tweet);
        }

        return allTweets;
    }

    public void deleteTweet(final long id) {

        ClientAPI.getInstance().deleteTweet(id, new ClientAPI.APIListener() {
            @Override
            public void callback() {

                realm.beginTransaction();

                Tweet tweet = realm.where(Tweet.class).equalTo("id", id).findFirst();
                tweet.removeFromRealm();

                realm.commitTransaction();

                Log.d("Delete tweetbrow", "Suppression de la tweetbrow terminée.");
            }
        });

    }

    public ArrayList<User> listAllUsers() {
        RealmResults<User> query = realm.where(User.class).findAll();
        query.sort("id", RealmResults.SORT_ORDER_DESCENDING);

        ArrayList<User> allUsers = new ArrayList<>();

        for (User user : query){
            allUsers.add(user);
        }

        return allUsers;
    }
}
