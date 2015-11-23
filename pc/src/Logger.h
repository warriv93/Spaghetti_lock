#pragma once
#include <string>
#include <iostream>

using namespace std;

class Logger
{
public:
  typedef enum {
    logLevelError = 0,
    logLevelInfo,
    logLevelDebug
  } logLevel_e;

  void Debug(string msg);
	void Info(string msg);
	//void Info(char * msg);
  void Error(string msg);
	void Error(std::exception *e);

	static Logger* getLogger();

  void setLevel(logLevel_e level);

private:
  static Logger *m_logger;

  int m_level;

  Logger();
  ~Logger();
  
  void lock();
  void unlock();
};

