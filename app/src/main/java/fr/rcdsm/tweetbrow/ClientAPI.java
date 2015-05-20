package fr.rcdsm.tweetbrow;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by rcdsm on 27/04/15.
 */
public class ClientAPI {

    private static ClientAPI instance;

    private Context context;

    private String urlApi;

    private Realm realm;


    public static void createInstance(Context appContext) {
        instance = new ClientAPI(appContext);
    }

    public static ClientAPI getInstance() {
        return instance;
    }

    private ClientAPI(Context appContext) {
        this.context = appContext;
        this.urlApi = "http://192.168.100.38:8888";

        this.realm = Realm.getInstance(appContext);
    }

    public interface APIListener{
        void callback();
    }

    public void connect(final String login, final String password, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);

        Log.d("Parametres", "params: " + params.toString());

        aq.ajax(urlApi+"/tweetbrow/connect", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    Log.d("Callback api", json.toString());

                    if (json.getString("reponse").equals("success")) {

                        SharedPreferences preferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", json.getJSONObject("data").getString("token"));
                        editor.putString("login", json.getJSONObject("data").getString("login"));
                        editor.putString("pseudo", json.getJSONObject("data").getString("pseudo"));
                        editor.putString("id", json.getJSONObject("data").getString("id"));
                        editor.commit();

                        realm.beginTransaction();
                            User userInstance = User.getInstance();
                            userInstance.setId(Long.valueOf(json.getJSONObject("data").getString("id")));
                            userInstance.setLogin(json.getJSONObject("data").getString("login"));
                            userInstance.setPseudo(json.getJSONObject("data").getString("pseudo"));
                            userInstance.setToken(json.getJSONObject("data").getString("token"));
                        realm.commitTransaction();

                        listener.callback();
                    } else
                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("Catch connect", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void logout(final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());

        aq.ajax(urlApi+"/tweetbrow/logout", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d("Callback api logout", json.toString());

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("Catch json register", "Exception " + e.getMessage());
                }

            }
        });

    }

    public void register(final String login, final String pseudo, final String password, final String email, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
        params.put("email", email);
        params.put("pseudo", pseudo);

        aq.ajax(urlApi+"/tweetbrow/register", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    Log.d("Callback api", json.toString());

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("Catch json register", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void syncTweetbrowAPI(final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());

        Log.d("Parametres", "params: " + params.toString());

        aq.ajax(urlApi+"/tweetbrow/timeline", params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    Log.d("Notes API", json.toString());
                    Log.d("URL API", url);

                    if (json.getString("reponse").equals("success")) {

                        Realm realm = Realm.getInstance(context);
                        realm.beginTransaction();

                        JSONArray notes = json.getJSONArray("data");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                        for (int i = 0; i < notes.length(); i++) {
                            Date date_create = dateFormat.parse(notes.getJSONObject(i).getString("date_create"));

                            notes.getJSONObject(i).put("date_create", date_create.getTime());

                            realm.createOrUpdateObjectFromJson(Tweet.class, notes.getJSONObject(i));
                        }

                        realm.commitTransaction();

                        Log.d("Sync API", "Synchronisation terminée.");

                        listener.callback();
                    } else
                        Log.e("Erreur get tweets api", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch get tweets api", "Exception " + e.getMessage());
                }
            }
        });

    }

    public void createTweet( final String content, final String id_parent, final APIListener listener){
        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("message", content);
        params.put("id_parent", id_parent);

        Log.d("Parametres", "params: " + params.toString());

        aq.ajax(urlApi+"/tweetbrow/tweet/add", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch create tweet", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void deleteTweet(final long id, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("tweet_id", String.valueOf(id));

        Log.d("Parametres", "params: " + params.toString());

        aq.ajax(urlApi+"/tweetbrow/tweet/delete", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur delete tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch delete tweet", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void favoris(final String tweet_id, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("tweet_id", tweet_id);

        try {

            Log.d("Parametres", "params: " + params.toString());

        } catch (Exception e) {
            Log.e("First catch register", "Exception " + e.getMessage());
        }

        aq.ajax(urlApi+"/tweetbrow/tweet/favoris", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch register", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void unfavoris(final String tweet_id, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("tweet_id", tweet_id);

        try {

            Log.d("Parametres", "params: " + params.toString());

        } catch (Exception e) {
            Log.e("First catch register", "Exception " + e.getMessage());
        }

        aq.ajax(urlApi+"/tweetbrow/tweet/unfavoris", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch register", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void retweet(final String tweet_id, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("tweet_id", tweet_id);

        try {

            Log.d("Parametres", "params: " + params.toString());

        } catch (Exception e) {
            Log.e("First catch register", "Exception " + e.getMessage());
        }

        aq.ajax(urlApi+"/tweetbrow/tweet/retweet", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch register", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void unretweet(final String tweet_id, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("tweet_id", tweet_id);

        try {

            Log.d("Parametres", "params: " + params.toString());

        } catch (Exception e) {
            Log.e("First catch register", "Exception " + e.getMessage());
        }

        aq.ajax(urlApi+"/tweetbrow/tweet/unretweet", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch register", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void syncAllUsers(final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());

        aq.ajax(urlApi+"/tweetbrow/user/all", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {

                        Realm realm = Realm.getInstance(context);
                        realm.beginTransaction();

                        RealmResults<User> query = realm.where(User.class).findAll();
                        query.clear();

                        JSONArray users = json.getJSONArray("data");

                        for (int i = 0; i < users.length(); i++) {
                            realm.createOrUpdateObjectFromJson(User.class, users.getJSONObject(i));
                        }

                        realm.commitTransaction();

                        Log.d("Sync users API", "Synchronisation terminée.");

                        listener.callback();

                    } else
                        Log.e("Erreur sync users", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch sync users", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void follow(final String id_following, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());

        aq.ajax(urlApi+"/tweetbrow/follow/"+id_following, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch register", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void unfollow(final String id_following, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());

        aq.ajax(urlApi+"/tweetbrow/unfollow/"+id_following, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                try {

                    if (json.getString("reponse").equals("success")) {
                        listener.callback();
                    } else
                        Log.e("Erreur create tweet", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch register", "Exception " + e.getMessage());
                }

            }
        });
    }

}
