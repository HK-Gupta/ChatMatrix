package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    CircleImageView profile_pic_chatClass;
    TextView text_receiver_name;
    CardView btn_send_message;
    EditText txt_write_message;
    RecyclerView message_recycler_view;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<MessageModel> messageModelArrayList;
    MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_class);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        receiverName = getIntent().getStringExtra("name_to_user");
        receiverImage = getIntent().getStringExtra("image_to_user");
        receiverUid = getIntent().getStringExtra("uid_to_user");

        messageModelArrayList = new ArrayList<>();

        profile_pic_chatClass = findViewById(R.id.profile_pic_chatClass);
        text_receiver_name = findViewById(R.id.text_receiver_name);
        btn_send_message = findViewById(R.id.btn_send_message);
        txt_write_message = findViewById(R.id.txt_write_message);
        message_recycler_view = findViewById(R.id.message_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        message_recycler_view.setLayoutManager(linearLayoutManager);
        mMessageAdapter = new MessageAdapter(ChatClass.this, messageModelArrayList);
        message_recycler_view.setAdapter(mMessageAdapter );


        Picasso.get().load(receiverImage).into(profile_pic_chatClass);
        text_receiver_name.setText("" + receiverName);

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
}