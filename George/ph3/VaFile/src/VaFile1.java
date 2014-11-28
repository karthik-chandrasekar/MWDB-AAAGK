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
	
	public VaFile1(int bLength, String epiFileName) {
		// Get Unique lines out
		dimMax = new ArrayList<Float>();
		dimMin = new ArrayList<Float>();
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
		}
		// Form VaFileEntries from each line
		// Insert the entries to the tree
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
	
	public VaFileEntry getVaFileEntry(String fileName) {
		// Check in the tree and respond with the Appropriate entry.
		return null;
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
}
