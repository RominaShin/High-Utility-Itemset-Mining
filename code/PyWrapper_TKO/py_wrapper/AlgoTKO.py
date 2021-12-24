import heapq
import datetime
from .UtilityList import UtilityList
from .Element import Element
from .Itemset import Itemset
from .PairItemUtility import PairItemUtility


class Algo_TKO:
    """
    #main class of algorithm
    """


    def __init__(self):

        self.total_time = 0
        self.hui_count = 0
        #self.k = k
        self.min_utility = 0

        # priority queue of k_itemset = []
        self.k_itemset = []
        self.k_itemset = heapq.heapify(self.k_itemset)

        # hashmap for item to their twu
        self.item_to_twu = {}

    # class pair:
    #     """
    #     class to show item and its utility
    #     """
    #
    #     def __init__(self, item=0, utility=0):
    #         self.item = item
    #         self.utility = utility

    def run_algorithm(self, input, output, k=0):
        """
        read file, add each item's utility to its twu, create a list with twu>min_utility
        map of utility list for each item, sort items based on their twu
        in second DB Scan
        create utility list of 1 itemset having twu>min_util
        create a list of
        :return:
        """

        # is it right to define these vars here? and with self?
        #self.input = input
        # self.output = output
        # self.k = k

        start_timestamp = datetime.datetime.now()
        self.min_utility = 1

        # First Scan of DataBase
        try:
            input_file = open(input, 'r')
            # split = []
            # items = []
            for line in input_file.readlines():
                if line != '\n' and line[0] != ' ' and line[0] != '@' and line[0] != '#' and line[0] != '%':
                    this_line = line
                    transaction_utility = this_line.split(':')[1]
                    items = [int(i) for i in this_line.split(':')[0].split(' ')]

                    # is it right to use self.item_to_twu here?
                    for item in items:
                        if item in list(self.item_to_twu.keys()) and transaction_utility > self.item_to_twu[item]:
                            # print(item_to_twu[item])
                            self.item_to_twu[item] = transaction_utility
                        if item not in list(self.item_to_twu.keys()):
                            self.item_to_twu[item] = transaction_utility
            # print(self.item_to_twu)
            input_file.close()

        except IOError:
            print('There is a problem finding your file')

        list_items = []
        item_to_utility_list = {}

        for item in self.item_to_twu:
            ulist = UtilityList(item)
            list_items = list_items.append(ulist)
            item_to_utility_list[item] = ulist

        # should we pass self to this method?
        def compare_uls(ul1, ul2):
            if self.item_to_twu[ul1.item] >= self.item_to_twu[ul1.item]:
                return ul1
            else:
                return ul2

        for i in range(0, len(list_items)):
            sorted_ul = True
            for j in range(len(list_items) - i + 1):
                # compare_uls(list_items[j],list_items[j+1])
                if self.item_to_twu[list_items[j].item] > self.item_to_twu[list_items[j + 1].item]:
                    list_items[j], list_items[j + 1] = list_items[j + 1], list_items[j]
                    sorted_ul = False
                elif self.item_to_twu[list_items[j].item] == self.item_to_twu[list_items[j + 1].item]:
                    if list_items[j] > list_items[j+1]:
                        list_items[j], list_items[j + 1] = list_items[j + 1], list_items[j]
                        sorted_ul = False

            if sorted_ul:
                break



        # Second DataBase Scan
        try:
            input_file = open(input, 'r')
            # split = []
            # items = []
            tid = 0

            for line in input_file.readlines():
                if line != '\n' and line[0] != ' ' and line[0] != '@' and line[0] != '#' and line[0] != '%':
                    this_line = line
                    transaction_utility = this_line.split(':')[1]
                    items = [int(i) for i in this_line.split(':')[0].split(' ')]
                    utility_values = [int(i) for i in this_line.split(':')[2].split(' ')]
                    revised_transaction = []

                    # is it right to use self.item_to_twu here?
                    for i in range(0,len(items)):
                        pair = PairItemUtility(items[i],utility_values[i])
                        revised_transaction = revised_transaction.append(pair)

                    remaining_utility = transaction_utility

                    #sort pairs in transaction
                    for i in range(0, len(revised_transaction)):
                        sorted_pair = True
                        for j in range(len(revised_transaction) - i + 1):
                            if self.item_to_twu[revised_transaction[j].item] > self.item_to_twu[revised_transaction[j + 1].item]:
                                revised_transaction[j], revised_transaction[j + 1] = revised_transaction[j + 1], revised_transaction[j]
                                sorted_pair = False

                            elif self.item_to_twu[revised_transaction[j].item] == self.item_to_twu[revised_transaction[j + 1].item]:
                                if revised_transaction[j] > revised_transaction[j + 1]:
                                    revised_transaction[j], revised_transaction[j + 1] = revised_transaction[j + 1], revised_transaction[j]
                                    sorted_pair = False

                        if sorted_pair:
                            break

                    for pair in revised_transaction:
                        remaining_utility -= pair.utility
                        utility_list_of_item = UtilityList(pair.item)
                        element = Element(tid,pair.utility,remaining_utility)
                        utility_list_of_item.addElement(element)
                    tid +=1

            # print(self.item_to_twu)
            input_file.close()

        except IOError:
            print('There is a problem finding your file')


        self.search([],None,list_items)
        #memory_usage
        self.total_time = datetime.datetime.now() - start_timestamp

    def search(self,prefix,pul,uls):
        self.prefix = prefix
        self.pul = pul
        self.uls = uls

        def arrayCopy(src, srcPos, dest, destPos, length):
            for i in range(length):
                dest[i + destPos] = src[i + srcPos]

        for i in range(0,len(uls)):
            X = uls[i]
            if X.sumIutils >= self.min_utility:
                self.write_out(prefix,X.item,X.sumIutils)

            if X.sumIutils + X.sumRutils >= self.min_utility:
                ex_uls = []
                for j in range(i+1,len(uls)):
                    Y = uls[j]
                    ex_uls.append(self.construct_utility_list(pul,X,Y))
                new_prefix =[]
                arrayCopy(prefix,0,new_prefix,0,len(prefix))
                new_prefix[len(prefix)] = X.item

                self.search(new_prefix,X,ex_uls)


    def construct_utility_list(self,p,px,py) -> UtilityList:
        self.p = p
        self.px = px
        self.py = py

        self.pxy = UtilityList(self.py.item)
        for ex in self.px.elements:
            try:
                ey = self.find_element_with_id(self.py,ex.tid)
            except NameError:
                continue

            #This must be checked
            if p == None:
                exy = Element(ex.tid,ex.iutils+ey.iutils,ey.rutils)
                self.pxy.addElement(exy)
            else:
                e = Element(self.p,ex.tid)
                if e != None:
                    exy = Element(ex.tid, ex.iutils + ey.iutils - e.iutils, ey.rutils)
                    self.pxy.addElement(exy)
        return self.pxy


    def find_element_with_id(self,ulist,tid) -> Element:
        self.ulist = ulist
        self.tid = tid

        list_elements = self.ulist.elements
        first = 0
        last = len(list_elements) -1

        while(first <= last):
            middle = last - first / 2
            if list_elements[middle].tid < self.tid:
                first = middle + 1

            if list_elements[middle].tid > self.tid:
                last = middle - 1

            else:
                return list_elements[middle]


    def write_out(self,prefix, item, utility):
        self.prefix = prefix
        # self.item = item
        # self.utility = utility
        # self.itemset = Itemset(self.prefix,self.item,self.utility)
        self.itemset = Itemset(self.prefix, item, utility)

        heapq.heappush(self.k_itemset,self.itemset)
        if len(self.k_itemset) > self.k:
            if utility > self.min_utility:
                lower = heapq.heappop(self.k_itemset)
                while len(self.k_itemset) > self.k:
                    if lower is None:
                        break
                    lower = heapq.heappop(self.k_itemset)
                self.min_utility = lower.utility


    def write_files_output(self,path):
        f = open(path,'r+')
        k_itemset_list = ['{} #Utils: {}'.format(item_set,item_set.utility) +'\n' for item_set in self.k_itemset]
        f.writelines(k_itemset_list)


    def print_result(self):
        print("=============  TKO-BASIC - v.2.28 ============="+'\n')
        print(" High-utility itemsets count :{} ".format(len(self.k_itemset)) )
        print("Min Utility is updated to : {}".format(self.min_utility))
        print(" Total time ~ " + self.total_time+ " s")
        print(" itemsets ~ " + self.k_itemset)
        #print(" Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        print("===================================================")





















