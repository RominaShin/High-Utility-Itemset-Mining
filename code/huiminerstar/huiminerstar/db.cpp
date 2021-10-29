#include<stdio.h>
#include<stdlib.h>
#include<memory.h>
#include"db.h"
#include"common.h"


extern int int_size;
extern int double_size;


DB::DB(char* trans_file, char* price_file, double minutil)
  : uist(NULL), ex_util(NULL), item_freq(NULL), item_twu(NULL)
{   
   trans = fopen( trans_file, "rt" );
   if( trans==NULL )
   {
      printf("Transaction file cannot open!\n");
      exit(0);
   }

   price = fopen( price_file, "rt" );
   if( price==NULL )
   {
      printf("Price file cannot open!\n");
      fclose( trans );
      exit(0);
   }

   scan_price_file();
   //ex_util is assigned, item_freq and item_twu are initialized

   cache = new char[64*1024];
   cache_size = 64*1024;
   scan_trans_file( minutil );
   //item_freq, item_twu, and MINUTIL, are assigned

   item_order = -1;
   init_a_Uist();
   create_a_Uist();
   uist->valid_check();
   //variable uist is assigned
   
   fclose( trans );
   fclose( price );

   //////////////////////////////////////////////////////////////
   //tist->show();
   //////////////////////////////////////////////////////////////
}


DB::~DB(void)
{
   delete []ex_util;
   delete []item_freq;
   delete []item_twu;
   delete []cache;

   delete uist;
}


void DB::scan_price_file(void)
{
   fseek( price, 0, SEEK_END );
   long ch_num = ftell( price );
   char* buffer = new char[ ch_num ];
   fseek( price, 0, SEEK_SET );
   fread( buffer, ch_num, 1, price );
   
   item_number = 0;
   int i = 0;
   for( ; i<ch_num; i++ )
     if( buffer[i]=='\n' ) 
       item_number++;

   ex_util = new double[ item_number ];
   int j = 0;
   double temp;

   i = 0;
   while( i<ch_num )
   {
      temp = 0;
      while( buffer[i]>='0' && buffer[i]<='9' )
	temp = temp * 10 + (double)( buffer[i++] - '0' ); //i++ 

      if( buffer[i]=='.' )
      {
	 i++;  //skip '.'
	 double reduce = (double)0.1;
	 while( buffer[i]>='0' && buffer[i]<='9' )
	 {
	    temp += ( (double)(buffer[i++] - '0') ) * reduce ;  //i++
	    reduce *= (double)0.1;
	 }
      }
     
      i++;  //skip '\n'
      ex_util[j++] = temp;  //j++
   }
      
   item_freq = new int[ item_number ];
   memset( item_freq, 0, item_number * int_size );
   item_twu = new double[ item_number ];
   memset( item_twu, 0, item_number * double_size );

   delete []buffer;
}


void DB::amplify_cache(void) //add 64k
{
   int len = cache_size + 64 * 1024;
   char* temp =new char[len];

   memcpy(temp, cache, cache_size);
   delete []cache;
   cache = temp;
   cache_size = len;
}


void DB::scan_trans_file(double percent)
{
   trans_number = 0;
   double total_util = 0;
   int* items = new int[item_number];
   int top;

   long ch_num;   
   do
   {
      int half = cache_size / 2; 
      ch_num = fread( cache, 1, half, trans ); 
      if( ch_num==half && cache[half-1]!='\n' )
      {
	 char c;
	 do
	 {
	    c = (char)fgetc( trans );
	    cache[ch_num++] = c;  //ch_num++

	    if( ch_num==cache_size )
              amplify_cache(); 
	 }while( c!='\n' );	 
      }

      int i = 0;
      int item;
      int quantity;
      double trans_util;

      while( i<ch_num )
      {
	 trans_util = 0;
	 top = 0;

	 while( 1 )
	 {
	    item = quantity = 0;

	    while( cache[i]>='0' && cache[i]<='9' )
	      item = item * 10 + (int)( cache[i++] - '0' );  //i++
	    
	    items[top++] = item;  //top++
	    i++;  //skip ' '

	    while( cache[i]>='0' && cache[i]<='9' )
	      quantity = quantity * 10 + (int)( cache[i++] - '0' );  //i++

	    trans_util += ( ex_util[item] * (double)quantity );
	    i++;  //skip ' '
	    if( cache[i]=='\n' )  break;
	 }
	 i++;     //skip '\n'
	 trans_number++;
	 ////get a transaction
	 
	 int j = 0;
	 for( ; j<top; j++ )
	 {
	    item_freq[ items[j] ] ++;
	    item_twu[ items[j] ] += trans_util;
	 }
	 total_util += trans_util;
      }

   }while( !feof(trans) );

   delete []items;
   MINUTIL = total_util * percent ;
}


void DB::init_a_Uist(void)
{
   twosomeTable t(item_number);

   int freq = 0;
   int i = 0;
   for( ; i<item_number; i++ )
     if( item_twu[i]>=MINUTIL )
     {
        freq += item_freq[i];
        t.append_twosome( i, item_twu[i] );
     }
     else
       item_freq[i] = -1;
   
   uist = new Uist( t.usedLen, trans_number, freq );
   switch( item_order )
   {
       case  -1  :  t.ascending_sort();   break;
       case   0  :  t.lexically_sort();   break;
       case   1  :  t.descending_sort();  break;
   }

   Uist &u = *uist;
   int accumulate_len = 0;
   for(i=0; i<t.usedLen; i++)
   {
      int item = t.table[i].item;
      u.ihead[i].item = item;
      u.ihead[i].start = u.ihead[i].end = accumulate_len;
      u.ihead[i].utility = u.ihead[i].remutil = 0;

      accumulate_len += item_freq[item];
      item_freq[item] = i;
   }
}


void DB::create_a_Uist(void)
{
   Uist &u = *uist;
   twosomeTable t(u.itemNum);
   int tid = 0;
   long ch_num;

   fseek( trans, 0, SEEK_SET );
   do
   {
      int half = cache_size / 2;
      ch_num = fread( cache, 1, half, trans ); 
      if( ch_num==half && cache[half-1]!='\n' )
      {
	 char c;
	 do
	 {
	    c = (char)fgetc( trans );
	    cache[ch_num++] = c;  //ch_num++

	    if( ch_num==cache_size )
              amplify_cache();
	 }while( c!='\n' );	 
      }

      int i = 0;
      int item;
      int quantity;

      while( i<ch_num )
      {
	 t.init();
	 
	 while( 1 )
	 {
	    item = quantity = 0;

	    while( cache[i]>='0' && cache[i]<='9' )
	      item = item * 10 + int( cache[i++] - '0' );  //i++

	    i++;  //skip ' '

	    while( cache[i]>='0' && cache[i]<='9' )
	      quantity = quantity * 10 + int( cache[i++] - '0' );  //i++

	    i++;  //skip ' '

	    if( item_freq[item]>=0 )  // >=0
	      t.append_twosome( item_freq[item], ex_util[item]*(double)quantity );

	    if( cache[i]=='\n' )  break;
	 }
	 i++;     //skip '\n'
	 tid++;   //from 1
	 ////get a transaction

	 if( t.usedLen==0 ) continue;
	 t.sort();
	 int j = t.usedLen - 1;
	 u.tindex[tid] = u.ihead[t.table[j].item].end;
	 ////////start
	 double remaining = 0;
	 for( ; j>0; j-- )
	 {
	    Element &e = *( u.list + u.ihead[t.table[j].item].end );
	    e.next = u.ihead[t.table[j-1].item].end;
	    e.iid = t.table[j].item;
	    e.util = t.table[j].twu;
	    u.ihead[t.table[j].item].end++;	 
	    u.ihead[t.table[j].item].utility += e.util;
	    u.ihead[t.table[j].item].remutil += remaining;
	    remaining += e.util;
	 }
	 ////////middle
	 Element &e = *( u.list + u.ihead[t.table[0].item].end );
	 e.next = - tid;
	 e.iid = t.table[0].item;
	 e.util = t.table[0].twu;
	 u.ihead[t.table[0].item].end++;
	 u.ihead[t.table[0].item].utility += e.util;
	 u.ihead[t.table[0].item].remutil += remaining;
	 ////////end
      }

   }while( !feof(trans) );

}
