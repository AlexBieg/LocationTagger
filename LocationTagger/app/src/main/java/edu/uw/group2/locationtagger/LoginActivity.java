package edu.uw.group2.locationtagger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);

                startActivity(intent);
            }
        });

        View signupButton = findViewById(R.id.signupBtn);
        signupButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                EditText userName = (EditText) findViewById(R.id.usernameTxt);
                EditText password = (EditText) findViewById(R.id.passwordTxt);

                Log.v(TAG, userName.getText().toString() + " " + password.getText().toString());
                //Create account on firebase
                //login with this button too
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);

                startActivity(intent);
            }
        });
    }
}
