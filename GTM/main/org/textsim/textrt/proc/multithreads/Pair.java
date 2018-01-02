package org.textsim.textrt.proc.multithreads;
/**
 * Pair class stores the FileID of two texts
 * @author Vivian
 * @since 10 June, 2013
 */
class Pair {
	
	private int textAID, textBID;

	public Pair( int textAID, int textBID)
	{
		this.textAID = textAID;
		this.textBID = textBID;
	}
	public void setID (int textAID, int textBID)
	{
		this.textAID = textAID;
		this.textBID = textBID;
	}
	
	public int getA()
	{
		return textAID;
	}
	
	public int getB()
	{
		return textBID;
	}
}
