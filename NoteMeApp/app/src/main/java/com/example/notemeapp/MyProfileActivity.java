package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.Arrays;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {
    SharedPreferences pref;
    String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //TODO: Set up adapter!!!
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.my_notes_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); // For improved performance - saw in a video tutorial.

        // TODO: Get from DB titles, descriptions...
        //List<String> lannisterListCharacters = Arrays.asList(getResources().getStringArray(R.array.lannister_characters_names));
        //MyProfileAdapter adapter = new MyProfileAdapter(lannisterListCharacters, title, description);
        //recyclerView.setAdapter(adapter);


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
        return pref.getString("users_username", null); // get username as String, Null if empty
    }
}