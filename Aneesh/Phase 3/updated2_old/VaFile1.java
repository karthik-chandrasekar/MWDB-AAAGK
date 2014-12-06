package updated2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents VaFile.
 *
 */
public class VaFile1 {

	List<Float>dimMax; 
	List<Float>dimMin;
	Map<Long,VaFileEntry> vaFileEntryMap;
	int bLength;
	
	public VaFile1(int bLength, String epiFileName) {
		// Get Unique lines out
		dimMax = new ArrayList<Float>();
		dimMin = new ArrayList<Float>();
		this.bLength = bLength;
		this.vaFileEntryMap = new HashMap<Long, VaFileEntry>();
		List <String>uniqueLines = this.getUniqueLines(epiFileName);
		List<Float> line1Vect = new Vector(uniqueLines.get(0)).getVector();
	
		// Size of vector in line 1 equals dimensions
		for(int dim = 0; dim < line1Vect.size(); dim++) {
			dimMax.add(dim, line1Vect.get(dim));
			dimMin.add(dim, line1Vect.get(dim));
		}
		
		for(String line : uniqueLines) { // to get the minimum and maximum values for each dimension by iterating through all vectors
			this.updateMinMaxDims(new Vector(line));
		}
		int i = 0;
		for(String line : uniqueLines) {
			Vector vector =  new Vector(line);
//			System.out.println("----------------------");
//			System.out.println("Unique line : " + i++ + " : " + line);
			
			VaFileEntry vaFileEntry = new VaFileEntry(vector, dimMin, dimMax, dimMax.size(), bLength); //dimMax.size() represents number of rows or dimensions
			long key = Long.parseLong(vaFileEntry.getCode(), 2);
			if(vaFileEntryMap.containsKey(key) == true) {
				vaFileEntryMap.get(key).getEntries().add(vector);
			} else {
				vaFileEntryMap.put(key, vaFileEntry);
			}
		}
	}
	
	private void updateMinMaxDims(Vector vector) {
		
		List<Float> dimValList = vector.getVector();
		for (int dim = 0; dim < dimValList.size(); dim++) {
			Float dimValue = dimValList.get(dim);
			if(dimMax.get(dim) < dimValue) {
				dimMax.set(dim, dimValue);
			} 
			if(dimMin.get(dim) > dimValue){
				dimMin.set(dim, dimValue);
			}
		}
	}
	
	public Map<Long, VaFileEntry> getVaFileEntryMap() {
		return vaFileEntryMap;
	}

	public void setVaFileEntryMap(Map<Long, VaFileEntry> vaFileEntryMap) {
		this.vaFileEntryMap = vaFileEntryMap;
	}

	public VaFileEntry getVaFileEntry(String code) {
		// Check in the tree and respond with the Appropriate entry.
		return this.getVaFileEntry(Integer.parseInt(code, 2));
	}
	
	public VaFileEntry getVaFileEntry(long key) {
		// Check in the tree and respond with the Appropriate entry.
		return this.vaFileEntryMap.get(key);
	}
	
	public int getSize() {
		//TODO Use getter instead.
		return this.bLength * vaFileEntryMap.size(); 
	}

	private  List<String> getUniqueLines(String epiFileName) {

		List <String>uniqueWords = new ArrayList<String>();
		List <String>bufferWords = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(epiFileName));
			String line;
			while ((line = br.readLine()) != null) {
				String bufferLine = line.substring(6);
				if(bufferWords.contains(bufferLine) == false) {
					//System.out.println(line);
					uniqueWords.add(line);
					bufferWords.add(bufferLine);
				}
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return uniqueWords;
	}

	@Override
	public String toString() {
		return "VaFile1 [dimMax=" + dimMax + ", dimMin=" + dimMin
				+ ", vaFileEntryMap=" + vaFileEntryMap + ", bLength=" + bLength
				+ "]";
	}
}
