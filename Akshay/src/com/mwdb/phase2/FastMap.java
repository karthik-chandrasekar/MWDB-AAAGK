package com.mwdb.phase2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

	int N;
	int r;
	double[][] coordinates; 
	double[] pivotDistances;
	int[][] PV;
	String DirPath;
	
	private ArrayList<File> files = new ArrayList<File>();
	
	public FastMap(int N,int r,String DirPath) {
		this.N = N;
		this.r = r; 
		coordinates = new double[N][r];
		pivotDistances = new double[r];
		PV = new int[2][r];
		this.DirPath=DirPath;
	}
	
	public void ComputeDistanceMatrix() {
		
		File folder = new File(DirPath);
		/*for( File file : folder.listFiles()){
			files.add(file);
		}*/
		files.add(new File(""));
		files.add(new File(""));
		files.add(new File(""));
		files.add(new File(""));
		files.add(new File(""));
		
		for(int i=0;i<N;i++){
    	   
    	   for(int j=i+1;j<N;j++){
    		   
    		   //OriginaldistanceMatrix[i][j]= getSimilarity(files.get(i), files.get(j));
    	   }
       }		
		
	}
	
	public void getReducedSpace(){
		
		//distanceMatrix = deepCopy(OriginaldistanceMatrix);
		
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
	    	
	    	    	
	    	coordinates[i][iteration] =  (Math.pow(distancebtwObjects(pv[0], i, iteration-1), 2) +
	    								- Math.pow(distancebtwObjects(pv[1], i, iteration-1), 2)
	    								+ Math.pow(pivotDistance, 2))
	    								 / (2.0*pivotDistance);
	    }
	    
	    iteration++;
	    R--;
		}  
	}

	private double[][] deepCopy(double[][] originaldistanceMatrix2) {
		
		 double[][] distancematrix = new double[originaldistanceMatrix2.length][];
		 for (int i=0; i <originaldistanceMatrix2.length; i++) {
			 distancematrix[i] = Arrays.copyOf(originaldistanceMatrix2[i], originaldistanceMatrix2[i].length);
		      }		
         return distancematrix;
		 
	}

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
	
	private double distancebtwObjects(int i, int i2, int iteration) {
		
		if(iteration<0)
			return getSimilarity(i, i2);

		/*if((i==PV[0][iteration] && i2== PV[1][iteration]) || (i==PV[1][iteration] && i2== PV[0][iteration]))
			return pivotDistances[iteration];
		*/
		else
			return Math.sqrt(Math.pow(distancebtwObjects(i, i2, iteration -1), 2)-Math.pow(coordinates[i][iteration]-coordinates[i2][iteration],2 ));
		
	}

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
				sum2 = sum2 + distancebtwObjects(i, j, r-1);
			}
		}
		return sum1-sum2;
	} 
		// assuming euclidian distance for finding distances between objs in target space for query by example.
	
	private int[] getSimilarSimulations(String filePath,int r, int k){
	    	  
	    	   double[] distances = getDistancesWithAllObjects(filePath);
	    	   
	    	   double[] newCoordiantes = new double[r];
	    
	    	   //distanceMatrix = deepCopy(OriginaldistanceMatrix);
	    	   
	    	   double pivotDistance = 0;
	    	   
	    	  
	    	   
	    	   for(int i=0;i<r;i++){
	    		
	    		  pivotDistance = distancebtwObjects(PV[0][i], PV[1][i], i-1);
	    		   
	    		  newCoordiantes[i] = (double)(Math.pow(distances[PV[0][i]], 2) + Math.pow(pivotDistance, 2)- Math.pow(distances[PV[1][i]], 2))/(2*pivotDistance);
	    		   
	    		  for(int j=0;j<N;j++){
	    			  distances[j]= Math.sqrt(Math.pow(distances[j], 2)-Math.pow(coordinates[j][i]-newCoordiantes[i],2) );
	    		  }
	    	   }
	   	      
	    	   Set<Integer> results = findSortedDistancesbtwPoints(newCoordiantes);
	    	   Iterator<Integer> it =  results.iterator();
	    	   int i=0;
	    	   while(it.hasNext() && i<k) { 
	    		   System.out.println(it.next());
				i++;
			}
	    	   
	    	   return null;
	      }

	private Set<Integer> findSortedDistancesbtwPoints(double[] newCoordiantes) {
		  
			HashMap<Integer,Double > results = new HashMap<Integer, Double>();
			for(int i=0;i<N;i++){
				results.put(i, getEuclideanDistances(newCoordiantes,coordinates[i]));
			}
			
			results = sortByValues(results);
			
			return results.keySet();
			//results
		}

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
		
	private double getEuclideanDistances(double[] newCoordiantes,
				double[] coordinates2) {
  
			double distance =0;
			for(int i=0;i<r;i++){
		    distance = distance + Math.pow(coordinates2[i]-newCoordiantes[i], 2);		
			}
			
			return Math.sqrt(distance);
		}

	private double[] getDistancesWithAllObjects(String filePath) {
			
			File folder = new File(DirPath);
			File searchInput = new File(filePath);
			
			double[] result = new double[N];
			
			for(int i=0;i<N;i++){
				
				result[i] = getSimilarity(i,0);
			
			}
			
			return result;
		}

	private double getSimilarity(int i, int i2) {

		if(i==i2)
			return 0;
		else
			return 2.5;
		}
		
	public static void main(String[] args) {
			
			FastMap fm = new FastMap(50, 10, "");
		    //fm.ComputeDistanceMatrix();
		    fm.getReducedSpace();
		 
		    System.out.println("Mapping Error: " + fm.calculateMappingError());
	     
		    fm.getSimilarSimulations("", 10, 5);
	
	}
}