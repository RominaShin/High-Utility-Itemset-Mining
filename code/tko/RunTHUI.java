import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

//import ca.pfv.spmf.algorithms.frequentpatterns.tko.AlgoTKO_Basic;

import java.io.File;

public class RunTHUI {
	public static void main(String [] args) throws IOException{
		String input = "D:/Romin/Doc/CE/Data Mining/code/project-java-copy/project-java-copy/CS_Utility_TopK_THUI.txt";
		String output = "";
		int k = 4;
		//boolean eucsPrune = false;

		AlgoTKO_Basic topk = new AlgoTKO_Basic();
		topk.runAlgorithm(input, output, k);
		topk.printStats();
	}
}
