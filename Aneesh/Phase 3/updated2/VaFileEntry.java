package updated2;

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
			float range = maxVal.get(i) - minVal.get(i); // gets the diffrence between the maximum value and minimm value in a dimension
			int bitsPerDim = getBitsPerDim(i, this.noOfBits, this.noOfDims); // for dimension i , get the number of bits
			float dimLen = getDimLen(range, bitsPerDim); //dimlen is the partition length
			dimLength.add(dimLen); // adds the partition length to the list
		}
		return dimLength; // each dimension along with its partition length
	}

	private float getDimLen(float range, int bitsPerDim) {
		// TODO Auto-generated method stub
		int totRegions = (int) Math.pow(2, bitsPerDim);
		return (range/totRegions); // returns each partition length for the particular dimension
	}

	private int getBitsPerDim(int i, int noOfBits, int noOfDims) {
		int dimLen = 0;
		int minPerDim = noOfBits / noOfDims;
		int remain    = noOfBits % noOfDims;
		if (i < remain) { // if i is in the first few dimension, it accommodates the left over bits 
			dimLen ++;
		}
		dimLen += minPerDim; // only the first few dimensions accommodate the left over bits (dimLen) 
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
	
	private List<Integer> getRegionData(Vector vector, List<Float> minVal, List<Float> dimLen) {
		// This computes the values of the vector.
		// Returns the region Number it Corresponds to.
		List<Integer> regData = new ArrayList<Integer>();
		List<Float> vectMag = vector.getVector();
		int noOfDims = vectMag.size();
		
		int region;
		for(int dimNo = 0; dimNo < noOfDims; dimNo++) {
			float vectVal = vectMag.get(dimNo); // vector value for the dimension dimNo
			float min  = minVal.get(dimNo); // least value in the dimension
			float dimLength = dimLen.get(dimNo); // space/length between two partitions in a dimension
			
			if(dimLength != 0) {
//				System.out.println("----------------------");
//				System.out.println("For dim : " + dimNo);
//				System.out.println("vectval : " + vectVal);
//				System.out.println("min : " + min);
//				System.out.println("dimLength : " + dimLength);
				region = (int)((vectVal - min)/dimLength); // partition starting point
//				System.out.println("region : " + region );
			} else {
				region = 0;
			}
			regData.add(dimNo, region); // adds the starting partition point per dimension
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
