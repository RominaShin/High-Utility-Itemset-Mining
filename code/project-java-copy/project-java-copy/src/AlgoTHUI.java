import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlgoTHUI {

	// variable for statistics
	public double maxMemory = 0; // the maximum memory usage
	public long startTimestamp = 0; // the time the algorithm started
	public long startTimestampPha2 = 0;
	public long endTimestamp = 0; // the time the algorithm terminated
	public int huiCount = 0; // the number of HUI generated
	public int candidateCount = 0;
	
	Map<Integer, Integer> mapItemToTWU;
	
	long minUtility = 0;
	int topkstatic = 0;

	// writer to write the output file
	BufferedWriter writer = null;

	PriorityQueue<Pattern> kPatterns = new PriorityQueue<Pattern>();
	PriorityQueue<Long> leafPruneUtils = null;
	
	boolean debug = false;
	public long totalContructTime = 0;
	public long totalWhile = 0;
	public int totalItem = 0;
	final int BUFFERS_SIZE = 200;
	private int[] itemsetBuffer = null;
	
	private java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
	Map<Integer, Map<Integer, Item>> mapFMAP = null;
    private StringBuilder buffer = new StringBuilder(32);
	
	Map<Integer, Map<Integer, Long>> mapLeafMAP = null;
	long riuRaiseValue = 0, leafRaiseValue = 0;
	int leafMapSize = 0;
	
	int[] totUtil;
	int[] ej;
	int[] pos;
	int[] twu;
	
	boolean EUCS_PRUNE=false; 
	boolean LEAF_PRUNE = true;
	
	class Pair {
		int item = 0;
		int utility = 0;
		Pair(int item, int utility){
			this.item = item;this.utility=utility;
		}
		public String toString() {
			return "[" + item + "," + utility + "]";
		}
	}
	class PairComparator implements Comparator<Pair> {
		@Override
		public int compare(Pair o1, Pair o2) {
			return compareItems(o1.item, o2.item);
		}
	}
	class UtilComparator implements Comparator<UtilityList> {
		@Override
		public int compare(UtilityList o1, UtilityList o2) {
			return compareItems(o1.item, o2.item);
		}
	}
	
	public AlgoTHUI() {

	}

	public AlgoTHUI(int top) {
		topkstatic = top;
	}
	
	String inputFile;
	public void runAlgorithm(String input, String output, boolean eucsPrune) throws IOException {

		//System.out.println("======================== Start Algorithm TopK New ====================");
		maxMemory = 0;
		itemsetBuffer = new int[BUFFERS_SIZE];
		this.EUCS_PRUNE = eucsPrune;
		Map<Integer, Long> RIU = new HashMap<Integer, Long>();
		
		inputFile = input;
		if (EUCS_PRUNE)
			mapFMAP = new HashMap<Integer, Map<Integer, Item>>();
		
		if (LEAF_PRUNE){
			mapLeafMAP = new HashMap<Integer, Map<Integer, Long>>();
			leafPruneUtils = new PriorityQueue<Long>();
		}
		
		startTimestamp = System.currentTimeMillis();
		writer = new BufferedWriter(new FileWriter(output));

		mapItemToTWU = new HashMap<Integer, Integer>();
		
		BufferedReader myInput = null;
		String thisLine;
		try {
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
			while ((thisLine = myInput.readLine()) != null) {
				if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
						|| thisLine.charAt(0) == '%' || thisLine.charAt(0) == '@') {continue;}
				String split[] = thisLine.split(":");
				String items[] = split[0].split(" ");
				String utilityValues[] = split[2].split(" ");
				int transactionUtility = Integer.parseInt(split[1]);
				for (int i = 0; i < items.length; i++) {
					Integer item = Integer.parseInt(items[i]);
					Integer twu = mapItemToTWU.get(item);
					twu = (twu == null) ? transactionUtility : twu + transactionUtility;
					mapItemToTWU.put(item, twu);
					
					int util = Integer.parseInt(utilityValues[i]);
					Long real = RIU.get(item);
					real = (real == null) ? util : util + real;
					RIU.put(item, real);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}

		// Raising threshold by real item utility
		raisingThresholdRIU(RIU, topkstatic);
		//System.out.println("raising RIU: " + minUtility+"  topk "+topkstatic+"  item "+mapItemToTWU.keySet().size());
		riuRaiseValue = minUtility;

		List<UtilityList> listOfUtilityLists = new ArrayList<UtilityList>();
		Map<Integer, UtilityList> mapItemToUtilityList = new HashMap<Integer, UtilityList>();
		
		for(Integer item: mapItemToTWU.keySet()){
			if(mapItemToTWU.get(item) >= minUtility){
				UtilityList uList = new UtilityList(item);
				mapItemToUtilityList.put(item, uList);
				listOfUtilityLists.add(uList);
			}
		}
        
		Collections.sort(listOfUtilityLists, new UtilComparator());
		
		int remainingUtility=0;
		long newTWU = 0;
		String key=null; Integer kTid;
		try {
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
			int tid = 0;
			while ((thisLine = myInput.readLine()) != null) {
				if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
						|| thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}
				String split[] = thisLine.split(":");
				String items[] = split[0].split(" ");
				String utilityValues[] = split[2].split(" ");
				remainingUtility = 0;
				newTWU = 0; // NEW OPTIMIZATION

				List<Pair> revisedTransaction = new ArrayList<Pair>();
				for (int i = 0; i < items.length; i++) {
					Pair pair = new Pair(Integer.parseInt(items[i]), Integer.parseInt(utilityValues[i]));
					if (mapItemToTWU.get(pair.item) >= minUtility) {
						revisedTransaction.add(pair);
						remainingUtility += pair.utility;
						newTWU += pair.utility; // NEW OPTIMIZATION
					}
				}
				if (revisedTransaction.size()==0) continue;
				Collections.sort(revisedTransaction, new PairComparator()); 

				remainingUtility = 0;
				for(int i = revisedTransaction.size() - 1; i>=0; i--){
					Pair pair =  revisedTransaction.get(i);
					UtilityList utilityListOfItem = mapItemToUtilityList.get(pair.item);		
					Element element = new Element(tid, pair.utility, remainingUtility);
					utilityListOfItem.addElement(element);
			
					if (EUCS_PRUNE) updateEUCSprune(i, pair, revisedTransaction, newTWU);
					if (LEAF_PRUNE) updateLeafprune(i, pair, revisedTransaction, listOfUtilityLists);
					remainingUtility += pair.utility;
				}				
				tid++; // increase tid number for next transaction
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}
		
		if (EUCS_PRUNE){
			raisingThresholdCUDOptimize(topkstatic);
			removeEntry();
		}
		RIU.clear();
	
		startTimestampPha2 = System.currentTimeMillis();

		if (LEAF_PRUNE){
			raisingThresholdLeaf(listOfUtilityLists);
			setLeafMapSize();
			removeLeafEntry();
			leafPruneUtils = null;
		}
		leafRaiseValue = minUtility;
		mapItemToUtilityList = null;
		
		checkMemory();
		thui(itemsetBuffer, 0, null, listOfUtilityLists);
		checkMemory();	
		
		writeResultTofile();
		writer.close();
		
		endTimestamp = System.currentTimeMillis();
		kPatterns.clear();
		
		
	}
	
	public void updateEUCSprune(int i, Pair pair, List<Pair> revisedTransaction, long newTWU){
	
		Map<Integer, Item> mapFMAPItem = mapFMAP.get(pair.item);
		if (mapFMAPItem == null) {
			mapFMAPItem = new HashMap<Integer, Item>();
			mapFMAP.put(pair.item, mapFMAPItem);
		}
		for (int j=i+1;j<revisedTransaction.size();j++){
			if (pair.item == revisedTransaction.get(j).item) continue;//kosarak dataset has duplicate items 
			Pair pairAfter = revisedTransaction.get(j);
			Item twuItem = mapFMAPItem.get(pairAfter.item);
			if (twuItem == null) twuItem = new Item();
			twuItem.twu += newTWU;
			twuItem.utility += (long) pair.utility + pairAfter.utility;
			mapFMAPItem.put(pairAfter.item, twuItem);
		}
	}
	
	public void updateLeafprune(int i, Pair pair, List<Pair> revisedTransaction, List<UtilityList> ULs){
	
		long cutil = (long)pair.utility;
		int followingItemIdx = getTWUindex(pair.item, ULs);
		Map<Integer, Long> mapLeafItem = mapLeafMAP.get(followingItemIdx);
		if (mapLeafItem == null) {
			mapLeafItem = new HashMap<Integer, Long>();
			mapLeafMAP.put(followingItemIdx, mapLeafItem);
		}						
		for (int j=i-1;j>=0;j--){
			if (pair.item == revisedTransaction.get(j).item) continue;//kosarak dataset has duplicate items 
			Pair pairAfter = revisedTransaction.get(j);
			
			if (ULs.get(--followingItemIdx).item != pairAfter.item) break;
			Long twuItem = mapLeafItem.get(followingItemIdx);
			if (twuItem == null) twuItem = new Long(0);
			cutil += pairAfter.utility;
			twuItem += cutil;
			mapLeafItem.put(followingItemIdx, twuItem);
		}
	}
	
	public int getTWUindex(int item, List<UtilityList> ULs){
		for (int i=ULs.size()-1;i>=0;i--) 
			if (ULs.get(i).item==item) return i;
		return -1;
	}
	
	public void setLeafMapSize(){
		for (Entry<Integer, Map<Integer, Long>> entry : mapLeafMAP.entrySet()) 
			leafMapSize += entry.getValue().keySet().size();
	}
	
	private int compareItems(int item1, int item2) {
		int compare = (int) (mapItemToTWU.get(item1) - mapItemToTWU.get(item2));		
		return (compare == 0) ? item1 - item2 : compare;
	}
	
	public void writeResultTofileUnord() throws IOException {

		Iterator<Pattern> iter = kPatterns.iterator();
		while (iter.hasNext()) {
			huiCount++; // increase the number of high utility itemsets found
			Pattern pattern = (Pattern) iter.next();
			StringBuilder buffer = new StringBuilder();
			buffer.append(pattern.prefix.toString());
			// write separator
			buffer.append(" #UTIL: ");
			// write support
			buffer.append(pattern.utility);			
			writer.write(buffer.toString());
			writer.newLine();
		}
		writer.close();
	}
	
	private void thui(int[] prefix, int prefixLength, UtilityList pUL, List<UtilityList> ULs) throws IOException {
		
		for (int i = ULs.size() - 1; i >= 0; i--){
			if (ULs.get(i).getUtils() >= minUtility) 
				save(prefix, prefixLength, ULs.get(i));
		}
		
		for (int i = ULs.size() - 2; i >= 0; i--) {//last item is a single item, and hence no extension
			checkMemory();
    		UtilityList X = ULs.get(i);
			if(X.sumIutils + X.sumRutils >= minUtility && X.sumIutils>0){//the utility value of zero cases can be safely ignored, as it is unlikely to generate a HUI; besides the lowest min utility will be 1
				if (EUCS_PRUNE){
					Map<Integer, Item> mapTWUF = mapFMAP.get(X.item);
					if (mapTWUF == null) continue;
				}
				
				List<UtilityList> exULs = new ArrayList<UtilityList>();
				for (int j=i+1;j<ULs.size();j++){
					UtilityList Y = ULs.get(j);
					candidateCount++;
					UtilityList exul = construct(pUL, X, Y);
					if (exul!=null) exULs.add(exul);
					
				}
				prefix[prefixLength] = X.item;
				thui(prefix, prefixLength+1, X, exULs);	
            }
		}
	}
	
	public String getPrefixString(int[] prefix, int length){
		String buffer = "";
		for (int i = 0; i < length; i++) {
			buffer += prefix[i];
			buffer += " ";
		}
		return buffer;
	}
	
	private UtilityList construct(UtilityList P, UtilityList px, UtilityList py)
    {
		UtilityList pxyUL = new UtilityList(py.item);
        int totUtil = px.sumIutils + px.sumRutils;
        int ei=0, ej=0, Pi=-1;
        
        Element ex=null, ey=null, e=null;
        while (ei<px.elements.size() && ej<py.elements.size()){
            if (px.elements.get(ei).tid > py.elements.get(ej).tid) {
                ++ej;continue;
           }//px not present, py pres
            if (px.elements.get(ei).tid < py.elements.get(ej).tid) {//px present, py not present
                totUtil = totUtil - px.elements.get(ei).iutils - px.elements.get(ei).rutils;
                if (totUtil < minUtility) {return null;}
                ++ei;
                ++Pi;//if a parent is present, it should be as large or larger than px; besides the ordering is by tid
                continue;
            }
            ex = px.elements.get(ei);
            ey = py.elements.get(ej);
            
            if (P==null){
                pxyUL.addElement(new Element(ex.tid, ex.iutils + ey.iutils, ey.rutils));
            }
            else{
                while (Pi<P.elements.size() && P.elements.get(++Pi).tid < ex.tid);
                e = P.elements.get(Pi);
                
                pxyUL.addElement(new Element(ex.tid, ex.iutils + ey.iutils - e.iutils, ey.rutils));
            }
            ++ei;++ej;
        }
        while (ei<px.elements.size()){
            totUtil = totUtil - px.elements.get(ei).iutils - px.elements.get(ei).rutils;
            if (totUtil < minUtility) {return null;}
            ++ei;
        }
		return pxyUL;
	}

	public void writeResultTofile() throws IOException {
	
		if (kPatterns.size()==0) return;
		List<Pattern> lp = new ArrayList<Pattern>();
		do {
			huiCount++; 
			Pattern pattern = kPatterns.poll();
			
			lp.add(pattern);
		}while (kPatterns.size() > 0);
		
		Collections.sort(lp, new Comparator<Pattern>() {
					public int compare(Pattern o1, Pattern o2) {
						return comparePatterns(o1, o2);
						//return comparePatternsIdx(o1, o2);
					}
				});
		
		for (Pattern pattern : lp){
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(pattern.prefix.toString());
			buffer.append(" #UTIL: ");
			// write support
			buffer.append(pattern.utility);
			
			writer.write(buffer.toString());
			writer.newLine();
		} 
		writer.close();
	}
	private int comparePatterns(Pattern item1, Pattern item2) {
		//int compare = (int) (Integer.parseInt(item1.split(" ")[0]) - Integer.parseInt(item2.split(" ")[0]));
		int i1 = (int)Integer.parseInt(item1.prefix.split(" ")[0]);
		int i2 = (int)Integer.parseInt(item2.prefix.split(" ")[0]);
		
		int compare = (int) (mapItemToTWU.get(i1) - mapItemToTWU.get(i2));
		return compare;
	}
	private int comparePatternsIdx(Pattern item1, Pattern item2) {
		int compare = item1.idx - item2.idx;
		return compare;
	}

	private double getObjectSize(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.close();
		double maxMemory = baos.size() / 1024d / 1024d;
		return maxMemory;
	}

	public int getMax(Map<Integer, Integer> map) {
		int r = 0;
		for (Integer value : map.values()) {
			if (value >= minUtility)
				r++;
		}
		return r;
	}

	public void raisingThresholdRIU(Map<Integer, Long> map, int k) {
		List<Map.Entry<Integer, Long>> list = new LinkedList<Map.Entry<Integer, Long>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Long>>() {
			@Override
			public int compare(Map.Entry<Integer, Long> value1, Map.Entry<Integer, Long> value2) {
				return (value2.getValue()).compareTo(value1.getValue());
			}
		});

		if ((list.size() >= k) && (k > 0)) {
			minUtility = list.get(k - 1).getValue();
		}
		list = null;
	}
	
	public void raisingThresholdCUDOptimize(int k) {
		PriorityQueue<Long> ktopls = new PriorityQueue<Long>();
		long value = 0L;
		for (Entry<Integer, Map<Integer, Item>> entry : mapFMAP.entrySet()) {
			for (Entry<Integer, Item> entry2 : entry.getValue().entrySet()) {
				value = entry2.getValue().utility;
				if (value>=minUtility){
					if (ktopls.size() < k)
						ktopls.add(value);
					else if (value > ktopls.peek()) {
						ktopls.add(value);
						do {
							ktopls.poll();
						} while (ktopls.size() > k);
					}
				}
			}
		}
		if ((ktopls.size() > k - 1) && (ktopls.peek() > minUtility))
			minUtility = ktopls.peek();
		ktopls.clear();
	}
	
	public void addToLeafPruneUtils(long value){
		if (leafPruneUtils.size() < topkstatic)
			leafPruneUtils.add(value);
		else if (value > leafPruneUtils.peek()) {
			leafPruneUtils.add(value);
			do {
				leafPruneUtils.poll();
			} while (leafPruneUtils.size() > topkstatic);
		}
	}
	
	public void raisingThresholdLeaf(List<UtilityList> ULs) {
		long value = 0L;
		//LIU-Exact
		for (Entry<Integer, Map<Integer, Long>> entry : mapLeafMAP.entrySet()) {
			for (Entry<Integer, Long> entry2 : entry.getValue().entrySet()) {
				value = entry2.getValue();
				if (value>=minUtility){
					addToLeafPruneUtils(value);
				}
			}
		}
		//LIU-LB
		for (Entry<Integer, Map<Integer, Long>> entry : mapLeafMAP.entrySet()) {
			for (Entry<Integer, Long> entry2 : entry.getValue().entrySet()) {
				value = entry2.getValue();
				if (value>=minUtility){
					
					int end = entry.getKey()+1;//master contains the end reference 85 (leaf)
					int st = entry2.getKey();//local map contains the start reference 76-85 (76 as parent)
					long value2 = 0L;
					//all entries between st and end processed, there will be go gaps in-between (only leaf with consecutive entries inserted in mapLeafMAP)
					
					for (int i=st+1;i<end-1;i++){//exclude the first and last e.g. 12345 -> 1345,1245,1235 estimates
						value2 = value - ULs.get(i).getUtils();
						if (value2>=minUtility) addToLeafPruneUtils(value2);
						for (int j=i+1;j<end-1;j++){
							value2 = value - ULs.get(i).getUtils() - ULs.get(j).getUtils();
							if (value2>=minUtility) addToLeafPruneUtils(value2);
							for (int k=j+1;k+1<end-1;k++){		
								value2 = value - ULs.get(i).getUtils() - ULs.get(j).getUtils() - ULs.get(k).getUtils();
								if (value2>=minUtility) addToLeafPruneUtils(value2);
							}
						}
					}					
				}
			}
		}
		for (UtilityList u:ULs){//add all 1 items
			value = u.getUtils();
			if (value>=minUtility) addToLeafPruneUtils(value);
		}
		if ((leafPruneUtils.size() > topkstatic - 1) && (leafPruneUtils.peek() > minUtility))
			minUtility = leafPruneUtils.peek();
	}
	
	private void removeEntry() {
		for (Entry<Integer, Map<Integer, Item>> entry : mapFMAP.entrySet()) {
			for (Iterator<Map.Entry<Integer, Item>> it = entry.getValue().entrySet().iterator(); it.hasNext();) {
				Map.Entry<Integer, Item> entry2 = it.next();
				if (entry2.getValue().twu < minUtility) {
					it.remove();
				}
			}
		}
	}
	
	private void removeLeafEntry() {
		for (Entry<Integer, Map<Integer, Long>> entry : mapLeafMAP.entrySet()) {
			for (Iterator<Map.Entry<Integer, Long>> it = entry.getValue().entrySet().iterator(); it.hasNext();) {
				Map.Entry<Integer, Long> entry2 = it.next();
				it.remove();
			}
		}
	}

	private void save(int[] prefix, int length, UtilityList X) {
	
		kPatterns.add(new Pattern(prefix, length, X, candidateCount));	
		if (kPatterns.size() > topkstatic) {
			if (X.getUtils() >= minUtility) {
				do {
					kPatterns.poll();
				} while (kPatterns.size() > topkstatic);
			}
			minUtility = kPatterns.peek().utility;
		}
	}
	
	private void checkMemory() {
		double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
		if (currentMemory > maxMemory) {
			maxMemory = currentMemory;
		}
	}
	
	public void printStats() throws IOException {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		System.out.println("=============  THUI ALGORITHM - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp)+ " ms");
		System.out.println(" Memory ~ " + df.format(maxMemory) + " MB");
		System.out.println(" High-utility itemsets count : " + huiCount+" Candidates "+candidateCount);
		System.out.println(" Final minimum utility : "+minUtility);
		File f = new File(inputFile);
        String tmp = f.getName();
        tmp = tmp.substring(0, tmp.lastIndexOf('.'));
		System.out.println(" Dataset : "+tmp);
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		System.out.println(" End time "+timeStamp);
		System.out.println("===================================================");

	}
}
