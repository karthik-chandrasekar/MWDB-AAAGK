import java.io.File;
import java.util.List;


public class TaskARunner {

	public static void main(String args[]) {
		
		String filePath = new File("").getAbsolutePath();
		int b = 20;
		VaFile1 vaQueryObject = new VaFile1(b, filePath.concat("/src/query.csv"));
		System.out.println("Size of the Query Object File" + vaQueryObject.getSize());
	}
}
