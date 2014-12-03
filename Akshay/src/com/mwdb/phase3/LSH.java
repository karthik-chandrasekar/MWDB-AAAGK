package com.mwdb.phase3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mwdb.phase2.FastMap;
import com.mwdb.phase2.SimilarityWrapper;

public class LSH {

	//double[][] distanceMatrix;
	double[][] coordinates;
	//ArrayList<Double[][]> rv = new ArrayList<Double[][]>();
	Double[][][] rv;
	int N;
	int K;
	int L;
	private ArrayList<File> files = new ArrayList<File>();
	private ArrayList<Hashtable<String, ArrayList<Integer>>> indexes = new ArrayList<Hashtable<String,ArrayList<Integer>>>();
	int r;
	//Double[] a;
	Double b;
    int w=3;
    FastMap fm;

	public LSH(String inputDir, int K, int L, int r) throws Exception{

		this.K=K;
		this.L=L;
		this.r=r;
		rv = new Double[L][K][r];
		
		loadFiles(inputDir);
		initializeIndexes();
		//computeDistanceMatrix();
		fm = new FastMap(r, "", 3);
	}

	private void initializeIndexes() {
		
		for(int i=0;i<L;i++){
		 Hashtable<String, ArrayList<Integer>> ht = new Hashtable<String, ArrayList<Integer>>();
		 indexes.add(ht);
		}
	}

	private void loadFiles(String DirPath) {

		File folder = new File(DirPath);
		for( File file : folder.listFiles()){
			files.add(file);
		}
		this.N = files.size();
	}


	public void run(){

		//call fastmap on objobj
		//call matlab to get randomvectors
		
		//coordinates = new double[N][r];
		try {
			
			fm.computeDistanceMatrix(files);
			coordinates = fm.getReducedSpace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Double[][] hashes = new Double[N][K];
		for(int i =0;i<L;i++){
			
			
			for(int j=0;j<K;j++){

				rv[i][j] = getRandomVector();
				getRandomShift();

				for(int n=0;n<N;n++){
					hashes[n][j]=computeHash(rv[i][j],b,n);
				}
			}
            buildHashTable(i,hashes);
       
		}
	}

	private void buildHashTable(int i, Double[][] hashes) {
		
		String key; 
		
		for(int j=0; j< N ;j++){
			key = getHashindex(hashes[j]);
			if(indexes.get(i).containsKey(key)){
				indexes.get(i).get(key).add(j);
			}
			else{
				
				ArrayList<Integer> list= new ArrayList<Integer>();
				list.add(j);
				indexes.get(i).put(key, list);
			}
		}
	}

	private String getHashindex(Double[] hashes) {
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<K; i++){
			sb = sb.append(hashes[i]);
		}
		return sb.toString();
	}

	private double computeHash(Double[] a, Double b, int n2) {
		
		double product = 0;
		   
		for(int i=0;i<r;i++){
			product = product + a[i]*coordinates[n2][i];
		}
      return Math.floor((product + b)/(double)w );
	}

	private void getRandomShift() {
		
		b= Math.random() * w;
	}

	private Double[] getRandomVector() {
		Double[] a = new Double[r];
		for(int i =0;i<r;i++){
			a[i]=getGaussianRandom();
		}
		return a;
	}

	private Double getGaussianRandom() {
	
		double x,y;
		do{
		 x = Math.random();
		}while(x==0);
		do{	
		y = Math.random();
		}while(y==0);
		
		return Math.sqrt(-2.0 * Math.log(x))*Math.cos(2.0* Math.PI*y);
	}

	public void search(String filePath, int t){
		
	    Hashtable<Integer, Integer> res = new Hashtable<Integer, Integer>();
	    Double[] hashes= new Double[K];
	    
	    double[] queryCoordinates = fm.getQueryCoordinates(filePath);
	    int vectors =0;
	    for(int i =0;i<L;i++){
			
			for(int j=0;j<K;j++){
//
//				getRandomVector();
				getRandomShift();
				hashes[j]=computeHashForQuery(rv[i][j],queryCoordinates);
			
			}
			String key= getHashindex(hashes);
			ArrayList<Integer> list = indexes.get(i).get(key);
			if(list!=null){
				vectors = vectors + list.size();
			for(int p :list){
				if(!res.contains(p))
					res.put(p, 1);
			}
			}
		}
	    System.out.println("Total vectors considered : "+ vectors);
	    System.out.println("unique vectors : "+ res.size());
	    Set<Integer> sortedFileIndexes = findSimilarity(res,filePath);
		System.out.println("Similar simulations: ");
	   
		Iterator<Integer> it =  sortedFileIndexes.iterator();
 	   int i=0;
 	   while(it.hasNext() && i<t) { 
 		   System.out.println(files.get(it.next()).getName());
			i++;
		}
	}
	
	
	private Double computeHashForQuery(Double[] a2,
			double[] queryCoordinates) {

		double product = 0;
		   
		for(int i=0;i<r;i++){
			product = product + a2[i]*queryCoordinates[i];
		}

		return Math.floor((product + b)/(double)w );
		
	}

	private Set<Integer> findSimilarity(Hashtable<Integer, Integer> res, String filePath) {
		
		HashMap<Integer, Double> simMap = new HashMap<Integer, Double>();
		try {
			SimilarityWrapper sw = new SimilarityWrapper();
			
			for(int i : res.keySet()){
			  
			double sim=	sw.getSimilarityForFiles(3, filePath,files.get(i).getAbsolutePath() );
			simMap.put(i, sim);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		simMap = sortByValues(simMap);
		return simMap.keySet();
	}

	private static HashMap sortByValues(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	                  .compareTo(((Map.Entry) (o1)).getValue());
	            }
	       });

	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }

	public static void main(String[] args) {

		LSH lsh;
		try {
			lsh = new LSH("/home/akshay/Desktop/phase2/word_1", 10, 30,70);
			lsh.run();
			System.out.println("done");
			lsh.search("/home/akshay/Desktop/phase2/word/n11.csv", 8);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}

}
