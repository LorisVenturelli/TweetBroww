package fr.rcdsm.tweetbrow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by rcdsm on 10/04/15.
 */
public class TweetView extends ActionBarActivity implements AdapterView.OnItemClickListener{

    String action;

    TextView login;
    TextView pseudo;
    TextView date;
    TextView message;

    TweetManager manager;
    Tweet tweet;

    SimpleDateFormat format;

    ImageView repondre;
    ImageView retweet;
    ImageView favoris;

    long id;

    Realm realm;

    ListView listReply;
    ArrayList<Tweet> allTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_layout);

        realm = Realm.getInstance(getApplicationContext());
        format = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa");

        repondre=(ImageView)findViewById(R.id.repondreButton);
        retweet=(ImageView)findViewById(R.id.retweetButton);
        favoris=(ImageView)findViewById(R.id.favoriButton);

        pseudo = (TextView) findViewById(R.id.pseudoView);
        login = (TextView) findViewById(R.id.loginView);
        date = (TextView) findViewById(R.id.dateView);
        message = (TextView) findViewById(R.id.messageView);

        listReply = (ListView) findViewById(R.id.listReply);

        manager = new TweetManager(this);


        Bundle b = getIntent().getExtras();
        if(b!=null) {

            action = b.getString("action");

            if (action.equals("view")) {

                setTitle("Tweet");

                 id = b.getLong("id");

                tweet = manager.getTweetWithId(id);

                login.setText("@" + tweet.getLogin());
                pseudo.setText(tweet.getPseudo());
                date.setText(format.format(tweet.getDate_create()));
                message.setText(tweet.getMessage());

                if(tweet.getRetweet()){
                    retweet.setImageResource(R.mipmap.retweet_blue);
                }
                if(tweet.getFavoris()) {
                    favoris.setImageResource(R.mipmap.favori_blue);
                }



                retweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tweet.getRetweet()) {
                            ClientAPI.getInstance().unretweet(String.valueOf(tweet.getId()), new ClientAPI.APIListener() {
                                @Override
                                public void callback() {
                                    realm.beginTransaction();
                                    retweet.setImageResource(R.mipmap.retweet);
                                    tweet.setRetweet(false);
                                    realm.commitTransaction();
                                }
                            });
                        } else {
                            ClientAPI.getInstance().retweet(String.valueOf(tweet.getId()), new ClientAPI.APIListener() {
                                @Override
                                public void callback() {
                                    realm.beginTransaction();
                                    retweet.setImageResource(R.mipmap.retweet_blue);
                                    tweet.setRetweet(true);
                                    realm.commitTransaction();
                                }
                            });
                        }
                    }
                });

                favoris.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tweet.getFavoris()) {
                            ClientAPI.getInstance().unfavoris(String.valueOf(tweet.getId()), new ClientAPI.APIListener() {
                                @Override
                                public void callback() {
                                    realm.beginTransaction();
                                    favoris.setImageResource(R.mipmap.favori);
                                    tweet.setFavoris(false);
                                    realm.commitTransaction();
                                }
                            });
                        } else {
                            ClientAPI.getInstance().favoris(String.valueOf(tweet.getId()), new ClientAPI.APIListener() {
                                @Override
                                public void callback() {
                                    realm.beginTransaction();
                                    favoris.setImageResource(R.mipmap.favori_blue);
                                    tweet.setFavoris(true);
                                    realm.commitTransaction();
                                }
                            });
                        }
                    }
                });

                repondre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TweetView.this, Reply.class);
                        intent.putExtra("login", User.getInstance().getLogin());
                        intent.putExtra("action", "reply");
                        intent.putExtra("pseudo", User.getInstance().getPseudo());
                        intent.putExtra("loginReply", login.getText());
                        startActivity(intent);

                    }
                });

            } else {
                setTitle("Ajout d'une tweetbrow");
            }
        }
        else{
            Log.e("Error","Le Bundle est vide mon gars :/");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(action.equals("view"))
            inflater.inflate(R.menu.menu_view, menu);
        else
            inflater.inflate(R.menu.menu_add, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    TweetView.this);

            // set title
            alertDialogBuilder.setTitle("Confirmation");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Confirmer la suppression de cette tweetbrow ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            manager.deleteTweet(tweet.getId());
                            TweetView.this.finish();
                        }
                    })
                    .setNegativeButton("Non",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            return true;
        }
        else if(id == R.id.action_save){
           /* String newTitle = title.getText().toString().trim();
            String newContent = content.getText().toString().trim();

            //if (action.equals("edit"))
            //    manager.updateTweet(tweet.getId(), newTitle, newContent);
            //else
            manager.addTweet(newTitle, newContent);*/

            finish();
        }
        else if(id == R.id.action_send_mail){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{});
            i.putExtra(Intent.EXTRA_SUBJECT, tweet.getPseudo());
            i.putExtra(Intent.EXTRA_TEXT, tweet.getMessage());
            try {
                startActivity(Intent.createChooser(i, "Envoi du mail ..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(TweetView.this, "Aucune application mail n'est installée.", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshListView();
    }

    private void refreshListView(){

        allTweet = manager.listAllTweetsWithParent(id);
        TweetAdapter adapter = new TweetAdapter(TweetView.this, allTweet);

        listReply.setOnItemClickListener(TweetView.this);
        listReply.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent = new Intent(TweetView.this, TweetView.class);
        intent.putExtra("action", "view");
        intent.putExtra("id", id);

        startActivity(intent);
    }
}