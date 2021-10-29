class Itemset :

	def __init__(self,itemset,utility,support):
        """
        pass int[] itemset, long utility, int support
        """

		self.itemset = itemset;
		self.utility = utility;
		self.support = support;


	#def toString():
	#	return Arrays.toString(itemset) + " utility : " + utility + " support:  " + support;
