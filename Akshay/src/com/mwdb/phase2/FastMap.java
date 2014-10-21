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
	double[][] OriginaldistanceMatrix;
	double[][] distanceMatrix;
	int[][] PV;
	String DirPath;
	
	private ArrayList<File> files = new ArrayList<File>();
	
	public FastMap(int N,int k,String DirPath) {
		this.N = N;
		this.r= k; 
		coordinates = new double[N][k];
		PV = new int[2][k];
		this.DirPath=DirPath;
	}
	
	private double[][] ComputeDistanceMatrix() {
		
		File folder = new File(DirPath);
		
		for( File file : folder.listFiles()){
			files.add(file);
		}
		
       for(int i=0;i<files.size();i++){
    	   
    	   for(int j=i+1;j<files.size();j++){
    		   
    		   OriginaldistanceMatrix[i][j]= getSimilarity(files.get(i), files.get(j));
    	   }
       }		
		
		return null;
	}
	
	private void getReducedSpace(int k, double[][] OriginaldistanceMatrix){
		
		distanceMatrix = deepCopy(OriginaldistanceMatrix);
		
		int iteration = 0;
		
		while(k>0){
		
			iteration++;
		
		int[] pv = chooseDistantObjects(distanceMatrix);
		PV[0][k]=pv[0];
		PV[1][k]=pv[1];
		
	    if(distanceMatrix[pv[0]][pv[1]]==0){
	    	for(int i=0;i<N;i++){
	    		coordinates[i][iteration]=0;
	    	}
	    }
		
	    for(int i=0;i<N;i++){
	    	coordinates[i][iteration] = (double)(Math.pow(distanceMatrix[pv[0]][i], 2) + Math.pow(distanceMatrix[pv[0]][pv[1]], 2)-Math.pow(distanceMatrix[pv[1]][i], 2))/(2*distanceMatrix[pv[0]][pv[1]]);
	    }
	    
	    for(int i=0;i<N;i++){
	    	for(int j=i+1;j<N;j++){
	    		
	    		distanceMatrix[i][j] = Math.sqrt(Math.pow(distanceMatrix[i][j], 2) - Math.pow(coordinates[i][iteration] - coordinates[j][iteration], 2));
	    	}
	    }
	    k--;
		}
	    
	}

	private double[][] deepCopy(double[][] originaldistanceMatrix2) {
		
		 double[][] distancematrix = new double[originaldistanceMatrix2.length][];
		 for (int i=0; i <originaldistanceMatrix2.length; i++) {
			 distancematrix[i] = Arrays.copyOf(originaldistanceMatrix2[i], originaldistanceMatrix2[i].length);
		      }		
         return distancematrix;
		 
	}

	private int[] chooseDistantObjects(double[][] distanceMatrix) {
		
		int[] pv = new int[2];
		
		double temp = Double.MIN_VALUE;
		//choosing initial pivot as first element
		for(int i=1;i<distanceMatrix.length;i++){
			
			if(distanceMatrix[0][i] > temp){
			    temp = distanceMatrix[0][i];
				pv[0] = i;
			
		   }
		}
		temp = Double.MIN_VALUE;
		
		for(int j=0; j < distanceMatrix.length;j++){
			
			
			if( j > pv[0] && distanceMatrix[pv[0]][j] > temp){
				 temp = distanceMatrix[pv[0]][j];
				 pv[1] = j;
				
			   }
			else if( j < pv[0] && distanceMatrix[j][pv[0]] > temp){
				 temp = distanceMatrix[j][pv[0]];
				 pv[1] = j;
				
			   }
		}
		return pv;
		}
	
	private double calculateMappingError(){
		double sum1 =0;
		double sum2 =0;
		int i=0;
		int j=0;
		
		for(i=0;i< OriginaldistanceMatrix.length; i++){
			
			for(j=i+1;j<OriginaldistanceMatrix.length;j++){
				sum1 = sum1 + OriginaldistanceMatrix[i][j];
			}
		}
		
		for(i=0;i< distanceMatrix.length; i++){
			
			for(j=i+1;j<distanceMatrix.length;j++){
				sum2 = sum2 + distanceMatrix[i][j];
			}
		}
		return sum1-sum2;
	} 
	// assuming euclidian distance for finding distances between objs in target space for query by example.
	
	      private int[] getSimilarSimulations(String filePath,int r, int k){
	    	  
	    	   double[] distances = getDistancesWithAllObjects(filePath);
	    	   
	    	   double[] newCoordiantes = new double[k];
	    	   
	    	   for(int i=0;i<k;i++){
	    		   newCoordiantes[i] = (double)(Math.pow(distances[PV[0][i]], 2) + Math.pow(OriginaldistanceMatrix[PV[0][i]][PV[1][i]], 2)- Math.pow(distances[PV[1][i]], 2))/(2*OriginaldistanceMatrix[PV[0][i]][PV[1][i]]);
	    	   }
	   	      
	    	   Set<Integer> results = findSortedDistancesbtwPoints(newCoordiantes);
	    	   
	    	   
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
		       // Defined Custom Comparator here
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
				
				result[i] = getSimilarity(files.get(i),searchInput);
			}
			
			return result;
		}

		private double getSimilarity(File file, File searchInput) {
			
			return 2.5;
		}
}
