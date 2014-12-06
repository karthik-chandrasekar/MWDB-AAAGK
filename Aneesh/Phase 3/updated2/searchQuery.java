package updated2;

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
import java.util.TreeMap;





public class searchQuery {
	

	public searchQuery(VaFile1 vaFileObj, List<Vector> queryVectors) {
		
		
		List<Vector> qVectors = queryVectors;
		
				
		for ( Vector vQ : qVectors ){
			System.out.println("-------------------------------------------");
			System.out.println("Next Query vector");
			List<Float> Li4vQ = new ArrayList<Float>(); // Li list for each query
			List<Float> Ui4vQ = new ArrayList<Float>(); // Ui list for each query
			List<Vector> nearestVector = new ArrayList<Vector>();
			List<Float> distance = new ArrayList<Float>();
			Map<Integer,Float> vQCandidate = new HashMap<Integer, Float>();
			//Vector nearestVector = null;
			//List<Float> vQCandidate = new ArrayList<Float>(); // Candidate list for each query
			
			System.out.println("-------------------------------------------");
			getLiUiForQueryVector(vQ,vaFileObj,Li4vQ,Ui4vQ);
			
		//	System.out.println(Li4vQ);
		//	System.out.println(Ui4vQ);
			getCandidates(Li4vQ,Ui4vQ,vQCandidate);
			System.out.println("No of Candidates : " + vQCandidate.size() );
			float nearestVectorDist = searchInCandidates(Li4vQ,Ui4vQ,vQCandidate,vaFileObj,vQ,nearestVector,distance);
			System.out.println("Nearest Vector : " + nearestVector );
			
			System.out.println("Nearest Query vector distance " + nearestVectorDist );

			// run phase 1
			// run phase 2
			
		}
		
	}// end of searchquery constructor
	
	private static Map<Integer, Float> sortByComparator(Map<Integer, Float> unsortMap) {
		 
		// Convert Map to List
		List<Map.Entry<Integer, Float>> list = 
			new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
			public int compare(Map.Entry<Integer, Float> o1,
                                           Map.Entry<Integer, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
		for (Iterator<Map.Entry<Integer, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	

	private float searchInCandidates(List<Float> li4vQ, List<Float> ui4vQ,
			Map<Integer, Float> vQCandidate, VaFile1 vaFileObj, Vector vQ, List<Vector> nearestVector, List<Float> distance2) {
		
		List<Float> distanceList = null;
		Map<Integer, Float> sortedMap = sortByComparator(vQCandidate); // To sort the hash map of candidate query vectors
		float leastDist = Float.MAX_VALUE;
		float maxDist = Float.MAX_VALUE;
		
		int distIndex = -1;
	//	System.out.println("Sorted map :");
	//	System.out.println(sortedMap);
		
		Iterator<Map.Entry<Integer, Float>> iterator = sortedMap.entrySet().iterator() ; // sort the map based on li values
		
		Set keyset=sortedMap.keySet();
		
		
		
		
		for(Integer f : sortedMap.keySet()){
			if(ui4vQ.get(f) < maxDist){
				maxDist = ui4vQ.get(f);
			}
		}
		
			leastDist = (Collections.max(sortedMap.values()));
		
			
		//System.out.println(leastDist);
		float distance = 0;
		
		while (iterator.hasNext()) { // to read each candidate VA entry and calculate the distance between the elements in the VA entry and the query vector
			//System.out.println("Next VA");
			Map.Entry<Integer, Float> candidateEntry = iterator.next(); // find the next candidate from the map
			//System.out.println(candidateEntry.getKey().toString());
			
					long key = Long.parseLong(candidateEntry.getKey().toString());
					
					// FIXME :add check for null in key	
					
					if(vaFileObj.vaFileEntryMap.containsKey(key) == true) {
					List<Vector> qVectors4mVA = vaFileObj.vaFileEntryMap.get(key).getEntries();
					float sum = (float) 0.0;
					
					for( int i = 0 ; i < qVectors4mVA.size() ; i++ ){
						distanceList = getDistance(qVectors4mVA.get(i),vQ); // adds the list of distance for each vector and query
						
						for(Float dist : distanceList){
								sum = sum + dist;  //get the euclidean distance
						}
						
						distance = (float) Math.sqrt(sum);
						
						qVectors4mVA.get(i).setDistance(distance);
						
						//if(distance < maxDist){ // filter out the vectors which are not near
							nearestVector.add(qVectors4mVA.get(i)); //add the vector as one of the nearest vector
						
							if(distance < leastDist){
								leastDist = distance;
								
							}
						//}
						
						
					}
					
					
					//System.out.println("distance : " + distance);
					System.out.println("maxDist : " + maxDist);
					System.out.println("leastDist : " + leastDist);
					
			}
			
			
			
		}
		
	//	System.out.println("distanceList" + distanceList);
		
		return  leastDist;
	}
	


	


	private List<Float> getDistance(Vector vector, Vector vQ) {
		// TODO Auto-generated method stub
		
		List<Float> distanceBwVec = new ArrayList<Float>();
		for(int i = 0 ; i < vector.getVector().size() ; i++){
			float dist =  Math.abs(vector.getVector().get(i) - vQ.getVector().get(i));
			
			
			distanceBwVec.add(dist*dist);
		}
		
		return distanceBwVec;
		
		
		
		
	}

	private void getCandidates(List<Float> li4vQ, List<Float> ui4vQ, Map<Integer, Float> vQCandidate) {
		float Lo = Float.MAX_VALUE;
		float Ho = Float.MAX_VALUE;
		
		
	   
		
		
		
		for(int i = 0 ; i < li4vQ.size() ; i++){
			if(li4vQ.get(i) < Ho){ // potential candidate
				
				vQCandidate.put(i,li4vQ.get(i)); // add the li to the candidate list to the index of the li which corresponds to the VA file
				//System.out.println(vQCandidate);
				
				if(li4vQ.get(i) < Lo){//new least
					Lo = li4vQ.get(i); //set new Lo
					Ho = ui4vQ.get(i); // Set new Ho
				}
				
				//remove the candidates whose Li is greater than Ho
				
				Iterator<Map.Entry<Integer, Float>> iterator = vQCandidate.entrySet().iterator() ;
				while (iterator.hasNext()) {
					
					Map.Entry<Integer, Float> candidateEntry = iterator.next();
		           // System.out.println(candidateEntry.getKey() +" :: "+ candidateEntry.getValue());
		            if(candidateEntry.getValue() > Ho ){
		            //You can remove elements while iterating.
		            	iterator.remove();
		            }
				}
					
					
				
//				int k = 0;
//				do{
//						k = 0;		
//						
//						for(Float vaQ : vQCandidate){
//							if(vaQ > Ho){
//								vQCandidate.remove(vQCandidate.indexOf(vaQ));
//								System.out.println(vQCandidate);
//								k = 1;
//								break;
//								
//							}
//						}
////					for(int j = 0 ; j < vQCandidate.size() ; j++){
////						if(vQCandidate.get(j) > Ho){
////							vQCandidate.remove(j);
////							System.out.println(vQCandidate);
////							k = 1;
////							break;
////							
////						}
////						
////					}
//				}while( k == 1 );
				
				
			}
		}
			
	}




	private void getLiUiForQueryVector(Vector vQ, VaFile1 vaFileObj, List<Float> li4vQ, List<Float> ui4vQ) {
		
	//	System.out.println(vQ.getVector());
		
		List<Float> Li = li4vQ;//new ArrayList<Float>(); // Li list for each query
		List<Float> Ui = ui4vQ;//new ArrayList<Float>(); // Ui list for each query
		
		Map<Long, VaFileEntry> vectorMap = vaFileObj.getVaFileEntryMap();
		Iterator iterV = vectorMap.entrySet().iterator();
		VaFileEntry vaV;
		
		//pass the query vector with the same file object parameters and  create a VA file for that Query-File combination
		VaFileEntry vaQ = new VaFileEntry(vQ, vaFileObj.dimMin, vaFileObj.dimMax, vaFileObj.dimMax.size(), vaFileObj.bLength); //dimMax.size() represents number of rows or dimensions
				
		
		String vectorCode;
		List<Float> vDimLen = new ArrayList<Float>();
		List<Float> vMinList = new ArrayList<Float>();
		List<Integer> vRegionData;//= new ArrayList<Integer>();
		List<Vector> vVectors;
		
					int j = 1;
					while(iterV.hasNext()){
						
						
						Map.Entry pairsV = (Map.Entry)iterV.next();
						vaV = (VaFileEntry) pairsV.getValue(); // gets the VA file from the hash map iteratively
						vectorCode = vaV.getCode(); //gets the VA code for the whole file
						vDimLen = vaV.getDimLength(); // gets the length of each partition per dimension
						vMinList = vaV.getMinList(); // gets the first value per dimension
						vRegionData = vaV.getRegionData(); //gets the starting partition point per dimension
						vVectors = vaV.getEntries();
						float l = 0;
						float u = 0;
						
						float lsum = 0;
						float usum = 0;
						
	
						
					//	System.out.println("vaQ Size : " + vaQ.dimLength.size());
					//	System.out.println("vaV Size : " + vaV.dimLength.size());
						
						
						for(int k=0;k<vRegionData.size() ; k++ ){ // for each of the dimension for each vector
							l = 0;
							u = 0;
							
						//	System.out.println("vRegionData.get(k) : " + vRegionData.get(k) );
						//	System.out.println("vaQ.getRegionData().get(k) : " + vaQ.getRegionData().get(k));
							
							if ( vRegionData.get(k) < vaQ.getRegionData().get(k) ){ // li = E(Vq - Vv[r+1])   
								// for Li, distance between the vector  in that dimension and the nearest partition point + 1 (since region(q) is greater than region(r))
								// for Ui, distance between the vector  in that dimension and the nearest partition point + 1 (since q is greater than r)
							 	
								// (Vq - Vv[r+1]) ==> minimum value per dimension + (region partition number * partition length + 1*partition length)
								// Vq ==> the vector value as per the dimension
								
								
								l =  vQ.getVector().get(k) - (vMinList.get(k)+( (vRegionData.get(k) * vDimLen.get(k)) + vDimLen.get(k)))  ;
								//System.out.println(l);
								
								u =  vQ.getVector().get(k) - (vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k))) ;
								
							}
							
							else if (vRegionData.get(k) == vaQ.getRegionData().get(k) ){ // Li = 0
								// for Li,  distance is zero as they both are in the same region
								// for Ui, distance between the vector  in that dimension and the nearest partition point + 1 (since q is greater than r)
								
								l = 0;
								
							//	System.out.println("LHS : " + ((vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k) ));
							//	System.out.println("RHS : " + (   vQ.getVector().get(k) - (  vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k))  )   ));
								
								
								if(   (  Math.abs((vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k) ))   >   Math.abs(vQ.getVector().get(k) - (vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)) + vDimLen.get(k))) ){
									u =  Math.abs((vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k) ); 
								}
								else{
									u =   Math.abs(vQ.getVector().get(k) - (  vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)) + vDimLen.get(k) ) )  ;
								}
							}
							
							else if ( vRegionData.get(k) > vaQ.getRegionData().get(k)){  // li = E(Vq - Vv[r])
								// for Li, distance between the vector  in that dimension and the nearest partition point (since region(q) is less than region(r))
								// for Ui, distance between the vector  in that dimension and the nearest partition point + 1 (since q is greater than r)
								
								l = (vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k); 
								
								u =  (vMinList.get(k)+( (vRegionData.get(k) * vDimLen.get(k)) + vDimLen.get(k))) - vQ.getVector().get(k) ;
								
							}
							
							//System.out.println(l);
							//System.out.println(u);
							lsum = lsum + (l * l); // squaring it for euclidean distance
							usum = usum + (u * u);
						}
						
						
						Li.add((float) Math.sqrt(lsum));
						Ui.add((float) Math.sqrt(usum));
						
						
					}//end of vector while
					
			//		System.out.println("Li : " + Li);
				//	System.out.println("Ui : " + Ui);

		
		
	}


	
	

}


