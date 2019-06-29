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

	WSADATA wsadata;//�ݒ��ۑ��@�\����
	SOCKET m_sock;//�\�P�b�g
	SOCKET sock;//����Ƃ̃\�P�b�g

	struct sockaddr_in addr;//�\�P�b�g�ʐM�̐ݒ����������
	struct sockaddr_in client;//�\�P�b�g�ʐM����̐ݒ����������

	FILE* fp;
	FILE* fp2;
	FILE* sendfile;

	char buf[256];
	char c;
	int len;
	char filepath[] = { "1.jpg" };
	

	//�t�@�C���I�[�v��
	if ((fp = fopen(filepath, "rb")) == NULL) {
		printf("fileopen error\n");
		exit(EXIT_FAILURE);//�G���[
	}
	fp2 = fopen("tmp.jpg", "wb");
	//�t�@�C���o�̓e�X�g

	while (fread(&c, sizeof(c), 1, fp) != NULL) {
		fwrite(&c, sizeof(c), 1, fp2);
	}
	printf("�o�͊���");
	fclose(fp);
	fclose(fp2);





	//�g�p����o�[�W�����w�� WORD��16bit�����Ȃ�����
	if (WSAStartup(MAKEWORD(2, 0), &wsadata) != 0) {
		printf("error:wsastart");
	}
	//IPv4 �\�P�b�g�̃^�C�v TCP/IP ��ݒ� ���s�����INVALID_SOCKET ��Ԃ�
	if ((m_sock = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
		printf("error : %d\n",WSAGetLastError());//�Ō�ɋN�����G���[�R�[�h�\��
		return;
	}

	//�\�P�b�g�̐ݒ�
	addr.sin_family = AF_INET;//IPv4�w��
	addr.sin_port = htons(PORT);//�|�[�g�ԍ��w�� �r�b�O�G���f�B�A���ϊ�
	addr.sin_addr.S_un.S_addr = INADDR_ANY;//IP�A�h���X�w��


	if (bind(m_sock, (struct sockaddr*) & addr, sizeof(addr)) != 0) {
		//TCP�N���C�A���g����̐ڑ��v����҂Ă��Ԃɂ���
	}
		
	listen(m_sock, 5);



	//TCP�N���C�A���g����̐ڑ��v�����󂯕t����
	while (1) {
		len = sizeof(client);
		sock = accept(m_sock, (struct sockaddr*) & client, &len);
		
		//send(sock, (char*)image->data->r,sizeof(image->data), 0);
		
			//�t�@�C���I�[�v��
		if ((sendfile = fopen(filepath, "rb")) == NULL) {
			printf("fileopen error\n");
			exit(EXIT_FAILURE);//�G���[
		}
		//�t�@�C���o�̓e�X�g

		while (fread(&c, sizeof(c), 1, sendfile) != NULL) {
			send(sock, &c, sizeof(c), 0);
		}
		fclose(sendfile);
		printf("�o��");
		
		closesocket(sock);
	}

	WSACleanup();//����
}
*/

void Client(void) {
	WSADATA wsaData;
	struct sockaddr_in server;
	SOCKET sock;

	unsigned int wi = 0, hi = 0;
	

	//winsock2�̏�����
	WSAStartup(MAKEWORD(2, 0), &wsaData);
	
	//�\�P�b�g�̍쐬
	sock = socket(AF_INET, SOCK_STREAM, 0);

	//�ڑ���w��
	server.sin_family = AF_INET;
	server.sin_port = htons(55556);

	server.sin_addr.S_un.S_addr = inet_addr("192.168.0.40");

	printf("�T�[�o�[�֐ڑ�");
	//�T�[�o�[�ɐڑ�
	while (connect(sock, (struct sockaddr*) & server, sizeof(server)) == -1) {
		printf("erroer %d", WSAGetLastError());
		return;
	}


	while (1) {
		printf("�ڑ� �����@\n");

		char c;
		char stat = 0;

		FILE *readfile;
		//�t�@�C���I�[�v��
		readfile = fopen("tmp3.jpg", "wb");
		//�t�@�C���o�̓e�X�g
		printf("�t�@�C����M�J�n");
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
		
		printf("��M����\n");
		closesocket(sock);
		break;
	}
	
	//winsock2�̏I������
	WSACleanup();


}