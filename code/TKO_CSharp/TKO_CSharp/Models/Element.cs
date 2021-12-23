namespace TKO_CSharp.Models
{
    public class Element
    {
        public int TId { get; set; }
        public int Iutils { get; set; }
        public int Rutils { get; set; }

        public Element(int tid, int iutils, int rutils)
        {
            TId = tid;
            Iutils = iutils;
            Rutils = rutils;
        }
    }
}
