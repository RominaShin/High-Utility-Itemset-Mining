#ifndef _db
#define _db


#include"common.h"


class Element
{
public:
  int    next;
  int    iid; 
  double util;
};
//sizeof(Element) = 4 * sizeof(int)


class Tuple
{
public:
  int     item;
  int     start;
  int     end;
  double  utility;
  double  remutil;
};
//sizeof(Tuple) = 7 * sizeof(int)


class Uist
{
public:
  int      itemNum;
  Tuple*   ihead;
  int*     tindex;
  Element* list;

  int       validNum;
  int*      validItem;
  int*      validLen;
  int       totalLen; //vary in Uist( Uist& u, int i )  

  inline Uist( int inum, int tnum, int listsize ) : itemNum(inum)
  {
     init( inum, tnum, listsize );
  }

  inline void valid_check(void)
  {
     validNum = 0;
     totalLen = 0;
     for(int i=0; i<itemNum; i++)
       if( ihead[i].start!=ihead[i].end )
       {
	  validItem[validNum] = i;
	  validLen[validNum] = ihead[i].end - ihead[i].start;
	  totalLen += validLen[validNum++];  //validNum++
       }
  }

  inline Uist( Uist& u, int i )
  {
     itemNum = u.validNum - i - 1;
     u.totalLen -= u.validLen[i];
     int estimLen = u.validLen[i] * itemNum;
     int minLen = u.totalLen < estimLen ? u.totalLen : estimLen;
     init( itemNum, u.validLen[i], minLen );

     int fixed = u.validLen[i++]; //i++
     ihead[0].item = u.ihead[u.validItem[i++]].item;  //i++
     ihead[0].start = ihead[0].end = 0;
     ihead[0].utility = ihead[0].remutil = 0;
     
     if( u.totalLen < estimLen )
        for(int j=1; j<itemNum; j++, i++)
	{
	   ihead[j].item = u.ihead[u.validItem[i]].item;
	   ihead[j].start = ihead[j].end = ihead[j-1].start + u.validLen[i-1];
	   ihead[j].utility = ihead[j].remutil = 0;
	}
     else
       for(int j=1; j<itemNum; j++, i++)
	{
	   ihead[j].item = u.ihead[u.validItem[i]].item;
	   ihead[j].start = ihead[j].end = ihead[j-1].start + fixed;
	   ihead[j].utility = ihead[j].remutil = 0;
	}
  }

  inline ~Uist(void)
  {
     delete [](int*)ihead;
  }  

private:
  inline void init( int inum, int tnum, int listsize )
  {
     itemNum = inum;
     ihead = (Tuple *)new int[ 8*inum + (tnum+1) + 4*listsize + 2*inum ];
     tindex = (int *)( ihead + inum );
     list = (Element *)( tindex + tnum + 1 );
     validItem = (int *)( list + listsize);
     validLen = validItem + inum;
  }
};


class DB
{
public:
  DB(char* trans_file, char* price_file, double minutil);
  ~DB(void);

  double MINUTIL;
  Uist* uist;

private:
  FILE* trans;
  FILE* price;

  int item_order;   //twu-asc(-1), lexi(0), twu-desc(1)
  int item_number;  //the number of items in db
  int trans_number; //the number of trans in db
  double* ex_util;
  int* item_freq;
  double* item_twu;

  void scan_price_file(void);
  void scan_trans_file(double percent);
  void init_a_Uist(void);
  void create_a_Uist(void);

  char* cache;
  int cache_size;
  void amplify_cache(void);
};

#endif
