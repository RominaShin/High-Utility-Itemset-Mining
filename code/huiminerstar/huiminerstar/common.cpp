#include<stdio.h>
#include<stdlib.h>
#include<memory.h>
#include"common.h"


int int_size = sizeof(int);
int double_size = sizeof(double);


/////////////////////rewrite new and delete//////////////////////////////////////
long current = 0;
long peak = 0;

void* operator new( size_t sz )
{
   sz = sz + 8;
   void* m = malloc( sz );
   if( !m )
   {
      printf("Out of memory!\n");
      exit( 0 );
   }
   long* x = (long *)m;
   *x = sz;
   x = x + 1;

   current += sz;
   if( peak < current )
     peak = current;

   return (void *)x;
}

void operator delete( void* m )
{
   long* x = (long *)m;
   x = x - 1;
   current -= *x;
   free( (void *)x );
}

void* operator new[]( size_t sz )
{
   sz = sz + 8;
   void* m = malloc( sz );
   if( !m )
   {
      printf("Out of memory!\n");
      exit( 0 );
   }
   long* x = (long *)m;
   *x = sz;
   x = x + 1;

   current += sz;
   if( peak < current )
     peak = current;

   return (void *)x;
}

void operator delete[]( void* m )
{
   long* x = (long *)m;
   x = x - 1;
   current -= *x;
   free( (void *)x );
}
//////////////////////////////////////////////////////////////////////////




//////////////////////// Class twosomeTable /////////////////////////////

void twosomeTable::descending_sort(int low, int high)
{
   int i = low;
   int j = high;

   key_twosome = table[i];
   key_twu = key_twosome.twu;

   while( i!=j )
   {
      while( i<j && key_twu>=table[j].twu )
	j--;
      if( i<j )
	table[i++] = table[j];

      while( i<j && table[i].twu>=key_twu )
	i++;
      if( i<j )
	table[j--] = table[i];
   }
    
   table[i] = key_twosome;
   if( low < (i-1) )
     descending_sort( low, i-1 );
   if( (i+1) < high )
     descending_sort( i+1, high );
}

void twosomeTable::ascending_sort(int low, int high)
{
   int i = low;
   int j = high;

   key_twosome = table[i];
   key_twu = key_twosome.twu;

   while( i!=j )
   {
      while( i<j && key_twu<=table[j].twu )
	j--;
      if( i<j )
	table[i++] = table[j];

      while( i<j && table[i].twu<=key_twu )
	i++;
      if( i<j )
	table[j--] = table[i];
   }

   table[i] = key_twosome;
   if( low < (i-1) )
     ascending_sort( low, i-1 );
   if( (i+1) < high )
     ascending_sort( i+1, high );
}

void twosomeTable::sort(int low, int high)
{
   int i = low;
   int j = high;

   key_twosome = table[i];
   key_item = key_twosome.item;

   while( i!=j )
   {
      while( i<j && key_item<=table[j].item )
	j--;
      if( i<j )
	table[i++] = table[j];

      while( i<j && table[i].item<=key_item )
	i++;
      if( i<j )
	table[j--] = table[i];
   }

   table[i] = key_twosome;
   if( low < (i-1) )
     sort( low, i-1 );
   if( (i+1) < high )
     sort( i+1, high );
}
//////////////////////////////////////////////////////////////////////////


