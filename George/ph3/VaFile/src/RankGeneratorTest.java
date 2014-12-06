import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class RankGeneratorTest {

	@Test
	public void testRankGenerator() {
		Vector qVector = new Vector("2,51,206,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137");
		Vector qVector1 = new Vector("2,51,206,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137");
		Vector qVector2 = new Vector("2,51,206,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137,0.34137");
		
		
		Vector closest = new Vector("3,51,206,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137");
		Vector closest1 = new Vector("4,51,206,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137");
		Vector closest2 = new Vector("3,51,206,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137,0.24137");
		
		float dist = (float) 0.250;
		float dist1 = (float) 0.200;
		float dist2 = (float) 0.250;
		
		List<Vector> queryVectors = new ArrayList<Vector>();
		List<Float> distances = new ArrayList<Float>();
		List<Vector> resultVectors = new ArrayList<Vector>();
		
		queryVectors.add(qVector1);
		queryVectors.add(qVector);
		queryVectors.add(qVector2);
		
		distances.add(dist);
		distances.add(dist1);
		distances.add(dist2);
		
		resultVectors.add(closest);
		resultVectors.add(closest1);
		resultVectors.add(closest2);
		
		RankGenerator rankG = new RankGenerator(queryVectors, 2);
		System.out.println(rankG.getTopK());
	}
}
