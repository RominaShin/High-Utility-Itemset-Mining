import java.util.ArrayList;
import java.util.List;

class UtilityList {
	int item;  // the item
	int sumIutils = 0;  // the sum of item utilities
	int sumRutils = 0;  // the sum of remaining utilities
	List<Element> elements = new ArrayList<Element>();  // the elements
	
	public UtilityList(int item){
		this.item = item;
	}	
	
	public int getUtils(){
		return this.sumIutils;
	}
	
	public void addElement(Element element){
		sumIutils += element.iutils;
		sumRutils += element.rutils;
		elements.add(element);
	}
}
