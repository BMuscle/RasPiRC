package com.example.piclient;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;


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

    private RadioGroup rgpL, rgpR;

    private int stat,val,speed[] = new int[2];//speed 0=L 1=R


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        instance = this;//getinstans用インスタンス保持
        tm = new ThreadManager(1);//スレッドプール　サイズ１で稼働
        camth = new Thread(camera = new Camera());//カメラスレッド作成

        stat = 0;val = 0;speed[0] = speed[1] = 1;//初期化



        /*OpenCVテスト*/
        if(!OpenCVLoader.initDebug()){
            Log.i("OpenCV","Failed");
        }else{
            Log.i("OpenCV","successflly built !");
        }
        //


        //リスナの登録
        findViewById(R.id.forwardbt).setOnClickListener(this);
        findViewById(R.id.backwardbt).setOnClickListener(this);
        findViewById(R.id.leftupbt).setOnClickListener(this);
        findViewById(R.id.leftdownbt).setOnClickListener(this);
        findViewById(R.id.rightupbt).setOnClickListener(this);
        findViewById(R.id.rightdownbt).setOnClickListener(this);
        findViewById(R.id.stopbt).setOnClickListener(this);

        rgpL = findViewById(R.id.rgpL);
        rgpL.setOnCheckedChangeListener(new RadioListenerLeft());
        rgpR = findViewById(R.id.rgpR);
        rgpR.setOnCheckedChangeListener(new RadioListenerRight());

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
    protected void onStop(){
        super.onStop();
        tm.shutdownThreadPool();
        camera.stopSocket();
        while(true) {
            try {
                camth.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onClick(View v) {
        if(v != null && camth.isAlive()){
            System.out.println("radio ID = "+getRadioState());
            switch (v.getId()){
                case R.id.forwardbt://前進処理
                    stat = 3; val = 1;
                    break;
                case R.id.backwardbt://後退処理
                    stat = 3; val = 2;
                    break;
                case R.id.leftupbt://左の履帯前回転
                    stat = 2; val = 1;
                    break;
                case R.id.leftdownbt://左の履帯後回転
                    stat = 2; val = 2;
                    break;
                case R.id.rightupbt://右の履帯前回転
                    stat = 1; val = 1;
                    break;
                case R.id.rightdownbt://右の履帯後回転
                    stat = 1; val = 2;
                    break;
                case R.id.stopbt://停止
                    stat = 3; val = 0;
                    break;
            }
            sendContlol();
        }
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public int getRadioState(){
        return  rgpL.getCheckedRadioButtonId();
    }

    public void sendMsg(int what){//ハンドラへメッセージを投げイベントを起こす
        msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    public void updateImage(String pathname) {//画像更新
        bmp = BitmapFactory.decodeFile(pathname);
        im.setImageBitmap(bmp);
        im.invalidate();
    }

    private void offSwitch(){
        s.setChecked(false);
        Toast.makeText(context, "コネクションエラー", Toast.LENGTH_SHORT).show();
    }

    private void sendContlol(){
        tm.submitThread(new Control(stat,val, speed));
    }

    public Mat getDrawableMat(int drawableResId){
        Mat tmp = null;
        try {
            tmp = Utils.loadResource(context, drawableResId,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
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
    class RadioListenerLeft implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.radio1L:
                    speed[0] = 1;
                    break;
                case R.id.radio2L:
                    speed[0] = 2;
                    break;
                case R.id.radio3L:
                    speed[0] = 3;
                    break;
            }
            if(camth.isAlive()) {
                sendContlol();
            }
        }
    }
    class RadioListenerRight implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            //OpenCV Test
            camera.OpenCVTest();
            switch (checkedId){
                case R.id.radio1R:
                    speed[1] = 1;
                    break;
                case R.id.radio2R:
                    speed[1] = 2;
                    break;
                case R.id.radio3R:
                    speed[1] = 3;
                    break;
            }
            if(camth.isAlive()) {
                sendContlol();
            }
        }
    }
}

