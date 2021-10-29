class Element {
	final int tid ;   
	final int iutils; 
	final int rutils; 
	
	public Element(int tid, int iutils, int rutils){
		this.tid = tid;
		this.iutils = iutils;
		this.rutils = rutils;
	}
	
	public void print(int depth){
		for (int i=0;i<depth;i++) System.out.print("\t");
		System.out.println("\t"+tid+" iutils: "+iutils+" rutils: "+rutils);
	}
}
