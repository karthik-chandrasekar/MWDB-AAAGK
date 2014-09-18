package task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;


import java.util.Map;
import java.util.Map.Entry;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class EpidemicWordsDictionary {
	
	public HashMap<Integer, String> StateIndexMap = new HashMap<>();
	public static int num_bands = 5;
	public static String foldername = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets\\";
	public static HashMap<Integer, Double> BandCenters = new HashMap<>();
	public static String new_folder_path = foldername+"\\normalized_files\\";
	public static String epidemic_word_file_path = foldername+"\\epidemic_word_files\\";
	public static Charset charset = Charset.forName("ISO-8859-1");
	public static int shift = 3;
	public static int winsize = 5;
	public static ArrayList<Double> bandboundaries;
	public static int num_files = 0;
	public static int skip_length = 0;
	static MatlabProxyFactory factory = new MatlabProxyFactory();		 
	static MatlabProxy proxy;
	
	public void normalizeFiles(ArrayList<File> filelist) throws IOException{
		
		for(int x=0;x<filelist.size();x++)
		{
		File file = filelist.get(x);
		String file_name = file.getName();
		System.out.println(file.toPath());
//		Path file_path = Paths.get("C:\\Users\\ANIL\\MWDB_INUSE\\EpidemicHeatMaps\\src\\task1\\", "1.csv");
	    
		List<String> rows = Files.readAllLines(file.toPath(), charset);
	    String[] headers = rows.get(0).split(",");
		
	    /* Finding max value */
	    Double maxvalue = 0.0;
	    for (int i=1;i<rows.size();i++) {
	        String[] rowval = rows.get(i).split(",");
	        for(int j=2;j<rowval.length;j++){
	        	if (Double.parseDouble(rowval[j]) > maxvalue){
	        		maxvalue = Double.parseDouble(rowval[j]);
	        	}
	        }
	    }
//	    System.out.println("maximum value is"+maxvalue);
	    /*Normalizing the values and writing into different values*/
	    
	    String new_filename = new_folder_path+"n"+file_name;
//		File nfile = new File(new_filename);
		PrintWriter writer = new PrintWriter(new_filename);
		for (int i=1;i<rows.size();i++) {
	        String[] rowval = rows.get(i).split(",");
	        for(int j=2;j<rowval.length;j++){
	        	Double nval = Double.parseDouble(rowval[j])/maxvalue;
//	        	nval = (double)Math.round(nval * 1000) / 1000;
//	        	System.out.println(String.valueOf(nval));
	        	writer.write(String.valueOf(nval));
	        	writer.write(",");
	        }
	        writer.write("\n");
	    }
		writer.close();
		}
	}
	
	public void callMatlab() throws MatlabConnectionException, MatlabInvocationException{
		 MatlabProxyFactory factory = new MatlabProxyFactory();
		 MatlabProxy proxy = factory.getProxy();
		 //set matlab path
		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		 proxy.eval(path);
//		 Object[] bounds = {1,3,4,5};
//		 proxy.set("bounds",bounds);
		 //call svd
		 proxy.eval("metric");
		 //Disconnect the proxy from MATLAB
		 proxy.disconnect();

	}
	
	
	/* Return files in a folder */
	public static ArrayList<File> listfiles(String foldername) {
	    File folder = new File(foldername);
	    ArrayList<File> files = new ArrayList<>();
	    // get all the files from a directory
	    File[] file_list = folder.listFiles();
	    for (File file : file_list) {
	        if (file.isFile()) {
	            files.add(file);
	        }
	    }
	    return files;
	}
	
	
	
	public void findQuantizationBands(int num_bands) throws MatlabConnectionException, MatlabInvocationException, FileNotFoundException{
		 MatlabProxyFactory factory = new MatlabProxyFactory();
		 MatlabProxy proxy = factory.getProxy();
		 //set matlab path
		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		 proxy.eval(path);
		 proxy.setVariable("r", num_bands);
		 proxy.eval("res=quantization(r)");
		 double[] bands= (double[]) proxy.getVariable("res");
		 proxy.disconnect();
//		 System.out.println("bands"+bands.length);
//		 Double[] bands = {0.5763,0.3141,0.0932,0.0150,0.0013};
//		 Double[] bands = {0.8176293512220892,0.9924021000786353,1.0000000000000002};
		 bandboundaries = new ArrayList<>();
//		 bandboundaries.add(0.0);
		 for(int i=0;i<bands.length;i++){
			 if(i == 0)
				 bandboundaries.add(bands[i]);
			 else
				 bandboundaries.add(bandboundaries.get(i-1)+bands[i]);
		 }
		 PrintWriter writer2 = new PrintWriter("band_boundary_representation.txt");
		 for(int i=0;i<bandboundaries.size();i++){
			 System.out.println(bandboundaries.get(i));
			 writer2.write(bandboundaries.get(i)+"\n");
		 }
		 writer2.close();
		 PrintWriter writer = new PrintWriter("band_center_representation.txt");
		 for(int i=0;i<bandboundaries.size();i++){
			 double center = 0.0;
			 if(i == 0)
				 center = bandboundaries.get(i)/2;
			 else
				 center = (bandboundaries.get(i-1)+bandboundaries.get(i))/2;
			 BandCenters.put(i, center);
			 writer.write("\n");
			 writer.write(String.valueOf(BandCenters.get(i+1)));
//			 System.out.println((i+1)+","+center);
		 }
		 writer.close();
	}
	
	public void constructWeightVectors() throws IOException{
		ArrayList<File> nfiles = listfiles(new_folder_path);
		PrintWriter writer = new PrintWriter("epidemic_word_file.csv");
		PrintWriter writer1 = new PrintWriter("epidemic_word_log_file.txt");
		for(int x=0;x<nfiles.size();x++)
		{
			File nfile = nfiles.get(x);
			List<String> rows = Files.readAllLines(nfile.toPath(), charset);
//			System.out.println("total rows"+rows.size());
			writer1.write("total rows "+rows.size()+"\n");
			writer1.write("filename "+nfile.getName());
			skip_length = (int) Math.floor(rows.size()/shift);
			int skip = (int)(rows.size()/shift);
			System.out.println("Skip length is "+skip_length);
			String[] headers = rows.get(0).split(",");
			PrintWriter swriter = new PrintWriter(epidemic_word_file_path+nfile.getName());
			for (int i=0;i<headers.length;i++) 
		    {
		    	
//		    	PrintWriter swriter = new PrintWriter(new BufferedWriter(new FileWriter((i+1)+".csv", true))); 
		    	int j=0;
		    	int timer=1;
//		    	if(i==34 && nfile.getName().equals("n25.csv"))
//		    		System.out.println("hello");
		    	writer1.write("Processing State "+(i+1)+"\n");
		    	while(j<rows.size())
		    	{
//		    		System.out.println("processing row "+j);
		    		writer1.write("processing row "+j+"\n");
		    		StringBuilder vector = new StringBuilder();
		    		String tempName = nfile.getName().split(".csv")[0];
		    		tempName = tempName.substring(1);
		    		vector.append(tempName);
		    		vector.append(",");
		    		vector.append(i+1);
		    		vector.append(",");
		    		vector.append(timer);
		    		vector.append(",");
		    		int cur = j;
		    		int count = 1;
		    		while(count <= winsize)
		    		{
		    			double outlier = 0.0;
		    			if(cur<rows.size())
		    			{
		    			String[] temprow = rows.get(cur).split(",");
		    			Double nval = 	Double.parseDouble(temprow[i]);
//		    			System.out.println("band decision value is"+nval);
		    			writer1.write("processing win row "+cur+"\n");
		    			
		    			writer1.write("band decision value is "+nval+"\n");
		    			int assign = 0;
		    			for(int k=0;k<bandboundaries.size()-1;k++)
		    			{
//		    				writer1.write("BB is "+bandboundaries.get(k)+"\n");
		    				if(nval <= bandboundaries.get(k)){
		    					vector.append(String.valueOf(BandCenters.get(k)));
//	    						System.out.println("band is"+k);
	    						writer1.write("band is "+k+"\n");
	    						vector.append(",");
	    						assign=1;
	    						outlier = BandCenters.get(k);
	    						break;
		    				}
		    			}
		    			if(assign == 0){
		    				vector.append(String.valueOf(BandCenters.get(bandboundaries.size()-1)));
//    						System.out.println("band is"+bandboundaries.size());
    						writer1.write("band is "+bandboundaries.size()+"\n");
    						vector.append(",");
    						assign=1;
		    			}
		    			}
		    			else{
		    				vector.append("NAN");
//    						System.out.println("band is "+"default");
    						writer1.write("band is "+"default"+"\n");
    						vector.append(",");
		    			}
		    			count++;
		    			cur++;
		    		}
		    		j = j + shift;
		    		timer = timer + shift;
		    		vector.append("\n");
//		    		System.out.println(vector.toString());
		    		if (vector.toString().endsWith(",\n")) {
		    			  String temp = vector.toString();
		    			  temp = temp.substring(0, temp.length() - 2);
//		    			  System.out.println("Final vector is"+temp);
		    			  if(!temp.contains("NAN")){
		    			  writer.write(temp);
		    			  writer.write("\n");
		    			  swriter.write(temp);
		    			  swriter.write("\n");
		    			  }
		    			}
		    	}
		    }
			swriter.close();
		}
		writer.close();
	}
	
	public void constructEpidemicWordAvgFiles() throws MatlabConnectionException, MatlabInvocationException{
		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		 proxy.eval(path);
		 proxy.setVariable("num_files", num_files);
		 proxy.setVariable("skip_length", skip_length);
		 proxy.eval("mwdb_task2_a_final(num_files,skip_length)");
//		 proxy.disconnect();
	}
	
	public void constructEpidemicWordDiffFiles() throws MatlabConnectionException, MatlabInvocationException{
//		MatlabProxyFactory factory = new MatlabProxyFactory();
//		 MatlabProxy proxy = factory.getProxy();
		 num_files = 50;
		 skip_length = 71;
		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		 proxy.eval(path);
		 proxy.setVariable("num_files", num_files);
		 proxy.setVariable("skip_length", skip_length);
		 proxy.eval("mwdb_task2_b_final(num_files,skip_length)");
//		 proxy.disconnect();
	}
	
	public static void main(String args[]) throws IOException, MatlabConnectionException, MatlabInvocationException{
		EpidemicWordsDictionary obj = new EpidemicWordsDictionary();
		boolean nfolder = new File(new_folder_path).mkdir();
		boolean mfolder = new File(epidemic_word_file_path).mkdir();
		ArrayList<File> filelist = listfiles(foldername);
		num_files = filelist.size();
//		obj.normalizeFiles(filelist);
		proxy = factory.getProxy();
//		obj.findQuantizationBands(num_bands);
//		obj.constructWeightVectors();
//		obj.callMatlab();
//		obj.constructEpidemicWordAvgFiles();
		obj.constructEpidemicWordDiffFiles();
	}
	
	
}
