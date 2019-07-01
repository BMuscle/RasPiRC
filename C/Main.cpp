#include <stdio.h>
#include "Socket.h"
#include <thread>
#include <iostream>

using namespace std;
int main(void) {
	try {
		std::thread server(Server);
		//std::thread client(Client);

		server.join();
		//client.join();
		
	}
	catch (std::exception& ex) {
		std::cerr << ex.what() << std::endl;
	}

	return 0;
}