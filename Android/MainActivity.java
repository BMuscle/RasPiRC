package com.example.piclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ThreadManager tm;
    Camera camera;
    ImageView im;

    private static MainActivity instance = null;
    Thread th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        tm = new ThreadManager(1);

        //リスナの登録
        findViewById(R.id.forwardbt).setOnClickListener(this);
        findViewById(R.id.backwardbt).setOnClickListener(this);
        findViewById(R.id.leftupbt).setOnClickListener(this);
        findViewById(R.id.leftdownbt).setOnClickListener(this);
        findViewById(R.id.rightupbt).setOnClickListener(this);
        findViewById(R.id.rightdownbt).setOnClickListener(this);
        findViewById(R.id.stopbt).setOnClickListener(this);
        //カメラスレッド作成起動
        im = findViewById(R.id.imageView);

        camera = new Camera(im);
        th = new Thread(camera);
        th.start();






    }


    @Override
    public void onClick(View v) {
        if(v != null){
            switch (v.getId()){
                case R.id.forwardbt://前進処理
                    tm.Submit_Thread(new SocketClient(3,1));
                    break;
                case R.id.backwardbt://後退処理
                    tm.Submit_Thread(new SocketClient(3,2));
                    break;
                case R.id.leftupbt://左の履帯前回転
                    tm.Submit_Thread(new SocketClient(2,1));
                    break;
                case R.id.leftdownbt://左の履帯後回転
                    tm.Submit_Thread(new SocketClient(2,2));
                    break;
                case R.id.rightupbt://右の履帯前回転
                    tm.Submit_Thread(new SocketClient(1,1));
                    break;
                case R.id.rightdownbt://右の履帯後回転
                    tm.Submit_Thread(new SocketClient(1,2));
                    break;
                case R.id.stopbt://停止
                    tm.Submit_Thread(new SocketClient(3,0));

                    break;

            }


        }
    }
    public static MainActivity getInstance(){
        return instance;
    }


}