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
    String inputFilePath;

    
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
    

    public double constructAMatrix(List<List<String>> fileOneWordList, List<List<String>> fileTwoWordList)
    {
        int rowSize = fileOneWordList.size();
        int colSize =  fileTwoWordList.size();
        List<String> rowWord, colWord;
        double fileSimilarity = 0 ;
        
        /***for(int i=0;i<rowSize;i++)
        {
            rowWord = fileOneWordList.get(i);
            for(int j=0; j<colSize;j++)
            {
                colWord = fileTwoWordList.get(j);
                similarity = getWordSimilarity(rowWord, colWord);
                if(similarity > maxSimilarity)
                {
                    maxSimilarity = similarity;
                }
                AMatrix[i][j] = similarity; 
            }
        }***/
        
        for(int i=0; i<rowSize; i++)
        {
            rowWord = fileOneWordList.get(i);
            for(int j=0; j<colSize; j++)
            {
                colWord = fileTwoWordList.get(j);
                fileSimilarity += getWordSimilarity(rowWord, colWord);
            }
        }
        
        //Normalize matrix values between 0 and 1
        /***for(int i=0; i<rowSize; i++)
        {
            for(int j=0;j<colSize; j++)
            {
                AMatrix[i][j] = AMatrix[i][j] / (double)maxSimilarity;
                if(AMatrix[i][j] > newMaxSimilarity)
                {
                    newMaxSimilarity = AMatrix[i][j];
                }
            }
        }***/
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
    
    public double getFileSimilarity(String fileNameOne, String fileNameTwo, String locationFile) throws Exception
    {
        List<List<String>> fileOneWordList = new ArrayList<List<String>>();
        List<List<String>> fileTwoWordList = new ArrayList<List<String>>();
        inputFilePath = locationFile;
        
        
        double fileSimilarity;
        
        adjacencyHashMap = formAdjacencyHashMap(inputFilePath);
        collectWords(fileNameOne, fileOneWordList);
        collectWords(fileNameTwo, fileTwoWordList); 
        //binaryVectorOne = getBinaryVector(fileOneWordList);
        //binaryVectorTwo = getBinaryVector(fileTwoWordList);
        fileSimilarity = constructAMatrix(fileOneWordList, fileTwoWordList);    
        
        //Multiply matrices
        //fileSimilarity = matrixMultiply(binaryVectorOne, AMatrix, binaryVectorTwo);
        //System.out.println(fileSimilarity);

        //System.out.println(binaryVectorOne.size());
        //System.out.println(binaryVectorTwo.size());
        //System.out.println(binaryVectorOne);
        //System.out.println(binaryVectorTwo);
        
        //printMatrix(AMatrix, binaryVectorOne.size(), binaryVectorTwo.size());
        System.out.println(fileSimilarity);
        System.out.println(fileOneWordList.size() + " " + fileTwoWordList.size());
        fileSimilarity = fileSimilarity / ((fileOneWordList.size() * fileTwoWordList.size()));
        System.out.println(fileSimilarity);
        return fileSimilarity;
    }
    
    void printMatrix(double[][] AMatrix, int rowSize, int colSize)
    {
        for(int i=0;i<rowSize;i++)
        {
            for(int j=0;j<colSize;j++)
            {
                System.out.print(AMatrix[i][j] + "\t");
            }
            System.out.println("\n");
        }
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
         String fileNameOne = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFileDiffHead";
         String fileNameTwo = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFileDiffHead";
         String locationFile = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";
         simObj.getFileSimilarity(fileNameOne, fileNameTwo, locationFile);
    }
}

