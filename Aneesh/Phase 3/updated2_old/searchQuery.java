package updated2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class searchQuery {

	public searchQuery(VaFile1 vaFileObj, List<Vector> queryVectors) {
		// TODO Auto-generated constructor stub
		
		List<Vector> qVectors = queryVectors;
				
		for ( Vector vQ : qVectors ){
			
			getLiUiForQueryVector(vQ,vaFileObj);

			// run phase 1
			// run phase 2
			
		}
		
	}// end of searchquery constructor
	
	
	

	private void getLiUiForQueryVector(Vector vQ, VaFile1 vaFileObj) {

		
		List<Float> Li = new ArrayList<Float>(); // Li list for each query
		List<Float> Ui = new ArrayList<Float>(); // Ui list for each query
		
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
						
						
						for(int k=0;k<vRegionData.size() ; k++ ){ // for each of the dimension for each vector
							l = 0;
							u = 0;
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
								
								if(   ((vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k) )   >   (   vQ.getVector().get(k) - (  vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k))  )   ) ){
									u =  ((vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k) ); 
								}
								else{
									u =    vQ.getVector().get(k) - (  vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k))  )   ;
								}
							}
							
							else if ( vRegionData.get(k) > vaQ.getRegionData().get(k)){  // li = E(Vq - Vv[r])
								// for Li, distance between the vector  in that dimension and the nearest partition point (since region(q) is less than region(r))
								// for Ui, distance between the vector  in that dimension and the nearest partition point + 1 (since q is greater than r)
								
								l = (vMinList.get(k)+ (vRegionData.get(k) * vDimLen.get(k)))  - vQ.getVector().get(k); 
								
								u =  (vMinList.get(k)+( (vRegionData.get(k) * vDimLen.get(k)) + vDimLen.get(k))) - vQ.getVector().get(k) ;
								
							}
							
							lsum = lsum + (l * l);
							usum = usum + (u * u);
						}
						
						
						Li.add((float) Math.sqrt(lsum));
						Ui.add((float) Math.sqrt(usum));
						
						
					}//end of vector while
					
					System.out.println("Li : " + Li);
					System.out.println("Ui : " + Ui);

		
		
	}


	
	

}
