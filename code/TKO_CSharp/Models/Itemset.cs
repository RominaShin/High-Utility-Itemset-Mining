using System;
using System.Text;

namespace TKO_CSharp.Models
{

    public class Itemset
    {

        public int[] itemset;
        public int item;
        public long utility; // absolute support


        public int[] getItemset()
        {
            return itemset;
        }

        public int getItem()
        {
            return item;
        }


        public Itemset(int[] itemset, int item, long utility)
        {
            this.itemset = itemset;
            this.item = item;
            this.utility = utility;
        }

        public int CompareTo(Itemset o)
        {
            if (o == this)
            {
                return 0;
            }

            long compare = this.utility - o.utility;
            if (compare > 0)
            {
                return 1;
            }

            if (compare < 0)
            {
                return -1;
            }

            return 0;
        }

        public String toString()
        {
            StringBuilder temp = new StringBuilder();
            foreach (var item in itemset)
            {
                temp.Append(item + ",");
            }

            temp.Append(item);
            return temp.ToString();
        }

    }
}
