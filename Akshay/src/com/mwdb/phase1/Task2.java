package com.mwdb.phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Task2 {

	
	static HashMap<String, String> pmatrix= new HashMap<String, String>();
	public static void execute(String wordFilesFolderName,String avgFilesFolderName,String diffFilesFolderName,String LocationFilePath, double a) {
		
		File graph = new File(LocationFilePath);
		final File folder = new File(wordFilesFolderName);
		computeLocationMatrix(graph);

		 File avgfolder = new File(avgFilesFolderName);
		 File difffolder = new File(diffFilesFolderName);
		 avgfolder.mkdirs();
		 difffolder.mkdirs();
		
		for( File file : folder.listFiles()){
		
		String line;
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(file));
				HashMap<String,ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
			 
				while ((line = br.readLine()) != null) { 
					String[] temp = line.split(",");
					
						String s =temp[0]+"-"+temp[1];
					if(!data.containsKey(s))
					{ ArrayList<String> l = new ArrayList<String>();
					l.add(temp[2]+getString(temp));
					data.put(s,l);
					}
					else{
						ArrayList<String> l = data.get(s);
						l.add(temp[2]+getString(temp));
						data.put(s, l);
						}
					
				}
				br.close();
	           
			 
			 br = new BufferedReader(new FileReader(file));
			 String avgfile = avgFilesFolderName+"/"+file.getName()+"_avg" ;
			 String difffile = diffFilesFolderName+"/"+file.getName()+"_diff" ;
			 
			 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(avgfile)));
			 PrintWriter outt = new PrintWriter(new BufferedWriter(new FileWriter(difffile)));
			
			 String[] vals;
			 ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
			ArrayList<Double> currentRow;
			//int rowcount = 0;
			 while ((line = br.readLine()) != null) {
				// rowcount++;
				 String[] temp = line.split(",");
				 String p = pmatrix.get(temp[1]);
				 currentRow = getCurrentRow(temp);
				 if(p!=null){
				 String[] proxStates = p.split(",");
				// double sum = computeSum(temp);
				
				 
				 for(int i=0; i< proxStates.length;i++){
					 ArrayList<String> l = data.get(temp[0]+"-"+proxStates[i]);
					 if(l!=null){
						
					 for(int j=0;j<l.size();j++){
						 vals = l.get(j).split(",");
						 if(temp[2].equalsIgnoreCase(vals[0])){
					//		 sum += Double.parseDouble(vals[2]);
							 {
						           rows.add(getList(vals));	
						           break;
							 }
							 
						 }
					 }
					
					 }
						 
				 }}
				 ArrayList<Double> avgVector =null;
				 if(rows.size()!=0){
					 avgVector =	calcAvg(rows);
				 }
				 
				 rows.clear();
				 ArrayList<Double> result = ComputeWinAvg(currentRow,avgVector,a); 
			   out.println(temp[0]+","+temp[1]+","+temp[2]+","+ArrayListDoubleToString(result));
			     
			   ArrayList<Double> result_diff = ComputeWinDiff(currentRow,avgVector,a);
			   outt.println(temp[0]+","+temp[1]+","+temp[2]+","+ArrayListDoubleToString(result_diff));
			   
				  if (avgVector!=null)
				   avgVector.clear();
				  result.clear();
				  currentRow.clear();
				  //outt.println(temp[0]+","+temp[1]+","+temp[2]+","+ (computeSum(temp) - avg)/computeSum(temp));
				 
			 }
			// System.out.println(rowcount);
			 out.flush();
			 outt.flush();
			 br.close();
			 out.close(); 
			 outt.close();
			 data.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		}
	

	private static void computeLocationMatrix(File graph) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(graph));
		    String line;
		    int count=0;
		    String[] states = null;
		  
		    ArrayList<String> prox = new ArrayList<String>();
		    while ((line = br.readLine()) != null) { 
	                count++;
	                if (count==1) {
	                    states = line.split(",");
	                    continue;
	                }
	               
	                String[] temp = line.split(",");
	                 for(int i=1;i<temp.length;i++){
	                	 if(Integer.parseInt(temp[i])==1)
	                		 prox.add(states[i]);
	                 }
	                 
	                 pmatrix.put(temp[0],ArrayListToString(prox));
	                 prox.clear();
			}
			br.close();
		
		
	}catch(Exception e){
		
	}
		}

	private static ArrayList<Double> ComputeWinDiff(
			ArrayList<Double> currentRow, ArrayList<Double> avgVector, double a) {

		ArrayList<Double> windiff = new ArrayList<Double>();
		for(int i=0;i<currentRow.size();i++){
			windiff.add((double)((currentRow.get(i)-(avgVector!=null?avgVector.get(i):0))/currentRow.get(i)));
		}
		return windiff;
		
	}

	private static String ArrayListDoubleToString(
			ArrayList<Double> window) {
		if (window.size()== 0)
			return null;
	String result = Double.toString(window.get(0));
	for(int i=1;i<window.size();i++)
		result += ","+ window.get(i);
	return result;
		
	}

	private static ArrayList<Double> ComputeWinAvg(ArrayList<Double> currentRow,
			ArrayList<Double> avgVector, double a) {
		
		ArrayList<Double> winavg = new ArrayList<Double>();
		for(int i=0;i<currentRow.size();i++){
			winavg.add(a*currentRow.get(i)+(avgVector!=null?(1-a)*avgVector.get(i):0));
		}
		return winavg;
	}

	

	private static ArrayList<Double> getCurrentRow(String[] temp) {
		ArrayList<Double> l = new ArrayList<Double>();
		for(int i=3;i<temp.length;i++)
			l.add(Double.parseDouble(temp[i]));
		return l;
	}

	private static ArrayList<Double> calcAvg(ArrayList<ArrayList<Double>> rows) {
		  ArrayList<Double> result = new ArrayList<Double>();
		  double sum=0;
		for(int i=0;i<rows.get(0).size();i++){
			
			for(int j=0;j<rows.size();j++){
				 sum += rows.get(j).get(i);	
				 
			}
			result.add(sum/rows.size());
			sum=0;
		}
		return result;
	}

	private static ArrayList<Double> getList(String[] vals) {
		ArrayList<Double> l = new ArrayList<Double>();
		for(int i=1;i<vals.length;i++)
			l.add(Double.parseDouble(vals[i]));
		return l;
	}

	private static String getString(String[] temp) {
		//return temp[2]+","+computeSum(temp);
		StringBuilder sb = new StringBuilder();
		
		for(int i=3;i<temp.length;i++)
		{
			sb.append(","+temp[i]);
		}
			return sb.toString();
    	}

	private static ArrayList<Double> getValues(String[] temp) {
		ArrayList<Double> values = new ArrayList<Double>();
		for(int i =3 ; i< temp.length;i++)
			values.add(Double.parseDouble(temp[i]));
			return values;
	}

	private static double computeSum(String[] temp) {
		double sum = 0;
		for(int i =3 ; i< temp.length;i++)
		sum += Double.parseDouble(temp[i]);
		return sum;
	}

	private static String ArrayListToString(ArrayList<String> window) {
		if (window.size()== 0)
				return null;
		String result = window.get(0);
    	for(int i=1;i<window.size();i++)
			result += ","+ window.get(i);
    	return result;
	}
}
