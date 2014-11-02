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
import matlabcontrol.extensions.MatlabTypeConverter;
import mwdb.phase2.TimeSeriesSimilarityGenerator;

public class Task3c 
{
	public HashMap<String, Integer> featureIndexMap = new HashMap<String,Integer>();
	public static HashMap<Double,String> fileIndexMap = new HashMap<Double, String>();
	private static final Charset charset = Charset.forName("ISO-8859-1");
//	private static String pathtofolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\epidemic_word_files";
	private static String pathtofolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\epidemic_word_files";
//	private static String inputQueryFolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\input";
	private static String inputQueryFolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\input";
	
	private static String matlab_path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
	
	public static File[] file_list;
	public static MatlabProxyFactory factory = null;
	public static MatlabProxy proxy = null;
	
	public void constructSimilaritySimilarityMatrix() throws Exception{
		SimilarityGenerator kobj = new SimilarityGenerator(Task3.location_file_path);
		for(int s=0;s<file_list.length;s++){
			fileIndexMap.put((double) (s+1), file_list[s].getName());
		}
		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\filefileSimilarity.csv");
		double[][] filefilesimilarity = new double[file_list.length][file_list.length];
		StringBuilder input = new StringBuilder();
		for(int i=0;i<file_list.length;i++){
			for(int j=0;j<file_list.length;j++){
				System.out.println(file_list[i].getAbsolutePath());
				filefilesimilarity[i][j] = kobj.getFileSimilarity(file_list[i].getAbsolutePath(),file_list[j].getAbsolutePath());
				input.append(filefilesimilarity[i][j]);
				input.append(",");
			}
			input.append("\n");
		}
		String output = input.toString().trim();
		swriter.write(output);
		swriter.close();
	}
	
	public void doFileFileSVD(Integer r) throws MatlabInvocationException, FileNotFoundException
	{
		 PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\svdfilefileoutput.csv");
		 System.out.println("Calculating SVD function in matlab");
		 //set matlab path
		 proxy.eval(Task3.matlab_path);
		 proxy.eval("final_result = svdcalc_file_file(r)");
		 proxy.setVariable("r", r);
		 MatlabTypeConverter obj = new MatlabTypeConverter(proxy);
		 proxy.eval("final_result = svdcalc(r)");
		 double[][] temp = obj.getNumericArray("final_result").getRealArray2D();
		 for(int l=0;l<r;l++)
		 {
		 System.out.println("Laten symantic - "+l); 
		 for(int i=0;i<temp[l].length/2;i++)
			{
				System.out.println(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
				swriter.write(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
			}
		 }
		 System.out.println("Matlab execution done");
	}
	

	
//	public static void Main() throws Exception{
//		factory = new MatlabProxyFactory();
//		proxy = factory.getProxy();
//		File folder = new File(pathtofolder);
//		file_list = folder.listFiles();
//		
//		for(int i=0;i<file_list.length;i++){
//			fileIndexMap.put(i+1, file_list[i].getName());
//		}
//		Iterator<Entry<Integer, String>> itr1 = fileIndexMap.entrySet().iterator();
//	    while (itr1.hasNext()) {
//	        Map.Entry pairs = (Map.Entry)itr1.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	    }
//		Task3c obj = new Task3c();
//		obj.constructSimilaritySimilarityMatrix();
//		
////		obj.constructLDAInputQuery();
//	}
	
}
