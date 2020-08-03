package com.example.moodmusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodmusic.Model.UploadSong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadMusicActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar mtoolbar2;
    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorageref;
    StorageTask mUploadsTasx;
    DatabaseReference referenceSongs;

    String songsMood;
    MediaMetadataRetriever metadataRetriever;

    String title1,artist1,durations1;
    TextView title,artist,dataa,duration;
    public String uploadId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);


        mtoolbar2= (Toolbar) findViewById(R.id.uploadToolbar);
        setSupportActionBar(mtoolbar2);
        getSupportActionBar().setTitle("Upload Music");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        textViewImage = findViewById(R.id.textViewSongsFilesSelected);
        progressBar = findViewById(R.id.progressbar);
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        duration = findViewById(R.id.duration);
        dataa = findViewById(R.id.dataa);




        metadataRetriever = new MediaMetadataRetriever();

        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageref = FirebaseStorage.getInstance().getReference().child("songs");
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);



        List<String> moods = new ArrayList<>();

        moods.add("joy");
        moods.add("sadness");
        moods.add("anger");
        moods.add("excitement");
        moods.add("rebellion");
        moods.add("fear");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,moods);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);




    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        songsMood = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this,"Selected:"+songsMood,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openAudioFiles (View v){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==101 && resultCode == RESULT_OK && data.getData() !=null){
            audioUri = data.getData();
            String fileName = getFileName(audioUri);
            textViewImage.setText(fileName);
            metadataRetriever.setDataSource(this,audioUri);


            artist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            dataa.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            duration.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

            artist1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            durations1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        }
    }
    private String getFileName(Uri uri){

        String result = null;
        if(uri.getScheme().equals("content")){

            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()) {

                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }

        }
        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut !=-1){
                result = result.substring(cut +1);
            }
        }
        return result;
    }

    public void uploadFileTofirebase (View v){
        if(textViewImage.equals("No file Selected")){
            Toast.makeText(this,"please selected an image",Toast.LENGTH_SHORT).show();

        }
        else{
            if(mUploadsTasx != null && mUploadsTasx.isInProgress()){
                Toast.makeText(this,"songs uploads in already progress",Toast.LENGTH_SHORT).show();

            }else{
                uploadFiles();

            }
        }
    }



    public void uploadFiles() {

        if(audioUri != null){
            Toast.makeText(this,"uploads please wait!",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = mStorageref.child(System.currentTimeMillis()+"."+getfileextension(audioUri));
            mUploadsTasx = storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UploadSong uploadSong =new UploadSong(songsMood,title1,artist1,durations1,uri.toString());

                            uploadId = referenceSongs.push().getKey();

                            referenceSongs.child(uploadId).setValue(uploadSong);

                            referenceSongs = FirebaseDatabase.getInstance().getReference().child("moods").child(uploadId);
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("joy","0");
                            userMap.put("sadness","0");
                            userMap.put("anger","0");
                            userMap.put("excitement","0");
                            userMap.put("rebelion","0");
                            userMap.put("fear","0");
                            referenceSongs.setValue(userMap);


                            /*referenceSongs.child(uploadId).child("joy").setValue(0);
                            referenceSongs.child(uploadId).child("sadness").setValue(0);
                            referenceSongs.child(uploadId).child("anger").setValue(0);
                            referenceSongs.child(uploadId).child("excitement").setValue(0);
                            referenceSongs.child(uploadId).child("rebelion").setValue(0);
                            referenceSongs.child(uploadId).child("fear").setValue(0);*/




                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }else{
            Toast.makeText(this,"No file Selected yo upload",Toast.LENGTH_SHORT).show();
        }


    }

    private String getfileextension(Uri audioUri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
}