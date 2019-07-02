#include "camera.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>

#define CAMERACOM "raspistill -w 640 -h 480 -q 50 -t 1 -o camera.jpg"
#define CAMERAPORT 55556

#define BUFSIZE 4096

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
		char buf[BUFSIZE];
		if((file = fopen("camera.jpg", "rb")) == NULL){//ファイルを開く
			perror("not open file");//ファイルが開けなかった場合
			exit(1);
		}
		memset(buf,0,sizeof(buf));
		while(fread(buf,sizeof(buf[0]), sizeof(buf),file) > 0){
			send(sock,buf,sizeof(buf),0);
		}
		fclose(file);//ファイルディスクリプタを閉じる
		return 1;
}
