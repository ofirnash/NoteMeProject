package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {
    Button goBackBtn;
    SharedPreferences pref;
    String loggedInUser;
    RecyclerView recyclerView;

    private static final String SERVER_ADDRESS_GET_ALL_NOTES = "http://192.168.1.55:8080/getallnotes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        goBackBtn = (Button)findViewById(R.id.btn_send_back_from_profile);

        //TODO: Set up adapter!!!
        recyclerView = (RecyclerView)findViewById(R.id.my_notes_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); // For improved performance

        loggedInUser = getLoggedInUser();
        getAllMyNotesFromDB();

        // Go back to maps
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

    private void getAllMyNotesFromDB(){
        // TODO  Fetch from DB all of my posts according to my username!!!
        JSONObject postJSON = new JSONObject();
        try {
            postJSON.put("username", loggedInUser);
            System.out.println(postJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SERVER_ADDRESS_GET_ALL_NOTES, postJSON, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                // TODO: Get all user's Notes - Will need to trigger Adapter!!!! Gets titles,description..
                //List<String> lannisterListCharacters = Arrays.asList(getResources().getStringArray(R.array.lannister_characters_names));
                //MyProfileAdapter adapter = new MyProfileAdapter(lannisterListCharacters, title, description);
                //recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueueFetcher.getInstance(this).getQueue().add(request);
    }
}