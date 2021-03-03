package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    EditText fullName; //TODO: Need to add box in the page
    EditText email;
    EditText password;
    EditText confirmPass;
    Button signUp;
    TextView text;
    TextView terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        text = (TextView) findViewById(R.id.text_view_sign_up);
        terms = (TextView) findViewById(R.id.text_view_terms);
        fullName = (EditText) findViewById(R.id.text_full_name_sign_up); //Add to the app!
        email = (EditText) findViewById(R.id.text_email_address_sign_up);
        password = (EditText) findViewById(R.id.text_password_sign_up);
        confirmPass = (EditText) findViewById(R.id.text_confirm_password_sign_up);

        // SignUp Successful-> Send to Maps
        // TODO: Need verification for SignUp credentials? (username + password) are good!!!
        signUp = (Button)findViewById(R.id.btn_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                boolean signUpData = checkSignUpData();
                if (!signUpData){
                    //TODO: add a popup that data is not good
                    Toast.makeText(getApplicationContext(), "Can't sign in. Make sure you have entered all data required properly", Toast.LENGTH_LONG).show();
                } else {
                    addToMongo();
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //This function checks that all required data is filled and that password was confirmed properly
    private boolean checkSignUpData() {
        boolean allowSignUp = true;
        if (fullName.getText().toString().isEmpty()){
            allowSignUp = false;
            fullName.setError("Must not be empty");
        }
        if (email.getText().toString().isEmpty()){
            allowSignUp = false;
            email.setError("Must not be empty");
        }
        if (password.getText().toString().isEmpty()){
            allowSignUp = false;
            password.setError("Must not be empty");
        }
        if (confirmPass.getText().toString().isEmpty()){
            allowSignUp = false;
            confirmPass.setError("Must not be empty");
        }
        if (password.getText().toString() != confirmPass.getText().toString()) {
            allowSignUp = false;
            //TODO: add a popup that says that the password don't match
            Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
        else {
            String verifyEmail = email.getText().toString();
            allowSignUp = Patterns.EMAIL_ADDRESS.matcher(verifyEmail).matches();
            if (!allowSignUp){
                email.setError("Email address is not valid");
            }
        }
        return allowSignUp;
    }

    private void addToMongo(){
        try{
            MongoClientURI uri  = new MongoClientURI("mongodb://localhost:27017/signup");
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
}