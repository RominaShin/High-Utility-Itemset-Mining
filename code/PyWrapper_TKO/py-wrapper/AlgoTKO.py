import heapq
import datetime





class Algo_TKO:
    """
    #main class of algorithm
    """

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



    class pair:
        """
        class to show item and its utility
        """

        def __init__(self,item=0,utility=0):
            self.item = item
            self.utility = utility



    def run_algorithm(self,input ="",output="",k=0):
        """
        read file, add each item's utility to its twu, create a list with twu>min_utility
        map of utility list for each item, sort items based on their twu
        in second DB Scan
        create utility list of 1 itemset having twu>min_util
        create a list of
        :return:
        """

        # is it right to define these vars here? and with self?
        self.input = input
        self.output = output
        self.k = k

        self.start_timestamp = datetime.datetime.now()
        self.min_utility = 1

        #First Scan of DataBase
        try:
            input_file = open(input,'r')
            #split = []
            #items = []
            for line  in input_file.readlines():
                if line!='\n' and line[0]!=' ' and line[0]!='@' and line[0]!='#' and line[0]!='%' :
                    this_line = line
                    transaction_utility = this_line.split(':')[1]
                    items = [int(i) for i in this_line.split(':')[0].split(' ')]

                    #is it right to use self.item_to_twu here?
                    for item in items:
                        if item in list(self.item_to_twu.keys()) and transaction_utility > self.item_to_twu[item]:
                            # print(item_to_twu[item])
                            self.item_to_twu[item] = transaction_utility
                        if item not in list(self.item_to_twu.keys()):
                            self.item_to_twu[item] = transaction_utility
            #print(self.item_to_twu)
            input_file.close()
        except IOError:
            print('There is a problem finding your file')
























