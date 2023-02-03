package com.example.chatappfirebase.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setting_profile = findViewById(R.id.settings_profile_img);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_rate:
                saveSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSettings() {
        final String newsStatus = settingStatus.getText().toString();
        final String newName = settingName.getText().toString();

        if (!newsStatus.isEmpty() && !newName.isEmpty()) {
            progressDialog.show();
            final DatabaseReference reference = firebaseDatabase.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
            final StorageReference storageReference = firebaseStorage.getReference().child("upload").child(firebaseAuth.getUid());

            if (setImageURI != null) {
                storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String finalImageUri = uri.toString();
                                reference.child("name").setValue(newName);
                                reference.child("status").setValue(newsStatus);
                                reference.child("imageUri").setValue(finalImageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                reference.child("name").setValue(newName);
                reference.child("status").setValue(newsStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Data updated Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Data not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}

// Old Method
/*

  if(setImageURI!=null){
          storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){
@Override
public void onComplete(@NonNull Task<UploadTask.TaskSnapshot>task){
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
@Override
public void onSuccess(Uri uri){
        String finalImageUri=uri.toString();
        Users users=new Users(firebaseAuth.getUid(),newName,email,finalImageUri,newsStatus);

        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>(){
@Override
public void onComplete(@NonNull Task<Void> task){
        if(task.isSuccessful()){
        Toast.makeText(SettingsActivity.this,"Successfully Updated",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        progressDialog.dismiss();
        }else{
        progressDialog.dismiss();
        Toast.makeText(SettingsActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
        }
        }
        });
        }
        });
        }
        });*/
