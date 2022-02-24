using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;

namespace TKO_CSharp.Models
{
    public class AlgoTkoBasic
    {

        long totalTime = 0;
        int k = 0;
        long minutility = 0;
        List<Itemset> kItemsets;
        Dictionary<int, int> mapItemToTWU = new Dictionary<int, int>();
        class Pair
        {
            public int Item = 0;
            public int Utility = 0;
        }
        public void RunTKOBaseAlgorithm(String input, String output, int k)
        {

            Stopwatch sw = new Stopwatch();
            sw.Start();
            this.minutility = 1;
            this.k = k;
            this.kItemsets = new List<Itemset>();
            string[] lines;
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
                    bool containsItem = mapItemToTWU.ContainsKey(item);
                    if (containsItem)
                        mapItemToTWU[item] = mapItemToTWU[item] + transactionUtility;
                    else
                        mapItemToTWU.TryAdd(item, transactionUtility);
                }
            }
            List<UtilityList> listItems = new List<UtilityList>();
            Dictionary<int, UtilityList> mapItemToUtilityList = new Dictionary<int, UtilityList>();
            foreach (int item in mapItemToTWU.Keys)
            {
                UtilityList uList = new UtilityList(item);
                listItems.Add(uList);
                mapItemToUtilityList.Add(item, uList);
            }

            listItems.Sort((o1, o2) =>
            {
                int compare = mapItemToTWU[o1.item] - mapItemToTWU[o2.item];
                if (compare == 0)
                {
                    return o1.item - o2.item;
                }
                return compare;
            });
            int tid = 0;
            foreach (var line in lines)
            {
                var split = line.Split(":");
                var items = split[0].Split(" ");
                var utilityValues = split[2].Split(" ");
                List<Pair> revisedTransaction = new List<Pair>();
                for (int i = 0; i < items.Length; i++)
                {
                    Pair pair = new Pair();
                    pair.Item = int.Parse(items[i]);
                    pair.Utility = int.Parse(utilityValues[i]);
                    revisedTransaction.Add(pair);
                }
                revisedTransaction.Sort((o1, o2) =>
                {
                    int compare = mapItemToTWU[o1.Item] - mapItemToTWU[o2.Item];
                    return (compare == 0) ? o1.Item - o2.Item : compare;
                });
                int remainingUtility = int.Parse(split[1]);
                foreach (var pair in revisedTransaction)
                {
                    remainingUtility -= pair.Utility;
                    UtilityList utilityListOfItem = mapItemToUtilityList[pair.Item];
                    Element element = new Element(tid, pair.Utility, remainingUtility);
                    utilityListOfItem.addElement(element);
                }
                tid++; // increase tid number for next transaction
            }
            SearchTKOBase(Array.Empty<int>(), null, listItems);
            sw.Stop();
            totalTime = sw.ElapsedMilliseconds / 1000;
        }
        public void RunTKO_RUZAlgorithm(String input, String output, int k)
        {

            Stopwatch sw = new Stopwatch();
            sw.Start();
            this.minutility = 1;
            this.k = k;
            this.kItemsets = new List<Itemset>();
            string[] lines;
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
                    bool containsItem = mapItemToTWU.ContainsKey(item);
                    if (containsItem)
                        mapItemToTWU[item] = mapItemToTWU[item] + transactionUtility;
                    else
                        mapItemToTWU.TryAdd(item, transactionUtility);
                }
            }
            List<UtilityList> listItems = new List<UtilityList>();
            Dictionary<int, UtilityList> mapItemToUtilityList = new Dictionary<int, UtilityList>();
            foreach (int item in mapItemToTWU.Keys)
            {
                UtilityList uList = new UtilityList(item);
                listItems.Add(uList);
                mapItemToUtilityList.Add(item, uList);
            }

            listItems.Sort((o1, o2) =>
            {
                int compare = mapItemToTWU[o1.item] - mapItemToTWU[o2.item];
                if (compare == 0)
                {
                    return o1.item - o2.item;
                }
                return compare;
            });
            int tid = 0;
            foreach (var line in lines)
            {
                var split = line.Split(":");
                var items = split[0].Split(" ");
                var utilityValues = split[2].Split(" ");
                List<Pair> revisedTransaction = new List<Pair>();
                for (int i = 0; i < items.Length; i++)
                {
                    Pair pair = new Pair();
                    pair.Item = int.Parse(items[i]);
                    pair.Utility = int.Parse(utilityValues[i]);
                    revisedTransaction.Add(pair);
                }
                revisedTransaction.Sort((o1, o2) =>
                {
                    int compare = mapItemToTWU[o1.Item] - mapItemToTWU[o2.Item];
                    return (compare == 0) ? o1.Item - o2.Item : compare;
                });
                int remainingUtility = int.Parse(split[1]);
                foreach (var pair in revisedTransaction)
                {
                    remainingUtility -= pair.Utility;
                    UtilityList utilityListOfItem = mapItemToUtilityList[pair.Item];
                    Element element = new Element(tid, pair.Utility, remainingUtility);
                    utilityListOfItem.addElement(element);
                }
                tid++; // increase tid number for next transaction
            }
            SearchTKOBase(Array.Empty<int>(), null, listItems);
            sw.Stop();
            totalTime = sw.ElapsedMilliseconds / 1000;
        }
        private void SearchTKOBase(int[] prefix, UtilityList pUl, List<UtilityList> uLs)
        {
            for (int i = 0; i < uLs.Count; i++)
            {
                UtilityList X = uLs[i];
                if (X.sumIutils >= minutility)
                {
                    WriteOut(prefix, X.item, X.sumIutils);
                }
                if (X.sumRutils + X.sumIutils >= minutility)
                {
                    List<UtilityList> exULs = new List<UtilityList>();
                    for (int j = i + 1; j < uLs.Count; j++)
                    {
                        UtilityList Y = uLs[j];
                        exULs.Add(Construct(pUl, X, Y));
                    }
                    int[] newPrefix = new int[prefix.Length + 1];
                    Array.Copy(prefix, 0, newPrefix, 0, prefix.Length);
                    newPrefix[prefix.Length] = X.item;
                    SearchTKOBase(newPrefix, X, exULs);
                }

            }
        }

        private void SearchTKO_RUZ(int[] prefix, UtilityList pUl, List<UtilityList> uLs)
        {
            for (int i = 0; i < uLs.Count; i++)
            {
                UtilityList X = uLs[i];
                var zElementsSummation = X.elements.Where(x => x.rutils == 0).Sum(x => x.iutils);
                if (X.sumIutils >= minutility)
                {
                    WriteOut(prefix, X.item, X.sumIutils);
                }
                if (zElementsSummation + X.sumRutils >= minutility)
                {
                    List<UtilityList> exULs = new List<UtilityList>();
                    for (int j = i + 1; j < uLs.Count; j++)
                    {
                        UtilityList Y = uLs[j];
                        exULs.Add(Construct(pUl, X, Y));
                    }
                    int[] newPrefix = new int[prefix.Length + 1];
                    Array.Copy(prefix, 0, newPrefix, 0, prefix.Length);
                    newPrefix[prefix.Length] = X.item;
                    SearchTKO_RUZ(newPrefix, X, exULs);
                }

            }
        }

        private void WriteOut(int[] prefix, int item, long utility)
        {
            Itemset itemset = new Itemset(prefix, item, utility);
            kItemsets.Add(itemset);
            kItemsets = kItemsets.OrderBy(x=>x.utility).ToList();
            if (kItemsets.Count > k)
            {
                if (utility > this.minutility)
                {
                    Itemset lower;
                    do
                    {
                        lower = kItemsets.First();
                        if (lower == null)
                        {
                            break; // / IMPORTANT
                        }
                        kItemsets.Remove(lower);
                    } while (kItemsets.Count > k);
                    this.minutility = kItemsets.First().utility;
                    Console.WriteLine(this.minutility);
                }
            }
        }

        private UtilityList Construct(UtilityList p, UtilityList px, UtilityList py)
        {
            // create an empty utility list for pXY
            UtilityList pxyUL = new UtilityList(py.item);
            // for each element in the utility list of pX
            foreach (var ex in px.elements)
            {
                // do a binary search to find element ey in py with tid = ex.tid
                Element ey = FindElementWithTid(py, ex.tid);
                if (ey == null)
                {
                    continue;
                }
                // if the prefix p is null
                if (p == null)
                {
                    // Create the new element
                    Element eXY = new Element(ex.tid, ex.iutils + ey.iutils, ey.rutils);
                    // add the new element to the utility list of pXY
                    pxyUL.addElement(eXY);

                }
                else
                {
                    // find the element in the utility list of p wih the same tid
                    Element e = FindElementWithTid(p, ex.tid);
                    if (e != null)
                    {
                        // Create new element
                        Element eXY = new Element(ex.tid, ex.iutils + ey.iutils - e.iutils,
                                    ey.rutils);
                        // add the new element to the utility list of pXY
                        pxyUL.addElement(eXY);
                    }
                }
            }
            // return the utility list of pXY.
            return pxyUL;
        }
        private Element FindElementWithTid(UtilityList ulist, int tid)
        {
            List<Element> list = ulist.elements;
            int first = 0;
            int last = list.Count - 1;
            while (first <= last)
            {
                int middle = (first + last) >> 1; // divide by 2

                if (list[middle].tid < tid)
                {
                    first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
                }
                else if (list[middle].tid > tid)
                {
                    last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
                }
                else
                {
                    return list[middle];
                }
            }
            return null;
        }   
        public void WriteResultTofile(String path)
        {
            StringBuilder buffer = new StringBuilder();
            foreach (var itemset in kItemsets.OrderByDescending(x=>x.utility).ToList())
            {
                for (int i = 0; i < itemset.getItemset().Length; i++)
                {
                    buffer.Append(itemset.getItemset()[i]);
                    buffer.Append(' ');
                }

                buffer.Append(itemset.item);

                buffer.Append(" #UTIL: ");
                buffer.Append(itemset.utility);
                buffer.Append(Environment.NewLine);
            }
            File.WriteAllText(path, buffer.ToString());
        }
        public void PrintStats()
        {
            Console.WriteLine("=============  TKO-BASIC - v.2.28 =============");
            Console.WriteLine(" High-utility itemsets count : " + kItemsets.Count);
            Console.WriteLine(" Total time ~ " + totalTime + " s");
            Console.WriteLine("===================================================");
        }
    }


}
