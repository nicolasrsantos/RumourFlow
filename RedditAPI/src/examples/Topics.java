package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

public class Topics {
      public static String output = "";
	  public static String google1TFolder = "";
	  public static String idName = "";
	  public static String clusterResultUrl = "";
	  public static String contentFolder = "";
	  public static void main(String args[]){
		  
		  for (int i = 0; i < args.length; i++){
			  if (args[i].equals("-id-name")){
				  idName = args[i+1];
			  }
			  if (args[i].equals("-cluster-name")){
				  clusterResultUrl = args[i+1];
			  }
			  if (args[i].equals("-output")){
				  output = args[i+1];
			  }
			  if (args[i].equals("-google")){
				  google1TFolder = args[i+1];
			  }
			  if (args[i].equals("-content")){
				  contentFolder = args[i+1];
			  }
		  }
		  try {
			extractTopics(idName, clusterResultUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  public static Cloud extractTopics(String idName, String result) throws IOException 
	    {
	    	List<String> listFiles = new ArrayList<String>();
	    	List<Integer> listClusters = new ArrayList<Integer>();   	
	    	System.out.print(idName);
	        File file = new File(idName);
	        FileReader fr = new FileReader(file);
	        BufferedReader br = new BufferedReader(fr);
	        String line;
	        while((line = br.readLine()) != null){
	            listFiles.add(line);
	        }
	        br.close();
	        fr.close();
	        System.out.print(result);
	        file = new File(result);
	        fr = new FileReader(file);
	        br = new BufferedReader(fr);

	        while((line = br.readLine()) != null){
	        	try{
	        		listClusters.add(Integer.valueOf(line));
	        	}catch(Exception ex){
	        		
	        	}
	        	
	        }
	        br.close();
	        fr.close();
	        
	        //Tag Cloud
	        Cloud cloud = new Cloud();
	        cloud.setMaxWeight(38.0);
	        try {
	        	System.out.print("Loading summarizer...");
	        	System.out.print(google1TFolder);
				Summarizer sum = new Summarizer(new File(google1TFolder + "/googleGramCorpus.uni"), new File(google1TFolder + "/googleGramCorpus.tri"), null);
				 //sumarization
		        List<List<String>> topics = new ArrayList<List<String>>();
		        
		        for (int i=0;i< 5;i++){
		        	File fileCluster = new File(output + "/cluster_" + i + ".txt");
		        	List<String> text = new ArrayList<String>();
		        	for (int j=0;j<listClusters.size();j++){        		
		        		if (listClusters.get(j) == i){
		        			String content = readFile(listFiles.get(j), Charset.defaultCharset());
		        			if (!content.isEmpty()){
		        				try{
		        					String sumText = sum.summarize(content, 1);
			        				text.add(sumText);
				        			//Create new tag
				        			Tag tag = new Tag(sumText,sumText);
				        			cloud.addTag(tag);
				        			FileUtils.write(fileCluster, listFiles.get(j) + "-" + sumText + "\r\n", true);
		        				}catch(Exception ex){
		        					
		        				}
		        				
		        			}
		        			
		        			
		        		}
		        		
		        	}
		        	topics.add(text);
		        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	        return cloud;	        	       
	    }
	    
	    static String readFile(String path, Charset encoding) 
	    		{
	    			try{
	    				 System.out.print(contentFolder);
	    				byte[] encoded = Files.readAllBytes(Paths.get(contentFolder + path.split(",")[1]));
	  	    		  	return new String(encoded, encoding);
	    			}catch(Exception ex){
	    				return "";
	    			}
	    		  
	    		}
}
