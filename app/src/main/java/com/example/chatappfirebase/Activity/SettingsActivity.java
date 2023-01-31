package com.example.chatappfirebase.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatappfirebase.Models.Users;
import com.example.chatappfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView setting_profile;
    EditText settingName, settingStatus;
    ImageView save;
    String email;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    Uri setImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setting_profile = findViewById(R.id.settings_profile_img);
        save = findViewById(R.id.save);
        settingName = findViewById(R.id.settings_name);
        settingStatus = findViewById(R.id.settings_status);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);


        DatabaseReference reference = firebaseDatabase.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        StorageReference storageReference = firebaseStorage.getReference().child("upload").child(firebaseAuth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                String image = Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();

                settingName.setText(name);
                settingStatus.setText(status);
                Picasso.get().load(image).into(setting_profile);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        save.setOnClickListener(view -> {
            String newsStatus = settingStatus.getText().toString();
            String newName = settingName.getText().toString();

            if (setImageURI != null) {
                progressDialog.show();
                storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String finalImageUri = uri.toString();
                                Users users = new Users(firebaseAuth.getUid(), newName, email, finalImageUri, newsStatus);

                                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                            progressDialog.dismiss();
                                        } else
                                            Toast.makeText(SettingsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                progressDialog.show();
                storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String finalImageUri = uri.toString();
                                Users users = new Users(firebaseAuth.getUid(), newName, email, finalImageUri, newsStatus);

                                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                            progressDialog.dismiss();
                                        } else
                                            Toast.makeText(SettingsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        setting_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                selectImageLauncher.launch("image/*");
            }
        });
    }

    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        setImageURI = uri;
                        setting_profile.setImageURI(setImageURI);
                    }
                }
            });
}