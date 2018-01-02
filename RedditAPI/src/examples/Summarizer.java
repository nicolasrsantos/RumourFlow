package examples;

import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.set.hash.THashSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import opennlp.tools.util.Span;

import org.textsim.exception.ProcessException;
import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;
import org.textsim.textrt.preproc.tokenizer.Tokenizer;

/**
 * The summarizer class summary the string into shorter length.
 * 
 * @author jmei
 */
public class Summarizer
{
    private Tokenizer tk;
    private StringSentencizer stt;
    private WordRtProcessor processor;
    private THashSet<String> stopwords;

    /**
     * Construct the Summarizer with pre-processed corpus and stop word file.
     *
     * @param  uniCorpus    Pre-processed unigram corpus file.
     * @param  triCorpus    Pre-processed trigram corpus file.
     * @param  stopFile     Stop word list file.
     * @throws IOException  If I/O issue occurs.
     * @throws ProcessException  If relatedness compuatation issue occurs.
     */
    public Summarizer(File uniCorpus, File triCorpus, File stopFile)
            throws IOException, ProcessException
    {
        tk = new PennTreeBankTokenizer();
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
        stt = new StringSentencizer();
        processor = new WordRtProcessor(uniCorpus, triCorpus);
        // Read stop words from stopword list.
        stopwords = new THashSet<String>();
        if (stopFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(stopFile))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    stopwords.add(line.trim());
                }
            }
        }
    }

    /**
     * Construct the Summarizer with similarity processor and stop words.
     *
     * @param  processor    A relatedness processor.
     * @param  stopFile     Stop word list file.
     * @throws IOException  If I/O issue occurs.
     * @throws ProcessException  If relatedness compuatation issue occurs.
     */
    public Summarizer(WordRtProcessor processor, File stopFile)
            throws IOException, ProcessException
    {
        tk = new PennTreeBankTokenizer();
        stt = new StringSentencizer();
        // Read stop words from stopword list.
        stopwords = new THashSet<String>();
        if (stopFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(stopFile))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    stopwords.add(line.trim());
                }
            }
        }
    }
    
    /**
     * Summarize the text. The summary has specific percentage of the original
     * length.
     * 
     * @param  text        A text.
     * @param  percentage  The percentage of the length the summary should be.
     * @return A summary.
     */
    public String summarize(String text, float percentage)
    {
        Sentence[] sentences = score(text);
        int num = (int) (sentences.length * percentage);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++)
            sb.append(sentences[i] + " ");
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
    
    /**
     * Summarize the text. The summary has specific number of sentences.
     * 
     * @param  text        A text.
     * @param  percentage  The percentage of the length the summary should be.
     * @return A summary.
     */
    public String summarize(String text, int num)
    {
        Sentence[] sentences = score(text);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++)
            sb.append(sentences[i] + " ");
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * Score the given text.
     *
     * @param  text     A text.
     * @param  percent  The percentage of the length of the summary to the
     *                  original text.
     * @return An array of scored sentences, which have been sorted descending
     *         order with their similarity score.
     */
    private Sentence[] score(String text)
    {
        // Collect all the distinct meaningful (non-stop) tokens.
        THashSet<String> tokenSet = new THashSet<String>();
        tk.tokenize(text);
        while (tk.hasMoreTokens()) {
            String curr = tk.nextToken();
            if (!stopwords.contains(curr))
                tokenSet.add(curr);
        }
        String[] tokens = tokenSet.toArray(new String[tokenSet.size()]);
        tokenSet = null;
        // Compute the inter-relatedness of the tokens.
        float[][] sims = new float[tokens.length][tokens.length];
        for (int i = 0; i < sims.length; i++) {
            for (int j = 0; j < i; j++) 
                sims[i][j] = sims[j][i];
            sims[i][i] = 1;
            for (int j = i + 1; j < sims[i].length; j++) 
                sims[i][j] = (float) processor.sim(tokens[i], tokens[j]);
        }
        // Compute the similarity value for each token and store in the hash map.
        TObjectFloatHashMap<String> scoreMap = new TObjectFloatHashMap<String>();
        for (int i = 0; i < sims.length; i++)
            scoreMap.put(tokens[i], score(sims[i]));
        tokens = null;
        sims = null;
        // Compute the similarity value for each sentence and store in the
        // parallel array. 
        Span[] spans = stt.sentencizePos(text);
        Sentence[] sentences = new Sentence[spans.length];
        for (int i = 0; i < spans.length; i++)
            sentences[i] = new Sentence(text, spans[i], score(spans[i].getCoveredText(text).toString(), scoreMap));
        Arrays.sort(sentences);
        return sentences;
    }
    
    /**
     * Score the given row in the matrix.
     * 
     * @param  row  The similarity scores of the row elements.
     * @return The similarity score of the row.
     */
    private float score(float[] row)
    {
        // Use the sum of mean and standard deviation as the score.
        return meanAddDev(row);
    }

    /**
     * Score the given sentence base on the similarity score of each word.
     * 
     * @param  sentence  A sentence.
     * @param  scoreMap  The hash map for word similarity score look up.
     * @return The similarity score of teh sentence.
     */
    private float score(String sentence, TObjectFloatHashMap<String> scoreMap)
    {
        // Collect all the distinct meaningful (non-stop) tokens.
        THashSet<String> tokenSet = new THashSet<String>();
        tk.tokenize(sentence);
        while (tk.hasMoreTokens()) {
            String curr = tk.nextToken();
            if (!stopwords.contains(curr))
                tokenSet.add(curr);
        }
        // Compute mean of scores of all the meaningful tokens and return as
        // sentence similarity.
        String[] tokens = tokenSet.toArray(new String[tokenSet.size()]);
        float[] scores = new float[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            scores[i] = scoreMap.get(tokens[i]);
        return meanAddDev(scores);
    }

    private float meanAddDev(float[] vals)
    {
        // Compute the mean and standard deviation for the similarities.
        float sum = 0;
        for (int i = 0; i < vals.length; i++)
            sum += vals[i];
        float mean = sum / vals.length;
        float dev = 0;
        for (int i = 0; i < vals.length; i++)
            dev += Math.pow(vals[i] - mean, 2);
        dev = (float) Math.sqrt(dev / vals.length);
        // Use the sum of mean and standard deviation as the score.
        return mean + dev;
    }

    /**
     * Sentence class stores the similarity information and reference to the
     * original text.
     * 
     * @author jmei
     */
    private class Sentence
            implements Comparable<Sentence>
    {
        public String text;
        private Span span;
        private float sim;

        public Sentence(String text, Span span, float sim)
        {
            this.text = text;
            this.span = span;
            this.sim = sim;
        }
        
        @Override
        public String toString()
        {
            return span.getCoveredText(text).toString();
        }

        @Override
        public int compareTo(Sentence other) {
            return (other.sim - sim > 0 ? 1 : -1);
        }
    }
}
