package fr.rcdsm.tweetbrow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;

/**
 * Created by rcdsm on 27/04/15.
 */
public class LoginActivity extends ActionBarActivity {

    EditText loginField;
    EditText passwordField;

    Button loginButton;
    Button goRegister;

    AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        loginField = (EditText) findViewById(R.id.loginLogin);
        passwordField = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        goRegister = (Button) findViewById(R.id.goRegister);

        aq  = new AQuery(this);

        ClientAPI.createInstance(this.getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClientAPI.getInstance().connect(loginField.getText().toString(), passwordField.getText().toString(), new ClientAPI.APIListener() {
                    @Override
                    public void callback() {
                        Toast.makeText(aq.getContext(), "Connecté en tant que " + loginField.getText().toString() + " !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
