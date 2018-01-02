package org.textsim.wordrt.proc;

import org.textsim.util.RtProcessor;

public interface WordRtProcessor
        extends RtProcessor {

    /**
     * Return the similarity of two given words.
     * 
     * @param w1  The id of one word.
     * @param w2  The id of another word.
     * 
     * @return The relatedness of two given word ids.
     */
    public double sim(int w1, int w2);
}