package com.example.prathibhaalam.samplerecyclerviewapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddANewCatActivity extends AppCompatActivity {

    public static final String IMAGE_URI = "image_uri";
    public static final String CAT_NAME = "cat_name";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int WRITE_PERMISSION_REQUEST = 2;

    private Uri imageUri;
    private Uri intentDataUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anew_cat);

    }

    public void openGalleryActivity(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void saveData(View view) {
        Intent intent = new Intent();
        intent.putExtra(IMAGE_URI, imageUri.toString());
        EditText name = findViewById(R.id.cat_name);
        intent.putExtra(CAT_NAME, name.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
            writeToStorage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            intentDataUri = data.getData();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
            } else {
                writeToStorage();
            }
        }
    }

    void writeToStorage() {

        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), intentDataUri);
            ImageView contactPhoto = findViewById(R.id.cat_photo);
            contactPhoto.setImageBitmap(bmp);
            int randomInt = new Random().nextInt(20);
            File  file = new File(Environment.getExternalStorageDirectory()+"/Pictures/download"+randomInt+".jpeg");
            imageUri = Uri.fromFile(file);
            Log.e("Image URI = ", imageUri.toString());
            FileOutputStream fout = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
