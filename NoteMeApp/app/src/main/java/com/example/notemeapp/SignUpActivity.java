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

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


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
        // TODO: Need verification for SignUp credentials
        signUp = (Button) findViewById(R.id.btn_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean signUpData = checkSignUpData();
                if (!signUpData) {
                    Toast.makeText(getApplicationContext(), "Can't sign in. Make sure you have entered all data required properly", Toast.LENGTH_LONG).show();
                } else {
                    storeUserInSharedPreferences();
                    Toast.makeText(getApplicationContext(), String.format("Welcome: %s!", loggedInUser), Toast.LENGTH_LONG).show();

                    addToMongo();
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);
                    startActivity(intent);
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
        //TODO: Check if username is enough for us...
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        editor.putString("users_username", username.getText().toString()); // Storing string
        editor.putString("users_password", password.getText().toString()); // Storing string

        editor.commit(); // commit changes

        loggedInUser = pref.getString("users_username", null); // getting String, Null if empty
    }

    private void addToMongo() {
        try {
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/signup");
            MongoClient client = new MongoClient(uri);

            MongoDatabase db = client.getDatabase(uri.getDatabase());
            MongoCollection<BasicDBObject> collection = db.getCollection("salam", BasicDBObject.class);

            BasicDBObject document = new BasicDBObject();
            document.put("name", "mkyong");
            document.put("age", 30);
            collection.insertOne(document);

            MongoCursor iterator = collection.find().iterator();

            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}