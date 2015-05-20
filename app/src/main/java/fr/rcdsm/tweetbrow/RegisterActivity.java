package fr.rcdsm.tweetbrow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by rcdsm on 27/04/15.
 */
public class RegisterActivity extends ActionBarActivity {

    EditText loginField;
    EditText passwordField;
    EditText rePasswordField;

    Button registerButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        loginField = (EditText) findViewById(R.id.registerLogin);
        passwordField = (EditText) findViewById(R.id.registerPassword);
        rePasswordField = (EditText) findViewById(R.id.registerPasswordConfirm);

        registerButton = (Button) findViewById(R.id.registerButton);
        cancelButton = (Button) findViewById(R.id.registerCancel);

        ClientAPI.createInstance(this.getApplicationContext());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (passwordField.getText().toString().equals(rePasswordField.getText().toString())) {
                    ClientAPI.getInstance().register(loginField.getText().toString(), passwordField.getText().toString(), new ClientAPI.APIListener() {
                        @Override
                        public void callback() {

                            ClientAPI.getInstance().connect(loginField.getText().toString(), passwordField.getText().toString(), new ClientAPI.APIListener() {
                                @Override
                                public void callback() {
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                } else
                    Toast.makeText(RegisterActivity.this.getApplicationContext(), "Les mot de passes ne se ressemblent pas !", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            }
        });

    }
}
