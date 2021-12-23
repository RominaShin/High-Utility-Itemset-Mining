class PairItemUtility:


	def __init__(self, item = 0, utility = 0):
		self.item = item
		self.utility = utility

	def toString(self) :
		return "[" + self.item + "," + self.utility + "]"
