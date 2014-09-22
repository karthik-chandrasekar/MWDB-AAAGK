package com.mwdb.phase1;

import java.util.Scanner;

public class Phase1Runnable {

	public static void main(String[] args){
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter no. of bands: ");
		int r = scanner.nextInt();
		scanner.nextLine();
		
		System.out.println("Enter window length: ");
		int w = scanner.nextInt();
		scanner.nextLine();
		
		System.out.println("Enter shift length: ");
		int h = scanner.nextInt();
		scanner.nextLine();
		
		System.out.println("Enter alpha length: ");
		double a = scanner.nextDouble();
		scanner.nextLine();
		
		System.out.println("Enter the directory of input files: ");
		String dir = scanner.nextLine();
		if(dir==null || dir.isEmpty())
			dir="/home/akshay/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets1";
		
		System.out.println("Enter file name to analyze: ");
		String file = scanner.nextLine();
				
		System.out.println("Choose a file or enter 0 to exit");
		System.out.println("Enter 1 to plot wrt to epidemic word file");
		System.out.println("Enter 2 to plot wrt to epidemic word avg file");
		System.out.println("Enter 3 to plot wrt to epidemic word diff file");
		
		int i =scanner.nextInt();
		while(i!=0){
			
			if(i==1){
				
				Task1.execute(r, w, h, dir);
				Task3.execute(file, 1, w);
                  				
			}
			else if(i==2 || i==3){
				Task1.execute(r, w, h, dir);
				Task2.execute(a);
				Task3.execute(file, i, w);
			}
			else
				System.out.println("Input correct value");
			
			i = scanner.nextInt();
		}
		scanner.close();
		System.out.println("Exiting..");
			
		
	}
	
}
