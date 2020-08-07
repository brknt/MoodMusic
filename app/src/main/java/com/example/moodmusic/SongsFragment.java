package com.example.moodmusic;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.moodmusic.Model.UploadSong;
import com.example.moodmusic.Model.Utility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class SongsFragment extends Fragment {

    private View SongsView;
    private RecyclerView mySongsList;
    private DatabaseReference SongsRef;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    public String musicLink;
    public String musicName;
    public String musicArtist;
    public String duration;



    public SongsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SongsView = inflater.inflate(R.layout.fragment_songs, container, false);

        mySongsList = (RecyclerView) SongsView.findViewById(R.id.songsList);
        mySongsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        SongsRef = FirebaseDatabase.getInstance().getReference().child("songs");


        return SongsView;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<UploadSong>()
                        .setQuery(SongsRef,UploadSong.class)
                        .build();

        FirebaseRecyclerAdapter<UploadSong,UploadSongViewHolder> adapter = new FirebaseRecyclerAdapter<UploadSong, UploadSongViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UploadSongViewHolder holder, final int position, @NonNull UploadSong model) {

                String userId = getRef(position).getKey();
                SongsRef.child(userId).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("songTitle")) {



                            musicLink = dataSnapshot.child("songLink").getValue().toString();
                            musicName = dataSnapshot.child("songTitle").getValue().toString();
                            musicArtist=dataSnapshot.child("artist").getValue().toString();
                            duration = Utility.convertDuration(Long.parseLong(dataSnapshot.child("songDuration").getValue().toString()));

                            holder.music_name.setText(musicName);
                            holder.music_duration.setText(duration);



                        }
                        else{

                            String  musicName = dataSnapshot.child("songTitle").getValue().toString();

                            holder.music_name.setText(musicName);

                        }

                        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String visit_userID = getRef(position).getKey();
                                Intent songplayIntent = new Intent(getActivity(),SongPlay.class);

                                songplayIntent.putExtra("visit_userID",visit_userID);
                                songplayIntent.putExtra("musicLink",musicLink);
                                songplayIntent.putExtra("musicName",musicName);
                                songplayIntent.putExtra("musicArtist",musicArtist);
                                songplayIntent.putExtra("musicDuration",duration);
                                getActivity().startActivity(songplayIntent);


                            }
                        });*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public UploadSongViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_items,parent,false);
                final UploadSongViewHolder viewHolder = new UploadSongViewHolder(view);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position =viewHolder.getAdapterPosition();
                        String visit_userID = getRef(position).getKey();

                        Intent songplayIntent = new Intent(getActivity(),SongPlay.class);

                        songplayIntent.putExtra("visit_userID",visit_userID);

                        getActivity().startActivity(songplayIntent);

                    }
                });
                return  viewHolder;
            }
        };
        mySongsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UploadSongViewHolder extends RecyclerView.ViewHolder{

        TextView music_name;
        TextView music_duration;
        ImageView image_art;
        ImageView play_image;
        public UploadSongViewHolder(@NonNull View itemView) {
            super(itemView);


            music_name = itemView.findViewById(R.id.song_title);
            music_duration = itemView.findViewById(R.id.duration);
            image_art = itemView.findViewById(R.id.music_img);


        }
    }

}