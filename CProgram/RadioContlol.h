#ifndef _RADIOCONTLOL_H_
#define _RADIOCONTLOL_H_

#include "Socket.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <err.h>
#include <errno.h>
#include <unistd.h>
#include <pigpiod_if2.h>
void threadRC(void);
void threadRCServer(void);



#endif
