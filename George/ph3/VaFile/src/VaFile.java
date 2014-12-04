import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of vaFile 
 * @author george
 *
 */
public class VaFile {

	private int bLength;
	private int noOfDims;
	private String epiFileName;
	
	// This would be the size of the file in bytes.
	// Count the number of zeroes for this.
	private int size;
	List<Float>dimMax = new ArrayList<Float>();
	List<Float>dimMin = new ArrayList<Float>();
	List<Integer>bitDims = new ArrayList<Integer>();
	List<String> vaList = new ArrayList<String>();
	
	public VaFile(int bLength, String epiFileName) {
		super();
		this.bLength = bLength;
		this.epiFileName = epiFileName;
		this.initData(epiFileName);
		this.vaList = this.getVaList(epiFileName);
	}

	private String addData(String data) {
		// Prune the data a bit before sending it through. 
		Vector vect = new Vector(data);
		return this.getBinary(vect);
	}
	
	private void updateMinMaxDims(String line) {
		String words[] = line.split(",");
		int dim;
		for (int i = 3; i < words.length; i ++) {
			dim = i - 3;
			Float dimValue = Float.parseFloat(words[i]);
			if(dimMax.get(dim) < dimValue) {
				dimMax.set(dim, dimValue);
			} else if (dimMax.get(dim) > dimValue) {
				dimMax.set(dim, dimValue);
			}
		}
	}
	
	private boolean initData(String epiFile) {
		// Update input data structures over here.
		// Update the max and Min
		// Find No Of Dims
		BufferedReader br;
		try {
				br = new BufferedReader(new FileReader(epiFile));
				String line;
				line = br.readLine();
				String words[] = line.split(",");
				
				// Subtracting the excess baggage of f,s,t
				this.noOfDims = words.length - 3;
				// Adding the initialization of paper stuff. 
				for(int i = 3; i < words.length; i++) {
					int dim = i - 3;
					dimMax.add(dim, Float.parseFloat(words[i]));
					dimMin.add(dim, Float.parseFloat(words[i]));
					int modVal = 0;
					modVal = this.bLength % this.noOfDims;
					if(dim < modVal) {
						modVal = 1;
					} else {
						modVal = 0;
					}
					int dimBit = (this.bLength/this.noOfDims) + modVal;
					this.bitDims.add(dim, dimBit);
				}
				
				do {
					this.updateMinMaxDims(line);
					line = br.readLine();
				} while(line != null);
				
				br.close();
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public List<String> getVaList(String epiFile) { 
		// Parse through the file again, based on the minMax 
		// get the dimensionality value.
		List<String> vaList = new ArrayList<String>();
		List <String>uniqueWords = new ArrayList<String>();
		
		BufferedReader br;
		try {
				br = new BufferedReader(new FileReader(epiFile));
				String line;
				while ((line = br.readLine()) != null) {
					//System.out.println(line);
					if(uniqueWords.contains(line) == false) {
						uniqueWords.add(line);
				    } else {
				    	continue;
					}
					vaList.add(this.addData(line));
				}
				br.close();
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vaList;
	}
	
	private String getBinary(Vector vector) {
		//TODO Return binary Value based on the epidemic values.
		List <Float>vectVal = vector.getVector();
		String bin = "";
		for(int i = 0 ; i < this.noOfDims; i++) {
			float min = this.dimMin.get(i);
			float max = this.dimMax.get(i);
			float range = max - min;
			float val = vectVal.get(i);
			int noOfRegions = (int)Math.pow(2.0, this.bitDims.get(i));
			float sizeOfOneRegion = (float) (range/noOfRegions);
			int currRegion = (int) ((val - min)/sizeOfOneRegion);
			String binRegion = Integer.toBinaryString(currRegion);
			if (binRegion.length() < this.bitDims.get(i)) {
				for(int i1 = 0; i1 < (this.bitDims.get(i1) - binRegion.length()); i1++) {
					binRegion = "0" + binRegion;
				}
			} else {
				System.out.println("Adv BinRegion =" + binRegion);
				binRegion = binRegion.substring(binRegion.length() - this.bitDims.get(i) , binRegion.length());
			   }
			System.out.println(currRegion);
			bin = bin + binRegion;
			// Use function to convert. Integer.toBinaryString(i);
		}
		return bin;
	}

	public void getVaFileEntry(String bitNumber) {
		
	    
	}
	
	@Override
	public String toString() {
		return "VaFile [bLength=" + bLength + ", noOfDims=" + noOfDims
				+ ", epiFileName=" + epiFileName + ", dimMax=" + dimMax
				+ ", dimMin=" + dimMin + ", bitDims=" + bitDims + ", vaList="
				+ vaList + "]";
	}
}
