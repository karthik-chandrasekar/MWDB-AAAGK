import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents VaFile.
 *
 */
public class VaFile1 {

	private List<Float>dimMax; 
	private List<Float>dimMin;
	private IndexTree vaTree;
	private int bLength;
	
	public VaFile1(int bLength, String epiFileName) {
		// Get Unique lines out
		dimMax = new ArrayList<Float>();
		dimMin = new ArrayList<Float>();
		vaTree = new IndexTree();
		this.bLength = bLength;
		List <String>uniqueLines = this.getUniqueLines(epiFileName);
		List<Float> line1Vect = new Vector(uniqueLines.get(0)).getVector();
	
		for(int dim = 0; dim < line1Vect.size(); dim++) {
			dimMax.add(dim, line1Vect.get(dim));
			dimMin.add(dim, line1Vect.get(dim));
		}
		
		for(String line : uniqueLines) {
			this.updateMinMaxDims(new Vector(line));
		}
		
		for(String line : uniqueLines) {
			Vector vector =  new Vector(line);
			VaFileEntry vaFileEntry = new VaFileEntry(vector, dimMin, dimMax, dimMax.size(), bLength);
			int key = Integer.parseInt(vaFileEntry.code, 2);
			Node exactEntry = vaTree.findExact(key);
			if(exactEntry != null) {
				exactEntry.getVaFileEntry().addEntry(vector);
			} else {
				Node entry = new Node(key, vaFileEntry);
				vaTree.addNode(entry);
			}
		}
	}
	
	private void updateMinMaxDims(Vector vector) {
		
		List<Float> dimValList = vector.getVector();
		for (int dim = 0; dim < dimValList.size(); dim++) {
			Float dimValue = dimValList.get(dim);
			if(dimMax.get(dim) < dimValue) {
				dimMax.set(dim, dimValue);
			} else if (dimMax.get(dim) > dimValue) {
				dimMax.set(dim, dimValue);
			}
		}
	}
	
	public VaFileEntry getVaFileEntry(String code) {
		// Check in the tree and respond with the Appropriate entry.
		return this.getVaFileEntry(Integer.parseInt(code, 2));
	}
	
	public VaFileEntry getVaFileEntry(int key) {
		// Check in the tree and respond with the Appropriate entry.
		Node foundNode = vaTree.find(key);
		return foundNode.getVaFileEntry();
	}
	
	public IndexTree getVaTree() {
		return vaTree;
	}
	
	public int getSize() {
		//TODO Use getter instead.
		return this.bLength * vaTree.noOfElements; 
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
		return "VaFile1 [dimMax=" + dimMax + ", dimMin=" + dimMin + ", vaTree="
				+ vaTree + "]";
	}
}
