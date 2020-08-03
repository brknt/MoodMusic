package com.example.moodmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.moodmusic.Model.Utility;
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

import java.io.IOException;
import java.util.HashMap;

public class SongPlay extends AppCompatActivity {

    private String SONGID;
    private DatabaseReference mDatabase;
    private DatabaseReference nDatabase;
    private StorageReference storageReference;
    private Toolbar mtoolbar;

    LinearLayout joyLinear1,sadnessLinear1,angerLinear1
            ,excitementLinear1,rebelionLinear1,fearLinear1;

    TextView joyText1,sadnessText1,angerText1
            ,excitementText1,rebelionText1,fearText1;

    int joyCount,sadnessCount,angerCount,excitementCount,rebelionCount,fearCount;


    TextView artistText;
    TextView songTitle;
    TextView durationTime;
    Button playBtn1;
    SeekBar volumeBar;
    SeekBar positionBar;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    MediaPlayer mediaPlayer;
    public int totalTime;
    public  String SONGDURATION2;
    int a;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);


        mtoolbar = (Toolbar) findViewById(R.id.songPlayToolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Song Play");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        artistText = (TextView) findViewById(R.id.artistText);
        songTitle = (TextView) findViewById(R.id.songTitle);
        durationTime = (TextView) findViewById(R.id.durationTime);
        playBtn1 = (Button)findViewById(R.id.playBtn);
        elapsedTimeLabel=(TextView)findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel=(TextView) findViewById(R.id.remainingTimeLabel);

        joyLinear1 = (LinearLayout)findViewById(R.id.joyLinear);
        sadnessLinear1 = (LinearLayout)findViewById(R.id.sadnessLinear);
        angerLinear1 = (LinearLayout)findViewById(R.id.angerLinear);
        excitementLinear1= (LinearLayout)findViewById(R.id.excitementLinear);
        rebelionLinear1 = (LinearLayout)findViewById(R.id.rebelionLinear);
        fearLinear1 = (LinearLayout)findViewById(R.id.fearLinear);

        joyText1 = (TextView)findViewById(R.id.joyText);
        sadnessText1 = (TextView)findViewById(R.id.sadnessText);
        angerText1 = (TextView)findViewById(R.id.angerText);
        excitementText1 = (TextView)findViewById(R.id.excitementText);
        rebelionText1 = (TextView)findViewById(R.id.rebelionText);
        fearText1 = (TextView)findViewById(R.id.fearText);




        SONGID = getIntent().getExtras().get("visit_userID").toString();

        mediaPlayer = new MediaPlayer();

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("songs").child(SONGID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String SONGARTIST = snapshot.child("artist").getValue().toString();
                String SONGNAME =snapshot.child("songTitle").getValue().toString();
                String SONGLINK=snapshot.child("songLink").getValue().toString();
                SONGDURATION2 =snapshot.child("songDuration").getValue().toString();
                String SONGDURATION = Utility.convertDuration(Long.parseLong(SONGDURATION2));






                artistText.setText(SONGARTIST);
                songTitle.setText(SONGNAME);
                durationTime.setText(SONGDURATION);



                try {
                    mediaPlayer.setDataSource(SONGLINK);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        nDatabase = FirebaseDatabase.getInstance().getReference().child("moods").child(SONGID);
        nDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String joy =snapshot.child("joy").getValue().toString();
                String sadness =snapshot.child("sadness").getValue().toString();
                String anger =snapshot.child("anger").getValue().toString();
                String excitement =snapshot.child("excitement").getValue().toString();
                String rebelion =snapshot.child("rebelion").getValue().toString();
                String fear =snapshot.child("fear").getValue().toString();

                joyText1.setText(joy);
                sadnessText1.setText(sadness);
                angerText1.setText(anger);
                excitementText1.setText(excitement);
                rebelionText1.setText(rebelion);
                fearText1.setText(fear);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        playBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying()){
                    //Stopping

                    playBtn1.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }else{
                    playBtn1.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();

                }
            }
        });



        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f,0.5f);

        totalTime = mediaPlayer.getDuration();



        //Position Bar
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Volume Bar
        volumeBar = (SeekBar)findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float volumeNum = progress /100f;
                mediaPlayer.setVolume(volumeNum,volumeNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Thread (Update positionBar & timeLabel
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer !=null){
                    try{
                        Message msg = new Message();
                        msg.what=mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);

                        Thread.sleep(1000);

                    }catch(InterruptedException e){}
                }
            }
        }).start();


        joyLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap <String,Object> updateMusic = new HashMap<>();
                joyCount = joyCount+1;

                updateMusic.put("joy",String.valueOf(joyCount));
                nDatabase.updateChildren(updateMusic);

                joyLinear1.setEnabled(false);
                joyLinear1.setBackgroundResource(R.color.colorPassive);

            }
        });
        sadnessLinear1.setClickable(true);
        sadnessLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap <String,Object> updateMusic = new HashMap<>();
                sadnessCount = sadnessCount+1;

                updateMusic.put("sadness",String.valueOf(sadnessCount));
                nDatabase.updateChildren(updateMusic);

                sadnessLinear1.setEnabled(false);
                sadnessLinear1.setBackgroundResource(R.color.colorPassive);
            }
        });
        angerLinear1.setClickable(true);
        angerLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap <String,Object> updateMusic = new HashMap<>();
                angerCount = angerCount+1;

                updateMusic.put("anger",String.valueOf(angerCount));
                nDatabase.updateChildren(updateMusic);

                angerLinear1.setEnabled(false);
                angerLinear1.setBackgroundResource(R.color.colorPassive);
            }
        });
        excitementLinear1.setClickable(true);
        excitementLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap <String,Object> updateMusic = new HashMap<>();
                excitementCount = excitementCount+1;

                updateMusic.put("excitement",String.valueOf(excitementCount));
                nDatabase.updateChildren(updateMusic);

                excitementLinear1.setEnabled(false);
                excitementLinear1.setBackgroundResource(R.color.colorPassive);
            }
        });
        rebelionLinear1.setClickable(true);
        rebelionLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap <String,Object> updateMusic = new HashMap<>();
                rebelionCount = rebelionCount+1;

                updateMusic.put("rebelion",String.valueOf(rebelionCount));
                nDatabase.updateChildren(updateMusic);

                rebelionLinear1.setEnabled(false);
                rebelionLinear1.setBackgroundResource(R.color.colorPassive);
            }
        });
        fearLinear1.setClickable(true);
        fearLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap <String,Object> updateMusic = new HashMap<>();
                fearCount = fearCount+1;

                updateMusic.put("fear",String.valueOf(fearCount));
                nDatabase.updateChildren(updateMusic);

                fearLinear1.setEnabled(false);
                fearLinear1.setBackgroundResource(R.color.colorPassive);
            }
        });


    }



private Handler handler = new Handler(){

    @Override
    public void handleMessage(@NonNull Message msg) {
        int currentPosition = msg.what;

        positionBar.setProgress(currentPosition);

        String elapsedTime = createTimeLabel(currentPosition);
        elapsedTimeLabel.setText(elapsedTime);

        int drtn =Integer.parseInt(SONGDURATION2);
        String remainingTime =createTimeLabel(drtn-currentPosition);
            Log.i("TAGdura", "handleMessage: "+SONGDURATION2);
        remainingTimeLabel.setText("-" + remainingTime);
    }
};

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time/1000/60;
        int sec = time/1000%60;

        timeLabel = min+ ":";
        if(sec<10) timeLabel +="0";
        timeLabel +=sec;

        return  timeLabel;

    }






}