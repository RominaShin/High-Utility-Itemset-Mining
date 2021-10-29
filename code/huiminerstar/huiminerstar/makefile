flags = -Wall -O3 -Winline -Wconversion


all::	upquark+


upquark+:	uim.o  miner.o  db.o  common.o
		g++  $(flags)  uim.o  miner.o  db.o  common.o  -o  upquark+


uim.o:		uim.cpp  miner.o  db.o  common.o  
		g++  $(flags) -c  uim.cpp


miner.o:	miner.cpp  miner.h  db.o  common.o
		g++  $(flags) -c  miner.cpp


db.o:		db.cpp  db.h  common.o
		g++  $(flags) -c  db.cpp


common.o:	common.cpp  common.h
		g++  $(flags) -c  common.cpp


clean:
		rm  upquark+  *.o  *~