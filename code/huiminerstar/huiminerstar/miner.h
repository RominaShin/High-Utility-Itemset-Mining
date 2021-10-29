#ifndef _miner
#define _miner


#include"common.h"
#include"db.h"


class Miner
{
public:
  Miner(double minutil, int len, char* filename)
    :  MINUTIL(minutil), IS(len, filename)
  {  mapping = new int[len];  }

  ~Miner(void)
   {  delete []mapping;  }

  void mine(Uist &u);

private:
  double MINUTIL;
  itemSet IS;
  int* mapping;

  void create_a_uist( Uist &u, int i, Uist &sub );
  void mine_core( Element* overlap, Uist &u );
  void create_a_sub_uist( Element* overlap, Uist &u, int i, Uist &sub );
};


#endif
