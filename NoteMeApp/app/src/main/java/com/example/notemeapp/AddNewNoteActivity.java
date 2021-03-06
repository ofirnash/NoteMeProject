package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class AddNewNoteActivity extends AppCompatActivity {
    Button createNoteBtn;
    Button takePhotoBtn;
    Button uploadPhotoBtn;
    ImageView photoChosen;
    EditText title;
    EditText description;
    String loggedInUser;
    SharedPreferences pref;
    LatLng notePosition;
    private static final String SERVER_ADDRESS = "http://192.168.1.55:8080/addnote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        title = (EditText) findViewById(R.id.text_new_note_name);
        description = (EditText) findViewById(R.id.text_new_note_description);
        photoChosen = (ImageView) findViewById(R.id.image_view_new_photo_uploaded);
        takePhotoBtn = (Button) findViewById(R.id.btn_take_photo_from_camera);
        uploadPhotoBtn = (Button) findViewById(R.id.btn_upload_photo_from_gallery);
        createNoteBtn = (Button) findViewById(R.id.btn_create_note);

        // Retrieve logged in username
        loggedInUser = getLoggedInUser();
        if (loggedInUser == null){
            // Retrieve failed -> Send to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
            }
        });

        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(uploadPhoto , 1);//one can be replaced with any action code
            }
        });

        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText() == null){
                    Toast.makeText(getApplicationContext(), "Note must have a title", Toast.LENGTH_LONG).show();
                }
                else {
                    notePosition = getIntent().getExtras().getParcelable("Marker_Position_To_Extract");
                    Log.d("New note position + ", notePosition.toString());
                    double latitude = notePosition.latitude;
                    double longitude = notePosition.longitude;
                    //TODO: See how to pass the image to the DB if exists
                    String image = "x";
                    addToMongo(loggedInUser,title,description,image,latitude,longitude);
                    Toast.makeText(getApplicationContext(), "Note Created!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String getLoggedInUser(){
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        return pref.getString("users_username", null); // get username as String, Null if empty
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bundle extras;
        Bitmap imageBitmap;
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    extras = imageReturnedIntent.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    photoChosen.setImageBitmap(imageBitmap);
                    //Uri selectedImage = imageReturnedIntent.getData();
                    // photoChosen.setImageURI(selectedImage);
                    photoChosen.setVisibility(View.VISIBLE);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    extras = imageReturnedIntent.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    photoChosen.setImageBitmap(imageBitmap);
                    photoChosen.setVisibility(View.VISIBLE);

//                    Uri selectedImage = imageReturnedIntent.getData();
//                    photoChosen.setImageURI(selectedImage);
                }
                break;
        }
    }

    public void addToMongo (String username, EditText noteTitle, EditText noteDescription, String noteImage,
                            double lat, double longi){
        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();
        JSONObject postJSON = new JSONObject();
        try {
            postJSON.put("userName", username);
            postJSON.put("title", title);
            postJSON.put("description", description);
            postJSON.put("image", noteImage);
            postJSON.put("latitude", lat);
            postJSON.put("longitude", longi);

            Log.d("PostJSON + " , String.valueOf(postJSON));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_ADDRESS, postJSON, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
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