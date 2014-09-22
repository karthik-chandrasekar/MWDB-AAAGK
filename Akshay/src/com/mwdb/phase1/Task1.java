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

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class Task1 {
    
	static double[] bands = {0.5763,0.3141,0.0932,0.015,0.0013}; 
    static boolean executed1=false;
    
    public static void execute(int r,int w,int h,String folderName){
    	
    	if(!executed1){
    	final File folder = new File(folderName);
    	try {
    	 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("epidemic_word_file.txt")));
    	for( File file : folder.listFiles()){
    	
    	//	System.out.println(file.getName());
        
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
                    while(k<r){  //temp.get(j)>=bands[k] && 
                        if(temp.get(j)<=bands[k+1])
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

            data.clear();    
           
        }
    	out.flush();
        
        out.close();
    	}catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	executed1=true;
    	}
    	}
    

    private static String ArrayListToString(ArrayList<Double> window) {
		String result = Double.toString(window.get(0));
    	for(int i=1;i<window.size();i++)
			result += ","+ Double.toString(window.get(i));
    	return result;
	}

	private static Double[] getBands(int i) {
		
		Double[] bandPoints = new Double[i+1];
		
      if (bands == null){
    	  MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy;
		try {
			proxy = factory.getProxy();
		
		//set matlab path
		String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		proxy.eval(path);
		proxy.setVariable("r", i);
		proxy.eval("res=quantization(r)");
		bands= (double[]) proxy.getVariable("res");
		proxy.disconnect();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      
    	  
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
            */
        	temp.add(Collections.max(data.get(i)));
        }
        return Collections.max(temp);
    }
}

