package com.example.piclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class Camera implements Runnable{
    private String HOST = "192.168.0.40";//ラズパイのIP
    //private String HOST = "10.0.2.2";//デバッグ用のローカルＩＰ(localhost)
    private int PORT = 55556;//カメラ接続用のポート
    private BufferedInputStream in;//ソケットのインプットストリーム
    private BufferedOutputStream out;//ファイル出力用

    private final String basepath = "/data/data/com.example.piclient/files/";
    private final String imgname = "CapImg";
    private final int BUFSIZE = 131070;//バッファサイズ


    public Camera() {

    }

    @Override
    public void run() {
        while(true){
            System.out.println("データ受信");
            try {
                long startTime = System.currentTimeMillis();//処理時間計算用
                if(sock() == 1) {
                    System.out.println("通信処理時間 = " + (System.currentTimeMillis() - startTime));
                    ImageClass imgclass = new ImageClass(basepath + imgname + ".jpg");
                    imgclass.run();//将来的にスレッド化する予定　わざわざメインアクティビティを呼んでいるのは描画反映を早くするため
                    System.out.println("トータル処理時間 = " + (System.currentTimeMillis() - startTime));
                    System.out.flush();
                }else{
                    break;
                }
            }catch(Exception e){

            }
        }
    }
    private  int sock(){

        //ソケット通信用の変数です．サーバ側と同じくソケットクラス，バッファへの読み書きクラスです．
        java.net.Socket sock;
        byte[] bi = new byte[BUFSIZE];
        System.out.println("カメラ受信開始");

        try{
            //ここでサーバへ接続されます
            sock = new java.net.Socket(HOST,PORT);//ソケットの作成
            in = new BufferedInputStream(sock.getInputStream());//受信用ストリーム作成
            out = new BufferedOutputStream(MainActivity.getInstance().openFileOutput(imgname +".jpg", MODE_PRIVATE));//出力用ストリーム作成
            int len;//受信データサイズ
            int i = 0;//受信データトータルサイズ確認用
            while((len = in.read(bi,0,BUFSIZE)) > 0){
                    out.write(bi, 0, len);
                    i += len;
            }
            System.out.println("受信データサイズ="+i);
            //ストリーム閉鎖
            in.close();
            out.close();
            //ソケット閉鎖
            sock.close();
            System.out.println("カメラソケット閉鎖");

        }catch(Exception ex) {
            System.out.print("エラー");
            System.out.println(ex);
            return -1;//異常終了
        }finally {

        }
        System.out.println("カメラソケット終了です");
        return 1;//正常終了

    }
}
class ImageClass extends Thread
{
    private String pathname;
    ImageClass(String pathname){
        this.pathname = pathname;
    }

    public void run(){
        MainActivity.updateImage(pathname);
    }
}
