package com.mgckaled.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Declaration of Member Variables
    private ImageView imagePlayPause;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;

    private MediaPlayer mediaPlayer;
    private Handler handler;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign variables to layout's View
        imagePlayPause = findViewById(R.id.imagePlayPause);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        playerSeekBar = findViewById(R.id.playerSeekBar);

        // instantiate objects to variables
        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        // Pass max values seekbar --> max divisions
        playerSeekBar.setMax(100);

        // Configure actions on click start and pause button.
        imagePlayPause.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()) {
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
                imagePlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                mediaPlayer.start();
                imagePlayPause.setImageResource(R.drawable.ic_play);
                updateSeekBar();
            }
        });

        prepareMediaPlayer();

        // Chance music position / part by touching the screen
        playerSeekBar.setOnTouchListener((v, event) -> {
            SeekBar seekBar = (SeekBar) v;
            int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
            mediaPlayer.seekTo(playPosition);
            textCurrentTime.setText(milliSecondstoTimer(mediaPlayer.getCurrentPosition()));
            return false;
        });

        // Show how much of music was loaded by seekbar.
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) ->
                playerSeekBar.setSecondaryProgress(percent));

        // When the music is finished, setup some config.
        mediaPlayer.setOnCompletionListener(mp -> {
            playerSeekBar.setProgress(0); // Reset seek bar progress
            imagePlayPause.setImageResource(R.drawable.ic_play); // change pause icon
            textCurrentTime.setText(R.string.duration); // current time changes to '0:00'
            textTotalDuration.setText(R.string.duration); // total time changes to '0:00'
            mediaPlayer.reset(); // start over
            prepareMediaPlayer(); // Call for prepare() method again.
        });

    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource("http://infinityandroid.com/music/good_times.mp3");
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondstoTimer(mediaPlayer.getDuration()));
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Interface Thread -> execute codes while they are active
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondstoTimer(currentDuration));
        }
    };


    // If music is playing, set progress according to current time.
    private void updateSeekBar(){
        if(mediaPlayer.isPlaying()) {
            playerSeekBar.setProgress(
                    (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater,1000);
        }
    }

    // Transformation of milliseconds to default time -> hh/mm/ss
    private String milliSecondstoTimer (long milliSeconds) {
        String timerString = "";
        String secondsString;

        int hours = (int)(milliSeconds / (1000 * 60 * 60));
        int minutes = (int)(milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }

}