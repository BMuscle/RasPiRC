package com.example.piclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static MainActivity instance = null;
    private static Context context;
    private static Handler handler;

    private Message msg;
    private ThreadManager tm;
    private Camera camera;
    private ImageView im;
    private Switch s;
    private Thread camth;
    private Bitmap bmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        instance = this;//getinstans用インスタンス保持
        tm = new ThreadManager(1);//スレッドプール　サイズ１で稼働
        camth = new Thread(camera = new Camera());//カメラスレッド作成

        //リスナの登録
        findViewById(R.id.forwardbt).setOnClickListener(this);
        findViewById(R.id.backwardbt).setOnClickListener(this);
        findViewById(R.id.leftupbt).setOnClickListener(this);
        findViewById(R.id.leftdownbt).setOnClickListener(this);
        findViewById(R.id.rightupbt).setOnClickListener(this);
        findViewById(R.id.rightdownbt).setOnClickListener(this);
        findViewById(R.id.stopbt).setOnClickListener(this);
        s = findViewById(R.id.connectswitch);
        s.setOnCheckedChangeListener(new SwitchListener());
        im = findViewById(R.id.imageView);

        handler = new Handler(){//UIに関する処理が別スレッドから投げられる場合ここへ
            //メッセージ受信
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        break;
                    case 1://オフスイッチ起動
                        offSwitch();
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        if(v != null && camth.isAlive()){
            switch (v.getId()){
                case R.id.forwardbt://前進処理
                    tm.submitThread(new Control(3,1));
                    break;
                case R.id.backwardbt://後退処理
                    tm.submitThread(new Control(3,2));
                    break;
                case R.id.leftupbt://左の履帯前回転
                    tm.submitThread(new Control(2,1));
                    break;
                case R.id.leftdownbt://左の履帯後回転
                    tm.submitThread(new Control(2,2));
                    break;
                case R.id.rightupbt://右の履帯前回転
                    tm.submitThread(new Control(1,1));
                    break;
                case R.id.rightdownbt://右の履帯後回転
                    tm.submitThread(new Control(1,2));
                    break;
                case R.id.stopbt://停止
                    tm.submitThread(new Control(3,0));
                    break;
            }
        }
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public void sendMsg(int what){//ハンドラへメッセージを投げイベントを起こす
        msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    public void updateImage(String pathname) {//画像更新
        bmp = BitmapFactory.decodeFile(pathname);
        //機体的にカメラがまっすぐ無理だったので回転
        im.setImageBitmap(bmp);
        im.invalidate();
    }

    private void offSwitch(){
        s.setChecked(false);
        Toast.makeText(context, "コネクションエラー", Toast.LENGTH_SHORT).show();
    }
    class SwitchListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {//スイッチの状態がオンに変更された時
                camth = new Thread(camera = new Camera());//カメラスレッド作成
                Toast.makeText(context, "コネクション開始", Toast.LENGTH_SHORT).show();
                //カメラスレッド起動
                camth.start();
            }else{//スイッチの状態がオフに変更された時
                if(camera != null) {
                    camera.stopSocket();//スレッドを停止させる
                    Toast.makeText(context, "コネクション切断しました", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

