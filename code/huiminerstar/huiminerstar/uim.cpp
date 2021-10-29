#include<stdio.h>
#include<stdlib.h>
#include<sys/time.h>
#include"db.h"
#include"miner.h"


extern long peak;


int main(int argc, char* argv[])
{
   struct timeval t1, t2;
   long double interval;
   gettimeofday(&t1, NULL);
  
   if( argc!=4 && argc!=5 )
   {
      printf("Usage :\n");
      printf("    upquark  trans_file  price_file  min_util  [output_filename]\n");
      printf("    [     ......upquark  is the name of algorithm......        ]\n");
      exit(0);
   }

   DB db( argv[1], argv[2], (double)(atof(argv[3])) );
   //db.tist->show();

   char* outfp = NULL;
   if( argc == 5 ) outfp = argv[4];
   Miner M( db.MINUTIL, db.uist->itemNum, outfp );

   M.mine( *db.uist );
   
   gettimeofday(&t2, NULL);
   interval = ((t2.tv_sec - t1.tv_sec) * 1000) + ((t2.tv_usec - t1.tv_usec) / 1000);
   interval = interval / 1000;
   printf("Mining time (second) : %Lf\n", interval);

   long double memusage = peak / (long double) (1024 * 1024 );
   printf("Peak memory (MB) : %Lf\n", memusage);
   
   return 0;
}
