using System.Collections.Generic;

namespace TKO_CSharp.Models
{
    public class UtilityList
    {
        public int item;  // the item
        public int sumIutils = 0;  // the sum of item utilities
        public int sumRutils = 0;  // the sum of remaining utilities
        public List<Element> elements = new List<Element>();  // the elements

        public UtilityList(int item)
        {
            this.item = item;
        }

        /**
		 * Method to add an element to this utility list and update the sums at the same time.
		 */
        public void addElement(Element element)
        {
            sumIutils += element.iutils;
            sumRutils += element.rutils;
            elements.Add(element);
        }
    }

}