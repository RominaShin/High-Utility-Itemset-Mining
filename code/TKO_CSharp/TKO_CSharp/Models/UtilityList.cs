using System.Collections.Generic;

namespace TKO_CSharp.Models
{
    public class UtilityList
    {
        public int Item { get; set; }
        public int SumIutils { get; set; }
        public int SumRutils { get; set; }
        public List<Element> Elements { get; set; }

		public UtilityList(int item)
		{
			Item = item;
			SumIutils = SumRutils = 0;
			Elements = new List<Element>();
		}

		public void AddElement(Element element)
		{
			SumIutils += element.Iutils;
			SumRutils += element.Rutils;
			Elements.Add(element);
		}
	}
}
