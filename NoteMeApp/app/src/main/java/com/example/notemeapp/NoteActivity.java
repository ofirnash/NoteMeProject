package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class NoteActivity extends AppCompatActivity {
    TextView noteName;
    TextView noteDescription;
    TextView noteLikesNum;
    ImageView noteImage;
    ImageButton likeBtn;
    Button sendBackToMapsBtn;
    LatLng markerPositionToExtract;

    private static final String SERVER_ADDRESS_GET_NOTE = "http://192.168.1.55:8080/getnote";
    private static final String SERVER_ADDRESS_ADD_LIKE = "http://192.168.1.55:8080/getnote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteName = (TextView) findViewById(R.id.text_view_note_name);
        noteDescription = (TextView) findViewById(R.id.text_view_note_description);
        noteLikesNum = (TextView) findViewById(R.id.text_view_note_likes_num);
        noteImage = (ImageView) findViewById(R.id.image_view_note);
        likeBtn = (ImageButton) findViewById(R.id.btn_like);
        sendBackToMapsBtn = (Button)findViewById(R.id.btn_back_to_maps);

        markerPositionToExtract = getIntent().getExtras().getParcelable("Marker_Position_To_Extract"); // Example: lat/lng: (37.4759737583517,-122.12302297353743)

        getNoteFromDB();

        // Like button
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // TODO: Implement +1 to like in DB
                addLikeToDB();
            }
        });

        // Back to Maps
        sendBackToMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    private void getNoteFromDB(){
        double latitude = markerPositionToExtract.latitude;
        double longitude = markerPositionToExtract.longitude;
        JSONObject postJSON = new JSONObject();
        try {
            postJSON.put("latitude", latitude);
            postJSON.put("longitude", longitude);
            System.out.println(postJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SERVER_ADDRESS_GET_NOTE, postJSON, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    noteName.setText(String.format("Title: %s", response.get("title").toString()));
                    noteDescription.setText(String.format("Description: %s", response.get("description").toString()));
                    noteLikesNum.setText(String.format("Likes: %s", response.get("likes").toString()));
                    noteImage.setImageURI(Uri.parse(response.get("image").toString()));
                }
                catch (JSONException e){
                    Log.e("NoteME", "unexpected JSON exception", e);
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

    private void addLikeToDB(){
        // TODO: How would we want to find the note? Currently did by lat,long - change if needed
        double latitude = markerPositionToExtract.latitude;
        double longitude = markerPositionToExtract.longitude;
        JSONObject postJSON = new JSONObject();
        try {
            postJSON.put("latitude", latitude);
            postJSON.put("longitude", longitude);
            System.out.println(postJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_ADDRESS_ADD_LIKE, postJSON, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Liked!", Toast.LENGTH_LONG).show();
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