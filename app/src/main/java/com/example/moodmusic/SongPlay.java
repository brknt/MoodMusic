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

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.moodmusic.Model.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class SongPlay extends AppCompatActivity {

    private String SONGID;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    private Toolbar mtoolbar;

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



        SONGID = getIntent().getExtras().get("visit_userID").toString();

        mediaPlayer = new MediaPlayer();

        mAuth = FirebaseAuth.getInstance();
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


    public void playButton(View view){
        if(!mediaPlayer.isPlaying()){
            //Stopping

            playBtn1.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }else{
            playBtn1.setBackgroundResource(R.drawable.ic_play);
            mediaPlayer.pause();

        }
    }



}