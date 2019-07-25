package donteco.testapp.audiohelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import donteco.testapp.audiohelper.player.Audio;
import donteco.testapp.audiohelper.player.AudioPlayer;

public class CrossfadeAudioActivity extends AppCompatActivity {

    private final ActivityHelper activityHelper = new ActivityHelper(this);
    private ArrayList<Audio> audioList;
    private Audio emptyAudio;

    private ArrayList<ImageButton> chooseSongImgBtns;

    private int curChooseMusicBtnId;

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

        activityHelper.fullScreen();

        backToMainButtonLogic((ImageButton) findViewById(R.id.img_btn_go_back_to_main));

        crossFadeDurationBar = findViewById(R.id.crossfade_seekbar);
        crossFadeDurationTV = findViewById(R.id.tv_current_seekbar_state);
        seekBarLogic();


        audioList = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        emptyAudio = new Audio();
        audioList.add(emptyAudio);
        audioList.add(emptyAudio);

        playPauseBtn = findViewById(R.id.img_btn_play_pause);
        playPauseBtn.setImageResource(ConstantsForApp.playMusicIconId);
        playPauseBtnLogic();

        ImageButton stopBtn = findViewById(R.id.img_btn_stop);
        stopBtn.setImageResource(ConstantsForApp.stopMisicIconId);
        stopAudioBtnLogic(stopBtn);

        songNamesTV = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        songNamesTV.add((TextView) findViewById(R.id.tv_first_song_name));
        songNamesTV.add((TextView) findViewById(R.id.tv_second_song_name));

        songImagesIV = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        songImagesIV.add((ImageView) findViewById(ConstantsForApp.leftChooseMusicIconId));
        songImagesIV.add((ImageView) findViewById(ConstantsForApp.rightChooseMusicIconId));

        chooseSongImgBtns = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
        chooseSongImgBtns.add( (ImageButton) findViewById(R.id.img_btn_choose_first_song) );
        chooseSongImgBtns.add( (ImageButton) findViewById(R.id.img_btn_choose_second_song) );
        selectAudioBtnLogic(chooseSongImgBtns);

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

                if(isPressed)
                {
                    playPauseBtn.setImageResource(ConstantsForApp.playMusicIconId);

                    if (audioPlayer != null)
                        audioPlayer.pause();

                    for (ImageButton chooseMusicBtn : chooseSongImgBtns)
                    {
                        chooseMusicBtn.setEnabled(true);
                        chooseMusicBtn.setImageResource(ConstantsForApp.chooseMusicIconPressedId);
                    }

                    crossFadeDurationBar.setEnabled(true);
                    //System.out.println("In pause mediaplayers size " + audioPlayer.getMediaPlayers().size() + " ");
                }
                else
                {
                    playPauseBtn.setImageResource(ConstantsForApp.pauseMisicIconId);

                    String infoFromSeekBar = crossFadeDurationTV.getText().toString();
                    String amountForSeconds = infoFromSeekBar.split(" ")[0];
                    /*if(audioPlayer != null)
                        System.out.println("In play mediaplayers size " + audioPlayer.getMediaPlayers().size() + " ");*/
                    startPlayingMusic(Integer.parseInt(amountForSeconds));

                    for (ImageButton chooseMusicBtn : chooseSongImgBtns)
                    {
                        chooseMusicBtn.setEnabled(false);
                        chooseMusicBtn.setImageResource(ConstantsForApp.chooseMusicIconDisabledId);
                    }

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

                playPauseBtn.setImageResource(ConstantsForApp.playMusicIconId);
                playPauseBtnLogic();

                if (audioPlayer != null)
                    audioPlayer.stopAllPlayers();

                for (ImageButton chooseMusicBtn : chooseSongImgBtns)
                {
                    chooseMusicBtn.setEnabled(true);
                    chooseMusicBtn.setImageResource(ConstantsForApp.chooseMusicIconId);
                }

                for (ImageView imageView : songImagesIV)
                    imageView.setImageResource(ConstantsForApp.standartAlbumIconId);

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

                if(ContextCompat.checkSelfPermission(CrossfadeAudioActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    curChooseMusicBtnId = view.getId();
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
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
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
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
            try
            {
                Uri uriAudio = data.getData();
                loadAudioFile(uriAudio);

                TextView songDescription = songNamesTV.get(curAudioIndex);
                ImageView albumImageStorage = songImagesIV.get(curAudioIndex);
                Bitmap albumImage;
                Audio audio = audioList.get(curAudioIndex);

                songDescription.setVisibility(View.VISIBLE);
                songDescription.setText(String.format("%s %s", audio.getArtist(), audio.getTitle()));

                if( (albumImage = audio.getAlbumImage()) != null)
                    albumImageStorage.setImageBitmap(albumImage);
                else
                    albumImageStorage.setImageResource(ConstantsForApp.standartAlbumIconId);

                setChooseMusicBtnImage();
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

            Audio newAudio = new Audio(title, artist, data, album, audioFileUri);
            addAudioToTheList(newAudio);

            cursor.close();
        }
    }

    private void addAudioToTheList(Audio newAudio)
    {
        switch (curChooseMusicBtnId)
        {
            case ConstantsForApp.leftChooseMusicBtnId: {
                curAudioIndex = ConstantsForApp.STARTING_POSITION;
                break;
            }
            case ConstantsForApp.rightChooseMusicBtnId:{
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
        audioPlayer.play(audioPlayer.getUriOfNextSong());
        /*System.out.println("Start music :" + audioPlayer.nonePlayer() + " size " + audioPlayer.getMediaPlayers().size());
        if(audioPlayer.nonePlayer())
            audioPlayer.play(audioPlayer.getUriOfNextSong());
        else
            audioPlayer.resume();*/
    }

    private void setChooseMusicBtnImage()
    {
        ImageButton imageButton;

        for (int i = 0; i < chooseSongImgBtns.size(); i++)
        {
            imageButton = chooseSongImgBtns.get(i);

            if(imageButton.getId() == curChooseMusicBtnId)
            {
                imageButton.setImageResource(ConstantsForApp.chooseMusicIconPressedId);
                break;
            }
        }
    }
}
