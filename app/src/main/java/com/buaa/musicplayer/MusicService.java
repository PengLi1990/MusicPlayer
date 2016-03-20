package com.buaa.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    //音乐播放器
    MediaPlayer player;
    boolean flag = false;
    Timer timer;
    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建服务时就生成音乐播放器
        player = new MediaPlayer();
        System.out.println("音乐播放器准备好");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁对象
        player.stop();
        player.release();

        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
       return new MusicControler();
    }

    class MusicControler extends Binder implements MusicPlayer{
        @Override
        public void pause() {
            MusicService.this.pause();
        }

        @Override
        public void continuePlay() {
            MusicService.this.continuePlay();
        }

        @Override
        public void play() {
            MusicService.this.play();
        }

        @Override
        public void seekTo(int progress) {
            MusicService.this.seekTo(progress);
        }
    }

    public void play(){

       player.reset();
        try {
           player.setDataSource("sdcard/kugoumusic/1.mp3");
            //player.setDataSource("http://192.168.3.54:8080/music/1.mp3");
            //同步准备（播放本地文件）
           // player.prepare();

            //异步准备（播放在线文件）
            player.prepareAsync();

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                //准备好后播放
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    addTimer();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //暂停播放
    public void pause(){
        player.pause();

    }

    //继续播放
    public void continuePlay(){

        player.start();

    }

    /*
    拖动进度条
     */
    public void seekTo(int progress){
        player.seekTo(progress);
    }

    public void addTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int duration = player.getDuration();
                    int curentposition = player.getCurrentPosition();
                    Message msg = MainActivity.handler.obtainMessage();

                    Bundle data = new Bundle();
                    data.putInt("duration", duration);
                    data.putInt("curentposition", curentposition);

                    msg.setData(data);

                    MainActivity.handler.sendMessage(msg);//这样就将数据返回到主线程了
                }
            }, 5, 500);


        }
    }

}
