package com.example.piclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.net.*;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Thread.sleep;

public class Camera implements Runnable{
   // private String HOST = "192.168.0.40";
   private String HOST = "10.0.2.2";
    private int PORT = 55556;
    ImageView im;

    public Camera(ImageView imageview) {
        im = imageview;
    }

    @Override
    public void run() {
        while(true){
            System.out.println("データ受信");
            sock();
            try {
                Bitmap bmp = BitmapFactory.decodeFile("/data/data/com.example.piclient/files/img.jpg");
                im.setImageBitmap(bmp);

            }catch(Exception e){

            }
        }
    }
    private  void sock(){

        //ソケット通信用の変数です．サーバ側と同じくソケットクラス，バッファへの読み書きクラスです．
        java.net.Socket sc2;
        byte[] bi = new byte[1024];
        System.out.println("カメラ受信開始");

        try{
            //ここでサーバへ接続されます
            sc2 = new java.net.Socket(HOST,PORT);

            BufferedInputStream in = new BufferedInputStream(sc2.getInputStream());
            BufferedOutputStream bout = new BufferedOutputStream(MainActivity.getInstance().openFileOutput("img.jpg",MODE_PRIVATE));
            System.out.println("データ受信");
            int len = 0;
            while((len = in.read(bi)) > 0){
                    bout.write(bi,0,len);
                System.out.println(len);
            }
            System.out.println(len);

            in.close();
            bout.close();



            //ソケット閉鎖
           sc2.close();
            System.out.println("ソケット閉鎖");

        }catch(Exception ex) {
            System.out.print("エラー");
            System.out.println(ex);
        }finally {

        }


        System.out.println("クライアント側終了です");

    }
}
