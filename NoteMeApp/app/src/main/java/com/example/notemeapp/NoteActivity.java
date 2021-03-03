package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteActivity extends AppCompatActivity {
    TextView noteName;
    TextView noteDescription;
    ImageView noteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // TODO: Extract note by lat/long -
        //                'user': user,
        //                'title': title,
        //                'description': description,
        //                'image': image,
        //                'latitude': latitude,
        //                'longitude': longitude

        noteName = (TextView) findViewById(R.id.text_view_note_name);
        noteDescription = (TextView) findViewById(R.id.text_view_note_description);
        noteImage = (ImageView) findViewById(R.id.image_view_note);

        noteName.setText("BLA");
        noteDescription.setText("BLA");
        //noteImage.setImageResource();

        // Back to Maps
        Button sendBackToMapsBtn = (Button)findViewById(R.id.btn_back_to_maps);
        sendBackToMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
//                Intent intent = new Intent(v.getContext(), MapsActivity.class);
//                startActivity(intent);
                finish(); // TODO - Test if it works (returns to the previous state), if not use above commented out code which will initialize everything again.
            }
        });
    }
}