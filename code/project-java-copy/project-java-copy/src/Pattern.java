public class Pattern implements Comparable<Pattern>{
	
	String prefix;
	long utility;
	int sup;
	int idx;//for sorting patterns in order of insertion 
	
	public Pattern(int[] prefix, int length, UtilityList X, int idx) {
		String buffer = "";
		for (int i = 0; i < length; i++) {
			buffer += prefix[i];
			buffer += " ";
		}
		buffer += "" +X.item;
		this.prefix = buffer;
		this.idx = idx;
		
		this.utility = X.getUtils();
		this.sup = X.elements.size();// + X.sup;//X.sup for closed items
	}

	public String getPrefix(){
		return this.prefix;
	}

	public int compareTo(Pattern o) {
		if(o == this){
			return 0;
		}
		long compare = this.utility - o.utility;
		if(compare !=0){
			return (int) compare;
		}
		return this.hashCode() - o.hashCode();
	}

}
