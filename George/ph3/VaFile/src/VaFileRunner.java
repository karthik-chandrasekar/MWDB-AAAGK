import java.io.File;

public class VaFileRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = new File("").getAbsolutePath();
		VaFile1 vaFileObj = new VaFile1(20, filePath.concat("/src/sim/epidemic_word_files/avgn2.csv"));
		System.out.println("Size of VaFile : " + vaFileObj.getSize() + "bytes");
	}
}
