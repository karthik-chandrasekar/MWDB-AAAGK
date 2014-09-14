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
	public static Charset charset = Charset.forName("ISO-8859-1");
	public static int shift = 1;
	public static int winsize = 3;
	public static ArrayList<Double> bandboundaries;
	
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
	    
	    /*Normalizing the values and writing into different values*/
	    
	    String new_filename = new_folder_path+"n"+file_name;
//		File nfile = new File(new_filename);
		PrintWriter writer = new PrintWriter(new_filename);
		for (int i=1;i<rows.size();i++) {
	        String[] rowval = rows.get(i).split(",");
	        for(int j=2;j<rowval.length;j++){
	        	Double nval = Double.parseDouble(rowval[j])/maxvalue;
	        	System.out.println(String.valueOf(nval));
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
//		 MatlabProxyFactory factory = new MatlabProxyFactory();
//		 MatlabProxy proxy = factory.getProxy();
//		 //set matlab path
//		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
//		 proxy.eval(path);
//		 //call svd
//		 proxy.setVariable("r", num_bands);
//		 proxy.eval("quantization");
//		 Object band_lengths = proxy.getVariable("band_lengths");
////		 Double[] bands = new Double[num_bands];
////		 bands = (Double[]) band_lengths;
//		 //Disconnect the proxy from MATLAB
//		 proxy.disconnect();
		 
		 Double[] bands = {0.5763,0.3141,0.0932,0.0150,0.0013};
		 bandboundaries = new ArrayList<>();
//		 bandboundaries.add(0.0);
		 for(int i=0;i<bands.length;i++){
			 if(i == 0)
				 bandboundaries.add(bands[i]);
			 else
				 bandboundaries.add(bandboundaries.get(i-1)+bands[i]);
		 }
		 for(int i=0;i<bandboundaries.size();i++){
			 System.out.println(bandboundaries.get(i));
		 }
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
			 System.out.println((i+1)+","+center);
		 }
		 writer.close();
	}
	
	public void constructWeightVectors() throws IOException{
		ArrayList<File> nfiles = listfiles(new_folder_path);
		PrintWriter writer = new PrintWriter("epidemic_word_file.txt");
		for(int x=0;x<nfiles.size();x++)
		{
			File nfile = nfiles.get(x);
			List<String> rows = Files.readAllLines(nfile.toPath(), charset);
		    String[] headers = rows.get(0).split(",");
		    for (int i=0;i<headers.length;i++) 
		    {
		    	int j=0;
		    	System.out.println("total rows"+rows.size());
		    	while(j<rows.size())
		    	{
		    		System.out.println("processing row"+j);
		    		StringBuilder vector = new StringBuilder();
		    		vector.append(nfile.getName());
		    		vector.append(",");
		    		vector.append(i);
		    		vector.append(",");
		    		int cur = j;
		    		int count = 1;
		    		while(count <= winsize)
		    		{
		    			if(cur<rows.size())
		    			{
		    			String[] temprow = rows.get(cur).split(",");
		    			Double nval = 	Double.parseDouble(temprow[i]);
		    			System.out.println("curr value is"+nval);
		    			for(int k=0;k<bandboundaries.size()-1;k++){
		    				if(k == 0){
		    					if(nval < bandboundaries.get(k))
		    						vector.append(String.valueOf(BandCenters.get(k)));
		    						System.out.println("band is"+k);
		    						vector.append(",");
//		    						break;
		    				}
		    				else{
		    					if(nval > bandboundaries.get(k) && nval < bandboundaries.get(k+1) )
		    					{
		    						vector.append(String.valueOf(BandCenters.get(k)));
		    						System.out.println("band is"+k);
		    						vector.append(",");
//		    						break;
		    					}
		    				}
		    				
		    			}
		    			}
		    			count++;
		    			cur++;
		    		}
		    		j = j + shift;
		    		vector.append("\n");
		    		System.out.println(vector.toString());
		    		writer.write(vector.toString());
		    	}
		    }

		}
	}
	
	public static void main(String args[]) throws IOException, MatlabConnectionException, MatlabInvocationException{
		EpidemicWordsDictionary obj = new EpidemicWordsDictionary();
		boolean nfolder = new File(new_folder_path).mkdir();
		ArrayList<File> filelist = listfiles(foldername);
//		obj.normalizeFiles(filelist);
		obj.findQuantizationBands(num_bands);
		obj.constructWeightVectors();
	}
	
	
}
