package com.example.chatmatrix;

import static com.example.chatmatrix.ChatClass.receiverImg;
import static com.example.chatmatrix.ChatClass.senderImg;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<MessageModel> messageAdapterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVED = 2;

    public MessageAdapter(Context context, ArrayList<MessageModel> messageAdapterArrayList) {
        this.mContext = context;
        this.messageAdapterArrayList = messageAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sender_layout, parent, false);
            return new SenderViewHolder(view);
        } else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.receiver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageAdapterArrayList.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder name = new AlertDialog.Builder(mContext);
                name.setTitle("Delete !");
                name.setCancelable(false);
                name.setMessage("Do you really want to Delete the message?");
                name.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        deleteMessage("senderRoom", "receiverRoom", FirebaseAuth.getInstance());
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                name.show();
                return false;
            }
        });

        if(holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.message_text.setText(messageModel.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.message_text.setText(messageModel.getMessage());
            Picasso.get().load(receiverImg).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel = messageAdapterArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageModel.getSenderId())) {
            return  ITEM_SEND;
        } else {
            return ITEM_RECEIVED;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView message_text;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.sender_pic_message);
            message_text = itemView.findViewById(R.id.sender_text_message);
        }
    }


    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView message_text;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.receiver_pic_message);
            message_text = itemView.findViewById(R.id.receiver_text_message);
        }
    }

    private void deleteMessage(String senderRoom, String receiverRoom, String messageId) {
        DatabaseReference senderReference = FirebaseDatabase.getInstance().getReference()
                .child("chats").child(senderRoom).child("message").child(messageId);

        DatabaseReference receiverReference = FirebaseDatabase.getInstance().getReference()
                .child("chats").child(receiverRoom).child("message").child(messageId);

        senderReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                displayToast("Message Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayToast("Failed To delete");
            }
        });

        receiverReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                displayToast("Message Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayToast("Failed To delete");
            }
        });

    }

    private void displayToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
