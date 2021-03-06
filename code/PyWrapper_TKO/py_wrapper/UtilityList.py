from .Element import Element

class UtilityList :

	def __init__(self,item,sumIutils=0,sumRutils=0,elements =[]):
		"""

		:param item:
		:param elements:
		:param sumIutils:
		:param sumRutils:
		"""
		self.item = item
		self.elements = elements
		self.sumIutils = sumIutils
		self.sumRutils = sumRutils

	def addElement(self,element):
		"""
		pass an Element object
		"""
		self.sumIutils += element.iutils
		self.sumRutils += element.rutils
		self.elements.append(element)
