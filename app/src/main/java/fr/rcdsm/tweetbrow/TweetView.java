package fr.rcdsm.tweetbrow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rcdsm on 10/04/15.
 */
public class TweetView extends ActionBarActivity {

    String action;

    EditText title;
    EditText content;

    TweetManager manager;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editnote);

        title = (EditText) findViewById(R.id.editTitle);
        content = (EditText) findViewById(R.id.editContent);

        manager = new TweetManager(this);

        Bundle b = getIntent().getExtras();
        action = b.getString("action");

        if (action.equals("edit")) {

            setTitle("Edition d'une tweetbrow");

            long id = b.getLong("id");

            tweet = manager.getTweetWithId(id);

            title.setText(tweet.getPseudo(), TextView.BufferType.EDITABLE);
            content.setText(tweet.getMessage(), TextView.BufferType.EDITABLE);
        } else {
            setTitle("Ajout d'une tweetbrow");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(action.equals("edit"))
            inflater.inflate(R.menu.menu_edit, menu);
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
            String newTitle = title.getText().toString().trim();
            String newContent = content.getText().toString().trim();

            //if (action.equals("edit"))
            //    manager.updateTweet(tweet.getId(), newTitle, newContent);
            //else
            manager.addTweet(newTitle, newContent);

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
}