package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class NoteActivity extends AppCompatActivity {
    TextView noteName;
    TextView noteDescription;
    ImageView noteImage;
    ImageButton likeBtn;
    Button sendBackToMapsBtn;
    LatLng markerPositionToExtract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        markerPositionToExtract = getIntent().getExtras().getParcelable("Marker_Position_To_Extract"); // Example: lat/lng: (37.4759737583517,-122.12302297353743)

        // TODO: Extract note by markerPositionToExtract: lat/long -
        //                'user': user,
        //                'title': title,
        //                'description': description,
        //                'image': image,
        //                'latitude': latitude,
        //                'longitude': longitude

        noteName = (TextView) findViewById(R.id.text_view_note_name);
        noteDescription = (TextView) findViewById(R.id.text_view_note_description);
        noteImage = (ImageView) findViewById(R.id.image_view_note);

        // TODO: Set text once extracted from DB and move to visible!!! This will be if the user doesn't enter anything. Default is invisible
        // Set
        noteName.setText("BLA");
        noteDescription.setText("BLA");
        //noteImage.setImageResource();

        // Like button
        likeBtn = (ImageButton) findViewById(R.id.btn_like);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "Liked!", Toast.LENGTH_LONG).show();
                // TODO: Implement +1 to like in DB
            }
        });

        // Back to Maps
        sendBackToMapsBtn = (Button)findViewById(R.id.btn_back_to_maps);
        sendBackToMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
}