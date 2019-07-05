package com.example.piclient;

import java.io.BufferedOutputStream;

public class Control implements Runnable{
    private final String HOST = "192.168.0.40";//ラズパイのIP
    //private final String HOST = "10.0.2.2";//デバッグ用のローカルＩＰ(localhost)
    private final int PORT = 55555;//機体制御用ポート

    //状態の取得変数
    private int stat;
    //データ
    private int val;

    public Control(int stat, int val){//動作情報を受け取る
        this.stat = stat;
        this.val = val;
    }

    @Override
    public void run() {
        socket();
    }

    public  void socket(){

        //ソケット通信用の変数です．サーバ側と同じくソケットクラス，バッファへの読み書きクラスです．
        java.net.Socket sock;
        try{
            //ここでサーバへ接続されます
            sock = new java.net.Socket(HOST,PORT);

            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            System.out.println("データ送信");
            //state = 2bitで左１＝左　右１　＝右　 1と3 00 01 10
            //val = 0 1 2 後進　停止　前進

            stat <<= 2;
            stat |= val;
            out.write(stat);
            //溜まっているデータ送信
            out.flush();
            //バッファ閉じる
            out.close();
            //ソケット閉鎖
            sock.close();
            System.out.println("ソケット閉鎖");

        }catch(Exception ex) {
            System.out.print("エラー");
            System.out.println(ex);
        }finally {

        }
        System.out.println("コントローラーソケット終了です");

    }
}
