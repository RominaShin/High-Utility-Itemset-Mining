import heapq





"""
#main class of algorithm
"""
class Algo_TKO:

    total_time = 0
    hui_count = 0
    k = 0
    min_utility = 0

    # priority queue of k_itemset = []
    k_itemset = []
    k_itemset = heapq.heapify(k_itemset)

    # hashmap for item to their twu
    item_to_twu = {}

    def __init__(self):
        pass


    """
    class to show item and its utility
    """
    class pair:

        def __init__(self,item=0,utility=0):
            self.item = item
            self.utility = utility



    def run_algorithm(self):




















