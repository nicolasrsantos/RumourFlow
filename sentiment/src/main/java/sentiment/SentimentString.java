package sentiment;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Generics;

public class SentimentString {
	 public static void main(String[] args){
	 try {
		 String sen = getSentimentString("No, they're not using what you might call a normal/'wild type' HIV virus. However the genetically-modified, therapeutic virus vector is derived from HIV. Same for the measles-based vector, etc.Having said that, there are recorded cases where other viral infections have been associated with cancer remission, but that's not what these headlines are talking about (and I don't believe it's ever been recorded with HIV).");
		 System.out.println(sen);
	 } catch (IOException e) {
	 // TODO Auto-generated catch block
	 e.printStackTrace();
	 }
	 }

	private static final NumberFormat NF = new DecimalFormat("0.0000");

	static enum Output {
		PENNTREES, VECTORS, ROOT, PROBABILITIES
	}

	static enum Input {
		TEXT
	}

	private SentimentString() {
	} // static methods

	/**
	 * Sets the labels on the tree (except the leaves) to be the integer value
	 * of the sentiment prediction. Makes it easy to print out with
	 * Tree.toString()
	 */
	static void setSentimentLabels(Tree tree) {
		if (tree.isLeaf()) {
			return;
		}

		for (Tree child : tree.children()) {
			setSentimentLabels(child);
		}

		Label label = tree.label();
		if (!(label instanceof CoreLabel)) {
			throw new IllegalArgumentException("Required a tree with CoreLabels");
		}
		CoreLabel cl = (CoreLabel) label;
		cl.setValue(Integer.toString(RNNCoreAnnotations.getPredictedClass(tree)));
	}

	/**
	 * Sets the labels on the tree to be the indices of the nodes. Starts
	 * counting at the root and does a postorder traversal.
	 */
	static int setIndexLabels(Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		tree.label().setValue(Integer.toString(index));
		index++;
		for (Tree child : tree.children()) {
			index = setIndexLabels(child, index);
		}
		return index;
	}

	/**
	 * Outputs the vectors from the tree. Counts the tree nodes the same as
	 * setIndexLabels.
	 */
	static int outputTreeVectors(PrintStream out, Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		out.print("  " + index + ":");
		SimpleMatrix vector = RNNCoreAnnotations.getNodeVector(tree);
		for (int i = 0; i < vector.getNumElements(); ++i) {
			out.print("  " + NF.format(vector.get(i)));
		}
		out.println();
		index++;
		for (Tree child : tree.children()) {
			index = outputTreeVectors(out, child, index);
		}
		return index;
	}

	/**
	 * Outputs the scores from the tree. Counts the tree nodes the same as
	 * setIndexLabels.
	 */
	static int outputTreeScores(PrintStream out, Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		out.print("  " + index + ":");
		SimpleMatrix vector = RNNCoreAnnotations.getPredictions(tree);
		for (int i = 0; i < vector.getNumElements(); ++i) {
			out.print("  " + NF.format(vector.get(i)));
		}
		out.println();
		index++;
		for (Tree child : tree.children()) {
			index = outputTreeScores(out, child, index);
		}
		return index;
	}

	/**
	 * Outputs a tree using the output style requested
	 */
	static void outputTree(PrintStream out, CoreMap sentence, List<Output> outputFormats) {
		Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
		for (Output output : outputFormats) {
			switch (output) {
			case PENNTREES: {
				Tree copy = tree.deepCopy();
				setSentimentLabels(copy);
				out.println(copy);
				break;
			}
			case VECTORS: {
				Tree copy = tree.deepCopy();
				setIndexLabels(copy, 0);
				out.println(copy);
				outputTreeVectors(out, tree, 0);
				break;
			}
			case ROOT: {
				out.println("  " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));
				break;
			}
			case PROBABILITIES: {
				Tree copy = tree.deepCopy();
				setIndexLabels(copy, 0);
				out.println(copy);
				outputTreeScores(out, tree, 0);
				break;
			}
			default:
				throw new IllegalArgumentException("Unknown output format " + output);
			}
		}
	}

	static final String DEFAULT_TLPP_CLASS = "edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams";

	public static void help() {
		System.err.println("Known command line arguments:");
		System.err.println("  -sentimentModel <model>: Which model to use");
		System.err.println("  -parserModel <model>: Which parser to use");
		System.err.println("  -file <filename>: Which file to process");
		System.err.println(
				"  -fileList <file>,<file>,...: Comma separated list of files to process.  Output goes to file.out");
		System.err.println("  -stdin: Process stdin instead of a file");
		System.err.println(
				"  -input <format>: Which format to input, TEXT or TREES.  Will not process stdin as trees.  If trees are not already binarized, they will be binarized with -tlppClass's headfinder, which means they must have labels in that treebank's tagset.");
		System.err.println(
				"  -output <format>: Which format to output, PENNTREES, VECTORS, PROBABILITIES, or ROOT.  Multiple formats can be specified as a comma separated list.");
		System.err.println(
				"  -filterUnknown: remove unknown trees from the input.  Only applies to TREES input, in which case the trees must be binarized with sentiment labels");
		System.err.println(
				"  -tlppClass: a class to use for building the binarizer if using non-binarized TREES as input.  Defaults to "
						+ DEFAULT_TLPP_CLASS);
	}

	/**
	 * Reads an annotation from the given filename using the requested input.
	 */
	public static List<Annotation> getAnnotations(StanfordCoreNLP tokenizer, Input inputFormat, String str,
			boolean filterUnknown) {
		switch (inputFormat) {
		case TEXT: {
			String text = str;
			Annotation annotation = new Annotation(text);
			tokenizer.annotate(annotation);
			List<Annotation> annotations = Generics.newArrayList();
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Annotation nextAnnotation = new Annotation(sentence.get(CoreAnnotations.TextAnnotation.class));
				nextAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
				annotations.add(nextAnnotation);
			}
			return annotations;
		}		
		default:
			throw new IllegalArgumentException("Unknown format " + inputFormat);
		}
	}

	public static String getSentiment(String[] args) throws IOException {
		String parserModel = null;
		String sentimentModel = null;

		String filename = null;
		String fileList = null;
		boolean stdin = false;

		boolean filterUnknown = false;

		List<Output> outputFormats = Collections.singletonList(Output.ROOT);
		Input inputFormat = Input.TEXT;

		String tlppClass = DEFAULT_TLPP_CLASS;

		for (int argIndex = 0; argIndex < args.length;) {
			if (args[argIndex].equalsIgnoreCase("-sentimentModel")) {
				sentimentModel = args[argIndex + 1];
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-parserModel")) {
				parserModel = args[argIndex + 1];
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-file")) {
				filename = args[argIndex + 1];
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-fileList")) {
				fileList = args[argIndex + 1];
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-stdin")) {
				stdin = true;
				argIndex++;
			} else if (args[argIndex].equalsIgnoreCase("-input")) {
				inputFormat = Input.valueOf(args[argIndex + 1].toUpperCase());
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-output")) {
				String[] formats = args[argIndex + 1].split(",");
				outputFormats = new ArrayList<Output>();
				for (String format : formats) {
					outputFormats.add(Output.valueOf(format.toUpperCase()));
				}
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-filterUnknown")) {
				filterUnknown = true;
				argIndex++;
			} else if (args[argIndex].equalsIgnoreCase("-tlppClass")) {
				tlppClass = args[argIndex + 1];
				argIndex += 2;
			} else if (args[argIndex].equalsIgnoreCase("-help")) {
				help();
				System.exit(0);
			} else {
				System.err.println("Unknown argument " + args[argIndex + 1]);
				help();
				throw new IllegalArgumentException("Unknown argument " + args[argIndex + 1]);
			}
		}

		// We construct two pipelines. One handles tokenization, if
		// necessary. The other takes tokenized sentences and converts
		// them to sentiment trees.
		Properties pipelineProps = new Properties();
		Properties tokenizerProps = null;
		if (sentimentModel != null) {
			pipelineProps.setProperty("sentiment.model", sentimentModel);
		}
		if (parserModel != null) {
			pipelineProps.setProperty("parse.model", parserModel);
		}
		if (stdin) {
			pipelineProps.setProperty("ssplit.eolonly", "true");
		}
		
			pipelineProps.setProperty("annotators", "parse, sentiment");
			pipelineProps.setProperty("enforceRequirements", "false");
			tokenizerProps = new Properties();
			tokenizerProps.setProperty("annotators", "tokenize, ssplit");
		

		int count = 0;
		if (filename != null)
			count++;
		if (fileList != null)
			count++;
		if (stdin)
			count++;
		if (count > 1) {
			throw new IllegalArgumentException("Please only specify one of -file, -fileList or -stdin");
		}
		if (count == 0) {
			throw new IllegalArgumentException("Please specify either -file, -fileList or -stdin");
		}

		StanfordCoreNLP tokenizer = (tokenizerProps == null) ? null : new StanfordCoreNLP(tokenizerProps);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(pipelineProps);

		if (filename != null) {
			// Process a file. The pipeline will do tokenization, which
			// means it will split it into sentences as best as possible
			// with the tokenizer.
			List<Annotation> annotations = getAnnotations(tokenizer, inputFormat, filename, filterUnknown);
			for (Annotation annotation : annotations) {
				pipeline.annotate(annotation);

				for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
					System.out.println(sentence);
					return "  " + sentence.get(SentimentCoreAnnotations.SentimentClass.class);
				}

			}
		}

		return "";
	}
	private static StanfordCoreNLP tokenizer;
	private static StanfordCoreNLP pipeline;
	private static Input inputFormat;
	static {
		boolean stdin = false;

		inputFormat = Input.TEXT;

		// We construct two pipelines. One handles tokenization, if
		// necessary. The other takes tokenized sentences and converts
		// them to sentiment trees.
		Properties pipelineProps = new Properties();
		Properties tokenizerProps = null;
		
		if (stdin) {
			pipelineProps.setProperty("ssplit.eolonly", "true");
		}
		
		
		pipelineProps.setProperty("annotators", "parse, sentiment");
		pipelineProps.setProperty("enforceRequirements", "false");
		tokenizerProps = new Properties();
		tokenizerProps.setProperty("annotators", "tokenize, ssplit");
		
		

	tokenizer = (tokenizerProps == null) ? null : new StanfordCoreNLP(tokenizerProps);
	pipeline = new StanfordCoreNLP(pipelineProps);
	}
	public static String getSentimentString(String str) throws IOException {
		

		// Process a file. The pipeline will do tokenization, which
		// means it will split it into sentences as best as possible
		// with the tokenizer.
		List<Annotation> annotations = getAnnotations(tokenizer, inputFormat, str, false);
		for (Annotation annotation : annotations) {
			pipeline.annotate(annotation);

			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				return sentence.get(SentimentCoreAnnotations.SentimentClass.class).toString();
			}

		}


		return "";
	}

}
