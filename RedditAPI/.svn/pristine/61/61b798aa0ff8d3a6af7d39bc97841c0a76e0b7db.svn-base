package com.github.jreddit.memes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class GraphJSON {
	public static void main(String args[]) {
		int n = Integer.valueOf(args[0]);
		String filename1 = "";
		String filename2 = "";
		String filename3 = "";
		String filename4 = "";
		String filename5 = "";
		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("-filename1")) {
				filename1 = args[i + 1];
			}
			if (args[i].equals("-filename2")) {
				filename2 = args[i + 1];
			}
			if (args[i].equals("-filename3")) {
				filename3 = args[i + 1];
			}
			if (args[i].equals("-filename4")) {
				filename4 = args[i + 1];
			}
			if (args[i].equals("-filename5")) {
				filename5 = args[i + 1];
			}
		}
		// max
		File file1 = new File(filename1);
		File file2 = new File(filename2);
		
		FileReader fr1;
		FileReader fr2;
		FileReader fr3;
		try {
			fr1 = new FileReader(file1);
			fr2 = new FileReader(file2);
			
			BufferedReader br1 = new BufferedReader(fr1);
			BufferedReader br2 = new BufferedReader(fr2);
			
			String line1 = "";
			String line2 = "";
			Gson gson = new Gson();

			// read all ids
			List<String> listIds = new ArrayList<String>();
			while ((line2 = br2.readLine()) != null) {
				listIds.add(line2.split(",")[1]);
			}
			
			
			//is misclustered?
			Map<String, String> listClusters = new HashMap<String,String>();
			for (int i=0;i<5;i++){
				File file3 = new File(filename5 + "/" + "cluster_" + i + ".txt");
				fr3 = new FileReader(file3);
				BufferedReader br3 = new BufferedReader(fr3);
				String line3 = "";
				
				while ((line3 = br3.readLine()) != null) {
					if (i == 0 && line3.contains("Trayvon Martin"))
					{
						listClusters.put(line3.split(",")[1], "true");
						continue;
					}
					if (i == 1 && line3.contains("EBOLA"))
					{	
						listClusters.put(line3.split(",")[1], "true");
						continue;
					}
					if (i == 2 && line3.contains("Ferguson"))
					{
						listClusters.put(line3.split(",")[1], "true");
						continue;
					}
					if (i == 3 && line3.contains("ISIS"))
					{
						listClusters.put(line3.split(",")[1], "true");
						continue;
					}
					if (i == 4 && line3.contains("Obama"))
					{
						listClusters.put(line3.split(",")[1], "true");
						continue;
					}
					listClusters.put(line3.split(",")[1], "false");
				}	
			}

			int ind = 0;
			List<Link> listLinks = new ArrayList<Link>();
			List<Nodes> listNodes = new ArrayList<Nodes>();
			while ((line1 = br1.readLine()) != null) {
				if (line1.length() <= 10) {
					// FileUtils.writeStringToFile(file3, line1 + "\r\n", true);
				} else {
					String[] list1 = null;
					list1 = line1.split(" ");
					Map<Integer, Double> weightList = new HashMap<Integer, Double>();
					// sort list1
					for (int j = 0; j < list1.length; j++) {
						weightList.put(j, Double.valueOf(list1[j]));
					}
					// sort
					Map<Integer, Double> sortMap = sortByValue(weightList);
					
					// read title
					File file4 = new File(filename4 + "/titles/all_titles/"
							+ listIds.get(ind));
					String title = FileUtils.readFileToString(file4);
					
					
					// get top 5 related submissions
					List<Integer> ints = new ArrayList<Integer>();
					
					 for (Map.Entry<Integer, Double> entry : sortMap.entrySet())
				        {
						 	if (entry.getValue() == 0.0){
						 		continue;
						 	}
						 	Link link1 = new Link(ind,entry.getKey(), entry.getValue());
						 	listLinks.add(link1);
				            ints.add(entry.getKey());
				            if (ints.size() == 3){
				            	break;
				            }
				        }					
					String keyword = listIds.get(ind).split("_")[1];
					String redditID = listIds.get(ind).split("_")[0];
					
					String color = "";
					switch (keyword){
						case "Obama": 
							color = "red";
							break;
						case "EBOLA": 
							color = "green";
							break;
						case "Ferguson": 
							color = "sandybrown";
							break;
						case "ISIS": 
							color = "deepskyblue";
							break;
						case "Trayvon Martin": 
							color = "slategrey";
							break;
					}
					Nodes node1 = new Nodes(ind, ints, 7.0, 7,
							title, title,
							ind,color,redditID,listClusters.get(listIds.get(ind++)),null, 0.0,0.0,"N/A");

					listNodes.add(node1);

					
				}
			}
			MemesJSON memes = new MemesJSON(listNodes, listLinks,null);
			String json = gson.toJson(memes);
			File file3 = new File(filename3);
			FileUtils.writeStringToFile(file3, json, false);

			br1.close();
			br2.close();
			fr1.close();
			fr2.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
