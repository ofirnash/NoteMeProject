package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {
    EditText username;
    EditText email;
    EditText password;
    EditText confirmPass;
    Button signUp;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String loggedInUser;
    TextView text;
    TextView terms;

    private static final String SERVER_ADDRESS = "http://192.168.1.55:8080/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        text = (TextView) findViewById(R.id.text_view_sign_up);
        terms = (TextView) findViewById(R.id.text_view_terms);
        username = (EditText) findViewById(R.id.text_user_name_sign_up);
        email = (EditText) findViewById(R.id.text_email_address_sign_up);
        password = (EditText) findViewById(R.id.text_password_sign_up);
        confirmPass = (EditText) findViewById(R.id.text_confirm_password_sign_up);

        // SignUp Successful-> Send to Maps
        signUp = (Button) findViewById(R.id.btn_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean signUpData = checkSignUpData();
                if (!signUpData) {
                    Toast.makeText(getApplicationContext(), "Can't sign in. Make sure you have entered all data required properly", Toast.LENGTH_LONG).show();
                } else {
                    addToMongo(username,email,password);
                }
            }
        });

        // Go Back to Login Activity
        Button backToLoginBtn = (Button)findViewById(R.id.btn_go_back_to_login);
        backToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    //This function checks that all required data is filled and that password was confirmed properly
    private boolean checkSignUpData() {
        boolean allowSignUp = true;
        if (username.getText().toString().isEmpty()) {
            allowSignUp = false;
            username.setError("Must not be empty");
        }
        if (email.getText().toString().isEmpty()) {
            allowSignUp = false;
            email.setError("Must not be empty");
        }
        if (password.getText().toString().isEmpty()) {
            allowSignUp = false;
            password.setError("Must not be empty");
        }
        if (confirmPass.getText().toString().isEmpty()) {
            allowSignUp = false;
            confirmPass.setError("Must not be empty");
        }
        if (!password.getText().toString().equals(confirmPass.getText().toString())) {
            allowSignUp = false;
            Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
        } else {
            String verifyEmail = email.getText().toString();
            allowSignUp = Patterns.EMAIL_ADDRESS.matcher(verifyEmail).matches();
            if (!allowSignUp) {
                email.setError("Email address is not valid");
            }
        }
        return allowSignUp;
    }

    private void storeUserInSharedPreferences(){
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        editor.putString("users_username", username.getText().toString()); // Storing string
        editor.putString("users_password", password.getText().toString()); // Storing string

        editor.commit(); // commit changes

        loggedInUser = pref.getString("users_username", null); // getting String, Null if empty
    }

    public void addToMongo (EditText username, EditText email, EditText password){
        String user = username.getText().toString();
        String emailAddress = email.getText().toString();
        String pass = password.getText().toString();
        JSONObject postJSON = new JSONObject();
        try {
            postJSON.put("userName", user);
            postJSON.put("email", emailAddress);
            postJSON.put("password", pass);
            System.out.println(postJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_ADDRESS, postJSON, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                if(response.toString().contains("successfully")) {
                    storeUserInSharedPreferences();
                    Toast.makeText(getApplicationContext(), String.format("Welcome: %s!", loggedInUser), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }
                else {
                    // User already exists
                    Toast.makeText(getApplicationContext(), "User name already exists! Please login or sign up with a new user", Toast.LENGTH_LONG).show();
                    // Refresh page
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                }
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