import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
        // Find the common matching matching characters with same index in given window of the words
        int matchCount = 0; //Total number of characters matching in window between two words
        int misMatchCount = 0;
        int totalLen = 0;
        double similarity = 0;
        int windowMatchBoost = 3; //
        int matchThreshold = 0;
        int iterationCount = 0;
        
        iterationCount = Math.min(rowWord.size(), colWord.size());
        for(int i=0; i < iterationCount ;i++)
        {
            if(i < 3)
            {
                continue;
            }
            //System.out.println(rowWord.get(i) + "  " + colWord.get(i));
            if(rowWord.get(i).equals(colWord.get(i)))
            {
                matchCount ++;
            }
        }
        
        totalLen = Math.max(rowWord.size(), colWord.size())-3;
        
        matchThreshold = totalLen/2;
        if (matchCount > matchThreshold)
        {
            similarity = ((double)1/(double)totalLen) * matchCount * windowMatchBoost;
        }
        else
        {
            similarity = 0;
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
        /*** Check if the state is equal between two given lists. State is located in index position 1 of the given lists. 
             If the state is same give similarity as 1
             If the states are neighbors assign similarity as 0.5
        ***/
        
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
        double windowMatch = 0.0;
        double stateMatch = 0.0;
        double timeMatch = 0.0;
        
        windowMatch = getWordMatch(rowWord, colWord);
        similarity += windowMatch;
        //if (windowMatch != 0.0){System.out.println("WordMatch " + similarity);}
        stateMatch = getStateMatch(rowWord, colWord);
        similarity += stateMatch;
        //if (stateMatch!= 0.0){System.out.println("StateMatch " + similarity);}
        timeMatch = getTimeMatch(rowWord, colWord);
        similarity += timeMatch;
        //if (timeMatch != 0.0){System.out.println("TimeMatch " + timeMatch);}
        
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
         String fileNameTwo = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/EpidemicWordFile";
         simObj.getFileSimilarity(fileNameOne, fileNameTwo);
    }
}

