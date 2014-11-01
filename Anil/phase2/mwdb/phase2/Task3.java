package mwdb.phase2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class Task3 
{
	private HashMap<String, Integer> featureIndexMap = new HashMap<String,Integer>();
	private static HashMap<Integer,String> fileIndexMap = new HashMap<Integer, String>();
	private static final Charset charset = Charset.forName("ISO-8859-1");
//	private static String pathtofolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\epidemic_word_files";
	private static String pathtofolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\epidemic_word_files";
//	private static String inputQueryFolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\input";
	private static String inputQueryFolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\input";
	
	private static String matlab_path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
	
	public static File[] file_list;
	private static MatlabProxyFactory factory = null;
	private static MatlabProxy proxy = null;
	
//	public void constructFeatureSpace() throws IOException
//	{	
//		File folder = new File(pathtofolder);
//		File[] file_list = folder.listFiles();
//		int index=1;
//		for(File file:file_list){
//			List<String> rows = Files.readAllLines(file.toPath(), charset);
//			for(int i=0;i<rows.size();i++){
//				String[] rvalues = rows.get(i).split(",");
//				StringBuilder word = new StringBuilder();
//				StringBuilder timeword = new StringBuilder();
//				for(int j=3;j<rvalues.length;j++){
//					word.append(rvalues[j]);
//					word.append(",");
//				}
//				String vword = word.toString();
//				timeword.append(rvalues[2]+"_");
//				timeword.append(vword);
//				String tword = timeword.toString();
//				if(!featureIndexMap.containsKey(vword)){
//					featureIndexMap.put(vword, index);
//					index++;
//				}
//				if(!featureIndexMap.containsKey(tword)){
//					featureIndexMap.put(tword, index);
//					index++;
//				}
//			}
//		}
//		//display the contents of map
//		Iterator<Entry<String, Integer>> itr = featureIndexMap.entrySet().iterator();
//	    while (itr.hasNext()) {
//	        Map.Entry pairs = (Map.Entry)itr.next();
////	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	    }
//	    System.out.println("Size of Feature space "+featureIndexMap.size());
//	}
//	

	public static void main(String args[]) throws IOException, MatlabConnectionException, MatlabInvocationException
	{
		//Initialize matlab factory class
		
		factory = new MatlabProxyFactory();
		proxy = factory.getProxy();
		
		
		
//		File folder = new File(pathtofolder);
//		file_list = folder.listFiles();
//		for(int i=0;i<file_list.length;i++){
//			fileIndexMap.put(i+1, file_list[i].getName());
//		}
//		Iterator<Entry<Integer, String>> itr1 = fileIndexMap.entrySet().iterator();
//	    while (itr1.hasNext()) {
//	        Map.Entry pairs = (Map.Entry)itr1.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	    }
		
		String input = "";
		String path = "";
		while(!input.equals("exit"))
		{
			input = "";
			System.out.print("Enter the task number : ");
			Scanner in = new Scanner(System.in);
			input = in.nextLine();
				//Task 3a
				if(input.equals("3a"))
				{
					path = "";
					System.out.print("Enter the input files folder path : ");
					Scanner p = new Scanner(System.in);
					path = p.nextLine();
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3a.file_list = file_list;
					Task3a.factory = factory;
					Task3a.proxy = proxy;
					Task3a obj1 = new Task3a();
					obj1.constructFeatureSpace();
					obj1.constructFeatureVectorsSVD();
					obj1.calculateSVD();
				}
				// Task 3b
				if(input.equals("3b"))
				{
					path = "";
					System.out.print("Enter the input files folder path : ");
					Scanner p = new Scanner(System.in);
					path = p.nextLine();
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3b.factory = factory;
					Task3b.proxy = proxy;
					Task3b.file_list = file_list;
					Task3b obj2 = new Task3b();
					obj2.constructFeatureSpace();
					obj2.constructLDAInput();
					obj2.calculateLDA();
				}
//				//Task 3c
//				if(input.equals("3c")){
//					path = "";
//					System.out.print("Enter the input files folder path : ");
//					Scanner p = new Scanner(System.in);
//					path = p.nextLine();
//					File folder = new File(path);
//					file_list = folder.listFiles();
//					Task3c.file_list = file_list;
//					Task3c.factory = factory;
//					Task3c.proxy = proxy;
//					Task3c obj = new Task3c();
//					obj.constructSimilaritySimilarityMatrix();
//				}
				//Task 3d
				if(input.equals("3d")){
					path = "";
					System.out.print("Enter the input query file folder path : ");
					Scanner p = new Scanner(System.in);
					path = p.nextLine();
					Task3d.factory = factory;
					Task3d.proxy = proxy;
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3d.featureIndexMap = Task3a.featureIndexMap;
					Task3d.file_list = file_list;
					Task3d.fileIndexMap = Task3a.fileIndexMap;
					Task3d obj = new Task3d();
//					obj.constructFeatureSpace();
					obj.constructFeatureVectorsQuerySVD();
					obj.doSVDSearch();
				}
				//Task 3e
				if(input.equals("3e")){
				path = "";
				System.out.print("Enter the input query file folder path : ");
				Scanner p = new Scanner(System.in);
				path = p.nextLine();
				Task3e.factory = factory;
				Task3e.proxy = proxy;
				File folder = new File(path);
				file_list = folder.listFiles();
				Task3e.featureIndexMap = Task3b.featureIndexMap;
				Task3e.file_list = file_list;
				Task3e.fileIndexMap = Task3b.fileIndexMap;
				Task3e obj = new Task3e();
//				obj.constructFeatureSpace();
				obj.constructLDAInputQuery();
				obj.doLDASearch();
			}
			}
		proxy.disconnect();
	}
	
}
