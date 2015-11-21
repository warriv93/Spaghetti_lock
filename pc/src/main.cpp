//
//  main.cpp
//  locker
//
//  Created by Tobias Müller on 10/11/15.
//  Copyright (c) 2015 Tobi. All rights reserved.
//

#include <iostream>
#include "Logger.h"

#include <sys/socket.h>
#ifdef __APPLE__
#include <IOBluetooth/IOBluetooth.h>
#else
#include <bluetooth/bluetooth.h>
#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>
#include <bluetooth/l2cap.h>
#endif

int main(int argc, const char * argv[]) {
  
  Logger *log = Logger::getLogger();
  
  log->Info("Spaghetti Lock is starting...");
  
  
  struct sockaddr_rc loc_addr = { 0 }, rem_addr = { 0 };
  char buf[1024] = { 0 };
  int s, client, bytes_read;
  socklen_t opt = sizeof(rem_addr);
  
  // allocate socket
  s = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
  if (s < 0) {
    log->Error("Can't create socket");
    exit(-1);
  }
  
  // bind socket to port 1 of the first available
  // local bluetooth adapter
  loc_addr.rc_family = AF_BLUETOOTH;
  loc_addr.rc_bdaddr = *BDADDR_ANY;
  loc_addr.rc_channel = (uint8_t) 1;
  bind(s, (struct sockaddr *)&loc_addr, sizeof(loc_addr));
  
  // put socket into listening mode
  listen(s, 1);
  
  // accept one connection
  client = accept(s, (struct sockaddr *)&rem_addr, &opt);
  
  ba2str( &rem_addr.rc_bdaddr, buf );
  fprintf(stderr, "accepted connection from %s\n", buf);
  memset(buf, 0, sizeof(buf));
  
  // read data from the client
  bytes_read = read(client, buf, sizeof(buf));
  if( bytes_read > 0 ) {
    printf("received [%s]\n", buf);
  }
  
  // close connection
  close(client);
  close(s);
  return 0;
  
  
  return 0;
}
