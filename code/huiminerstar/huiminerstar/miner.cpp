#include"common.h"
#include"db.h"
#include"miner.h"


void Miner::mine(Uist &u)
{
   if( u.itemNum==0 ) return;

   int upbound = u.itemNum - 1;
   for(int i=0; i<upbound; i++)
   {
      if( u.ihead[i].utility>=MINUTIL )
	IS.output( u.ihead[i].item, u.ihead[i].utility );

      if( (u.ihead[i].utility+u.ihead[i].remutil) >= MINUTIL )
      {
	 IS.push( u.ihead[i].item );
	 Uist* sub = new Uist( u, i );
	 create_a_uist( u, i, *sub );
	 mine_core( u.list+u.ihead[i].start, *sub );
	 delete sub;
	 IS.pop();
      }
   }

   if( u.ihead[upbound].utility>=MINUTIL )
     IS.output( u.ihead[upbound].item, u.ihead[upbound].utility );

   IS.show();
}


void Miner::create_a_uist( Uist &u, int i, Uist &sub )
{
   int offset = i + 1;
   int tid = 1;
   for(int j=u.ihead[i].start; j<u.ihead[i].end; j++,tid++)
   {
      while( u.list[j].next>=0 )
	u.list[j].next = u.list[u.list[j].next].next;

      int p = u.tindex[ - u.list[j].next ];
      if( p!=j )
      {
	 double temp;
	 double singleuil = u.list[j].util;

	 int item = u.list[p].iid - offset;
	 sub.tindex[tid] = sub.ihead[item].end;
	 ////////start
	 double remaining = 0;
	 while( u.list[p].next!=j )
	 {
	    int nextitem = u.list[u.list[p].next].iid - offset;
	    sub.list[sub.ihead[item].end].next = sub.ihead[nextitem].end;
	    sub.list[sub.ihead[item].end].iid = item;
	    sub.list[sub.ihead[item].end].util = temp = u.list[p].util + singleuil;
	    sub.ihead[item].end++;
	    sub.ihead[item].utility += temp;
	    sub.ihead[item].remutil += remaining;
	    remaining += u.list[p].util;

	    item = nextitem;
	    p = u.list[p].next;
	 }
	 ////////middle
	 sub.list[sub.ihead[item].end].next = - tid;
	 sub.list[sub.ihead[item].end].iid = item;
	 sub.list[sub.ihead[item].end].util = temp = u.list[p].util + singleuil;
	 sub.ihead[item].end++;
	 sub.ihead[item].utility += temp;
	 sub.ihead[item].remutil += remaining;
	 ////////end
      }
   }

   sub.valid_check();
}


void Miner::mine_core( Element* overlap, Uist &u )
{
   if( u.validNum==0 ) return;

   int vitem;
   int upbound = u.validNum - 1;
   for( int i=0; i<upbound; i++ )
   {
      vitem = u.validItem[i];
      if( u.ihead[vitem].utility>=MINUTIL )
	IS.output( u.ihead[vitem].item, u.ihead[vitem].utility );

      if( (u.ihead[vitem].utility+u.ihead[vitem].remutil) >= MINUTIL )
      {
	 IS.push( u.ihead[vitem].item );
	 Uist* sub = new Uist( u, i );
	 create_a_sub_uist( overlap, u, i, *sub );
	 mine_core( u.list+u.ihead[vitem].start, *sub );
	 delete sub;
	 IS.pop();
      }
   }

   vitem = u.validItem[upbound];
   if( u.ihead[vitem].utility>=MINUTIL )
     IS.output( u.ihead[vitem].item, u.ihead[vitem].utility );   
}


void Miner::create_a_sub_uist(Element* overlap, Uist &u, int i, Uist &sub)
{
   int id = 0;
   for(int j=i+1; j<u.validNum; j++)
     mapping[ u.validItem[j] ] = id++;

   overlap -= 1; //offset

   int vitem = u.validItem[i];
   int tid = 1;
   for(int j=u.ihead[vitem].start; j<u.ihead[vitem].end; j++, tid++)
   {
      while( u.list[j].next>=0 )
	u.list[j].next = u.list[u.list[j].next].next;

      int p = u.tindex[ - u.list[j].next ];
      if( p!=j )
      {
	 double temp;
	 double prefixuil = overlap[-u.list[j].next].util;
	 double singleuil = u.list[j].util - prefixuil;
	 
	 int item = mapping[u.list[p].iid];
	 sub.tindex[tid] = sub.ihead[item].end;
	 ////////start
	 double remaining = 0;
	 while( u.list[p].next!=j )
	 {
	    int nextitem = mapping[u.list[u.list[p].next].iid];
	    sub.list[sub.ihead[item].end].next = sub.ihead[nextitem].end;
	    sub.list[sub.ihead[item].end].iid  = item;
	    sub.list[sub.ihead[item].end].util = temp = u.list[p].util + singleuil;
	    sub.ihead[item].end++;
	    sub.ihead[item].utility += temp;
	    sub.ihead[item].remutil += remaining;
	    remaining += ( u.list[p].util - prefixuil );

	    item = nextitem;
	    p = u.list[p].next;
	 }
	 ////////middle
	 sub.list[sub.ihead[item].end].next = - tid;
	 sub.list[sub.ihead[item].end].iid  = item;
	 sub.list[sub.ihead[item].end].util = temp = u.list[p].util + singleuil;
	 sub.ihead[item].end++;
	 sub.ihead[item].utility += temp;
	 sub.ihead[item].remutil += remaining;
	 ////////end
      }
   }

   sub.valid_check();
}
