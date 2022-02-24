namespace TKO_CSharp.Models
{
    public class Element
    {
        // The three variables as described in the paper:
        public int tid = -1;   // transaction id
        public int iutils = 0; // itemset utility
        public int rutils = 0; // remaining utility

        public Element(int tid, int iutils, int rutils)
        {
            this.tid = tid;
            this.iutils = iutils;
            this.rutils = rutils;
        }
    }

}
