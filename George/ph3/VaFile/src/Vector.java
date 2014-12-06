import java.util.ArrayList;
import java.util.List;

public class Vector {
	
	String file;
	String State;
	String time;
	List<Float> vector;
	Float distance;
	List<Float> LiPerFile;
	public List<Float> getLiPerFile() {
		return LiPerFile;
	}

	public void setLiPerFile(List<Float> liPerFile) {
		LiPerFile = liPerFile;
	}

	public int getVectorID() {
		return vectorID;
	}

	public void setVectorID(int vectorID) {
		this.vectorID = vectorID;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}



	List<Float> UiPerFile;
	int vectorID;
	
	
	public Vector(String line) {
	    String words[] = line.split(",");
	    String file;
		String state;
		String time;
		List<Float> vector;
		
		file = words[0];
		state = words[1];
		time  = words[2];
		
		vector = new ArrayList<Float>();
	    for(int i = 3; i < words.length; i++) {
	    	 vector.add(i - 3, Float.parseFloat(words[i]));
	    }
	    
		this.file = file;
		State = state;
		this.time = time;
		this.vector = vector;
	}
	
	public String getFile() {
		return file;
	}
	
	public String getState() {
		return State;
	}
	
	public List<Float> getVector() {
		return vector;
	}

	@Override
	public String toString() {
		return "Vector [file=" + file + ", State=" + State + ", time=" + time
				+ ", vector=" + vector + ", distance=" + distance
				+ ", LiPerFile=" + LiPerFile + ", UiPerFile=" + UiPerFile
				+ ", vectorID=" + vectorID + "]";
	}
}
