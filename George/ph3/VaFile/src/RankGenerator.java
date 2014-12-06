import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class outputs the top k ranked files if it exists
 * 
 * @author george
 *
 */
public class RankGenerator {

	private List <Vector> resultVector;
	
	private List<String> topK;
	private int k;
	
	public RankGenerator(List<Vector> resultVector, int k) {
		super();
		this.resultVector = resultVector;
		this.k = k;
		this.topK = this.getTopKFileFactor();
	}

	/**
	 * Returns the top k files. 
	 * @return
	 */
	private <String>List getTopKFileFactor() {
		
		Map<String, Float> rankList = new HashMap<String, Float>();
		Map<String, Integer> rankCount = new HashMap<String, Integer>();
		// Have a map Of Files to the Vectors.
		// When a file is encountered add the distance corresponding to it.
		for(int i=0 ; i < resultVector.size(); i++) {
			Vector closest = resultVector.get(i);
			Float dist = closest.getDistance();
			String file = (String) closest.getFile();
			if(rankList.containsKey(file)) {
				rankCount.put(file, rankCount.get(file) + 1);
				rankList.put(file, rankList.get(file) + dist);
			} else {
				rankList.put(file, dist);
				rankCount.put(file, 1);
			}
		}
		return this.getTopKFromMap(rankList, rankCount);
	}
	
	private <String>List getTopKFromMap(Map <String, Float> rankList, Map<String, Integer> rankCount) {
		
		List<String> fileList = new ArrayList();
		int k = this.k;
		Map<Float, String> revMap = new TreeMap<Float, String>();
		for(Entry<String, Integer> rankC : rankCount.entrySet()) {
			String key = rankC.getKey();
			int count = rankC.getValue();
			rankList.put(key, rankList.get(key)/count);
			float newVal = rankList.get(key).floatValue();
			if(revMap.containsKey(newVal) == true) {
				newVal = (float) (newVal + 0.0000001);
			}	
			revMap.put(newVal, key);
		}
		
		// Sort RankList Based OnValue
		Iterator<Entry<Float, String>> iterator = revMap.entrySet().iterator();
		while(iterator.hasNext() && k != 0) {
			k--;
			fileList.add(iterator.next().getValue());
		}
		return fileList;
	}

	public List<String> getTopK() {
		return topK;
	}

	public void setTopK(List<String> topK) {
		this.topK = topK;
	}
}
