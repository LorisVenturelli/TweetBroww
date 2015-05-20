package fr.rcdsm.tweetbrow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    String id_parent;

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

        id_parent = null;

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

        login.setText("@"+User.getInstance().getLogin());
        pseudo.setText(User.getInstance().getPseudo());

        if (action.equals("reply")) {

            setTitle("Repondre à un Tweet");

            id_parent = b.getString("id_parent");
            Tweet parent = manager.getTweetWithId(Long.valueOf(id_parent));

            message.setText("@" + parent.getLogin() + " ");
            message.setSelection(message.getText().length());

        } else if(action.equals("add")) {
            setTitle("Ajouter un Tweet");
        }

        tweeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager.addTweet(message.getText().toString(), id_parent);

                Intent intent = new Intent(Reply.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

}
