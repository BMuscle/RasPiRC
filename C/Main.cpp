#include <stdio.h>
#include "Socket.h"
#include <thread>
#include <iostream>

using namespace std;
int main(void) {
	try {
		std::thread client(Client);

		client.join();
		
	}
	catch (std::exception& ex) {
		std::cerr << ex.what() << std::endl;
	}

	return 0;
}