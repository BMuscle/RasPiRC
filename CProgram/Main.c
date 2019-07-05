#include "Socket.h"
#include "camera.h"
#include "RadioContlol.h"
#include <pthread.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <err.h>
#include <errno.h>
#include <unistd.h>
#include <pigpiod_if2.h>
//build 
#include "camera.c"
#include "RadioContlol.c"
#include "Socket.c"


pthread_t th1, th2, th3;
int ret1, ret2, ret3;

/*---------------------------------------------------------
 * 	prottype
 * --------------------------------------------------------*/
void threadManager(void);

	
int main(void)
{
	printf("main start\n");
	threadManager();

	
	
	return 0;
}

void threadManager(void){
	
	ret1 = pthread_create(&th1, NULL, (void*)threadRC, NULL);
	ret2 = pthread_create(&th2, NULL, (void*)threadCamera,NULL);
	ret3 = pthread_create(&th3, NULL, (void*)threadRCServer, NULL);
	

	ret1 = pthread_join(th1, NULL);
	ret2 = pthread_join(th2, NULL);
	ret3 = pthread_join(th3, NULL);
}
/*
void GPIOPWM (int pi,int pin,int pulse){
	
		機能 サーボの設定 outputモードにしている必要あり
		引数 pi pigpioのインスタンス
			 PIN GPIO番号を指定
			 pulse パルス幅
	
	printf("\npuluse = %d",pulse);
	set_servo_pulsewidth(pi,pin, pulse);
}

int SG90_angle_PWM(int angle){
	
		SG90用のangle をパルス幅に変更する
		引数 angle -90度から+90度で指定
	
	//500 - 2400 で-90から+90まで
	//安全のため下限600上限2400にする あとでサーボの90度測る
	int down = 600;
	int up = 2400;

	//変換用
	
	angle = (angle)*HENKAN;
	if (angle < down){
		angle = down;
	}
	else if (angle > up){
		angle = up;
	}
	return angle;
}

void Handle(int pi,int angle){
	
		機能 ラズパイのSG90のサーボモータを曲げる outputモードにしている必要あり
		引数 pi pigpioのインスタンス
			 angle 曲げたい角度 angleで 90 - +90で指定

	

	//アングルをPWM変換する SG90用
	int pwm = SG90_angle_PWM(angle);
	//ステアリング用ピンをPWM変更 SG90なので周期50hz
	GPIOPWM(pi,SERVO,pwm);
}
*/


	
	
	
	
