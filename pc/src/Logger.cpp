#include "Logger.h"
#include <mutex>

using namespace std;

Logger* Logger::m_logger = NULL;

Logger::Logger()
{
  m_level = logLevelInfo;
}

Logger* Logger::getLogger(){
	
  if (m_logger == NULL) {
    m_logger = new Logger();
  }

	return m_logger;
}

void Logger::Error(std::exception *e){

  if (m_level >= logLevelError) {
    lock();
    cerr << "ERROR: " + string(e->what()) << endl;
    unlock();
  }
}

void Logger::Error(string msg) {

  if (m_level >= logLevelError) {
    lock();
    cerr << "Error: " << msg << endl;
    unlock();
  }
}

void Logger::Info(string msg){

  if (m_level >= logLevelInfo) {
    lock();
    cout << msg << endl;
    unlock();
  }
}

void Logger::Info(char * msg){

  if (m_level >= logLevelInfo) {
    lock();
    cout << msg << endl;
    unlock();
  }
}

void Logger::Debug(string msg) {

  if (m_level >= logLevelDebug) {
    lock();
    cout << msg << endl;
    unlock();
  }
}

void Logger::setLevel(logLevel_e level) {

  m_level = level;
}

Logger::~Logger()
{
}

void Logger::lock() {
}

void Logger::unlock() {
}
