
package org.textsim.textrt.proc.singlethread;


import java.io.IOException;
import java.nio.file.Path;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.wordrt.proc.HashedWordRtProcessor;

/**
* The text similariy between two texts
* First step: construct two lists reading words from two input processed texts
* Second step: get rid of match words in two files and count the num of same words
* Third step: construct a words matrix with words relatedness between two words;
* calculate the mean of rt of the each line, which is not less than the sum of mean and sd of the line;
* sum all the mean value of each line;
* text similarity = (sumofmean +numofsamewords)*(wordsoffile1+wordsoffile2)/2*wordsoffile1*wordsoffile2 
* @author Vivian
*
*/

public class SinglethreadTextRtProcessor_Hash {

	 private double textrt;
	 public HashedWordRtProcessor RTCollection;
	/**
	 *null constructor: initialize text similarity
	 */
	public SinglethreadTextRtProcessor_Hash()
	{
		textrt = 0.0;
		RTCollection = null;
		
	}
	/**
	 * constructor: initialize text similarity and load trigram file into memory
	 * @param trigramFile  the name of trigram file with word relatedness
	 * @param trigramNum   the number of pairs in trigram file
	 * @param unigramNum   the number of words in unigram file
	 * @throws IOException
	 */
	public SinglethreadTextRtProcessor_Hash(Path binaryFile) throws IOException
	{
		textrt = 0.0;
		loadBinaryTrigram(binaryFile);

	}
	
	/**
	 * load Trigram into memory to find word relatedness
	 * @param trigramFile  the name of trigram file with word relatedness
	 * @param trigramNum   the number of pairs in trigram file
	 * @param unigramNum   the number of words in unigram file
	 * @throws IOException 
	 */
	public void loadBinaryTrigram(Path binaryFile) throws IOException{
		
		RTCollection = new HashedWordRtProcessor(binaryFile);
		
	}
	/**
	 * compute the text similarity between two texts
	 * @param textA      the name of first text
	 * @param textB      the name of second text
	 * @param numofSame  the amount of same words between two texts
	 * @return text similarity
	 * @throws IOException
	 */
	public double computeTextRT(TextInstance textA, TextInstance textB) throws IOException
	{
		textrt = 0.0;
		textA.compareTexts(textB);
        int numofSame = textA.getSameword();
		double sumofMean = 0.0;
		int sizeA = textA.getCont().size() + textA.getRej().size();
		int sizeB = textB.getCont().size() + textB.getRej().size();
		
		if(sizeA <= sizeB){
			sumofMean = computesumofmean(textA,textB);
		}else {
			sumofMean = computesumofmean(textB,textA);
		}
					
		double numerator = (numofSame+sumofMean)*(textA.getTotalcount()+textB.getTotalcount());
		double denomenator = 2*textA.getTotalcount()*textB.getTotalcount();
		textrt = numerator/denomenator;
	    return  textrt;
	}

	/**
	 * compute the sum of mean of each line
	 * @param textA
	 * @param textB
	 * @param rejtextBmap
	 * @return sum of mean of each line
	 */
	private double computesumofmean(TextInstance textA, TextInstance textB)
	{
		int[] idKeyA = textA.getCont().keySet().toArray();
		int[] idKeyB = textB.getCont().keySet().toArray();
		int[] rejtextBlist = textB.getRej().values();
		
		double[] line;
		int numofrt , numofoneline, numofrejword = 0;
		double sumofmean=0.0;
		double sumofallrt, meanofline, dev, stddev, sumofrt, meanofrt, rt = 0;
		double refVal = 0;
		
		if(RTCollection == null){
			throw new NullPointerException("Null table exception"); 
		}
		
		
		for(int i=0; i<rejtextBlist.length;i++)
			numofrejword += rejtextBlist[i];
		
		for(int i=0; i<idKeyA.length; i++)
		{
			line = new double[idKeyB.length];// an array stores words relatedness of each line
			numofoneline=0;
	 		sumofallrt = 0; 
	 		dev = 0;
	 		stddev = 0;
	 		numofrt = 0;
	 		sumofrt = 0;
	 		meanofrt = 0;
	 		
	 		
			for(int g=0; g<idKeyB.length; g++)
			{
				/**
				 * Modified : 
				 * Adding refVal to avoid multiple lookup
				 */
				
				refVal = textB.getCont().get(idKeyB[g]);
				
				rt = RTCollection.sim(idKeyA[i] , idKeyB[g]);  
			//	System.out.println(idKeyA[i]+ " "+ idKeyB[g]+" "+rt);
				numofoneline += refVal;
				line[g]= rt;
				sumofallrt += rt*refVal;	
				
//				numofoneline += textB.getCont().get(idKeyB[g]);
//				line[g]= rt;
//				sumofallrt += rt*textB.getCont().get(idKeyB[g]);
				
			}
			
			meanofline = sumofallrt/(numofoneline+numofrejword);

			
			for(int g=0; g<line.length; g++)
			{
				
				dev += (line[g]-meanofline)*(line[g]-meanofline)*textB.getCont().get(idKeyB[g]);
			}
			
			stddev = Math.sqrt((dev+meanofline*meanofline*numofrejword)/(numofrejword+numofoneline));
			
			for(int g=0; g<line.length; g++)
			{   
				if(line[g]>=(stddev+meanofline))
				{
					sumofrt+=line[g]*textB.getCont().get(idKeyB[g]);
					
					numofrt+=textB.getCont().get(idKeyB[g]);
				}
			}
			
			if(numofrt==0)
				meanofrt=0;
			else
				meanofrt=sumofrt/numofrt;
			
			sumofmean+=meanofrt*textA.getCont().get(idKeyA[i]);
			
		}
	
		return sumofmean;
	}
}


