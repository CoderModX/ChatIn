package com.chatin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatin.ChatActivity;
import com.chatin.models.Messages;
import com.chatin.R;
import com.chatin.models.Friends;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */

public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mLastMessage;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        // Inflate the layout for this fragment
        mChatList = mMainView.findViewById(R.id.chatlist_RecyclerView);


        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLastMessage = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query MessageTime = mLastMessage.orderByChild("Time");

        FirebaseRecyclerOptions<Friends> options=new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendsDatabase,Friends.class).build();
        FirebaseRecyclerAdapter<Friends, ChatsFragment.ChatListViewHolder> friendRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, ChatsFragment.ChatListViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ChatsFragment.ChatListViewHolder holder, int position, @NonNull Friends model) {
                Messages mc = new Messages();
                String type = mc.getType();
                final String list_user_id=getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userName = snapshot.child("name").getValue().toString();
                        String userImage = snapshot.child("image").getValue().toString();
                        holder.setName(userName);
                        holder.setUserImage(userImage);
                        holder.mView.findViewById(R.id.UserList_userStatus).setVisibility(View.INVISIBLE);

//                        Query LastMessage = mLastMessage.child(list_user_id).limitToLast(1);

//                        LastMessage.addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                                String data = snapshot.child("message").getValue().toString();
//                                if (!data.isEmpty() || data != null) {
//                                    holder.mView.findViewById(R.id.UserList_userStatus).setVisibility(View.VISIBLE);
//                                    holder.setStatus("Last Message: " + data);
//                                }
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });


                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                chatIntent.putExtra("user_name",userName);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

            }

            @NonNull
            @Override
            public ChatsFragment.ChatListViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersmodel,parent,false);
                return new ChatsFragment.ChatListViewHolder(view);
            }
        };

        friendRecyclerViewAdapter.startListening();
        mChatList.setAdapter(friendRecyclerViewAdapter);
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ChatListViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }
        public void setStatus(String status){
            TextView userStatusView = mView.findViewById(R.id.UserList_userStatus);
            userStatusView.setText(status);
        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.userList_UserName);
            userNameView.setText(name);
        }



        public void setUserImage(final String thumb_img){
            final CircleImageView userImageView = mView.findViewById(R.id.userImage);
            Picasso.get().load(thumb_img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.account_circle).into(userImageView, new Callback() {
                @Override
                public void onSuccess() { }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(thumb_img).placeholder(R.drawable.account_circle).into(userImageView);
                }
            });
        }
    }
}