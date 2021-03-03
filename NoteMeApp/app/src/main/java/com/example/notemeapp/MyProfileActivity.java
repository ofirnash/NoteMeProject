package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MyProfileActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //TODO: Fetch from DB all of my posts according to my username
        loggedInUser = getLoggedInUser();
        //fetchMyNotesFromDB();

        // Go back to maps
        Button goBackBtn = (Button)findViewById(R.id.btn_send_back_from_profile);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    private String getLoggedInUser(){
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        //editor = pref.edit();
        return pref.getString("users_username", null); // get username as String, Null if empty
    }
}