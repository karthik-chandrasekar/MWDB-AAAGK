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
    HashMap<String, Map<String, List<String>>> wordsIndexMap = new HashMap<String, Map<String, List<String>>>(); 
    HashMap<String, Integer> wordsIndexHash  = new HashMap<String, Integer>();
    int wordsIndex = 0;
    HashMap<String, List<String>> adjacencyHashMap;
    List<String> tempList;

    
    public void collectWords(String fileName, List<List<String>> wordList) throws FileNotFoundException
    {
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
    
    
    public List<Integer> getBinaryVector(List<List<String>> wordList)
    {
        List<Integer> binaryVectorOne = new ArrayList<Integer>(Collections.nCopies(wordList.size(), 1));
        return binaryVectorOne; 
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
                    valueList.add("US-"+headerList.get(count));
                }
                count ++;
            }
            adjacencyHashMap.put("US-"+header, valueList);
        }     
        return adjacencyHashMap;
    }
    
    public boolean isNeighbor(String stateOne, String stateTwo)
    {
        return adjacencyHashMap.get(stateOne).contains(stateOne);
    }
    
    public double getStateMatch(List<String> listOne, List<String> listTwo)
    {
        double similarity = 0.0;
        
        if(listOne.get(1).equals(listTwo.get(1)))
        {
            similarity = 1;
        }
        else if(isNeighbor(listOne.get(1), listOne.get(2)))
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
    
    
    public double getStateTimeMatching(String rowWord, String colWord)
    {
        double similarity = 0.0;
        
        
        return 0.0;
    }
    
    public double getWordSimilarity(List<String> rowWord, List<String> colWord)
    {
        //State Time word match - 1
        //State word match[If two states are neighbors] - 0.75  
        //Time word match - 0.6 
        //Word match  - 0.4
        
        
        double similarity = 0.0;
        
        similarity += getWordMatch(rowWord, colWord);
        similarity += getStateMatch(rowWord, colWord);
        similarity += getTimeMatch(rowWord, colWord);
        
        return similarity;
    }
    

    public double[][] constructAMatrix(List<List<String>> fileOneWordList, List<List<String>> fileTwoWordList)
    {
        double similarity = 0;
        int rowSize = fileOneWordList.size();
        int colSize =  fileTwoWordList.size();
        double [][] AMatrix = new double[rowSize][colSize];
        List<String> rowWord, colWord;
        
        for(int i=0;i<rowSize;i++)
        {
            rowWord = fileOneWordList.get(i);
            for(int j=0; j<colSize;j++)
            {
                colWord = fileTwoWordList.get(j);
                similarity = getWordSimilarity(rowWord, colWord);
                AMatrix[i][j] = similarity; 
            }
        }
        return AMatrix;
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
    
    public void main() throws Exception
    {
        List<List<String>> fileOneWordList = new ArrayList<List<String>>();
        List<List<String>> fileTwoWordList = new ArrayList<List<String>>();
        String fileNameOne = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFileDiff";
        String fileNameTwo = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFileDiff";
        List<Integer> binaryVectorOne;
        List<Integer> binaryVectorTwo;
        String inputFilePath = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";
        double fileSimilarity;
        
        adjacencyHashMap = formAdjacencyHashMap(inputFilePath);
        collectWords(fileNameOne, fileOneWordList);
        collectWords(fileNameTwo, fileTwoWordList); 
        binaryVectorOne = getBinaryVector(fileOneWordList);
        binaryVectorTwo = getBinaryVector(fileTwoWordList);
        double[][] AMatrix = constructAMatrix(fileOneWordList, fileTwoWordList);    
        
        //Multiply matrices
        fileSimilarity = matrixMultiply(binaryVectorOne, AMatrix, binaryVectorTwo);
        
        System.out.println(fileSimilarity);
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
         
         SimilarityGenerator simObj = new SimilarityGenerator();
         simObj.main();
    }
}

