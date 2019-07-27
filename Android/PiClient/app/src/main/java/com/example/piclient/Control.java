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
    //スピード
    private int speed[] = new int[2];

    public Control(int stat, int val, int speed[]){//動作情報を受け取る
        this.stat = stat;
        this.val = val;
        this.speed = speed;
    }

    @Override
    public void run() {
        //通信用データ作成
        stat <<= 2;
        stat |= val;
        stat <<= 2;
        stat |= speed[0];
        stat <<= 2;
        stat |= speed[1];

        //データ送信
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
            /* speed を実装するには
            radioボタンによるギアの作成
            受信側で上手く受取処理するには
            ここでスピードをつける現在の状態を取得して送信する
            区切りも考える　送信しているのはstat
            intは32bit?
            今 state 2bit val 2bit 残り24bit とりあえず3速で　2bit 使用してみる
            speed2bit さらに右に入れた
             */
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
