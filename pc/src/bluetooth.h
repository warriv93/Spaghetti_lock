#pragma once
#include <Logger.h>


using namespace std;

class BT_Conn {
public:
  BT_Conn(int fd) : m_fd(fd) { }
  
  int getFd() { return m_fd; }

private: 
  int m_fd;
};

class BT_Com {
public:

  BT_Com(Logger *log);

  int init();
  
  BT_Conn *waitForConnection();
  
  int recv(BT_Conn *c, char *buf, size_t len);
  
  int send(BT_Conn *, char *buf, size_t len);
  
  void closeConnection(BT_Conn *c);

private:
  
  Logger *m_log;
  int m_sock;

};

