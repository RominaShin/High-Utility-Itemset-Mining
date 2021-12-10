import AlgoTKO

class main:

    def __init__(self,input="",output="",k=0):
        self.input = input
        self.output = output
        self.k = k

        input = "D:/Romin/Doc/CE/Data Mining/code/Py_Wrapper_TKO/py_wrapper/DB_Utility.txt";
        #output = "thuiRes.txt";
        k = 4


        topk = AlgoTKO()
        topk.runAlgorithm(input, output, k)
        #topk.printStats()


if __name__ == '__main__':
    main()