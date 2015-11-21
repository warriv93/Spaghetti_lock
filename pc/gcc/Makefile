CC=g++
CFLAGS=-c -Wall
LDFLAGS=

SRC_PATH=../src/
SOURCES=$(SRC_PATH)main.cpp $(SRC_PATH)bluetooth.cpp $(SRC_PATH)Logger.cpp

INCLUDES=$(SRC_PATH)

LIBS=bluetooth

OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=ms_locker

all: $(SOURCES) $(EXECUTABLE)
    
$(EXECUTABLE): $(OBJECTS) 
		$(CC) $(LDFLAGS) -I$(INCLUDES) $(OBJECTS) -l$(LIBS) -o $@ 

.cpp.o:
		$(CC) $(CFLAGS) -I$(INCLUDES) $< -o $@