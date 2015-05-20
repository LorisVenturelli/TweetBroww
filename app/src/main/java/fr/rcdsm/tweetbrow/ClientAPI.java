package fr.rcdsm.tweetbrow;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by rcdsm on 27/04/15.
 */
public class ClientAPI {

    private static ClientAPI instance;

    private Context context;


    public static void createInstance(Context appContext) {
        instance = new ClientAPI(appContext);
    }

    public static ClientAPI getInstance() {
        return instance;
    }

    private ClientAPI(Context appContext) {
        this.context = appContext;
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

        try {

        } catch (Exception e) {
            Log.e("First catch connect", "Exception " + e.getMessage());
        }

        aq.ajax("http://172.31.1.120:8888/tweetbrow/connect", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d("Callback api", json.toString());

                try {

                    if (json.getString("reponse").equals("success")) {

                        SharedPreferences preferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", json.getJSONObject("data").getString("token"));
                        editor.commit();

                        User userInstance = User.getInstance();
                        userInstance.setLogin(login);
                        userInstance.setToken(json.getJSONObject("data").getString("token"));

                        listener.callback();
                    } else
                        Log.e("Erreur connect", "API return false : " + json.toString());

                } catch (Exception e) {
                    Log.e("Catch connect", "Exception " + e.getMessage());
                }

            }
        });
    }

    public void register(final String login, final String password, final String email, final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
        params.put("email", email);

        try {
            Log.d("Parametres", "params: " + params.toString());
        } catch (Exception e) {
            Log.e("First catch register", "Exception " + e.getMessage());
        }

        aq.ajax("http://172.31.1.120:8888/tweetbrow/register", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d("Callback api", json.toString());

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

    public void syncTweetbrowAPI(final APIListener listener){

        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());

        Log.d("Parametres", "params: " + params.toString());

        aq.ajax("http://172.31.1.120:8888/tweetbrow/timeline", params, JSONObject.class, new AjaxCallback<JSONObject>() {

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

    public void createTweet(final String title, final String content, final APIListener listener){
        final AQuery aq = new AQuery(context);

        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("message", content);

        Log.d("Parametres", "params: " + params.toString());

        aq.ajax("http://172.31.1.120:8888/tweetbrow/tweet/add", params, JSONObject.class, new AjaxCallback<JSONObject>() {
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

        aq.ajax("http://172.31.1.120:8888/tweetbrow/tweet/delete", params, JSONObject.class, new AjaxCallback<JSONObject>() {
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

}
