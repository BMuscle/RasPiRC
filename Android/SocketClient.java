package com.example.piclient;

import java.io.BufferedOutputStream;

public class SocketClient implements Runnable{
    private String HOST = "192.168.0.40";
    private int PORT = 55555;
    //状態の取得変数
    private int stat;

    //データ
    private int val;

    public SocketClient(int stat, int val){
        this.stat = stat;
        this.val = val;
    }
    public SocketClient(int port){
        PORT = port;
    };
    public SocketClient(){};

    public int getPort(){
        return PORT;
    }
    public String getHost(){
        return HOST;
    }

    @Override
    public void run() {
        socket();
    }

    public  void socket(){

        //ソケット通信用の変数です．サーバ側と同じくソケットクラス，バッファへの読み書きクラスです．
        java.net.Socket sc;
        //PrintStream output = null;



        try{
            //ここでサーバへ接続されます
            sc = new java.net.Socket(HOST,PORT);

            //output = new PrintStream(sc.getOutputStream());
            //送信用バッファ作成
           // BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream()) );
            BufferedOutputStream out = new BufferedOutputStream(sc.getOutputStream());

            System.out.println("データ送信");
            //state = 2bitで左１＝左　右１　＝右　 1と3 00 01 10
            //val = 0 1 2 後進　停止　前進

            stat <<= 2;
            stat |= val;
            out.write(stat);
            //溜まっているでーた送信
            out.flush();
            //バッファ閉じる
            out.close();


            //ソケット閉鎖
            sc.close();
            System.out.println("ソケット閉鎖");

        }catch(Exception ex) {
            System.out.print("エラー");
            System.out.println(ex);
        }finally {

        }


        System.out.println("クライアント側終了です");

    }
}
