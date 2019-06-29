#include <stdio.h>//fprintf(),printf(),perror()
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>//struct sockaddr_in, struct sockaddr,
#include <netinet/in.h>
#include <errno.h>//error
#include <unistd.h>
#include "Socket.h"
#include "camera.h"


//サーバー関係のマクロ
//#define IP 192.168.
#define IP "192.168.0.40"
#define PORT 55555
//接続要求の受付数
#define MAXPENDING 5
#define BUFFERSIZE 1024
/*-------------------------------------------
 * 	メモ　将来的には数値だけをやり取りする
 * 
 * 
 * 
 * -----------------------------------------*/

int par = 0;
int state = 0;




/*--------------------
	scokaddr_inの中身
	u_char sin_family;(アドレスファミリ. IPv4の指定はAF_INET)
	u_short sin_port; (ポート番号) 変換する関数があるので直接入れない
	struct in_addr sin_addr;(IPアドレス)
----------------------*/


void Server(void) 
{
	 printf("サーバー開始");
	 int sockmain;
	 //int n;
	 struct sockaddr_in addr;
	 struct sockaddr_in client;
	 socklen_t len;
	 int sock;
	 
	 //char buf[1024];
	 char imbuf;

	 if((sockmain = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		 perror("socket");
		 return;
	 }
	 

	 addr.sin_family = AF_INET;
	 addr.sin_port = htons(PORT);
	 addr.sin_addr.s_addr = INADDR_ANY;
	 
	int yes = 1;
	if (setsockopt(sockmain, SOL_SOCKET, SO_REUSEADDR, (const char *)&yes, sizeof(yes)) < 0){
		perror("ERROR on setsockopt");
		return;
	}
    
	 if(( bind(sockmain, (struct sockaddr *)&addr, sizeof(addr))) == -1){
		 perror("bind");
		 return;
	 }

	 listen(sockmain, MAXPENDING);
	 printf("socket\nwait...");
	 while (1) {
	   
	   len = sizeof(client);
	   sock = accept(sockmain, (struct sockaddr *)&client, &len);//クライアント接続待ち
	   printf("on\n");
		/*
	   memset(buf,0,sizeof(buf));//受信用バッファ0クリア
	   n = read(sock,buf, sizeof(buf));
	   printf("n == %d buf = %s \n",n,buf);
	   state = BufferState(buf);
	   printf("state == %d  ",state);
		*/
	  // if(state == 0){//画像の送信要求がある場合
		FILE *file;
		imbuf = 0;
		if((file = fopen("camera.jpg", "rb")) == NULL){//ファイルを開く
		   perror("not open file");//ファイルが開けなかった場合
		   exit(1);
		}
		printf("send file");
		while ((fread(&imbuf, sizeof(imbuf), 1, file)) > 0) {
			send(sock, &imbuf, sizeof(imbuf),0);
		}
		printf("close");
	   //データ送信
	   fclose(file);//ファイルディスクリプタを閉じる

	  // }
	   close(sock);
	   break;
	 }
	 printf("サーバー終了");
	 close(sockmain);

}

void serverTCP(int port,int(*method)(int sock)){
	 printf("サーバー開始");
	 int sockserver, sockclient;
	 int endflag = 1;
	 struct sockaddr_in addr;
	 struct sockaddr_in client;
	 socklen_t len;

	 if((sockserver = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		 perror("socket");
		 return;
	 }

	 addr.sin_family = AF_INET;
	 addr.sin_port = htons(port);
	 addr.sin_addr.s_addr = INADDR_ANY;
	 
	int yes = 1;
	if (setsockopt(sockserver, SOL_SOCKET, SO_REUSEADDR, (const char *)&yes, sizeof(yes)) < 0){
		perror("ERROR on setsockopt");
		return;
	}
    
	 if((bind(sockserver, (struct sockaddr *)&addr, sizeof(addr))) == -1){
		 perror("bind");
		 return;
	 }

	 listen(sockserver, MAXPENDING);
	 printf("socket\nwait...");
	 while (endflag) {
		len = sizeof(client);
		sockclient = accept(sockserver, (struct sockaddr *)&client, &len);//クライアント接続待ち
		if(method(sockclient) == -1){
			endflag = 0;
		}
		close(sockclient);
	 }
	 printf("サーバー終了");
	 close(sockserver);

}

/*--------------------------------------
	関数名：BufferState
	機　能：Bufを読み取りステータスを返す 
	引　数：char *buf 文字列の先頭アドレス
	戻り値：

---------------------------------------*/
int BufferState(char *buf) {
	printf("atoi=%d",atoi(buf));
	int i = atoi(buf);
	return i;//	左2bit１の場合左右　右２bit　前進後退　012					
}
int Get_State(){
	int temp = state;
	state = 0;
	return temp;
}
int Get_Par(){
	return par;
}
