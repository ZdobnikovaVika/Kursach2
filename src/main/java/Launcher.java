import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.Scanner;

public class Launcher {
    @Argument(required = true, metaVar = "InputName", usage = "Input file name")
    private String inputFileName = null;

    @Option(name = "-o")
    private String outputFileName = null;


    public static void main(String[] args) {

        Launcher work = new Launcher();
        work.launcher(args);
    }

    private void launcher(String[] args){
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException exert){
            System.err.print("Error with file");
            System.exit(10);
        }
        try {
            File file = new File(inputFileName);
            Scanner scan = new Scanner(file);
            Decision decision = new Decision(scan.nextLine());
            File outfile = new File(outputFileName);
            FileWriter fw = new FileWriter(outfile);
            fw.write(decision.toString());
            fw.close();
        } catch (IOException exep){
            System.err.print("Error IOE");
            System.exit(11);
        }

    }
}
