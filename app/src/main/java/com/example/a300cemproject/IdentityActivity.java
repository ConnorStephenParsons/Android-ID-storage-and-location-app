package com.example.a300cemproject;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdentityActivity extends AppCompatActivity implements View.OnClickListener {

    //Variables for the request codes when working with the gallery and camera permissions
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int CAMERA_OPEN_REQUEST_CODE = 101;
    public static final int GALLERY_ACCESS_CODE = 102;
    //defining variables for the elements on the page
    ImageView studentIdImage;
    Button cameraButton;
    Button galleryButton;
    TextView banner;
    String currentPhotoPath;
    StorageReference storageReference;
    Button locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);

        //assigning the resources via ID to the variables created for the elements on the page
        studentIdImage = (ImageView) findViewById(R.id.studentId);
        cameraButton = (Button) findViewById(R.id.camera);
        galleryButton = (Button) findViewById(R.id.gallery);
        banner = (TextView) findViewById(R.id.banner);
        locationButton = (Button) findViewById(R.id.location);

        //Allows the reference to be saved for the image in the firebase storage drive
        storageReference = FirebaseStorage.getInstance().getReference();

        //Initiating the button to be able to go to the new location sensors screen
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(IdentityActivity.this, LocationActivity.class);
                startActivity(myIntent);
            }
        });




        //clickable methods for the camera and gallery button implemented with message
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IdentityActivity.this, "The Camera button has been clicked...", Toast.LENGTH_LONG).show();
                CameraPermissions();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IdentityActivity.this, "The Gallery button has been clicked...", Toast.LENGTH_LONG).show();
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_ACCESS_CODE);
            }
        });

    }
    //When the user clicks on the camera button, it will ask them permissions to use the camera
    private void CameraPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            cameraOpen();
        }
    }


    //If the user does not click the permission, then they will not be able to use the camera sensor
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                cameraOpen();
            }else {
                Toast.makeText(this, "Camera Permission acceptance is needed to use this camera button.", Toast.LENGTH_LONG).show();
            }
        }
    }
    //Function allowing the camera to be opened successfully
    private void cameraOpen() {
    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(camera, CAMERA_OPEN_REQUEST_CODE);
    }


    //Allows the image we take inside of the app to be saved to the bitmap image which is the green placeholder box via the request code as an activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Image is uploaded to the image view via camera if the user accepts the permission
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_OPEN_REQUEST_CODE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            studentIdImage.setImageBitmap(image);
        }
        //User is able to upload their image through the gallery to enable them to upload a image they have taken locally
        if (requestCode == GALLERY_ACCESS_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageContent = data.getData();
                //Timestamp and location of the image has been recorded
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExtension(imageContent);
                //The jpeg image can be uploaded when the activity has finished
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);

                firebaseUpload(imageContent, imageFileName);
            }

        }
    }

    //A function which takes the image the user has taken, and stores it inside of firebase storage
    private void firebaseUpload(Uri imageContent, String ImageFileName) {
        //A folder called images will be created on firebase storage to save the identity picture into
        final StorageReference image = storageReference.child("images/");
        image.putFile(imageContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            //a snapshot of the picture will be uploaded to firebase if successful and a picasso notice will be displayed under the image View from the imageContent Uri.
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: The URL of the image is " + uri.toString());
                        Picasso.get().load(uri).into(studentIdImage);
                    }
                });
                //If everything has gone well, then the user will be notified that their image has been uploaded to firebase.
                Toast.makeText(IdentityActivity.this, "Image uploaded successfully.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            //user will be notified if their image has failed to be uploaded
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IdentityActivity.this, "The image has failed to upload to firebase unfortunately...", Toast.LENGTH_LONG).show();
            }
        });

    }


    //allows the getFileExtension for the image content to successfully render using the Mime import
    private String getFileExtension(Uri imageContent) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(imageContent));
    }


    //Allowing us to save the image file location

    private File createImageFile() throws IOException {
        // Creating an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Allowing the image to be saved to external storage
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // getting the actual image path to display the image from gallery
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
    }
}
