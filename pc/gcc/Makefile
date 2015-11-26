CC=/usr/bin/g++-4.8
CFLAGS=-c -Wall
LDFLAGS=

SRC_PATH=../src/
SOURCES=$(SRC_PATH)main.cpp $(SRC_PATH)bluetooth.cpp $(SRC_PATH)Logger.cpp

INCLUDES=-I$(SRC_PATH)

LIBS=bluetooth 

OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=ms_locker

all: clean $(SOURCES) $(EXECUTABLE)
    
$(EXECUTABLE): $(OBJECTS) 
		$(CC) $(LDFLAGS) $(INCLUDES) $(OBJECTS) -l$(LIBS) -o $@ 

.cpp.o:
		$(CC) $(CFLAGS) $(INCLUDES) $< -o $@

clean: 
		rm -f $(SRC_PATH)*.o $(EXECUTABLE)
