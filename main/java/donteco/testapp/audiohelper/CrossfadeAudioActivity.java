package donteco.testapp.audiohelper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;


import java.util.ArrayList;

public class CrossfadeAudioActivity extends AppCompatActivity {

    private final ActivityHelper activityHelper = new ActivityHelper(this);
    private ArrayList<Audio> audioList;
    private Audio emptyAudio;

    private ArrayList<ImageButton> imageButtons;

    private static final int standartAlbumIconId =  R.drawable.standart_music_icon;
    private static final int leftChooseMusicIconId = R.id.iv_standart_music_icon_left;
    private static final int rightChooseMusicIconId = R.id.iv_standart_music_icon_right;

    private int curChooseMusicBtnId;
    private static final int leftChooseMusicBtnId = R.id.img_btn_choose_first_song;
    private static final int rightChooseMusicBtnId = R.id.img_btn_choose_second_song;

    private ArrayList <TextView> songNamesTV;
    private ArrayList<ImageView> songImagesIV;

    private SeekBar crossFadeDurationBar;
    private TextView crossFadeDurationTV;

    private ImageButton playPauseBtn;

    private AudioPlayer audioPlayer;

    //Kinda bad
    private int curAudioIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_crossfade_effect);

        activityHelper.getRidOfTopBar();

        backToMainButtonLogic((ImageButton) findViewById(R.id.img_btn_go_back_to_main));

        /*standartAlbumIconId = R.drawable.standart_music_icon;
        leftChooseMusicIconId = R.id.iv_standart_music_icon_left;
        rightChooseMusicIconId = R.id.iv_standart_music_icon_right;*/

        crossFadeDurationBar = findViewById(R.id.crossfade_seekbar);
        crossFadeDurationTV = findViewById(R.id.tv_current_seekbar_state);
        seekBarLogic();


        audioList = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        emptyAudio = new Audio();
        audioList.add(emptyAudio);
        audioList.add(emptyAudio);

        playPauseBtn = findViewById(R.id.img_btn_play_pause);
        playPauseBtn.setImageResource(R.drawable.crossfade_activity_play_icon);
        playPauseBtnLogic();

        ImageButton stopBtn = findViewById(R.id.img_btn_stop);
        stopBtn.setImageResource(R.drawable.crossfade_activity_stop_icon);
        stopAudioBtnLogic(stopBtn);

        songNamesTV = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        songNamesTV.add((TextView) findViewById(R.id.tv_first_song_name));
        songNamesTV.add((TextView) findViewById(R.id.tv_second_song_name));

        songImagesIV = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        songImagesIV.add((ImageView) findViewById(leftChooseMusicIconId));
        songImagesIV.add((ImageView) findViewById(rightChooseMusicIconId));

        imageButtons = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        imageButtons.add( (ImageButton) findViewById(R.id.img_btn_choose_first_song) );
        imageButtons.add( (ImageButton) findViewById(R.id.img_btn_choose_second_song) );
        selectAudioBtnLogic(imageButtons);

    }

    private void backToMainButtonLogic(ImageButton backToMenuBtn)
    {
        backToMenuBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrossfadeAudioActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void seekBarLogic()
    {
        crossFadeDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                i += 2;
                String output = i + " seconds";
                crossFadeDurationTV.setText(output);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    //Change isPressed state!!!!
    private void playPauseBtnLogic()
    {
        playPauseBtn.setOnClickListener(new View.OnClickListener()
        {
            boolean isPressed = false;

            @Override
            public void onClick(View view)
            {
                if(audioList.contains(emptyAudio))
                {
                    Toast.makeText(CrossfadeAudioActivity.this, "You should choose two audio files", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check for errors
                if(isPressed)
                {
                    playPauseBtn.setImageResource(R.drawable.crossfade_activity_play_icon);

                    audioPlayer.pause();

                    for (ImageButton chooseMusicBtn : imageButtons)
                        chooseMusicBtn.setEnabled(true);

                    crossFadeDurationBar.setEnabled(true);

                }
                else
                {
                    playPauseBtn.setImageResource(R.drawable.crossfade_activity_pause_icon);

                    String infoFromSeekBar = crossFadeDurationTV.getText().toString();
                    String[] amountForSeconds = infoFromSeekBar.split(" ");
                    startPlayingMusic(Integer.parseInt(amountForSeconds[0]));

                    for (ImageButton chooseMusicBtn : imageButtons)
                        chooseMusicBtn.setEnabled(false);

                    crossFadeDurationBar.setEnabled(false);
                }

                isPressed = !isPressed;

            }
        });
    }

    private void stopAudioBtnLogic(final ImageButton stopAudioBtn)
    {
        stopAudioBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(audioList.contains(emptyAudio))
                {
                    Toast.makeText(CrossfadeAudioActivity.this, "There is nothing to stop", Toast.LENGTH_SHORT).show();
                    return;
                }

                audioPlayer.stopAllPlayers();
                playPauseBtn.setImageResource(R.drawable.crossfade_activity_play_icon);

                for (ImageButton chooseMusicBtn : imageButtons)
                    chooseMusicBtn.setEnabled(true);

                crossFadeDurationBar.setEnabled(true);
            }
        });
    }

    private void selectAudioBtnLogic(final ArrayList<ImageButton> chooseSongBtns)
    {
        View.OnClickListener chooseSongListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                activityHelper.getPermission();
                Intent intent;

                if(ContextCompat.checkSelfPermission(CrossfadeAudioActivity.this, ConstantsForApp.STORAGE_PERMISSION_REQUEST)
                        == ConstantsForApp.PERMISSION_GRANTED)
                {
                    curChooseMusicBtnId = view.getId();
                    intent = new Intent(ConstantsForApp.ACTION_PICK, ConstantsForApp.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, ConstantsForApp.MY_PERMISSION_REQUEST);
                }
            }
        };

        for (ImageButton imageButton : chooseSongBtns)
            imageButton.setOnClickListener(chooseSongListener);
    }

    @Override
    protected void onStop() {
        if(audioPlayer != null)
            audioPlayer.stopAllPlayers();

        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == ConstantsForApp.MY_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == ConstantsForApp.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, ConstantsForApp.STORAGE_PERMISSION_REQUEST)
                    == ConstantsForApp.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == ConstantsForApp.MY_PERMISSION_REQUEST
                && resultCode == RESULT_OK
                && data != null)
        {
            //the selected audio.
            Uri uriAudio = data.getData();
            try {
                loadAudioFile(uriAudio);

                TextView tv = songNamesTV.get(curAudioIndex);
                ImageView iv = songImagesIV.get(curAudioIndex);
                Bitmap bm;
                Audio audio = audioList.get(curAudioIndex);

                tv.setVisibility(View.VISIBLE);
                tv.setText(String.format("%s %s", audio.getAlbum(), audio.getArtist()));

                if( (bm = audio.getAlbumImage()) != null)
                    iv.setImageBitmap(bm);
                else
                    iv.setImageResource(standartAlbumIconId);

                setChooseMusicBtnImage();

                //curAudioIndex++;
            }
            catch (Exception e){
                Toast.makeText(this, "Something wrong, try one more time!  ", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadAudioFile(Uri audioFileUri)
    {
        String[] dataForCursorLoader = new String[]{MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST};

        CursorLoader loader = new CursorLoader(getApplicationContext(), audioFileUri, dataForCursorLoader, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if(cursor != null)
        {
            cursor.moveToFirst();
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

            if(artist.equals("<unknown>"))
                artist = "";

            if(album.equals("<unknown>"))
                album = "";

            Audio newAudio = new Audio(data, title, album, artist, audioFileUri);
            addAudioToTheList(newAudio);

            cursor.close();
        }
    }

    private void addAudioToTheList(Audio newAudio)
    {
        switch (curChooseMusicBtnId)
        {
            case leftChooseMusicBtnId: {
                curAudioIndex = ConstantsForApp.STARTING_POSITION;
                break;
            }
            case rightChooseMusicBtnId:{
                curAudioIndex = ConstantsForApp.STARTING_POSITION + 1;
                break;
            }
        }
        // Save to audioList
        audioList.set(curAudioIndex, newAudio);
    }

    private void startPlayingMusic(int crossfadeDuration)
    {
        if(audioPlayer == null)
            audioPlayer = new AudioPlayer(CrossfadeAudioActivity.this);

        audioPlayer.setCrossFadeDurationMS(crossfadeDuration);
        audioPlayer.setSongList(audioList);
        audioPlayer.setCurSongPlayingIndex(ConstantsForApp.STARTING_POSITION);
        audioPlayer.play(audioPlayer.getUriOfNextSong());
    }

    private void setChooseMusicBtnImage()
    {
        ImageButton imageButton;

        for (int i = 0; i < imageButtons.size(); i++)
        {
            imageButton = imageButtons.get(i);

            if(imageButton.getId() == curChooseMusicBtnId)
            {
                imageButton.setImageResource(R.drawable.checkmark_music_icon);
                break;
            }
        }
    }
}
