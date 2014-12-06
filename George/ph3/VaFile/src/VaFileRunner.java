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
		
		List<Vector> nearestVectors = new ArrayList<Vector>();
		
		String filePath = new File("").getAbsolutePath();
		int b = 20;
		int k = 5;
		VaFile1 vaQueryObject = new VaFile1(b, filePath.concat("/src/query.csv"));
		List<Vector> queryVectors  = getQueryVectors(vaQueryObject);
		
		File[] files = new File("src/DATA").listFiles();
		int candidates = 0;
		int size = 0;
		for (File file : files) {
			VaFile1 vaFileObj = new VaFile1(b, file.getAbsolutePath());
			searchQuery s  = new searchQuery(vaFileObj,queryVectors);
			nearestVectors.addAll(s.getNearestVectors());
			candidates += s.getNoOfCandiates();
			size += vaFileObj.getSize();
		}
		
		System.out.println("Total Size of Index Entries " + size );
		System.out.println("Size Accessed from Index " + candidates * b);
		RankGenerator rankG = new RankGenerator(nearestVectors, k);
		System.out.println("Total Number of compressed Vectors Used " + candidates);
		System.out.println("The top " + k + " files are");
		for(String file : rankG.getTopK()) {
			System.out.println(file);
		}
		
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
				//System.out.println(vaQ.getEntries().get(i));
				queryV.add(   vaQ.getEntries().get(i)   );
			}
						
		}
 		
		
		return queryV;
	}
}
