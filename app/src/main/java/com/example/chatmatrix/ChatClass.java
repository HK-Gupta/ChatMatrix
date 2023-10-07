package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatClass extends AppCompatActivity {

    public static String senderImg;
    public static String receiverImg;
    String senderRoom, receiverRoom;
    String  receiverName, receiverImage, receiverUid, senderUid;
    CardView btn_send_message;
    EditText txt_write_message;
    ImageView send_attach_file;
    RecyclerView message_recycler_view;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<MessageModel> messageModelArrayList;
    MessageAdapter mMessageAdapter;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chat Matrix");
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#344955"));
        actionBar.setBackgroundDrawable(colorDrawable);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        receiverName = getIntent().getStringExtra("name_to_user");
        receiverImage = getIntent().getStringExtra("image_to_user");
        receiverUid = getIntent().getStringExtra("uid_to_user");

        messageModelArrayList = new ArrayList<>();

        btn_send_message = findViewById(R.id.btn_send_message);
        txt_write_message = findViewById(R.id.txt_write_message);
        message_recycler_view = findViewById(R.id.message_recycler_view);
        send_attach_file = findViewById(R.id.send_attach_file);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        message_recycler_view.setLayoutManager(linearLayoutManager);
        mMessageAdapter = new MessageAdapter(ChatClass.this, messageModelArrayList);
        message_recycler_view.setAdapter(mMessageAdapter );

        actionBar.setTitle(""+ receiverName);

        senderUid = firebaseAuth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        DatabaseReference reference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("message");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModelArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageModelArrayList.add(messageModel);
                }
                mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilePic").getValue().toString();
                receiverImg = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");  // You can specify the file type(s) you want to allow here
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 999);
            }
        });

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txt_write_message.getText().toString();
                if(message.isEmpty()) {
                    Toast.makeText(ChatClass.this, "Nothing to Send", Toast.LENGTH_SHORT).show();
                    return;
                }

                txt_write_message.setText("");
                Date date = new Date();
                MessageModel messageModel = new MessageModel(message, senderUid, date.getTime());



                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("chats").child(senderRoom)
                            .child("message").push().setValue(messageModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.getReference().child("chats").child(receiverRoom)
                                            .child("message").push().setValue(messageModel)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                }
                                            });
                                }
                            });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        Intent intent = new Intent(ChatClass.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999) {
            if (resultCode == RESULT_OK && data != null) {
                fileUri = data.getData();
            }
        }
    }
}