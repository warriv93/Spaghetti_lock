//
//  main.cpp
//  locker
//
//  Created by Tobias Müller on 10/11/15.
//  Copyright (c) 2015 Tobi. All rights reserved.
//

#include <iostream>
#include <stdlib.h>
#include <signal.h>
#include "Logger.h"
#include "bluetooth.h"

static int run = 1;

void sig_handler(int signo) {

  if (signo == SIGINT)
    run = 0;
}

using namespace std;

int main(int argc, const char * argv[]) {

  //signal(SIGINT, sig_handler);
  
  Logger *log = Logger::getLogger();
  log->setLevel(Logger::logLevelDebug);
  
  log->Info("Spaghetti Lock is starting...");
  
  BT_Com *com = new BT_Com(log);
  
  if (com->init() < 0) exit(-1);
  
  BT_Conn *c = com->waitForConnection();
  
  if (c) {
    // loopback
    char buf[1024] = { 0 };
    int len = com->recv(c, buf, sizeof(buf));
    if (len) com->send(c, buf, len);
    com->closeConnection(c);
  }
  
  delete com;
  
  
  return 0;
}
