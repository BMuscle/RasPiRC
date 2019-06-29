#include "RadioContlol.h"

#define RCPORT 55555
//左
#define MOTER1UP 23
#define MOTER1DOWN 24
//右
#define MOTER2UP 20
#define MOTER2DOWN 21

int pi;
int state;

int getState(void);
void setState(int s);
void ALLOFF(void);

void RasPi_initialize(){
	state = 0;
	pi = pigpio_start(NULL,NULL);
	//Pin init
	set_mode(pi,MOTER1DOWN,PI_OUTPUT);
	set_mode(pi,MOTER1UP,PI_OUTPUT);
	set_mode(pi,MOTER2UP,PI_OUTPUT);
	set_mode(pi,MOTER2DOWN,PI_OUTPUT);
	//Pin ALL OFF
	ALLOFF();

	
}
void ALLOFF(void){
	//Pin ALL OFF
	gpio_write(pi,MOTER1UP,PI_OFF);
	gpio_write(pi,MOTER1DOWN,PI_OFF);
	gpio_write(pi,MOTER2UP,PI_OFF);
	gpio_write(pi,MOTER2DOWN,PI_OFF);
}

/*--------------------------------------
	関数名：RCThread
	機　能：ラジコンのメインスレッド 実際に操作をする　サーバーが書き込むbufへアクセス
	引　数：
	戻り値：
	* 
	* 	　 10 停止処理
	* 　　 1　前進
	*	  2  後進
	*     3  左前回転
	*     4  左後回転
	*     5  右前回転
	* 　　 6  右後回転

---------------------------------------*/
void threadRC(void) {
    int flag = 1;
    RasPi_initialize();//ラズパイ初期化
	while (flag) {
		usleep(10);
		int st = getState();
		int val = (st & 0x3);
		//左の処理
		if(st != 0){
			printf("val=%d",val);
			if((st & 0x8) != 0){
				if(val == 1){
					printf("左前回転");
					gpio_write(pi,MOTER1UP,PI_ON);
					gpio_write(pi,MOTER1DOWN,PI_OFF);
				}else if(val == 2){
					printf("左後回転");
					gpio_write(pi,MOTER1UP,PI_OFF);
					gpio_write(pi,MOTER1DOWN,PI_ON);					
				}else{
					gpio_write(pi,MOTER1UP,PI_OFF);
					gpio_write(pi,MOTER1DOWN,PI_OFF);	
				}
			}
			//右の処理
			if((st & 0x4) != 0){
				if(val == 1){
					printf("右前回転");
					gpio_write(pi,MOTER2UP,PI_ON);
					gpio_write(pi,MOTER2DOWN,PI_OFF);
				}else if(val == 2){
					printf("右後回転");
					gpio_write(pi,MOTER2UP,PI_OFF);
					gpio_write(pi,MOTER2DOWN,PI_ON);					
				}else{
					gpio_write(pi,MOTER2UP,PI_OFF);
					gpio_write(pi,MOTER2DOWN,PI_OFF);	
				}
			}
		}
		
	}
    printf("stop\n");
	pigpio_stop(pi);
}

int recvState(int sock){
    char buf = 0;
    int n = 0;
    
    
	if((n = recv(sock,&buf, sizeof(buf),0)) < 1){
		return -1;
	}
	printf("n == %d buf = %c \n",n,buf);
	setState((int)buf);
	printf("state == %d\n",(int)state);
	printf("state == %c\n",state);
	return 1;
}
int getState(void){
	int tmp = state;
    state = 0;
    return tmp;
}
void setState(int s){
    state = s;
}

void threadRCServer(void){
	while(1){
		serverTCP(RCPORT,recvState);
	}
}
