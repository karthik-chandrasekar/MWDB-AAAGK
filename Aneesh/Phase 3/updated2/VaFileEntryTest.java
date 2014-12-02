package updated2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class VaFileEntryTest {

	@Test
	public void testCode() {
		Vector vector = new Vector("2,51,206,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137");
		List<Float> minVal = new ArrayList<Float>();
		minVal.add((float) 0.0);
		minVal.add((float) 0.0);
		minVal.add((float) 0.0);
		minVal.add((float) 0);
		minVal.add((float) 0.0);
		minVal.add((float) 0.0);
		minVal.add((float) 0.0);
		minVal.add((float) 0);
		List<Float>maxVal = Arrays.asList((float)1.0, (float)0.5, (float)0.40, (float)2.0, (float)1.0, (float)0.5, (float)0.4, (float)2.0);
		VaFileEntry VaFileEntry = new VaFileEntry(vector, minVal, maxVal, 8, 20);
	}

}
