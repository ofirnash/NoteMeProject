package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String loggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendToSignUpBtn = (Button)findViewById(R.id.btn_send_to_sign_up);
        sendToMapsBtn = (Button)findViewById(R.id.btn_login);

        // Sign Up Page
        sendToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Login Successful-> Send to Maps
        // TODO: Need verification for login credentials (username + password) are good!!!
        sendToMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                username = (EditText) findViewById(R.id.text_username);
                password = (EditText) findViewById(R.id.text_password);
                if (checkLoginData()) {
                    if (!checkMatchingAccountInDB()) {
                        Toast.makeText(getApplicationContext(), "The username or password you’ve entered doesn’t match any account.", Toast.LENGTH_LONG).show();
                    } else {
                        // Shared prefs - Store Attempt!
                        storeUserInSharedPreferences();
                        Toast.makeText(getApplicationContext(), String.format("Welcome: %s!", loggedInUser), Toast.LENGTH_LONG).show();

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

    private boolean checkMatchingAccountInDB(){
        //TODO: Check matching credentials in DB
        return true; // FOR TESTING! REMOVE
//        try {
//            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/login");
//            MongoClient client = new MongoClient(uri);
//
//            MongoDatabase db = client.getDatabase(uri.getDatabase());
//            MongoCollection<BasicDBObject> collection = db.getCollection("users", BasicDBObject.class);
//
//            // FIX!!!
//            BasicDBObject document = new BasicDBObject();
//            document.put("name", "mkyong");
//            document.put("age", 30);
//            collection.insertOne(document);
//
//            MongoCursor iterator = collection.find().iterator();
//
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void storeUserInSharedPreferences(){
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        editor.putString("users_username", username.getText().toString()); // Storing string
        editor.putString("users_password", password.getText().toString()); // Storing string

        editor.commit(); // commit changes

        loggedInUser = pref.getString("users_username", null); // getting String, Null if empty
    }
}