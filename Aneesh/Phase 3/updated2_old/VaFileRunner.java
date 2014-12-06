package updated2;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VaFileRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = new File("").getAbsolutePath();
		
		//read the query vector and get the list of vectors
		VaFile1 vaQueryObject = new VaFile1(20, filePath.concat("/src/updated2/sim/epidemic_word_files/avgn2.csv"));
		//System.out.println("Size of VaFile : " + vaQueryObject.getSize() + "bytes");
		
		List<Vector> queryVectors  = getQueryVectors(vaQueryObject);
		
		//read file 1 and get the VAFile object whch ahs all the VAs
		VaFile1 vaFileObj = new VaFile1(20, filePath.concat("/src/updated2/sim/epidemic_word_files/avgn2.csv"));
		System.out.println("Size of VaFile : " + vaFileObj.getSize() + "bytes");
		
		
		// Runa  search for each file against the list of vectors
		searchQuery s  = new searchQuery(vaFileObj,queryVectors);
	}

	private static List<Vector> getQueryVectors(VaFile1 vaFileObj) {
		
		Map<Long, VaFileEntry> queryMap = vaFileObj.getVaFileEntryMap();
		Iterator iterQ = queryMap.entrySet().iterator();
		VaFileEntry vaQ;
		String queryCode;
		List<Float> qDimLen = new ArrayList<Float>();
		List<Float> qMinList = new ArrayList<Float>();
		List<Integer> qRegionData;//= new ArrayList<Integer>();
		List<Vector> queryV = new ArrayList<Vector>();
		
		while(iterQ.hasNext()){
			Map.Entry pairs = (Map.Entry)iterQ.next();
			vaQ = (VaFileEntry) pairs.getValue();
			
			for(int i = 0 ; i < vaQ.getEntries().size() ; i++ ){
				System.out.println(vaQ.getEntries().get(i));
				queryV.add(   vaQ.getEntries().get(i)   );
			}
						
		}
 		
		
		return queryV;
	}
}
