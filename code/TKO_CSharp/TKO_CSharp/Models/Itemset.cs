using System;

namespace TKO_CSharp.Models
{
    public class Itemset : IComparable
    {
        public int[] Items { get; private set; }
        public int Item { get; private set; }
        public int Utility { get; private set; }

		public Itemset(int[] items, int item, int utility)
		{
			Items = items;
			Item = item;
			Utility = utility;
		}

		public override String ToString()
		{
			string temp = "";
            foreach (var item in Items)
            {
				temp = temp + item + ",";
            }

			return temp;
		}
		public int CompareTo(object obj)
        {
			Itemset itemset = (Itemset)obj;
			if (itemset == this)
			{
				return 0;
			}
			long compare = this.Utility - itemset.Utility;
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
    }
}
