package fr.rcdsm.tweetbrow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by rcdsm on 20/05/15.
 */
public class Reply extends ActionBarActivity {

    String action;

    TweetManager manager;

    EditText message;
    TextView login;
    TextView pseudo;
    TextView numberChar;
    Button tweeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_tweet);

        manager = new TweetManager(this);
        message = (EditText)findViewById(R.id.editNewsReply);
        login =(TextView)findViewById(R.id.loginReply);
        pseudo = (TextView)findViewById(R.id.pseudoReply);
        numberChar=(TextView)findViewById(R.id.numberCharReply);
        tweeter = (Button)findViewById(R.id.buttonReply);


        Bundle b = getIntent().getExtras();
        action = b.getString("action");

        final TextWatcher txwatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                numberChar.setText(String.valueOf(140-s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        };

        message.addTextChangedListener(txwatcher);

        if (action.equals("reply")) {

            setTitle("Repondre à un Tweet");

            String logins = b.getString("login");
            String pseudos = b.getString("pseudo");
            String loginsReplay = b.getString("loginReply");

            login.setText("@"+logins);
            pseudo.setText(pseudos);
            message.setText(loginsReplay);
            message.setSelection(message.getText().length());

            tweeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TweetManager tweetManag = new TweetManager(getApplicationContext());
                    Tweet tweets = new Tweet();
                    tweets.setMessage(message.getText().toString());
                    tweetManag.addTweet(message.getText().toString());
                    Intent intent = new Intent(Reply.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        } else if(action.equals("add")) {
            setTitle("Ajouter un Tweet");

            String logins = b.getString("login");
            String pseudos = b.getString("pseudo");

            login.setText(logins);
            pseudo.setText("@"+pseudos);

            tweeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TweetManager tweetManag = new TweetManager(getApplicationContext());
                    Tweet tweets = new Tweet();
                    tweets.setMessage(message.getText().toString());
                    tweetManag.addTweet(message.getText().toString());
                    finish();
                }
            });


        }


    }

}
