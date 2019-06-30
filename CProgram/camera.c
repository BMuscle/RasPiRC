#include "camera.h"
#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>

#define CAMERACOM "raspistill -w 1920 -h 1080 -q 100 -t 20 -o camera.jpg"
#define CAMERAPORT 55556

int sendCamera(int sock);
/*---------------------------------------
	関数名：Camera_Initialize()
	機　能：カメラクラスの初期化
	引　数：
	戻り値：
---------------------------------------- */
void Camera_Shooting(void){
	system(CAMERACOM);
}
/*---------------------------------------
	関数名：Camera_Run()
	機　能：スレッド化するループ処理　
			定期的にカメラ撮影をしメンバ変数へ保持
	引　数：なし
	戻り値：なし
---------------------------------------- */
void threadCamera(void){
	printf("camera");
	serverTCP(CAMERAPORT,sendCamera);
}

int sendCamera(int sock){
		Camera_Shooting();
		FILE *file;//ファイルディスクリプタ
		char imbuf = 0;//ファイル送信用バッファ
		if((file = fopen("camera.jpg", "rb")) == NULL){//ファイルを開く
			perror("not open file");//ファイルが開けなかった場合
			exit(1);
		}
		printf("send file");//送信処理
		fflush(stdout);
		while ((fread(&imbuf, sizeof(imbuf), 1, file)) > 0) {
			send(sock, &imbuf, sizeof(imbuf),0);
		}
		printf("close");
		fflush(stdout);
		fclose(file);//ファイルディスクリプタを閉じる
		return 1;
}
