package com.example.unabiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import com.squareup.picasso.*;

public class Mapping_mode extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =1;
    StorageReference storage;
    EditText url_string;
    Button upload_image;
    Button confirm_url_button;
    ImageView PreviewImage;
    public Uri mImageUri;
    String IMAGE_KEY = "image";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapping_mode);

        url_string = findViewById(R.id.UrlEntry);
        upload_image = findViewById(R.id.UrlUpload);
        confirm_url_button = findViewById(R.id.ConfirmURL);
        PreviewImage = findViewById(R.id.PreviewImage);
        storage = FirebaseStorage.getInstance().getReference();


        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_stringText = url_string.getText().toString();
                if(url_stringText.isEmpty()){
                    Toast.makeText(Mapping_mode.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                } else {
                    LoadImage loadImage = new LoadImage(PreviewImage);
                    loadImage.execute(url_stringText);
                    System.out.println(url_stringText);

                }
            }
        });

        confirm_url_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_stringText = url_string.getText().toString();
                Intent intent = new Intent(getApplicationContext(), Mapping.class);
                System.out.println(url_stringText);
                intent.putExtra(IMAGE_KEY, url_stringText);
                url_string.setText("");
                startActivity(intent);
            }
        });


    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public LoadImage(ImageView PreviewImage) {
            this.imageView = PreviewImage;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URLlink = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(URLlink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            PreviewImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            //System.out.println(mImageUri.toString());

            Picasso.get().load(mImageUri).into(PreviewImage);
        }
    }
}


