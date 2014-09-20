package com.mwdb.phase1;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class DataFormatter {
    static Double[] bands = {0.5763,0.3141,0.0932,0.015,0.0013}; 
    
    public static void main(String[] args){
    	
    	final File folder = new File("/home/akshay/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets1");
    	int r=5;
        int w=5,h=3;
        
    	for( File file : folder.listFiles()){
    	
    	//	System.out.println(file.getName());
        try {
        BufferedReader br = new BufferedReader(new FileReader(file)); 
        String line;
        String[] states = null;
        int NoOfStates = 51;
        ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
        while(data.size() < NoOfStates) data.add(new ArrayList<Double>());
        int count=0;
            while ((line = br.readLine()) != null) { 
                count++;
                if (count==1) {
                    states = line.split(",");
                    continue;
                }
                   
                       
                String[] columns = line.split(",");
               
                for(int i=0; i< NoOfStates;i++)
                data.get(i).add(new Double(columns[i+2]));
               
               }
           
            normalize(data);
           
            Double[] bands = getBands(r);
           
           
           
            for(int i=0;i<data.size();i++){
                ArrayList<Double> temp =data.get(i);
                for(int j=0; j<temp.size();j++){
                    int k = 0;
                    while(k<r){
                        if(temp.get(j)>=bands[k] && temp.get(j)<bands[k+1])
                            {temp.set(j, (bands[k]+bands[k+1])/2);
                             break;}
                        else if(temp.get(j)>bands[r])
                        	temp.set(j, (bands[r-1]+bands[r])/2);
                        else 
                        	k++;
                        	
                    }
                    
            
                }
            }
           
            ArrayList<Double> window = new ArrayList<>();
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("epidemic_word_file.txt", true)));
            int s,k,t;
            for(int i=0;i<data.size();i++){
                ArrayList<Double> temp = data.get(i);
                t = 1;
                for(int j=0; j<temp.size();j=j+h){
                	
                   k=j;
                   s = temp.size();
                   int p=0;
                    while(k < j+w && k<s){
                    window.add(temp.get(k));
                       k++;
                       p=k;
                    } 
                    while(k>=s && k < j+w ){
                    	window.add(temp.get(p-1));
                    	k++;
                    }
                    String win = ArrayListToString(window);
                    window.clear();
                    
                        out.println(file.getName()+","+states[i+2].split("-")[1]+","+t+","+ win);
                        t=t+h;
                }
                
                
            }

            out.flush();
            data.clear();
            out.close();
           
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	}
    }

    private static String ArrayListToString(ArrayList<Double> window) {
		String result = Double.toString(window.get(0));
    	for(int i=1;i<window.size();i++)
			result += ","+ Double.toString(window.get(i));
    	return result;
	}

	private static Double[] getBands(int i) {
        
      if (bands == null){return null;}
      
    	  Double[] bandPoints = new Double[i+1];
    	  bandPoints[0]=(double) 0;
    	  for(int j =0;j<bands.length; j++){
    		  bandPoints[j+1] = bands[j] + bandPoints[j];   
      }
        return bandPoints;
    }

    private static void normalize(ArrayList<ArrayList<Double>> data) {
       
        double max  = findMax(data);
        for(int i =0; i< data.size();i++){
           
            ArrayList<Double> temp = data.get(i);
           
             for(int j=0; j<temp.size();j++ ){
                temp.set(j, temp.get(j)/max);
            }
        }
       
    }

	private static Double findMax(ArrayList<ArrayList<Double>> data) {
        ArrayList<Double> temp = new ArrayList<>();
        for(int i=0;i<data.size();i++){
            /*System.out.println(i);
            System.out.println(data.get(i));
            */temp.add(Collections.max(data.get(i)));
        }
        return Collections.max(temp);
    }
}

