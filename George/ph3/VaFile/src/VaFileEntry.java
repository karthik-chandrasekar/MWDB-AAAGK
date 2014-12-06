import java.util.ArrayList;
import java.util.List;

/* Represents one entry in the VaFile */
public class VaFileEntry {

	public	 String code;
	List<Vector> entries;
	public List<Float> minList; 
	public List<Float> dimLength;
	public List<Float> maxList;
	public List<Integer> regionData;

	public List<Integer> getRegionData() {
		return regionData;
	}

	public void setRegionData(List<Integer> regionData) {
		this.regionData = regionData;
	}

	private int noOfDims;
	private int noOfBits;

	public VaFileEntry(Vector vector, List<Float> minVal, List<Float> maxVal, int noOfDims, int noOfBits) {
		this.minList = minVal;
		this.maxList = maxVal;
		this.entries = new ArrayList<Vector>();
		this.noOfBits = noOfBits;
		this.noOfDims = noOfDims;
		this.dimLength = calcDimLength(minVal, maxVal);
		this.code = calcCode(vector);
	}

	private List<Float> calcDimLength(List<Float> minVal, List<Float> maxVal) {
		List<Float> dimLength = new ArrayList<Float>();
		for(int i = 0; i < minVal.size(); i++) {
			float range = maxVal.get(i) - minVal.get(i);
			int bitsPerDim = getBitsPerDim(i, this.noOfBits, this.noOfDims);
			float dimLen = getDimLen(range, bitsPerDim);
			dimLength.add(dimLen);
		}
		return dimLength;
	}

	private float getDimLen(float range, int bitsPerDim) {
		// TODO Auto-generated method stub
		int totRegions = (int) Math.pow(2, bitsPerDim);
		return (range/totRegions);
	}

	private int getBitsPerDim(int i, int noOfBits, int noOfDims) {
		int dimLen = 0;
		int minPerDim = noOfBits / noOfDims;
		int remain    = noOfBits % noOfDims;
		if (i < remain) {
			dimLen ++;
		}
		dimLen += minPerDim;
		return dimLen;
	}
	
	public String getCode() {
		return code;
	}

	public void addEntry(Vector entry) {
		String code = this.calcCode(entry);
		if(code == this.code) {
			this.entries.add(entry);
		}
	}

	private String calcCode(Vector vector) {
		regionData = getRegionData(vector, this.minList, this.dimLength);
		return getCode(regionData, this.dimLength, this.noOfBits);
	}
	
	private String getCode(List<Integer> regionData, List<Float> dimLength, int noOfBits2) {
		// TODO Auto-generated method stub
		int noOfDims = this.noOfDims;
		String compBin = "";
		for(int dim = 0; dim < noOfDims; dim++) {
			String binVal = "";
			int noOfBits = this.getBitsPerDim(dim, noOfBits2, noOfDims);
			binVal = Integer.toBinaryString(regionData.get(dim));
			int binLen = binVal.length();
			if (binLen < noOfBits) {
				while(binLen < noOfBits) {
					binVal = "0" + binVal;
					binLen++;
				}
			} else if (binLen > noOfBits) {
				binVal = binVal.substring(binLen - noOfBits);
			}
			compBin += binVal;
		}
		return compBin;
	}
	/***
	 * 
	 * @param vector - The vector whose Region is to be found.
	 * @param minVal - The list of Minimum Values for each dimension
	 * @param dimLen - The length in each dimension
	 * @return - The region Numbers corresponding to the regionData. 
	 */
	private List<Integer> getRegionData(Vector vector, List<Float> minVal, List<Float> dimLen) {
		// This computes the values of the vector.
		// Returns the region Number it Corresponds to.
		List<Integer> regData = new ArrayList<Integer>();
		List<Float> vectMag = vector.getVector();
		int noOfDims = vectMag.size();
		
		int region;
		for(int dimNo = 0; dimNo < noOfDims; dimNo++) {
			float vectVal = vectMag.get(dimNo);
			float min  = minVal.get(dimNo);
			float dimLength = dimLen.get(dimNo);
			if(dimLength != 0) {
				region = (int)((vectVal - min)/dimLength);
			} else {
				region = 0;
			}
			regData.add(dimNo, region); // adds the starting partition point per dimension
			
			// Last point(MaxPoint) is to be included in the last region as the boundary includes it.
			if(region == noOfDims) {
				region --;
			}
			regData.add(dimNo, region);
		}
		return regData;
	}
	
	public List<Vector> getEntries() {
		return entries;
	}

	public void setEntries(List<Vector> entries) {
		this.entries = entries;
	}

	public List<Float> getMinList() {
		return minList;
	}

	public void setMinList(List<Float> minList) {
		this.minList = minList;
	}

	public List<Float> getDimLength() {
		return dimLength;
	}

	public void setDimLength(List<Float> dimLength) {
		this.dimLength = dimLength;
	}

	public List<Float> getMaxList() {
		return maxList;
	}

	public void setMaxList(List<Float> maxList) {
		this.maxList = maxList;
	}

	public int getNoOfDims() {
		return noOfDims;
	}

	public void setNoOfDims(int noOfDims) {
		this.noOfDims = noOfDims;
	}

	public int getNoOfBits() {
		return noOfBits;
	}

	public void setNoOfBits(int noOfBits) {
		this.noOfBits = noOfBits;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "VaFileEntry [code=" + code + ", entries=" + entries
				+ ", minList=" + minList + ", dimLength=" + dimLength
				+ ", noOfDims=" + noOfDims + ", noOfBits=" + noOfBits + "]";
	}
}
