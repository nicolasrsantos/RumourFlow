package examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

/**
 * The string sentencizer class allows an application to break a string into
 * sentence. This class detect a punctuation character marks the end of a
 * sentence or not. In this sense a sentence is defined as the longest white
 * space trimmed character sequence between two punctuation marks. The first and
 * last sentence make an exception to this rule. The first non whitespace
 * character is assumed to be the begin of a sentence, and the last non
 * whitespace character is assumed to be a sentence end. 
 *
 * The implementation of this class is based on SentenceDetectorME in OpenNLP.
 * 
 * @author Jie Mei
 * 
 * @see opennlp.tools.sentdetect.SentenceDetectorME
 * @see opennlp.tools.sentdetect.SentenceModel
 */
public class StringSentencizer
{
    /**
     * The model file used by sentence detector. It is a pre-trained sentence
     * detector model provided for OpenNLP 2.5 series.
     *
     * @see <a href="http://opennlp.sourceforge.net/models-1.5/">http://opennlp.sourceforge.net/models-1.5/</a>
     */
    private final String MODEL_FILE = "./en-sent.bin";

    /**
     * The underlying sentence detector used for detecting sentences.
     */
    private SentenceDetectorME detector;

    /**
     * Construct the string sentencizer.
     * 
     * @throws IOException  When I/O issue occurs.
     */
    public StringSentencizer()
            throws IOException
    {
        try (InputStream model = new FileInputStream(MODEL_FILE)) {
        	String currentDir = System.getProperty("user.dir");
            System.out.println("Current dir using System:" +currentDir);
            detector = new SentenceDetectorME(new SentenceModel(model));
        } catch (Exception e) {
            throw new RuntimeException("Model file not exist in " + MODEL_FILE);
        }
    }

    /**
     * Break the given string into array of sentences.
     *
     * @param  str  A string.
     * @return An array of sentences detected in the given string, ordered by
     *         their occurrence.
     */
    public String[] sentencize(String str)
    {
        return detector.sentDetect(str);
    }

    /**
     * Break the given string into array of spans.
     *
     * @param  str  A string.
     * @return An array of spens detected in the given string, ordered by their
     *         occurrence.
     */
    public Span[] sentencizePos(String str)
    {
        return detector.sentPosDetect(str);
    }
}
