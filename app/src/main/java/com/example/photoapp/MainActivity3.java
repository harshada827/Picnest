package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity3 extends AppCompatActivity {

    Button btnUpload, btnView, btnLogout;
    ImageView imgPreview;
    Uri imageUri;

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore firestore;

    private static final int PICK_IMAGE = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        btnUpload = findViewById(R.id.button1);   // give id to your upload button in XML
        btnView = findViewById(R.id.button2);     // give id to your view button in XML
        btnLogout = findViewById(R.id.logout);    // give id to your logout button in XML
        imgPreview = new ImageView(this); // you can also place ImageView in xml for preview

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        // Upload Image
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        // View Image
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example logout (if using FirebaseAuth)
                // FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity3.this,MainActivity2.class);
                startActivity(intent);
                Toast.makeText(MainActivity3.this, "Logged out", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Open gallery
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    // Upload to Firebase Storage
    private void uploadImage() {
        if (imageUri != null) {
            String fileName = "images/" + UUID.randomUUID().toString();
            StorageReference ref = storageRef.child(fileName);

            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    saveToFirestore(uri.toString());
                                    Toast.makeText(MainActivity3.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity3.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Save download URL in Firestore
    private void saveToFirestore(String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("imageUrl", imageUrl);

        firestore.collection("images")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity3.this, "URL Saved in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
