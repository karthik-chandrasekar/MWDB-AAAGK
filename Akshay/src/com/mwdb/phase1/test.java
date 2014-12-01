package com.mwdb.phase1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class test {

	public static void main(String[] args) {
		
		File folder = new File("/home/akshay/Desktop/FrameFiles");
		PrintWriter p;
		try {
			p = new PrintWriter("verbs.txt");
			for (File f : folder.listFiles()){
				p.println(f.getName().split("\\.")[0]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
