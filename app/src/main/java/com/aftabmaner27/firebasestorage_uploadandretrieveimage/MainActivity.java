package com.aftabmaner27.firebasestorage_uploadandretrieveimage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aftabmaner27.firebasestorage_uploadandretrieveimage.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ActivityResultLauncher<String> launcher;

    // CREATE OBJECT
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//CREATE INSTANCE
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

//SET IMAGE ON DATABASE
        database.getReference().child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.getValue(String.class);
                Picasso.get()
                        .load(image)
                        .into(binding.imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//SET IMAGE FORM GALLARY
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        binding.imageView.setImageURI(uri);

//STORE IMAGE IN FIREBASE STORAGE
                        final StorageReference reference = storage.getReference()
                                .child("Image");

                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        database.getReference().child("Image")
                                                .setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                    }
                                });

                            }
                        });

                    }
                });

//CLICK BUTTON TO OPEN GALLARY
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");

            }
        });
    }
}