package mwdb.phase2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class Task3 {

	private HashMap<String, Integer> featureIndexMap = new HashMap<String,Integer>();
	private static final Charset charset = Charset.forName("ISO-8859-1");
//	private static String pathtofolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\epidemic_word_files";
	private static String pathtofolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\epidemic_word_files";
//	private static String inputQueryFolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\input";
	private static String inputQueryFolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\input";
	
	private static String matlab_path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
	
	public void constructFeatureSpace() throws IOException
	{	
		File folder = new File(pathtofolder);
		File[] file_list = folder.listFiles();
		int index=0;
		for(File file:file_list){
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			for(int i=0;i<rows.size();i++){
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				StringBuilder timeword = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				if(!featureIndexMap.containsKey(vword)){
					featureIndexMap.put(vword, index);
					index++;
				}
				if(!featureIndexMap.containsKey(tword)){
					featureIndexMap.put(tword, index);
					index++;
				}
			}
		}
		//display the contents of map
		Iterator<Entry<String, Integer>> itr = featureIndexMap.entrySet().iterator();
	    while (itr.hasNext()) {
	        Map.Entry pairs = (Map.Entry)itr.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	    }
	    System.out.println("Size of Feature space "+featureIndexMap.size());
	}
	
	public void constructFeatureVectors() throws IOException{
		HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>(); 
		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\svdinput.csv");
		File folder = new File(pathtofolder);
		File[] file_list = folder.listFiles();
		int index=0;
		for(File file:file_list){
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			// Loop over each row and find number of occurrances in the file
			for(int i=0;i<rows.size();i++)
			{
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				// Additional feature - <time,word>
				StringBuilder timeword = new StringBuilder();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				if(featureIndexMap.containsKey(vword)){
					int localindex = featureIndexMap.get(vword);
					if(!fvectorMap.containsKey(localindex)){
						fvectorMap.put(localindex, 1);
					}
					else{
						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
					}
				}
				if(featureIndexMap.containsKey(tword)){
					int localindex = featureIndexMap.get(tword);
					if(!fvectorMap.containsKey(localindex)){
						fvectorMap.put(localindex, 1);
					}
					else{
						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
					}
				}
			}
			StringBuilder fvectorwriter = new StringBuilder();
			int featurespaceSize = featureIndexMap.size();
			for(int k=0;k<featurespaceSize;k++){
				if(fvectorMap.containsKey(k)){
					fvectorwriter.append(fvectorMap.get(k));
					fvectorwriter.append(",");
				}
				else{
					fvectorwriter.append("0");
					fvectorwriter.append(",");
				}
			}
			String output = fvectorwriter.toString().trim();
			swriter.write(output);
			swriter.write("\n");
		}
		swriter.close();
	}
	
	public static void calculateSVD() throws MatlabConnectionException, MatlabInvocationException{
		MatlabProxyFactory factory = new MatlabProxyFactory();
		 MatlabProxy proxy = factory.getProxy();
		 //set matlab path
//		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		 proxy.eval(matlab_path);
		 proxy.eval("res=svdcalc()");
//		 double[] bands= (double[]) proxy.getVariable("res");
//		 proxy.disconnect();
	}
	
	public void constructFeatureVectorsQuery() throws IOException{
		HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>(); 
		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\svdqueryinput.csv");
		File folder = new File(inputQueryFolder);
		File[] file_list = folder.listFiles();
		int index=0;
		for(File file:file_list){
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			// Loop over each row and find number of occurrances in the file
			for(int i=0;i<rows.size();i++)
			{
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				// Additional feature - <time,word>
				StringBuilder timeword = new StringBuilder();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				if(featureIndexMap.containsKey(vword)){
					int localindex = featureIndexMap.get(vword);
					if(!fvectorMap.containsKey(localindex)){
						fvectorMap.put(localindex, 1);
					}
					else{
						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
					}
				}
				if(featureIndexMap.containsKey(tword)){
					int localindex = featureIndexMap.get(tword);
					if(!fvectorMap.containsKey(localindex)){
						fvectorMap.put(localindex, 1);
					}
					else{
						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
					}
				}
			}
			StringBuilder fvectorwriter = new StringBuilder();
			int featurespaceSize = featureIndexMap.size();
			for(int k=0;k<featurespaceSize;k++){
				if(fvectorMap.containsKey(k)){
					fvectorwriter.append(fvectorMap.get(k));
					fvectorwriter.append(",");
				}
				else{
					fvectorwriter.append("0");
					fvectorwriter.append(",");
				}
			}
			fvectorwriter.toString();
			swriter.write(fvectorwriter.toString());
			swriter.write("\n");
		}
		swriter.close();
	}
	
	public void constructSimilaritySimilarityMatrix() throws FileNotFoundException{
		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\filefileSimilarity.csv");
		File folder = new File(pathtofolder);
		File[] file_list = folder.listFiles();
		double[][] filefilesimilarity = new double[file_list.length][file_list.length];
		StringBuilder input = new StringBuilder();
		for(int i=0;i<file_list.length;i++){
			for(int j=0;j<file_list.length;j++){
				filefilesimilarity[i][j] = getFileFileSimilarity(file_list[i],file_list[j]);
				input.append(filefilesimilarity[i][j]);
				input.append(",");
			}
			input.append("\n");
		}
		String output = input.toString().trim();
		swriter.write(output);
		swriter.close();
	}
	
	
	private double getFileFileSimilarity(File file, File file2) {
		// TODO Auto-generated method stub
		return 1.5;
	}

	public static void main(String args[]) throws IOException, MatlabConnectionException, MatlabInvocationException{
		Task3 obj = new Task3();
		obj.constructFeatureSpace();
//		obj.constructFeatureVectors();
//		obj.constructFeatureVectorsQuery();
//		obj.calculateSVD();
		obj.constructSimilaritySimilarityMatrix();
	}
	
}
