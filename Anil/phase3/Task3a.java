package mwdb.phase3;

import java.io.File;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabTypeConverter;
import mwdb.phase2.SimilarityWrapper;
import mwdb.phase2.Task3;

public class Task3a implements Comparable<File>
{
	private static File[] file_list;
	private static HashMap<Double,String> fileIndexMap = new HashMap<Double,String>(); 
//	public static String matlab_path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
	public static String location_file_path = "C:\\Users\\ANIL\\Documents\\MATLAB\\LocationMatrix.csv";
	private static MatlabProxyFactory factory = null;
	private static MatlabProxy proxy = null;
	
	
	
	public static final Comparator<File> FilenameComparator = new Comparator<File>(){

        @Override
        public int compare(File o1, File o2) {
        	String xtemp = o1.getName().substring(0,o1.getName().indexOf("."));
        	String ytemp = o2.getName().substring(0,o2.getName().indexOf("."));
            return Integer.compare(Integer.parseInt(xtemp), Integer.parseInt(ytemp));
        }
      
    };
	
	public void constructFileFileSimilarityMatrix(int option, double threshold) throws Exception{
			SimilarityWrapper sobj = new SimilarityWrapper();
//			SimilarityGenerator kobj = new SimilarityGenerator(Task3.location_file_path);
			for(int s=0;s<file_list.length;s++){
				fileIndexMap.put((double) (s+1), file_list[s].getName());
			}
			PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\filefileSimilaritypg.csv");
			double[][] filefilesimilarity = new double[file_list.length][file_list.length];
			StringBuilder input = new StringBuilder();
			double maxsim = 0;
			double minsim = Double.MAX_VALUE;
			for(int i=0;i<file_list.length;i++)
			{
				System.out.println("-"+i);
				for(int j=i;j<file_list.length;j++)
				{
										
					double simvalue = sobj.getSimilarityForFiles(option,file_list[i].getAbsolutePath(),file_list[j].getAbsolutePath());
					System.out.println(j+"->"+simvalue);
					if(simvalue > maxsim){
						maxsim = simvalue;
					}
					if(simvalue < minsim){
						minsim = simvalue;
					}
					if(simvalue > threshold)
					{
					filefilesimilarity[i][j] = 1; 
					filefilesimilarity[j][i] = filefilesimilarity[i][j];
					}
					else{
						filefilesimilarity[i][j] = 0; 
						filefilesimilarity[j][i] = filefilesimilarity[i][j];
					}
				}
			}
//			for(int i=0;i<file_list.length;i++){
//				for(int j=0;j<file_list.length;j++){
//					input.append(filefilesimilarity[i][j]);
//					input.append(",");
//				}
//				input.append("\n");
//			}
			for(int i=0;i<file_list.length;i++)
			{
				String temp = "";
				for(int j=0;j<file_list.length;j++){
					temp = temp + String.valueOf(filefilesimilarity[i][j]);
					temp = temp + ",";
				}
				temp = temp.substring(0,temp.length()-1);
				input.append(temp);
				input.append("\n");
			}
			
			String output = input.toString();
			swriter.write(output);
			swriter.close();
			System.out.println("Maximum similarity value:"+maxsim);
			System.out.println("Minimum similarity value:"+minsim);
		}
	
	public void callMatlab(double c, int k) throws MatlabInvocationException, MatlabConnectionException
	{
		factory = new MatlabProxyFactory();
		proxy = factory.getProxy();
//		 PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\ldaoutput.csv");	
		 proxy.eval(Task3.matlab_path);
		 proxy.setVariable("c", c);
		 MatlabTypeConverter obj = new MatlabTypeConverter(proxy);
		 proxy.eval("pagerank(c)");
		 double[][] temp = obj.getNumericArray("final_result").getRealArray2D();
		 for(int l=0;l<k;l++)
		 {  
			System.out.println("File with high page rank - "+l); 
			for(int i=0;i<temp[l].length/2;i++)
			{
				System.out.println(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
//				swriter.write(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
			}
		 }
		 
	}
	
	
	public static void main(String args[])throws Exception
	{
		Scanner p = new Scanner(System.in);
		System.out.print("Enter the input files folder path : ");
		String path = p.nextLine();
		File folder = new File(path);
		file_list = folder.listFiles();
		Arrays.sort(file_list, Task3a.FilenameComparator);
		for(int i=0;i<file_list.length;i++){
			System.out.println(file_list[i].getName());
		}
		System.out.print("Enter the 'k' value : ");
		int k = Integer.parseInt(p.nextLine());
		System.out.print("Enter the threshold to make an edge : ");
		double threshold = Double.parseDouble(p.nextLine());
		System.out.print("Enter the 'c' value : ");
		double c = Double.parseDouble(p.nextLine());
		int r,option = 0;
		System.out.print("Select a similarity measure : "
				+"1 - Task 1a\t"
				+"2 - Task 1b\t"
				+"3 - Task 1c\t"
				+"4 - Task 1d\t"
				+"5 - Task 1e\t"
				+"6 - Task 1f\t"
				+"7 - Task 1g\t"
				+"8 - Task 1h\t");
		option = Integer.parseInt(p.nextLine());
//		String path = "E:\\MWDB\\demo\\rawsimulationfiles";
//		String path = "E:\\MWDB\\SampleData_P3_F14\\Data";
//		String path = "E:\\MWDB\\SampleData_P3_F14\\word_files";
		
		Task3a obj = new Task3a();
		obj.constructFileFileSimilarityMatrix(option, threshold);
//		obj.callMatlabPageRank(c,k);
		
	}

	@Override
	public int compareTo(File o) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
	
