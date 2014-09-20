package com.mwdb.phase1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Task3 {

	public static void main(String[] args) {
		File EWfile = new File("epidemic_word_file_diff.txt");
		File graph = new File("LocationMatrix.csv");
        String InputFile="Sample1.csv";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(graph));
		
	    String line;
	    int count=0;
	    String[] states = null;
	    HashMap<String, String> pmatrix= new HashMap<String, String>();
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
		
		br = new BufferedReader(new FileReader(EWfile));
		HashMap<String,ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
		 while ((line = br.readLine()) != null) { 
			String[] temp = line.split(",");
			
				String s =temp[0];
	
			if(!data.containsKey(s))
			{ ArrayList<String> l = new ArrayList<String>();
			l.add(temp[1]+","+temp[2]+getString(temp));
			data.put(s,l);
			}
			else{
				ArrayList<String> l = data.get(s);
				l.add(temp[1]+","+temp[2]+getString(temp));
				data.put(s, l);
				}
		 }
           
		 br.close();

		 ArrayList<String> vals = data.get(InputFile);
		 ArrayList<Double> temp=null;
		 String[] win;
		 double min=1,max =0;
		 String maxState="",minState="";
		 String minTime="",maxTime="";
		 Double val;
		 for(int i =0;i<vals.size();i++){
			 win=vals.get(i).split(",");
			 temp = getCurrentRow(win);
			 val = computeStrenth(temp);
			 if(Math.max(max, val)==val){
				 max = val;
				 maxState = win[0];
				 maxTime = win[1];
			 }
			 if(Math.min(min, val)==val){ //considering last min
				 min = val;
				 minState = win[0];
				 minTime = win[1];
			 }
			 
			 temp.clear();
			 
		 }
        
		 System.out.println("Max State : "+ maxState);
		 System.out.println("Min State : "+ minState);
		 System.out.println("Max value : "+ max);
		 System.out.println("Min value : "+ min);
		 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private static Double computeStrenth(ArrayList<Double> temp) {
		
		double sum=0;
		for(int i=0; i<temp.size();i++){
			sum = sum + temp.get(i)*temp.get(i);
		}
		
		
		return Math.sqrt(sum);
	}

	private static ArrayList<Double> getCurrentRow(String[] temp) {
		ArrayList<Double> l = new ArrayList<Double>();
		for(int i=2;i<temp.length;i++)
			l.add(Double.parseDouble(temp[i]));
		return l;
	}

	private static String ArrayListToString(ArrayList<String> window) {
		if (window.size()== 0)
				return null;
		String result = window.get(0);
    	for(int i=1;i<window.size();i++)
			result += ","+ window.get(i);
    	return result;
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
}
