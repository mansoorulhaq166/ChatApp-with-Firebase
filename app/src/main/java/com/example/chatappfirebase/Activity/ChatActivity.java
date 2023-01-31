package com.example.chatappfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.chatappfirebase.Adapters.MessageAdapter;
import com.example.chatappfirebase.Models.Messages;
import com.example.chatappfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String receiverName, receiverImage, receiverUid, senderUid;
    CircleImageView profileImage;
    TextView profile_name;
    FirebaseAuth auth;
    FirebaseDatabase database;
    public static String senderImage;
    public static String rImage;
    String senderRoom, receiverRoom;
    EditText messageText;
    CardView messageButton;
    RecyclerView messageRecycler;
    ArrayList<Messages> messagesList;

    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        profileImage = findViewById(R.id.chat_profile_image);
        profile_name = findViewById(R.id.user_name_chat);
        messageText = findViewById(R.id.message_text);
        messageButton = findViewById(R.id.message_button);
        messageRecycler = findViewById(R.id.chat_recyclerView);
        AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.message_text, RegexTemplate.NOT_EMPTY, R.string.message_error);

        //setting recycler layout from end
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(linearLayoutManager);

        messagesList = new ArrayList<>();

        messageAdapter = new MessageAdapter(this, messagesList);
        messageRecycler.setAdapter(messageAdapter);

        receiverUid = getIntent().getStringExtra("userUid");
        receiverName = getIntent().getStringExtra("userName");
        receiverImage = getIntent().getStringExtra("userImage");

        Picasso.get().load(receiverImage).into(profileImage);
        profile_name.setText(" " + receiverName);

        senderUid = auth.getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesList.add(messages);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                senderImage = Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
                rImage = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageText.getText().toString();

                if (awesomeValidation.validate()) {
                    messageText.setText("");
                    Date date = new Date();

                    Messages message1 = new Messages(message, senderUid, date.getTime());
                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push()
                            .setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .push()
                                            .setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });

                }
            }
        });
    }
}