import java.io.File;

public class VaFileRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello after 4 years.");
		String filePath = new File("").getAbsolutePath();
		VaFile1 vaFileObj = new VaFile1(33, filePath.concat("/src/sim/epidemic_word_files/avgn2.csv"));
		System.out.print(vaFileObj);
	}
}
