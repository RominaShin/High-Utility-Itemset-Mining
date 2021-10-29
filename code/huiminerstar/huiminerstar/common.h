#ifndef _common
#define _common


#include<stdlib.h>
#include<stdio.h>
#include<memory.h>


void* operator new( size_t sz );
void operator delete( void* m );
void* operator new[]( size_t sz );
void operator delete[]( void* m );


class twosome
{
public:
  int item;
  double twu;
};


class twosomeTable
{
public:
  twosome* table;
  int usedLen;
  //the used length of table, table[ 0<-->(usedLen-1) ]

public:
  inline twosomeTable(void) 
    : table(NULL), usedLen(0) 
  {}

  inline twosomeTable(int len) 
    : usedLen(0)
  {  table = new twosome[len];  }

  inline void setLength(int len)
  {  table = new twosome[len];  } 

  inline ~twosomeTable(void) 
  {  delete []table;  }

  inline void init(void)
  {  usedLen = 0;  }

  inline void append_twosome(int i, double u)
  {  
     table[usedLen].item = i;
     table[usedLen++].twu = u;
  }

  inline void descending_sort(void)
  {
     if( usedLen>=2 )
       descending_sort(0, usedLen-1);
  }
  ////sort in twu-descending order

  inline void ascending_sort(void)
  {
     if( usedLen>=2 )
       ascending_sort(0, usedLen-1);
  }
  ////sort in twu_ascending order

  inline void lexically_sort(void)
  {
     return;
  }
  ////sort in twu_lexicology order

  inline void sort(void)
  {
     if( usedLen>=2 )
       sort(0, usedLen-1);
  }
  ////sort in item_ascending order

private:
  twosome key_twosome;
  double key_twu;
  int key_item;
  ////work for the following functions

  void descending_sort(int low, int high);
  void ascending_sort(int low, int high);
  void sort(int low, int high);
  ////these three functions are called by the above cognominal functions  
};




extern FILE* out_fp;
extern int int_size;

class itemSet
{
public:
  inline itemSet(int len, char* filename)
  {
     items = new int[len];
     top = 0;
     results = new int[len];
     memset( results, 0, len * int_size );
     upbound = len;
     if( filename!=NULL )
     {
        outflag = 1;
	outfile = fopen( filename, "w" );
	if( outfile==NULL )
	{
	   printf("Cannot output to %s\n", filename);
	   exit(0);
	}
     }
     else
       outflag = 0;
  }

  inline void show( void )
  {
     int total = 0;
     for( int i=0; i<upbound; i++ )
       if( results[i] != 0 )
       {
	  printf( "%d\n", results[i] );
	  total += results[i];
       }
     printf("Total number of itemsets : %d\n", total);
  }

  inline ~itemSet(void)
  {  
     delete []items;
     delete []results;
     if( outflag ) fclose( outfile );
  }

  inline void push(int item)
  {  items[top++] = item;  }

  inline void pop(void)
  {  top--;  }

  inline void output(int item, double utility)
  {
     results[top]++;
     if( outflag )
     {  
        for(int i=0; i<top; i++)
	  fprintf( outfile, "%d ", items[i]);
	fprintf( outfile, "%d (%f)\n", item, utility );
     }
  }
    
private:
  int*  items;
  int   top;
  int*  results;
  int   upbound;
  int   outflag;
  FILE* outfile;
};

#endif
