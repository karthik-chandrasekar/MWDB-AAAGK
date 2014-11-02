import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class Task1_c_d_e {
	
	private String f1,f2;
	
	private static double[][] readWords = new double[15000][10];
	
	// Constructor to initialise the string variables with the file names 
	public Task1_c_d_e(String file1, String file2) {
		
		f1 = file1;
		f2 = file2;
	}
	
	 

	public Double compareFiles() {
		
		ArrayList<String> bag = new ArrayList<>();
		ArrayList<String> bag1 = new ArrayList<>();
		ArrayList<String> bag2 = new ArrayList<>();
		ArrayList<String> bagFile1 = new ArrayList<>();
		ArrayList<String> bagFile2 = new ArrayList<>();
		HashSet<String> hs = new HashSet<>();
		  ArrayList<double[][]> allWords = new ArrayList<>();
		  
		 int simScore = 0; 
		
		 readFile(f1,bag); // read file 1 and insert the words into the bag arraylist
		
		 bagFile1.addAll(bag);
		hs.addAll(bag); // to remove duplicates by adding all words into a hash set
		for ( String s : hs)
			System.out.println(s); // To display all the elements in the hash set
		
		bag.clear();
		bag.addAll(hs);
		allWords.add(readWords); // 
		hs.clear();
		readFile(f2,bag1);
		allWords.add(readWords);
		bagFile2.addAll(bag1);
		hs.addAll(bag1);
		bag1.clear();
		bag1.addAll(hs);
		
		hs.clear();
		
		hs.addAll(bag);
		hs.addAll(bag1);
		
		for ( String s : hs)
			System.out.println(s);
		
		bag2.addAll(hs);
		
		
		
		
		System.out.println("Done, Unique lines : " + hs.size());
		System.out.println("Bag Size :" + bag.size());
		System.out.println("Bag 1 Size :" + bagFile1.size());
		System.out.println("Bag 2 Size :" + bagFile2.size());
		
		int[][] binary = new int[2][bag2.size()];
		int[] result = new int[bag2.size()];
		
		for ( String s : hs)
			System.out.println(s);
		
			
		int fcount1 = 0;
		int fcount2 = 0;
		for( int i = 0 ; i < bag2.size() ; i++){
						
			if (bagFile1.contains(bag2.get(i))){
			//	System.out.println(bag.indexOf(bagFile1.get(i)));
				binary[0][i] = 1;
				fcount1++;
			}
			
			if (bagFile2.contains(bag2.get(i))){
				//	System.out.println(bag.indexOf(bagFile1.get(i)));
					binary[1][i] = 1;
					fcount2++;
				}
			
		}
		
		
		
		System.out.println();
		
		for( int i = 0 ; i < bag2.size() ; i++){
		 System.out.print(binary[0][i] + " ");
		}
		System.out.println();
		for( int i = 0 ; i < bag2.size() ; i++){
			 System.out.print(binary[1][i] + " ");
			}
		System.out.println();
		System.out.println("Fcount1 : " + fcount1);
		System.out.println("Fcount2 : " + fcount2);
		System.out.println("Bag	: " + bag2.size());
		//System.out.println(binary[0]);
		//System.out.println(binary[1]);
			
		for( int i = 0 ; i< bag2.size() ; i++ ){
			if(binary[0][i] == binary[1][i]){
				result[i] = 1;
				simScore++;
			}
			else{
				result[i] = 0;
			}
			System.out.print(result[i] + " ");
		}
		System.out.println();
		
		//System.out.println(simScore +" " + bag2.size() );
		double res = (((double)simScore / (double)bag2.size())*100);
		//res = Math.round(res*100.0)/100.0;
		//System.out.println("Similarity Score : " + Math.round(res*100.0)/100.0  + "%");
		return res/100;
		
		
		
	}	

	private static void readFile(String f, ArrayList<String> bag) {
		// TODO Auto-generated method stub
		String csvFile = f;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int count = 0;
		
		
		//readWords = null;
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
					
			        // use comma as separator
				String[] words = line.split(cvsSplitBy);
				String temp = null;
				for( int i = 3 ; i < words.length ; i++){
				//System.out.print("<" + words[i].trim() + ">");
					//System.out.println(">"+words[i]+"<");
					
						words[i] = words[i].trim();
					if (words[i].length() != 0){
						readWords[count][i-3] = Double.parseDouble(words[i]);// Double.parseDouble(words[i].trim());
					
					}
					else{
						readWords[count][i-3] = 0.0;
					
					}

				//System.out.print(readWords[count][i-3]);
				if(temp == null)
					temp = words[i] + ",";
				else
					temp = temp + words[i] + ",";
				
				}

				//rows.add(cols);
			//	System.out.println(temp.substring(0, temp.length()-1));
				temp = temp.substring(0, temp.length()-1);
				bag.add(temp);
	//			System.out.println();
				
				
				count++;
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println("Rows.size : " + rows.size());
	//	System.out.println("Rows.length : " + rows.get(0).length);
		
		
		
		
		
		
	  }


	

}
