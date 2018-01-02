package org.textsim.textrt.proc.singlethread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.textsim.wordrt.proc.DefaultWordRtProcessor;
import org.textsim.wordrt.proc.WordRtProcessor;

public class SinglethreadTextRtProcessor
        extends AbstractTextRtProcessor {

    public WordRtProcessor RTCollection;
    
    public SinglethreadTextRtProcessor(WordRtProcessor wordrtProcessor) {
        this.RTCollection = wordrtProcessor;
    }

    public SinglethreadTextRtProcessor(String binaryFile)
            throws IOException {
        this(new File(binaryFile));
    }

    public SinglethreadTextRtProcessor(File binaryFile)
            throws IOException {
        loadBinaryTrigram(binaryFile);
    }

    /**
     * constructor: initialize text similarity and load trigram file into memory
     * @param trigramFile  the name of trigram file with word relatedness
     * @param trigramNum   the number of pairs in trigram file
     * @param unigramNum   the number of words in unigram file
     * @throws IOException
     */
    public SinglethreadTextRtProcessor(Path binaryFile)
            throws IOException {
        this(binaryFile.toFile());
    }
    
    /**
     * load Trigram into memory to find word relatedness
     * @param trigramFile  the name of trigram file with word relatedness
     * @param trigramNum   the number of pairs in trigram file
     * @param unigramNum   the number of words in unigram file
     * @throws IOException 
     */
    private void loadBinaryTrigram(File binaryFile)
            throws IOException {
        
        RTCollection = new DefaultWordRtProcessor(binaryFile);
    }
    
    @Deprecated
    public double compute(TextInstance textA, TextInstance textB, float w1, float w2)
            throws IOException {
        
        return computeTextRT(textA.deepClone(), textB.deepClone(), w1, w2);
    }

    @Deprecated
    public double computeTextRT(TextInstance textA, TextInstance textB) {

        return computeTextRT(textA.deepClone(), textB.deepClone(), 1, 1);
    }
    
    /**
     * compute the text similarity between two texts
     * @param textA      the name of first text
     * @param textB      the name of second text
     * @param numofSame  the amount of same words between two texts
     * @return text similarity
     * @throws IOException
     */
    @Deprecated
    public double computeTextRT(TextInstance textA, TextInstance textB, float w1, float w2) {

        double textrt = 0.0;
        textA.compareTexts(textB);
        int numofSame = textA.getSameword();
        double sumofMean = 0.0;
        int sizeA = textA.getCont().size() + textA.getRej().size();
        int sizeB = textB.getCont().size() + textB.getRej().size();
        
        if(sizeA <= sizeB) {
            sumofMean = computesumofmean(textA,textB);
        } else {
            sumofMean = computesumofmean(textB,textA);
        }
        double numerator = (w1 * numofSame + w2 * sumofMean) * (textA.getTotalcount() + textB.getTotalcount());
        double denomenator = 2 * textA.getTotalcount() * textB.getTotalcount();
        textrt = numerator / denomenator;
        return  textrt;
    }

    /**
     * compute the text similarity between two texts and return the output as a SplitResult
     * @param textA      the name of first text
     * @param textB      the name of second text
     * @param numofSame  the amount of same words between two texts
     * @return text similarity
     * @throws IOException
     */
    @Deprecated
    public SplitResult computeTextRTSplit(TextInstance text1, TextInstance text2) {
        if ((text1.getTotalcount() == 0) && (text2.getTotalcount() == 0)) {
          // Both files are empty => Similarity is 1.0
          return new SplitResult(0.0, 0.0);
        } else if ((text1.getTotalcount() == 0) || (text2.getTotalcount() == 0)) {
          // Only one file is empty => Similarity is 0
          return new SplitResult(0.0, 0.0);
        }

        TextInstance textA = text1.deepClone();
        TextInstance textB = text2.deepClone();
            
        textA.compareTexts(textB);
        int numofSame = textA.getSameword();
        double sumofMean = 0.0;
        int sizeA = textA.getCont().size() + textA.getRej().size();
        int sizeB = textB.getCont().size() + textB.getRej().size();
        
        if(sizeA <= sizeB) {
            sumofMean = computesumofmean(textA,textB);
        } else {
            sumofMean = computesumofmean(textB,textA);
        }

        SplitResult out = new SplitResult();
        
        double numerator1 = numofSame * (textA.getTotalcount() + textB.getTotalcount());
        double numerator2 = sumofMean * (textA.getTotalcount() + textB.getTotalcount());
        
        double denominator = 2 * textA.getTotalcount() * textB.getTotalcount();
                
        out.resultA = numerator1/denominator;
        out.resultB = numerator2/denominator;
        
        return out;
    }
    
    /**
     * compute the sum of mean of each line
     * @param textA
     * @param textB
     * @param rejtextBmap
     * @return sum of mean of each line
     */
    private double computesumofmean(TextInstance textA, TextInstance textB) {

//        int[] idKeyA = textA.getCont().keySet().toArray();
//        int[] idKeyB = textB.getCont().keySet().toArray();
        int[] idKeyA = textA.getCont().keys();
        int[] idKeyB = textB.getCont().keys();
        int[] rejtextBlist = textB.getRej().values();
        
        double[] line;
        int numofrt , numofoneline, numofrejword = 0;
        double sumofmean=0.0;
        double sumofallrt, meanofline, dev, stddev, sumofrt, meanofrt, rt = 0;
        double refVal = 0;
        
        if (RTCollection == null) {
            throw new NullPointerException("Null table exception"); 
        }
        
        //Sum all of the occurrences of all the rejwords
        for (int i = 0; i < rejtextBlist.length; i++) {
            numofrejword += rejtextBlist[i];
        }
        //For each word in text A
        for (int i = 0; i < idKeyA.length; i++) {
            line = new double[idKeyB.length];// an array stores words relatedness of each line
            numofoneline = 0;
            sumofallrt = 0; 
            dev = 0;
            stddev = 0;
            numofrt = 0;
            sumofrt = 0;
            meanofrt = 0;
            //For each word in text B
            for (int g=0; g < idKeyB.length; g++) {
                /**
                 * Modified : 
                 * Adding refVal to avoid multiple lookup
                 * refVal is how many time the word appears
                 */
                int tmp = idKeyB[g];
                refVal = textB.getCont().get(tmp);
                rt = RTCollection.sim(idKeyA[i], idKeyB[g]);  
                numofoneline += refVal;
                line[g]= rt;
                sumofallrt += rt*refVal;    
            }
            
            meanofline = sumofallrt/(numofoneline+numofrejword);
            
            for (int g = 0; g < line.length; g++) {
                int tmp = idKeyB[g];
                dev += (line[g] - meanofline) * (line[g] - meanofline) * textB.getCont().get(tmp);
            }
            
            stddev = Math.sqrt((dev+meanofline * meanofline * numofrejword)/(numofrejword + numofoneline));
            
            //For length of idKeyB
            for (int g = 0; g < line.length; g++) {   
                if (line[g] >= stddev + meanofline) {
                        int tmp = idKeyB[g];
                    sumofrt += line[g] * textB.getCont().get(tmp);
                    numofrt += textB.getCont().get(tmp);
                }
            }
            if(numofrt == 0) {
                meanofrt = 0;
            } else {
                meanofrt = sumofrt / numofrt; 
            }
            sumofmean += meanofrt * textA.getCont().get(idKeyA[i]);
        }
        return sumofmean;
    }

    @Override
    public double sim(TextInstance t1, TextInstance t2) {
        return computeTextRT(t1, t2);
    }

    @Override
    @Deprecated
    public SplitResult simSp(TextInstance t1, TextInstance t2) {
        return computeTextRTSplit(t1, t2);
    }

}