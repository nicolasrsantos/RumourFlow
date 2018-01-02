package org.textsim.cli;

import java.io.PrintWriter;
import java.nio.file.Paths;

import org.apache.commons.cli.*;
import org.textsim.exception.ProcessException;
import org.textsim.textrt.preproc.TextrtPreproc;
import org.textsim.textrt.proc.SMP.SMP_OneFile;
import org.textsim.wordrt.preproc.WordrtPreproc;
import org.textsim.wordrt.proc.WordSim;

/**
 * Provide command line interface.
 * 
 * @author <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class Main {

    private final static int WIDTH = 150;  // Number of character per line in console
    
    /**
     * Defines command line options.
     * 
     * @return A defined Option object
     */
    @SuppressWarnings("static-access")
    private static Options createOptions() {
        
        Options options = new Options();

        // help option (h)
        OptionGroup support = new OptionGroup();
        support.addOption(new Option("h", "help", false, "print the help message"));

        // process options (upper-case letters)
        OptionGroup process = new OptionGroup();
        process.addOption(new Option("N", "ngram-preproc", false, "preprocess the Unigram and Trigram"));
        process.addOption(new Option("D", "dataset-preproc", false, "preprocess the dataset to be binary"));
        process.addOption(new Option("P", "processing", false, "process the binary corpus and get the similarity result"));
        process.addOption(new Option("W", "word-similarity", false, "process a csv word-file and produce similarities"));

        // directory & path options (lower-case letters)
        options.addOption(OptionBuilder.withLongOpt("dataset")
                                       .hasArgs(1)
                                       .withArgName("DIR")
                                       .create("dataset"));
        options.addOption(OptionBuilder.withLongOpt("wordcsv")
                                       .hasArgs(1)
                                       .withArgName("CSV")
                                       .create("wordcsv"));
        options.addOption(OptionBuilder.withLongOpt("excep-word")
                                       .hasArgs(1)
                                       .withArgName("PATH")
                                       .create("ew"));
        options.addOption(OptionBuilder.withLongOpt("temp")
                                       .hasArgs(1)
                                       .withArgName("DIR")
                                       .create("tempDir"));
        options.addOption(OptionBuilder.withLongOpt("num-of-threads")
                                       .hasArgs(1)
                                       .withArgName("NUM")
                                       .create("threads"));
        options.addOption(OptionBuilder.withLongOpt("token-filter")
                                       .hasArgs(1)
                                       .withArgName("NAME")
                                       .create("tf"));
        options.addOption(OptionBuilder.withLongOpt("name")
                                       .hasArgs(1)
                                       .withArgName("NAME")
                                       .create("name"));
        options.addOption(OptionBuilder.withLongOpt("output-dir")
                                       .hasArgs(1)
                                       .withArgName("DIR")
                                       .create("outDir"));
        options.addOption(OptionBuilder.withLongOpt("output-path")
                                       .hasArgs(1)
                                       .withArgName("PATH")
                                       .create("outFile"));
        options.addOption(OptionBuilder.withLongOpt("regex")
                                       .hasArgs(1)
                                       .withArgName("REGEX")
                                       .create("regex"));
        options.addOption(OptionBuilder.withLongOpt("stop-word")
                                       .hasArgs(1)
                                       .withArgName("PATH")
                                       .create("sw"));
        options.addOption(OptionBuilder.withLongOpt("trigram")
                                       .hasArgs(1)
                                       .withArgName("DIR/PATH")
                                       .create("tri"));
        options.addOption(OptionBuilder.withLongOpt("unigram")
                                       .hasArgs(1)
                                       .withArgName("DIR/PATH")
                                       .create("uni"));
        options.addOption(OptionBuilder.withLongOpt("weight1")
                                       .hasArgs(1)
                                       .withArgName("VALUE")
                                       .create("w1"));
        options.addOption(OptionBuilder.withLongOpt("weight2")
                                       .hasArgs(1)
                                       .withArgName("VALUE")
                                       .create("w2"));
        
        options.addOptionGroup(process);
        options.addOptionGroup(support);
        
        return options;
        
    }
    
    /**
     * Defines response to the user input.
     * 
     * @param cmd      A tokenized command line input.
     * @param options  A defined command line options.
     * 
     * @throws Exception 
     */
    private static void makeChoice(CommandLine cmd, Options options)
            throws Exception {
        
        if (cmd.hasOption("h")) {
            showHelp(options);
        } else if (cmd.hasOption("N")) {
            preprocNgram(cmd, options);
        } else if (cmd.hasOption("D")) {
            preprocDocumentset(cmd, options);
        } else if (cmd.hasOption("P")) {
          proc(cmd, options);
        } else if (cmd.hasOption("W")) {
          procWordPairs(cmd, options);
        }
    }
    
    /**
     * Pre-process the n-gram corpus file.
     * 
     * @param cmd      A tokenized command line input.
     * @param options  A defined command line options.
     * 
     * @throws Exception 
     */
    private static void preprocNgram(CommandLine cmd, Options options)
            throws Exception {
        
        if (cmd.hasOption("uni") && cmd.hasOption("tri")) {
                WordrtPreproc.preprocessCorpus(
                        WordrtPreproc.BINARY,
                        cmd.getOptionValue("uni"),
                        cmd.getOptionValue("tri"),
                        cmd.hasOption("outDir") ? cmd.getOptionValue("outDir") : null,
                        cmd.hasOption("tempDir") ? cmd.getOptionValue("tempDir") : null,
                        cmd.hasOption("name") ? cmd.getOptionValue("name") : null,
                        cmd.hasOption("regex") ? cmd.getOptionValue("regex") : null);
        } else {
            // Set descriptions
            options.getOption("uni").setDescription("the directory/path of the original trigram file(s)");
            options.getOption("tri").setDescription("the directory/path of the original trigram file(s)");
            options.getOption("outDir").setDescription("the directory of output");
            options.getOption("temp").setDescription("the path of the temporary directory");
            options.getOption("name").setDescription("the name of the output file");
            options.getOption("regex").setDescription("the regular expression used for filtering the corpus");
            // Print info
            System.out.println();
            System.out.println("Generate preprocessed unigram and trigram files.");
            System.out.println();
            System.out.println("SYNOPSIS");
            System.out.println();
            System.out.println("     java -jar TextSimLib.jar -N -uni dir -tri dir [-outFile dir] [-temp dir] [-regex regex]");
            System.out.println();
            System.out.println("IMPERATIVE Options");
            System.out.println();
            new HelpFormatter().printOptions(new PrintWriter(System.out, true), WIDTH,
                    new Options().addOption(options.getOption("uni"))
                                 .addOption(options.getOption("tri")),
                    5, 4);
            System.out.println();
            System.out.println("OPTIONAL Options");
            System.out.println();
            new HelpFormatter().printOptions(new PrintWriter(System.out, true), WIDTH,
                    new Options().addOption(options.getOption("outDir"))
                                 .addOption(options.getOption("tempDir"))
                                 .addOption(options.getOption("name"))
                                 .addOption(options.getOption("regex")),
                    5, 4);
            System.out.println();
        }
    }
    
    /**
     * Pre-process the binary corpus file.
     * 
     * @param cmd      A tokenized command line input.
     * @param options  A defined command line options.
     * 
     * @throws ProcessException 
     */
    private static void preprocDocumentset(CommandLine cmd, Options options)
            throws ProcessException {
        
        if (cmd.hasOption("dataset") && cmd.hasOption("uni")) {
            TextrtPreproc.process(
                    TextrtPreproc.multiThreded,
                    cmd.getOptionValue("dataset"),
                    cmd.getOptionValue("uni"),
                    cmd.hasOption("outFile") ? cmd.getOptionValue("outFile") : null,
                    cmd.hasOption("sw") ? cmd.getOptionValue("sw") : null,
                    cmd.hasOption("ew") ? cmd.getOptionValue("ew") : null,
                    cmd.hasOption("tf") ? cmd.getOptionValue("tf") : "D");
        } else {
            // Set descriptions
            options.getOption("dataset").setDescription("the directory of the original dataset");
            options.getOption("uni").setDescription("the path of the preprocessed unigram file");
            options.getOption("outFile").setDescription("the path of output file.\n" +
            		                                    "if this option is not given, it will generate a default name.");
            options.getOption("sw").setDescription("the path of the stopword list");
            options.getOption("ew").setDescription("the path of the exception word list");
            options.getOption("tf").setDescription("the character indentifiers of tokenfilters:\n" +
                                                   "    D: default token filter\n" +
            		                               "    S: stanford token filter");
            // Print info
            System.out.println();
            System.out.println("Preprocess the document set to be a binary corpus.");
            System.out.println();
            System.out.println("SYNOPSIS");
            System.out.println();
            System.out.println("     java -jar TextSimLib.jar -D -dataset path -uni path [-outDir dir] [-sw path] [-ew path] [-tf DS]");
            System.out.println();
            System.out.println("IMPERATIVE Options");
            System.out.println();
            new HelpFormatter().printOptions(new PrintWriter(System.out, true), WIDTH,
                    new Options().addOption(options.getOption("dataset"))
                                 .addOption(options.getOption("uni")),
                    5, 4);
            System.out.println();
            System.out.println("OPTIONAL Options");
            System.out.println();
            new HelpFormatter().printOptions(new PrintWriter(System.out, true), WIDTH,
                    new Options().addOption(options.getOption("outFile"))
                                 .addOption(options.getOption("sw"))
                                 .addOption(options.getOption("ew"))
                                 .addOption(options.getOption("tf")),
                    5, 4);
            System.out.println();
        }
    }
    
    /**
     * Process the similarities and generate .graph file.
     * 
     * @param cmd      A tokenized command line input.
     * @param options  A defined command line options.
     * 
     * @throws Exception 
     */
    private static void proc(CommandLine cmd, Options options)
            throws Exception {
        
        if (cmd.hasOption("threads") && cmd.hasOption("dataset") && cmd.hasOption("outFile") && cmd.hasOption("tri")) {
            SMP_OneFile.process(new String[]{
                    cmd.getOptionValue("threads"),
                    cmd.getOptionValue("dataset"),
                    cmd.getOptionValue("outFile"),
                    cmd.getOptionValue("tri"),
                    cmd.hasOption("w1") ? cmd.getOptionValue("w1") : "1",
                    cmd.hasOption("w2") ? cmd.getOptionValue("w2") : "1"});
        } else {
            // Set descriptions
            options.getOption("threads").setDescription("the number of threads");
            options.getOption("dataset").setDescription("the directory of the original dataset");
            options.getOption("outFile").setDescription("the path of the output file");
            options.getOption("tri").setDescription("the path of the trigram file");
            options.getOption("w1").setDescription("the weight 1.\n" +
            		                               "if this option is not used, it will use default value 1");
            options.getOption("w2").setDescription("the weight 2.\n" +
            		                               "if this option is not used, it will use default value 1");
            // Print info
            System.out.println();
            System.out.println("Process the binary document, and generate matrix result (.graph file).");
            System.out.println();
            System.out.println("SYNOPSIS");
            System.out.println();
            System.out.println("     java -jar TextSimLib.jar -P -threads intValue -dataset path -outFile path -tri path");
            System.out.println();
            System.out.println("IMPERATIVE Options");
            System.out.println();
            new HelpFormatter().printOptions(new PrintWriter(System.out, true), WIDTH,
                    new Options().addOption(options.getOption("threads"))
                                 .addOption(options.getOption("dataset"))
                                 .addOption(options.getOption("outFile"))
                                 .addOption(options.getOption("tri")),
                    5, 4);
            System.out.println();
            System.out.println("OPTIONAL Options");
            System.out.println();
            new HelpFormatter().printOptions(new PrintWriter(System.out, true), WIDTH,
                    new Options().addOption(options.getOption("w1"))
                                 .addOption(options.getOption("w2")),
                    5, 4);
            System.out.println();
        }
    }

    /**
     * Calculate similarities for the words in the input file.
     * 
     * @param cmd      A tokenized command line input.
     * @param options  A defined command line options.
     * 
     * @throws Exception 
     */
    private static void procWordPairs(CommandLine cmd, Options options)
            throws Exception {
      if (cmd.hasOption("wordcsv") && cmd.hasOption("uni") && cmd.hasOption("tri")) {
        WordSim sim = new WordSim();
        sim.setInputPath(Paths.get(cmd.getOptionValue("wordcsv")));
        sim.setUnigramPath(Paths.get(cmd.getOptionValue("uni")));
        sim.setTrigramPath(Paths.get(cmd.getOptionValue("tri")));
        sim.generateOutputFile();
      }
    }

    /**
     * Print help information in the console.
     * 
     * @param options  A defined command line options.
     */
    private static void showHelp(Options options) {
        
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setLeftPadding(4);
        helpFormatter.setWidth(WIDTH);
        
        System.out.println();
        System.out.println("                                 Text Similarity Library");
        System.out.println();
        System.out.println("NAME");
        System.out.println();
        System.out.println("     TextSimLib.jar -- preprocess the corpus and calculate the test similarity");
        System.out.println();
        System.out.println("VERSION");
        System.out.println();
        System.out.println("      1.0 (21 June, 2013)");
        System.out.println();
        System.out.println("OPTIONS");
        System.out.println();
        helpFormatter.printOptions(
                new PrintWriter(System.out, true), WIDTH,
                new Options().addOptionGroup(options.getOptionGroup(options.getOption("N")))
                             .addOptionGroup(options.getOptionGroup(options.getOption("h"))),
                5, 4);
        System.out.println();
        
    }
    
    /**
     * Program entry.
     * 
     * @param args  command line parameters
     */
    public static void main(String[] args) {
        
        Options options = createOptions();
        CommandLineParser parser = new PosixParser();
        
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage() + ". Use -h option for help.");
            System.exit(-1);
        }
        
        try {
            makeChoice(cmd, options);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // For testing
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    
}
