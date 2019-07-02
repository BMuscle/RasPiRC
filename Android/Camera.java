package com.example.piclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Thread.sleep;

public class Camera implements Runnable{
    private String HOST = "192.168.0.40";
   //private String HOST = "10.0.2.2";
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
                im.setImageBitmap(null);
                im.setImageBitmap(bmp);
                im.invalidate();
            }catch(Exception e){

            }
        }
    }
    private  void sock(){

        //ソケット通信用の変数です．サーバ側と同じくソケットクラス，バッファへの読み書きクラスです．
        java.net.Socket sc2;
        byte[] bi = new byte[4096];
        System.out.println("カメラ受信開始");

        try{
            //ここでサーバへ接続されます
            sc2 = new java.net.Socket(HOST,PORT);
            BufferedInputStream in = new BufferedInputStream(sc2.getInputStream());
            BufferedOutputStream bout = new BufferedOutputStream(MainActivity.getInstance().openFileOutput("img.jpg",MODE_PRIVATE));
            System.out.println("カメラデータ受信");
            int len ;
            int i = 0;
            while((len = in.read(bi,0,1024)) > 0){//i=190464
                    bout.write(bi, 0, len);
                    i += len;
            }
            System.out.println(len);
            System.out.println("i="+i);//とりあえず現時点では送信側が早くにソケット閉じてしまっている

            in.close();
            bout.close();
            //ソケット閉鎖
            sc2.close();
            System.out.println("カメラソケット閉鎖");

        }catch(Exception ex) {
            System.out.print("エラー");
            System.out.println(ex);
        }finally {

        }


        System.out.println("クライアント側終了です");

    }
}
