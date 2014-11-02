package mwdb.phase2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimilarityWrapper {

	public double getSimilarityForFiles(int option, String f1, String f2) throws Exception{
		double similarity = 0;
		if(option == 1 ){
			//task 1a
			System.out.println("task 1a");
			String query = "python F://python2.7.5//code_mwdb//eucled.py "+f1+" "+f2;
			System.out.println(query);
			Process p = Runtime.getRuntime().exec(query);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = in.readLine();
		    p.waitFor();
		    p.destroy();
		    System.out.println(s);
			similarity = Double.parseDouble(s);
		}
		
		if(option == 2){
			//task 1b
			System.out.println("task 1a");
			String query = "python F://python2.7.5//code_mwdb//dtwdynamic.py "+f1+" "+f2;
			System.out.println(query);
			Process p = Runtime.getRuntime().exec(query);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = in.readLine();
		    p.waitFor();
		    p.destroy();
			similarity = Double.parseDouble(s);
		}
//		if(option.equals("1") ){
//			//task 1c
//			
//		}
//		if(option.equals("1") ){
//			//task 1d
//			
//		}
//		if(option.equals("1") ){
//			//task 1e
//			
//		}
		else if(option == 6 || option == 7 || option == 8){
			SimilarityGenerator kobj = new SimilarityGenerator(Task3.location_file_path);
			similarity = kobj.getFileSimilarity(f1,f2); 
		}
		return similarity;		
	}
	
	public static void main(String args[]) throws Exception{
		SimilarityWrapper sw = new SimilarityWrapper();
		sw.getSimilarityForFiles(1, "E://MWDB//Anil_Kuncham_MWDB_Phase1//output//Epidemic_Simulation_Datasets_50//epidemic_word_files//avgn9.csv", "E://MWDB//Anil_Kuncham_MWDB_Phase1//output//Epidemic_Simulation_Datasets_50//epidemic_word_files//avgn9.csv" );
	}
}
