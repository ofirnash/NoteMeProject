package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    Button sendToSignUpBtn;
    Button sendToMapsBtn;
    EditText username;
    EditText password;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String loggedInUser;

    private static final String SERVER_ADDRESS = "http://192.168.1.55:8080/login";

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
                    logInUser(username, password);
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

    private void logInUser (EditText username, EditText password){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        JSONObject postJSON = new JSONObject();
        try {
            postJSON.put("userName", user);
            postJSON.put("password", pass);
            System.out.println(postJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_ADDRESS, postJSON, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                //TODO check if credentials match
                if(response.getBoolean("added")) {
                    storeUserInSharedPreferences();
                    Toast.makeText(getApplicationContext(), String.format("Welcome back: %s!", loggedInUser), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }
                else {
                    // Credentials don't match
                    Toast.makeText(getApplicationContext(), "The username or password you’ve entered doesn’t match any account.", Toast.LENGTH_LONG).show();
                    // TODO: Test if it works, if not then remove the finish()
                    // Refresh page
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                //return response.toString().contains("exists");
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