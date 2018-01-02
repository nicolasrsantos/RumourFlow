package examples;

import java.io.File;
import java.io.IOException;

import org.textsim.exception.ProcessException;
import org.textsim.util.Unigram;
import org.textsim.wordrt.preproc.WordrtPreproc;
import org.textsim.wordrt.proc.DefaultWordRtProcessor;

import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * The word relatedness processor class computes the semantic relatedness
 * between a word pair using Google Trigram word relatedness measure.
 *
 * @author Jie Mei
 *
 * @see org.textsim.wordrt.preproc.WordrtPreproc
 */
public class WordRtProcessor
{
    private DefaultWordRtProcessor wordProc;
    private TObjectIntHashMap<String> idMap;

    public WordRtProcessor(File uniCorpus, File triCorpus)
            throws IOException, ProcessException
    {
        idMap = Unigram.readIDMap(WordrtPreproc.BINARY, new File[]{uniCorpus});
        wordProc = new DefaultWordRtProcessor(triCorpus);
    }

    public double sim(String word1, String word2)
    {
        return wordProc.sim(idMap.get(word1), idMap.get(word2));
    }
}
