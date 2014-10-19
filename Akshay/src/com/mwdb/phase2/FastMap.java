package com.mwdb.phase2;

import java.util.Arrays;
import java.util.HashMap;

public class FastMap {

	int N;
	int r;
	double[][] coordinates; 
	double[][] OriginaldistanceMatrix;
	double[][] distanceMatrix;
	int[][] PV;
	
	public FastMap(int N,int k) {
		this.N = N;
		this.r= k; 
		coordinates = new double[N][k];
		PV = new int[2][k];
	}
	
	private double[][] ComputeDistanceMatrix(String DirPath) {
		return null;
	}
	
	private void getReducedSpace(int k, double[][] OriginaldistanceMatrix){
		
		distanceMatrix = deepCopy(OriginaldistanceMatrix);
		
		int iteration = 0;
		
		while(k<=0){
		
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
	    	for(int j=0;j<N;j++){
	    		if(i==j){
	    			continue;
	    		}
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
			
		for(int j=0; j < distanceMatrix.length;j++){
			
			if(pv[0] == j)
				continue;
			
			if(distanceMatrix[pv[0]][j] > temp){
				 temp = distanceMatrix[pv[0]][j];
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
	   	      
	    	   int[] results = findSortedDistancesbtwPoints(newCoordiantes);
	    	   
	    	   
	    	  return null;
	      }

		private int[] findSortedDistancesbtwPoints(double[] newCoordiantes) {
		  
			HashMap<Integer,Double > results = new HashMap<Integer, Double>();
			for(int i=0;i<N;i++){
				results.put(i, getEuclideanDistances(newCoordiantes,coordinates[i]));
			}
			
			return null;
			//results
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
			// TODO Auto-generated method stub
			return null;
		}
}
