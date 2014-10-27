import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.*;

class SimilarityGenerator
{
    HashMap<String, List<String>> adjacencyHashMap;
    
    public SimilarityGenerator(String inputLocationFile) throws Exception
    {
        adjacencyHashMap = formAdjacencyHashMap(inputLocationFile);
    }

    
    public void collectWords(String fileName, List<List<String>> wordList) throws FileNotFoundException
    {
        List<String> tempList;

        //1.csv,US-AK,2012-01-01 12:00:00,0.28816285436749933,0.28816285436749933,0.28816285436749933,0.28816285436749933,0.28816285436749933
        File inputFile = new File(fileName);
        Scanner input = new Scanner(inputFile);
        
        while(input.hasNextLine())
        {
            tempList = Arrays.asList(input.nextLine().split(","));
            if (!wordList.contains(tempList))
            {
                wordList.add(tempList);
            }
        }
    }
    
    
    public double getWordMatch(List<String> rowWord, List<String> colWord)
    {
        int matchCount = 0;
        int misMatchCount = 0;
        int totalLen = 0;
        double similarity = 0;
        
        for(int i=0;i< Math.min(rowWord.size(), colWord.size());i++)
        {
            if(rowWord.get(i).equals(colWord.get(i)))
            {
                matchCount ++;
            }
        }
        totalLen = Math.max(rowWord.size(), colWord.size());
        misMatchCount = totalLen - matchCount;
        if (misMatchCount > ((double)totalLen/2))
        {
            similarity = 0;
        }
        else
        {
            similarity = (1/(double)totalLen) * matchCount;
        }
        return similarity;      
    }
    
    
    public HashMap<String, List<String>> formAdjacencyHashMap(String inputFilePath) throws Exception
    {
        //Parse adjacency matrix and form a state, adjacent states hash map

        HashMap<String, List<String>> adjacencyHashMap = new HashMap<String, List<String>>();
        List<String> headerList;
        List<String> valueList;
        int count;
        String header="";


        Scanner scannerObj = new Scanner(new File(inputFilePath));

        headerList = Arrays.asList(scannerObj.nextLine().split(","));
        while(scannerObj.hasNextLine())
        {
            count = 0;
            valueList = new ArrayList<String>();
            for(String value : scannerObj.nextLine().split(","))
            {
                if(count == 0)
                {
                    header = value;
                    count++;
                    continue;
                }
                else if (Integer.parseInt(value)==1)
                {
                    valueList.add(headerList.get(count));
                }
                count ++;
            }
            adjacencyHashMap.put(header, valueList);
        }    
        return adjacencyHashMap;
    }
    
    public boolean isNeighbor(String stateOne, String stateTwo)
    {
        //System.out.println(stateOne);
        //System.out.println(stateTwo);
        if (stateOne==null || stateTwo==null)return false;
        
        return adjacencyHashMap.get(stateOne).contains(stateTwo);
    }
    
    public double getStateMatch(List<String> listOne, List<String> listTwo)
    {
        double similarity = 0.0;
        
        if(listOne.get(1).equals(listTwo.get(1)))
        {
            similarity = 1;
        }
        else if(isNeighbor(listOne.get(1), listTwo.get(1)))
        {
            similarity = 0.5;
        }
        return similarity;
    }
        
    
    double getTimeMatch(List<String> listOne, List<String> listTwo)
    {
        double similarity = 0.0;
        
        if(listOne.get(2).equals(listTwo.get(2)))
            similarity = 0.2;
        return similarity;      
    }
    
    
    public double getWordSimilarity(List<String> rowWord, List<String> colWord)
    {   
        double similarity = 0.0;
        
        similarity += getWordMatch(rowWord, colWord);
        similarity += getStateMatch(rowWord, colWord);
        similarity += getTimeMatch(rowWord, colWord);
        
        return similarity;
    }
    

    public double constructAMatrix(List<List<String>> fileOneWordList, List<List<String>> fileTwoWordList)
    {
        int rowSize = fileOneWordList.size();
        int colSize =  fileTwoWordList.size();
        List<String> rowWord, colWord;
        double fileSimilarity = 0 ;
        //System.out.println(adjacencyHashMap);

        
        for(int i=0; i<rowSize; i++)
        {
            rowWord = fileOneWordList.get(i);
            for(int j=0; j<colSize; j++)
            {
                colWord = fileTwoWordList.get(j);
                fileSimilarity += getWordSimilarity(rowWord, colWord);
            }
        }
        
    
        //System.out.println("Max similarity " + maxSimilarity);
        //System.out.println("New Max Similarity" + newMaxSimilarity);
        
        return fileSimilarity;
    }
    
    double matrixMultiply(List<Integer> binaryVectorOne, double[][] AMatrix, List<Integer> binaryVectorTwo)
    {
        double fileSimilarity = 0;
        double [][] tempMatrix = new double[1][binaryVectorTwo.size()];
        double temp=0;
                
        //Multiply binaryVectorOne and AMatrix
        for(int i=0;i<binaryVectorTwo.size();i++)
        {
            temp = 0;
            for(int j=0;j<binaryVectorOne.size();j++)
            {
                temp += binaryVectorOne.get(j) * AMatrix[j][i];
            }
            tempMatrix[0][i] = temp;
        }
        
        //Multiply tempMatrix and binaryVectorTwo
        for(int i=0; i<binaryVectorTwo.size();i++)
        {
            temp = 0;
            temp += tempMatrix[0][i] * binaryVectorTwo.get(i);
        }
        fileSimilarity = temp;
        
        return fileSimilarity; 
    }
    
    public double getFileSimilarity(String fileNameOne, String fileNameTwo) throws Exception
    {
        List<List<String>> fileOneWordList = new ArrayList<List<String>>();
        List<List<String>> fileTwoWordList = new ArrayList<List<String>>();
        double fileSimilarity;
        
        collectWords(fileNameOne, fileOneWordList);
        collectWords(fileNameTwo, fileTwoWordList); 
        fileSimilarity = constructAMatrix(fileOneWordList, fileTwoWordList);    
        fileSimilarity = fileSimilarity / ((fileOneWordList.size() * fileTwoWordList.size()));
        System.out.println(fileSimilarity);
        return fileSimilarity;
    }
}

public class TimeSeriesSimilarityGenerator {
    public static void main(String args[]) throws Exception
    {
         Logger logger = Logger.getLogger("MyLogger");
         FileHandler fh;

         fh = new FileHandler("TimeSeriesSimilarityGenerator.log");
         logger.addHandler(fh);
         logger.info("Logger starts");
         
         String locationFile = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";
         SimilarityGenerator simObj = new SimilarityGenerator(locationFile);
         String fileNameOne = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFile";
         String fileNameTwo = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFileAvg";
         simObj.getFileSimilarity(fileNameOne, fileNameTwo);
    }
}

