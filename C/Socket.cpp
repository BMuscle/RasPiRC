#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>
#include <conio.h>
#include <WS2tcpip.h>
#include <math.h>

#pragma once
#pragma warning(disable: 4996)
#pragma comment(lib,"ws2_32.lib")

#define WIDTH 5
#define HIGHT 5
#define COLORBIT 3

#define SIZE 90000

#define UINTSIZE 10

#define PORT 55556
/*
void Server(void) {

	WSADATA wsadata;//設定を保存　構造体
	SOCKET m_sock;//ソケット
	SOCKET sock;//相手とのソケット

	struct sockaddr_in addr;//ソケット通信の設定を書き込む
	struct sockaddr_in client;//ソケット通信相手の設定を書き込む

	FILE* fp;
	FILE* fp2;
	FILE* sendfile;

	char buf[256];
	char c;
	int len;
	char filepath[] = { "1.jpg" };
	

	//ファイルオープン
	if ((fp = fopen(filepath, "rb")) == NULL) {
		printf("fileopen error\n");
		exit(EXIT_FAILURE);//エラー
	}
	fp2 = fopen("tmp.jpg", "wb");
	//ファイル出力テスト

	while (fread(&c, sizeof(c), 1, fp) != NULL) {
		fwrite(&c, sizeof(c), 1, fp2);
	}
	printf("出力官僚");
	fclose(fp);
	fclose(fp2);





	//使用するバージョン指定 WORDは16bit符号なし整数
	if (WSAStartup(MAKEWORD(2, 0), &wsadata) != 0) {
		printf("error:wsastart");
	}
	//IPv4 ソケットのタイプ TCP/IP を設定 失敗するとINVALID_SOCKET を返す
	if ((m_sock = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
		printf("error : %d\n",WSAGetLastError());//最後に起きたエラーコード表示
		return;
	}

	//ソケットの設定
	addr.sin_family = AF_INET;//IPv4指定
	addr.sin_port = htons(PORT);//ポート番号指定 ビッグエンディアン変換
	addr.sin_addr.S_un.S_addr = INADDR_ANY;//IPアドレス指定


	if (bind(m_sock, (struct sockaddr*) & addr, sizeof(addr)) != 0) {
		//TCPクライアントからの接続要求を待てる状態にする
	}
		
	listen(m_sock, 5);



	//TCPクライアントからの接続要求を受け付ける
	while (1) {
		len = sizeof(client);
		sock = accept(m_sock, (struct sockaddr*) & client, &len);
		
		//send(sock, (char*)image->data->r,sizeof(image->data), 0);
		
			//ファイルオープン
		if ((sendfile = fopen(filepath, "rb")) == NULL) {
			printf("fileopen error\n");
			exit(EXIT_FAILURE);//エラー
		}
		//ファイル出力テスト

		while (fread(&c, sizeof(c), 1, sendfile) != NULL) {
			send(sock, &c, sizeof(c), 0);
		}
		fclose(sendfile);
		printf("出力");
		
		closesocket(sock);
	}

	WSACleanup();//消す
}
*/

void Client(void) {
	WSADATA wsaData;
	struct sockaddr_in server;
	SOCKET sock;

	unsigned int wi = 0, hi = 0;
	

	//winsock2の初期化
	WSAStartup(MAKEWORD(2, 0), &wsaData);
	
	//ソケットの作成
	sock = socket(AF_INET, SOCK_STREAM, 0);

	//接続先指定
	server.sin_family = AF_INET;
	server.sin_port = htons(55556);

	server.sin_addr.S_un.S_addr = inet_addr("192.168.0.40");

	printf("サーバーへ接続");
	//サーバーに接続
	while (connect(sock, (struct sockaddr*) & server, sizeof(server)) == -1) {
		printf("erroer %d", WSAGetLastError());
		return;
	}


	while (1) {
		printf("接続 成功　\n");

		char c;
		char stat = 0;

		FILE *readfile;
		//ファイルオープン
		readfile = fopen("tmp3.jpg", "wb");
		//ファイル出力テスト
		printf("ファイル受信開始");
		int e = 1;
		int i = 0;
		while (e) {
			switch (recv(sock, &c, sizeof(c), 0)) {
			case -1:
				printf("erroer %d",WSAGetLastError());
				e = 0;
				break; 
			case 0:
				e = 0;
				break;
			default:
				fwrite(&c, sizeof(c), 1, readfile);
			}
		}
		

		fclose(readfile);
		
		printf("受信完了\n");
		closesocket(sock);
		break;
	}
	
	//winsock2の終了処理
	WSACleanup();


}