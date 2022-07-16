package com.example.diplomski;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.diplomski.Adapter.ChatAdapter;
import com.example.diplomski.Models.MessageModel;
import com.example.diplomski.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String senderRoomGlobal, receiverRoomGlobal, senderIdGlobal, receiverIdGlobal;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        final String senderId = auth.getUid();
        senderIdGlobal = senderId;
        Uri a = getIntent().getData();
        String receiverId = getIntent().getStringExtra("userId");
        receiverIdGlobal = receiverId;
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);

        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;
        senderRoomGlobal = senderRoom;
        receiverRoomGlobal = receiverRoom;

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean imageProcessedFlag = false;
                messageModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel model = null;
                    if(snapshot1.getKey().equals("image") && !imageProcessedFlag) {
                        imageProcessedFlag = true;
                        model = new MessageModel(senderId, "", snapshot1.getValue().toString(), new Date().getTime());
                        messageModels.add(model);
                    } else if(!snapshot1.getKey().equals("message") && !snapshot1.getKey().equals("timestamp") && !snapshot1.getKey().equals("uId") && !snapshot1.getKey().equals("")) {
                        model = snapshot1.getValue(MessageModel.class);
                        model.setMessageId(snapshot1.getKey());
                        messageModels.add(model);
                    }
                }

                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.enterMessage.getText().toString();
                final MessageModel model = new MessageModel(senderId, message, "");
                model.setTimestamp(new Date().getTime());
                binding.enterMessage.setText("");

                database.getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {

        }
        else {
            if (data.getData() != null) {
                Uri sFile = data.getData();

                final StorageReference reference = storage.getReference().child("image").child(FirebaseAuth.getInstance().getUid());

                reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final MessageModel model = new MessageModel(senderIdGlobal, "", uri.toString(), new Date().getTime());
                                database.getReference().child("chats").child(receiverRoomGlobal).push().setValue(model);
                                database.getReference().child("chats").child(senderRoomGlobal).push().setValue(model);
                            }
                        });
                    }
                });
            }
        }
    }
}