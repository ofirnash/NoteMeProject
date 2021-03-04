package com.example.notemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AddNewNoteActivity extends AppCompatActivity {
    Button createNoteBtn;
    Button takePhotoBtn;
    Button uploadPhotoBtn;
    ImageView photoChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        photoChosen = (ImageView) findViewById(R.id.image_view_new_photo_uploaded);

        takePhotoBtn = (Button) findViewById(R.id.btn_take_photo_from_camera);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
            }
        });

        uploadPhotoBtn = (Button) findViewById(R.id.btn_upload_photo_from_gallery);
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(uploadPhoto , 1);//one can be replaced with any action code
            }
        });

        createNoteBtn = (Button) findViewById(R.id.btn_create_note);
        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Note Created!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
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
}