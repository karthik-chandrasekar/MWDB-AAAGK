
public class call_1_c_d_e {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Double result = 0.0;
		String file_1 = "/Users/aneeshu/Desktop/MWDB/Epi/n1.csv";
		String file_2 = "/Users/aneeshu/Desktop/MWDB/Avg/avgn4.csv";		
		Task1_c_d_e t1 = new Task1_c_d_e(file_1,file_2);
		result = t1.compareFiles();
		System.out.println("Similarity Score : " + result);

	}

}
