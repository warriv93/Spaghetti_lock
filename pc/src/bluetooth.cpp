#include "bluetooth.h"

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>


#include <unistd.h>
#include <sys/socket.h>

using namespace std;


BT_Com::BT_Com(Logger *log) {

  m_log = log;
  m_sock = -1;
}

int BT_Com::init() {
  
  // allocate socket
  m_sock = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
  if (m_sock < 0) {
    m_log->Error("BT_Com: Can't create socket");
    return (-1);
  }
  
  struct sockaddr_rc loc_addr = { 0 };
  
  // bind socket to port 1 of the first available
  // local bluetooth adapter
  loc_addr.rc_family = AF_BLUETOOTH;
  memset(&loc_addr.rc_bdaddr, 0, sizeof(loc_addr.rc_bdaddr)); // BDADDR_ANY
  loc_addr.rc_channel = (uint8_t) 1;
  
  if (bind(m_sock, (struct sockaddr *)&loc_addr, sizeof(loc_addr)) < 0) {
    m_log->Error("BT_Com: Can't bind to socket");
    return (-1);
  }
  
  m_log->Info("BT_Com: Initalization complete");
  
  return 0;
}

BT_Conn *BT_Com::waitForConnection() {

  struct sockaddr_rc rem_addr = { 0 };
  socklen_t opt = sizeof(rem_addr);

  // put socket into listening mode
  listen(m_sock, 1);
  
  // accept one connection
  int client = accept(m_sock, (struct sockaddr *)&rem_addr, &opt);
  
  if (client < 0) { 
    m_log->Info("BT_Com: couldn't accept connection");
    return NULL;
  }
  
  char buf[50];
  ba2str( &rem_addr.rc_bdaddr, buf);
  m_log->Info("BT_Com: accepted connection from " + string(buf));
  
  BT_Conn *c = new BT_Conn(client);
  
  return c;
}

int BT_Com::recv(BT_Conn *c, char *buf, size_t len) {

  if (c == NULL) return -1;

  // read data from the client
  int bytes_read = read(c->getFd(), buf, len);
  
  if (bytes_read > 0) {
    m_log->Debug("BT_Com: received [" + string(buf) + "] bytes");
  }
  
  return bytes_read;
}
  
int BT_Com::send(BT_Conn *c, char *buf, size_t len) {

  if (c == NULL) return -1;

  int status = write(c->getFd(), buf, len);
  
  if (status < 0) {
    m_log->Error("BT_Com: Couldn't send data of len " + len);
  }
  
  m_log->Debug("BT_Com: sent [" + string(buf) + "] bytes");
  
  return status;
}

void BT_Com::closeConnection(BT_Conn *c) {

  if (c == NULL) return;
  
  m_log->Debug("BT_Com: closing connection");
  
  close(c->getFd());

  delete c;
}


