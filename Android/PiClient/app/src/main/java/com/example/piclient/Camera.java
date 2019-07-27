package com.example.piclient;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import static android.content.Context.MODE_PRIVATE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class Camera implements Runnable{
    private String HOST = "192.168.0.40";//ラズパイのIP
    //private String HOST = "10.0.2.2";//デバッグ用のローカルＩＰ(localhost)
    private int PORT = 55556;//カメラ接続用のポート
    private BufferedInputStream in;//ソケットのインプットストリーム
    private BufferedOutputStream out;//ファイル出力用

    private final String basepath = "/data/data/com.example.piclient/files/";
    private final String imgname = "Pika.jpg";
    private final int BUFSIZE = 131070;//バッファサイズ

    private boolean netflag;


    public Camera() {
        netflag = true;
    }

    @Override
    public void run() {
        while(netflag){
            System.out.println("データ受信");
            try {
                long startTime = System.currentTimeMillis();//処理時間計算用
                if(sock() == 1) {
                    System.out.println("通信処理時間 = " + (System.currentTimeMillis() - startTime));
                    ImageClass imgclass = new ImageClass(basepath + imgname);
                    imgclass.run();//将来的にスレッド化する予定　わざわざメインアクティビティを呼んでいるのは描画反映を早くするため?
                    System.out.println("トータル処理時間 = " + (System.currentTimeMillis() - startTime));
                    System.out.flush();
                }else{
                    System.out.println("通信終了");
                    stopSocket();
                    MainActivity.getInstance().sendMsg(1);
                    System.out.println("完了");
                }
            }catch(Exception e){
                System.out.println("スレッドエラー" + e);
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
            out = new BufferedOutputStream(MainActivity.getInstance().openFileOutput(imgname, MODE_PRIVATE));//出力用ストリーム作成
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
            return 0;//異常終了
        }finally {

        }
        System.out.println("カメラソケット終了です");
        return 1;//正常終了

    }
    public void stopSocket(){
        netflag = false;
    }

    /*OpenCV*/
    public void OpenCVTest() {
        System.out.println("OpenCVText");
        //画像ロード
        Mat im = Imgcodecs.imread(basepath + imgname );  //入力画像
        Mat pattern = MainActivity.getInstance().getDrawableMat(R.drawable.pika2);   //テンプレート画像
        Mat result = new Mat();
        //テンプレートマッチング
        Imgproc.matchTemplate(im,pattern,result,Imgproc.TM_CCOEFF_NORMED);
        // 検出結果から相関係数がしきい値以下の部分を削除
        Imgproc.threshold(result, result, 0.8, 1.0, Imgproc.THRESH_TOZERO);
        //テンプレート画像の部分をもと画像に赤色の矩形
        for(int i = 0; i < result.rows(); i++){
            for(int j = 0; j < result.cols(); j++){
                if(result.get(i, j)[0] > 0){
                    Imgproc.rectangle(im, new Point(j, i), new Point(j + pattern.cols(), i + pattern.rows()), new Scalar(0, 0, 255));
                }
            }
        }

        //テスト画像
        Imgcodecs.imwrite(basepath+"test.jpg", im);
        System.out.println("完了");
    }
}
class ImageClass extends Thread
{
    private String pathname;
    ImageClass(String pathname){
        this.pathname = pathname;
    }

    public void run(){
        MainActivity.getInstance().updateImage(pathname);
    }
}
