package com.example.das_proyect1.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.das_proyect1.R;

public class MusicService extends Service {
    private MediaPlayer player=null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Logs","Iniciamos la musica");
        player= MediaPlayer.create(this, R.raw.ella_anda_suelta);
        player.setLooping(true);
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Logs","destroy la musica");
        if(player!=null){
            player.stop();
        }
        super.onDestroy();
    }
}
