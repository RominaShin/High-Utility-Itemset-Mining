from py_wrapper.AlgoTKO import Algo_TKO

# class main:
#
#     def __init__(self, input="", output="", k=0):
#         self.input = input
#         self.output = output
#         self.k = k
#
#         topk = Algo_TKO
#         topk.run_algorithm(self.input, self.output, self.k)
#         # topk.printStats()


if __name__ == '__main__':
    # m = main(input="D:/Romin/Doc/CE/Data Mining/code/Py_Wrapper_TKO/py_wrapper/DB_Utility.txt"
    #          , output="thuiRes.txt"
    #          , k=4)

    input = "D:/Romin/Doc/CE/Data Mining/code/Py_Wrapper_TKO/py_wrapper/DB_Utility.txt"
    output = "thuiRes.txt"
    k = 4

    topk = Algo_TKO
    topk.run_algorithm(input, output, k)