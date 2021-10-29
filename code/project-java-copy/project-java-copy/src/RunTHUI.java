import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class RunTHUI {
	public static void main(String [] args) throws IOException{
		String input = "";
		String output = "";
		int topN = 100;
		boolean eucsPrune = false;
        if(args.length > 0) {
			input = ".//"+args[0]+".txt";
            output = ".//"+args[1]+".txt";
			topN = Integer.parseInt(args[2]);
			if (args.length>3)
				eucsPrune = (Integer.parseInt(args[3])==1?true:false);
        }
		AlgoTHUI topk = new AlgoTHUI(topN);
		topk.runAlgorithm(input, output, eucsPrune);
		topk.printStats();
	}
}
