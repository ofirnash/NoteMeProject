package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MainActivity extends AppCompatActivity {
    Button sendToSignUpBtn;
    Button sendToMapsBtn;
    EditText username;
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sign Up Page
        sendToSignUpBtn = (Button)findViewById(R.id.btn_send_to_sign_up);
        sendToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Login Successful-> Send to Maps
        // TODO: Need verification for login credentials (username + password) are good!!!
        sendToMapsBtn = (Button)findViewById(R.id.btn_login);
        sendToMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (checkLoginData()) {
                    if (!checkMatchingAccount()) {
                        //TODO: add a popup that data is not good - Login unsuccessful
                        Toast.makeText(getApplicationContext(), "The username or password you’ve entered doesn’t match any account.", Toast.LENGTH_LONG).show();
                    } else {
                        // TODO: Add to SharedPreferences in order to save his login session.
                        //addToMongo();
                        Intent intent = new Intent(v.getContext(), MapsActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private boolean checkLoginData(){
        boolean allowLogin = true;
        username = (EditText) findViewById(R.id.text_username);
        password = (EditText) findViewById(R.id.text_password);

        if (username.getText().toString().isEmpty()) {
            allowLogin = false;
            username.setError("Must not be empty");
        }
        if (password.getText().toString().isEmpty()) {
            allowLogin = false;
            password.setError("Must not be empty");
        }
        return allowLogin;
    }

    private boolean checkMatchingAccount(){
        //TODO: Check matching credentials in DB
        return true; // FOR TESTING! REMOVE
    }
}