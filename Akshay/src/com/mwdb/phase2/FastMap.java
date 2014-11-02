package com.mwdb.phase2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FastMap {

	int N; // no. of objects
	int r; // reduced space dimension
	double[][] coordinates;  // coordinates of every object in the reduced space
	double[] pivotDistances; // distances between pivot objects in every iteration
	int[][] PV; // indices of the pivot objects in every iteration
	String DirPath; // input directory path of simulation files.
	SimilarityWrapper sWrapper; // object to compute similatiry measure given two files 
	private ArrayList<File> files = new ArrayList<File>(); 
	double[][] distanceMatrix;
	int option; 
	
	
	public FastMap(int r,String DirPath, int option) throws Exception {
		
		this.r = r;
		this.DirPath=DirPath;
		loadFiles();
		coordinates = new double[N][r];
		pivotDistances = new double[r];
		PV = new int[2][r];
		this.option = option;
		this.sWrapper = new SimilarityWrapper();
		distanceMatrix = new double[N][N];
		computeDistanceMatrix();
		
	}
	// compute the initial distances between every object
	private void computeDistanceMatrix() {
		
		double sim;
		for(int i=0;i<N;i++){
			for(int j=i+1;j<N;j++){
				try {
					
				
				   
					 sim  = sWrapper.getSimilarityForFiles(option, files.get(i).getAbsolutePath(), files.get(j).getAbsolutePath());
				if(sim==0)
					distanceMatrix[i][j]= Double.MAX_VALUE;
				else
					distanceMatrix[i][j] = 1/sim;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("DistanceMatrix computed");
	}

	private void loadFiles() {
		
		File folder = new File(DirPath);
		for( File file : folder.listFiles()){
			files.add(file);
		}
		this.N = files.size();
	}
	
	// find the coordinates of the objects in the reduced space
	
	public void getReducedSpace(){
		
		
		int iteration = 0;
		int R=r;
		while(R>0){
		
		int[] pv = chooseDistantObjects(iteration-1);
		PV[0][iteration]=pv[0];
		PV[1][iteration]=pv[1];
		
	    if(distancebtwObjects(pv[0], pv[1], iteration-1)==0){
	    	for(int i=0;i<N;i++){
	    		coordinates[i][iteration]=0;
	    	}
	    }
		double pivotDistance = distancebtwObjects(pv[0], pv[1], iteration-1);
		pivotDistances[iteration]=pivotDistance;
	    for(int i=0;i<N;i++){
	    	
	    	    	
	    	coordinates[i][iteration] = (Math.pow(distancebtwObjects(pv[0], i, iteration-1), 2)
	    								- Math.pow(distancebtwObjects(pv[1], i, iteration-1), 2)
	    								+ Math.pow(pivotDistance, 2))
	    								 / (2.0*pivotDistance);
	    }
	    
	    iteration++;
	    System.out.println("Iteration - "+iteration+" done.");
	    R--;
		}  
	}


// find the pivot objects using the projected distances of objects in the given iteration
	private int[] chooseDistantObjects(int iteration) {
		
		int[] pv = new int[2];
		
		double temp = Double.MIN_VALUE;
		double dist=0;
		//choosing initial pivot as first element
		for(int i=1;i<N;i++){
			
			dist = distancebtwObjects(0,i,iteration);
			if(dist > temp){
			    temp = dist;
				pv[0] = i;
			
		   }
		}
		temp = Double.MIN_VALUE;
		
		for(int j=0; j < N;j++){			
			
			dist = distancebtwObjects(pv[0],j,iteration);
			
			if( dist > temp){
				 temp = dist;
				 pv[1] = j;
				
			   }
		}
		return pv;
		}
	
	// find the projected distance in an iteration between two objects recursively 
	private double distancebtwObjects(int i, int i2, int iteration) {
		
		if(iteration<0)
			return getDistance(i, i2);
		else
			// checks the double point overflow error.
		{ double temp = Math.pow(distancebtwObjects(i, i2, iteration -1), 2)-Math.pow(coordinates[i][iteration]-coordinates[i2][iteration],2 );
			return Math.sqrt(temp<0?0:temp);
		}
	}
	// find the projected distance in an iteration between query object and other object recursively
private double distancebtwObjects(int i, File file, double[] newCoordinates,int iteration) {
		
		if(iteration<0)
			return getDistance(i, file);
		else{
			double temp = Math.pow(distancebtwObjects(i, file,newCoordinates, iteration -1), 2)-Math.pow(coordinates[i][iteration]-newCoordinates[iteration],2 );
			return Math.sqrt(temp<0?0:temp);
		}
	}
	
	// calculates the error in the mapping of objects from higher dimension to a lower dimension.
	public double calculateMappingError(){
		
		double sum1 =0;
		double sum2 =0;
		int i=0;
		int j=0;
		
		for(i=0;i< N; i++){
			
			for(j=i+1;j<N;j++){
				sum1 = sum1 + distancebtwObjects(i, j, -1);
			}
		}
		
		for(i=0;i< N; i++){
			
			for(j=i+1;j<N;j++){
				sum2 = sum2 + getEuclideanDistances(coordinates[i], coordinates[j]);
			}
		}
		return sum1-sum2;
	} 
		
	//Map the query object to the reduced dimension and find top k similar objects
		private int[] getSimilarSimulations(String filePath,int r, int k){
	    	  
	    	   
	    	   double[] newCoordiantes = new double[r];
	       	   
	    	   double pivotDistance = 0;
	    	   
	    	   File query = new File(filePath); 
	    	   
	    	   for(int i=0;i<r;i++){
	
	    		  pivotDistance = pivotDistances[i];
	    		   
	    		  newCoordiantes[i] = (double)(Math.pow(distancebtwObjects(PV[0][i], query,newCoordiantes, i-1), 2) - Math.pow(distancebtwObjects(PV[1][i], query,newCoordiantes, i-1), 2) + Math.pow(pivotDistance, 2))/(2*pivotDistance);
	    		   
	    	   }
	    	// assuming euclidean distance for finding distances between objs in target space for query by example.   	      
	    	   Set<Integer> results = findSortedDistancesbtwPoints(newCoordiantes);
	    	   Iterator<Integer> it =  results.iterator();
	    	   int i=0;
	    	   while(it.hasNext() && i<k) { 
	    		   System.out.println(files.get(it.next()).getName());
				i++;
			}
	    	   
	    	   return null;
	      }
// find distance measures of every object from the query object in the reduces space.
	private Set<Integer> findSortedDistancesbtwPoints(double[] newCoordiantes) {
		  
			HashMap<Integer,Double > results = new HashMap<Integer, Double>();
			for(int i=0;i<N;i++){
				results.put(i, getEuclideanDistances(newCoordiantes,coordinates[i]));
			}
			
			results = sortByValues(results);
			
			return results.keySet();
			
		}
// sort the hashmap based on values in increasing order.
	private static HashMap sortByValues(HashMap map) { 
		       List list = new LinkedList(map.entrySet());
		       
		       Collections.sort(list, new Comparator() {
		            public int compare(Object o1, Object o2) {
		               return ((Comparable) ((Map.Entry) (o1)).getValue())
		                  .compareTo(((Map.Entry) (o2)).getValue());
		            }
		       });

		       HashMap sortedHashMap = new LinkedHashMap();
		       for (Iterator it = list.iterator(); it.hasNext();) {
		              Map.Entry entry = (Map.Entry) it.next();
		              sortedHashMap.put(entry.getKey(), entry.getValue());
		       } 
		       return sortedHashMap;
		  }
		
	// calculates euclidean distance between two coordinates
	private double getEuclideanDistances(double[] newCoordiantes,
				double[] coordinates2) {
  
			double distance =0;
			for(int i=0;i<r;i++){
		    distance = distance + Math.pow(coordinates2[i]-newCoordiantes[i], 2);		
			}
			
			return Math.sqrt(distance);
		}


	private double getDistance(int i, int i2)  {

		if(i==i2)
			return 0;
		else
			return i<i2?distanceMatrix[i][i2]:distanceMatrix[i2][i];
		}
	

	private double getDistance(int i, File file)  {

		
		double sim=0;
		try {
						
			 sim  = sWrapper.getSimilarityForFiles(option, files.get(i).getAbsolutePath(), file.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				if(sim==0)
					return Double.MAX_VALUE;
				else
					return 1/sim;
		}
	
	
	
	public static void main(String[] args) {
			
			FastMap fm;
			try {
				fm = new FastMap(8, "/home/akshay/Desktop/phase2/wordfiles",6);
			    fm.getReducedSpace();
			    System.out.println("Mapping Error: " + fm.calculateMappingError());
			     
			    fm.getSimilarSimulations("/home/akshay/Desktop/phase2/query.csv_word.txt", 8, 4);
		 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		   
	}
}