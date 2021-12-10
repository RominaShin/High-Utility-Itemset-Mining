
import java.io.IOException;
public class Executor {
    public static void main(String [] args) throws IOException {
        String input = "";
        String output = "";
        int topN = 100;
        boolean eucsPrune = false;

        input = "D:/Romin/Doc/CE/Data Mining/code/project-java-copy/project-java-copy/CS_Utility_TopK_THUI.txt";
        output = "thuiRes.txt";
        topN = 4;
        eucsPrune = true;

        AlgoTHUI topk = new AlgoTHUI(topN);
        topk.runAlgorithm(input, output, eucsPrune);
        topk.printStats();
    }
}
