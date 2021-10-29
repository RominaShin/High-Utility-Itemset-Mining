import Element

class UtilityList :

	sumRutils = 0
	sumIutils = 0
	elements = []

	def __init__(,item):
		.item = item


	def getUtils():
		return .sumIutils


	def addElement(element):
		"""
		pass an Element object
		"""
		sumIutils += element.iutils
		sumRutils += element.rutils
		elements.append(element)
