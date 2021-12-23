using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TKO_CSharp.Models
{
    public class TKO_Algorithm
    {
        public long ExecutionTime { get; private set; }
        public int K { get; private set; }
        public int HUICount { get; private set; }
        public int MinUtility { get; private set; }
        public Dictionary<int, int> ItemsTWU { get; private set; }
        public List<Itemset> KItemsets { get; private set; }
        public TKO_Algorithm()
        {
            ItemsTWU = new Dictionary<int, int>();
            KItemsets = new List<Itemset>();
        }

        public void RunAlgorithm(string input, string output, int k)
        {
            var sw = Stopwatch.StartNew();
            string[] lines;
            K = k;
            try
            {
                lines = File.ReadAllLines(input);
            }
            catch (Exception)
            {
                Console.WriteLine("Something went wrong while reading from file.");
                return;
            }

            foreach (var line in lines)
            {
                if (string.IsNullOrWhiteSpace(line) || line.StartsWith('#') || line.StartsWith('%') || line.StartsWith('@'))
                    continue;

                var splits = line.Split(":");
                var items = splits[0].Split(" ");
                int transactionUtility = int.Parse(splits[1]);
                for (int i = 0; i < items.Length; i++)
                {
                    int item = int.Parse(items[i]);
                    bool containsItem = ItemsTWU.ContainsKey(item);
                    if (containsItem)
                    {
                        ItemsTWU[item] = ItemsTWU[item] + transactionUtility;
                    }
                    else
                    {
                        ItemsTWU.TryAdd(item, transactionUtility);
                    }
                }
            }

            List<UtilityList> listItems = new List<UtilityList>();
            Dictionary<int, UtilityList> itemsUtilittList = new Dictionary<int, UtilityList>();

            foreach (var item in ItemsTWU.Keys)
            {
                UtilityList uList = new UtilityList(item);
                listItems.Add(uList);
                itemsUtilittList.TryAdd(item, uList);
            }

            int CompareUtilitLists(UtilityList u1, UtilityList u2)
            {
                int compare = ItemsTWU[u1.Item] - ItemsTWU[u2.Item];
                if (compare == 0)
                {
                    return u1.Item - u2.Item;
                }
                return compare;
            }

            listItems.Sort(CompareUtilitLists);

            int transactionId = 0;

            foreach (var line in lines)
            {
                if (string.IsNullOrWhiteSpace(line) || line.StartsWith('#') || line.StartsWith('%') || line.StartsWith('@'))
                    continue;

                var splits = line.Split(":");
                var items = splits[0].Split(" ");
                int remainingUtility = int.Parse(splits[1]);
                var utilities = splits[2].Split(" ");

                List<Pair> revisedTransactions = new List<Pair>();

                for (int i = 0; i < items.Length; i++)
                {
                    var pair = new Pair
                    {
                        Item = int.Parse(items[i]),
                        Utility = int.Parse(utilities[i])
                    };
                    revisedTransactions.Add(pair);
                }

                int ComparePairs(Pair p1, Pair p2)
                {
                    int compare = ItemsTWU[p1.Item] - ItemsTWU[p2.Item];

                    return (compare == 0) ? p1.Item - p2.Item : compare;
                }

                revisedTransactions.Sort(ComparePairs);

                foreach (Pair pair in revisedTransactions)
                {
                    remainingUtility = remainingUtility - pair.Utility;
                    UtilityList utilityListOfItem = itemsUtilittList[pair.Item];
                    Element element = new Element(transactionId, pair.Utility, remainingUtility);
                    utilityListOfItem.AddElement(element);
                }
                transactionId++;
            }

            Search(new int[0], null, listItems);

            ExecutionTime = sw.ElapsedMilliseconds / 1000;
        }
        public void WriteResultTofile(string path)
        {
            string temp = "";
            foreach (var itemset in KItemsets)
            {
                for (int i = 0; i < itemset.Items.Length; i++)
                {
                    temp = temp + itemset.Items[i] + " ";
                }

                temp = temp + itemset.Item;
                temp = temp + " #UTIL: " + itemset.Utility;
                temp = temp + "\n";
            }

            File.WriteAllText(path, temp);
            
        }
        public void PrintStats()
        {
            Console.WriteLine("=============  TKO-BASIC - v.2.28 =============");
            Console.WriteLine(" High-utility itemsets count : " + KItemsets.Count);
            Console.WriteLine(" Total time ~ " + ExecutionTime+ " s");
            //System.out.println(" Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
            Console.WriteLine("===================================================");
        }


        #region Private Methods
        private void Search(int[] prefix, UtilityList pUL, List<UtilityList> ULs)
        {
            for (int i = 0; i < ULs.Count; i++)
            {
                UtilityList X = ULs[i];
                if (X.SumIutils >= MinUtility)
                {
                    WriteOut(prefix, X.Item, X.SumIutils);
                }
                var zElementsIutilSummation = X.Elements.Where(e => e.Rutils == 0).Sum(x=>x.Iutils);


                if (X.SumIutils + X.SumRutils >= MinUtility)
                //if (zElementsIutilSummation + X.SumRutils >= MinUtility)
                {
                    // This list will contain the utility lists of pX extensions.
                    List<UtilityList> exULs = new List<UtilityList>();
                    // For each extension of p appearing
                    // after X according to the ascending order
                    for (int j = i + 1; j < ULs.Count; j++)
                    {
                        UtilityList Y = ULs[j];
                        // we construct the extension pXY
                        // and add it to the list of extensions of pX
                        exULs.Add(Construct(pUL, X, Y));
                    }
                    // We create new prefix pX
                    int[] newPrefix = new int[prefix.Length + 1];
                    Array.Copy(prefix, 0, newPrefix, 0, prefix.Length);
                    newPrefix[prefix.Length] = X.Item;

                    // We make a recursive call to discover all itemsets with the
                    // prefix pX
                    Search(newPrefix, X, exULs);
                }

            }
        }
        private void WriteOut(int[] prefix, int item, int utility)
        {
            Itemset itemset = new Itemset(prefix, item, utility);
            KItemsets.Add(itemset);
            if (KItemsets.Count > K)
            {
                if (utility > this.MinUtility)
                {
                    KItemsets = KItemsets.OrderByDescending(x => x.Utility).ToList();
                    Itemset lower;
                    do
                    {

                        lower = KItemsets.LastOrDefault();
                        if (lower == null)
                        {
                            break; // / IMPORTANT
                        }
                        KItemsets.Remove(lower);
                    } while (KItemsets.Count > K);
                    this.MinUtility = KItemsets.LastOrDefault()?.Utility ?? 0;
                    Console.WriteLine(MinUtility);
                }
            }
        }

        private UtilityList Construct(UtilityList P, UtilityList px, UtilityList py)
        {
            UtilityList pxyUL = new UtilityList(py.Item);
            foreach (Element ex in px.Elements)
            {
                Element ey = FindElememtWithTId(py, ex.TId);
                if (ey == null)
                {
                    continue;
                }
                if (P == null)
                {
                    Element eXY = new Element(ex.TId, ex.Iutils + ey.Iutils, ey.Rutils);
                    pxyUL.AddElement(eXY);

                }
                else
                {
                    Element e = FindElememtWithTId(P, ex.TId);
                    if (e != null)
                    {
                        Element eXY = new Element(ex.TId, ex.Iutils + ey.Iutils - e.Iutils, ey.Rutils);
                        pxyUL.AddElement(eXY);
                    }
                }
            }
            return pxyUL;
        }

        private Element FindElememtWithTId(UtilityList ulist, int tid)
        {
            List<Element> list = ulist.Elements;

            int first = 0;
            int last = list.Count - 1;

            while (first <= last)
            {
                int middle = (first + last) / 2;

                if (list[middle].TId < tid)
                {
                    first = middle + 1;
                }
                else if (list[middle].TId > tid)
                {
                    last = middle - 1;
                }
                else
                {
                    return list[middle];
                }
            }
            return null;
        }
        #endregion


    }
}
