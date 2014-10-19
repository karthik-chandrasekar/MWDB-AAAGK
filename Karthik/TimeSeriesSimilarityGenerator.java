import java.io.*;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.*;

class SimilarityGenerator
{
    HashMap<String, Map<String, List<String>>> wordsIndexMap = new HashMap<String, Map<String, List<String>>>(); 
    HashMap<String, Integer> wordsIndexHash  = new HashMap<String, Integer>();
    int wordsIndex = 0;
    String fileNameOne = "";
    String fileNameTwo = "";

    public List<Integer> collectWords(String fileName) throws FileNotFoundException
    {
        File inputFile = new File(fileName);
        Scanner input = new Scanner(inputFile);
        List<String> tempList = new ArrayList<String>();
        String wordTempString;
        String metaTempString;
        List<Integer> wordsIndexList = new ArrayList<Integer>();
        Map<String, List<String>> tempMap = null;
        List<String> metaTempList = null;
        
        while(input.hasNextLine())
        {
            tempList = Arrays.asList(input.nextLine().split(","));
            wordTempString="";
            metaTempString="";
            
            for(int i=0;i< tempList.size();i++)
            {
                
                if (i<3)
                {
                    if(i==1)continue;
                    metaTempString = tempList.get(i) + "-";
                }
                else
                {
                    wordTempString += tempList.get(i) + "-";
                }
            }
            wordTempString = wordTempString.substring(0, wordTempString.length()-1);
                    
            
            if(wordsIndexMap.containsKey(wordTempString))
            {
                tempMap = wordsIndexMap.get(wordTempString);
                
                if(tempMap.containsKey(fileName))
                {
                    metaTempList = tempMap.get(fileName);
                    metaTempList.add(metaTempString);
                    tempMap.put(fileName, metaTempList);
                }
                else
                {
                    metaTempList = new ArrayList<String>();
                    metaTempList.add(metaTempString);
                    tempMap.put(fileName, metaTempList);
                }
            }
            else
            {
                tempMap = new HashMap<String, List<String>>();              
                metaTempList = new ArrayList<String>();
                wordsIndexHash.put(wordTempString, wordsIndex); 
                wordsIndex++;
                tempMap.put(fileName, metaTempList);
            }
            wordsIndexMap.put(wordTempString, tempMap);

        }
        return wordsIndexList;      
    }
    
    public List<Integer> getBinaryVector(List<Integer> wordsIndexOne)
    {
        List<Integer> binaryVectorOne = new ArrayList<Integer>();
        
        for(int i=0;i<wordsIndexMap.size();i++)
        {
            if(wordsIndexOne.contains(i))
            {
                binaryVectorOne.add(1);
            }
            else
            {
                binaryVectorOne.add(0);
            }
        }
        return wordsIndexOne;       
    }
    
    public double getWordMatch(String rowWord, String colWord)
    {
        //Find the euclidean distance between given two words
        return 0.0;
        
    }
    
    public boolean isNeighbor(String stateOne, String stateTwo)
    {
        return true;
    }
    
    public double getStateMatch(List<String> listOne, List<String> listTwo)
    {
        String tempStateOne ="";
        String tempStateTwo = "";
        double similarity = 0.0;
        
        for(String stateTimeOne: listOne)
        {
            tempStateOne = stateTimeOne.split("-")[0];
            for(String stateTimeTwo: listTwo)
            {
                tempStateTwo = stateTimeTwo.split("-")[0];
                if (tempStateOne.equals(tempStateTwo))
                {
                    similarity += 10;
                }
                else if(isNeighbor(tempStateOne, tempStateTwo))
                {
                    similarity += 5;
                }
                else 
                {
                    similarity += 0;
                }
            }
        }
        return similarity;
    }
    
    double getTimeMatchValue(int tempTimeOne, int tempTimeTwo)
    {
        //Return 1 % euclidean distance(tempTimeOne, tempTimeTwo)
        return 0.0;
    }
    
    double getTimeMatch(List<String> listOne, List<String> listTwo)
    {
        int tempTimeOne;
        int tempTimeTwo;
        double similarity=0.0;
        
        for(String stateTimeOne: listOne)
        {
            tempTimeOne = Integer.parseInt(stateTimeOne.split("-")[1]);
            for(String stateTimeTwo: listTwo)
            {
                tempTimeTwo = Integer.parseInt(stateTimeTwo.split("-")[1]);
                similarity += getTimeMatchValue(tempTimeOne, tempTimeTwo);
            }
        }
        return similarity;
    }
    
    public double getStateTimeWordMatch(String rowWord, String colWord)
    {
        double similarity = 0.0;
        Map<String, List<String>> rowWordMap = wordsIndexMap.get(rowWord);
        Map<String, List<String>> colWordMap = wordsIndexMap.get(colWord);
        if(rowWordMap.containsKey(fileNameOne) && colWordMap.containsKey(fileNameTwo))
        {
            similarity += getStateMatch(rowWordMap.get(fileNameOne), rowWordMap.get(fileNameTwo));
            similarity += getTimeMatch(rowWordMap.get(fileNameOne), rowWordMap.get(fileNameTwo));
        }
        else if (rowWordMap.containsKey(fileNameTwo) && colWordMap.containsKey(fileNameOne))
        {
            similarity += getStateMatch(rowWordMap.get(fileNameTwo), rowWordMap.get(fileNameOne));
            similarity += getTimeMatch(rowWordMap.get(fileNameTwo), rowWordMap.get(fileNameOne));
        }
        return similarity;
    }

    
    public double getWordSimilarity(String rowWord, String colWord)
    {
        //State Time word match - 1
        //State word match[If two states are neighbors] - 0.75  
        //Time word match - 0.6 
        //Word match  - 0.4
        
        Map<String, List<String>> tempMapOne;
        Map<String, List<String>> tempMapTwo;
        double similarity = 0.0;
        
        similarity += getWordMatch(rowWord, colWord);
        similarity += getStateTimeWordMatch(rowWord, colWord);
        
        return similarity;
    }
    
    public void constructAmatrix()
    {       
        double similarity=0;
        List<String> allWordsList = new ArrayList(wordsIndexMap.keySet());
        double [][] AMatrix = new double[allWordsList.size()][allWordsList.size()];
        int rowWordIndex = 0;
        int colWordIndex = 0;
        
        for(String rowWord: allWordsList)
        {   rowWordIndex = wordsIndexHash.get(rowWord);
            for(String colWord: allWordsList)
            {
                similarity = getWordSimilarity(rowWord, colWord);   
                colWordIndex = wordsIndexHash.get(colWord);
                AMatrix[rowWordIndex][colWordIndex] = similarity;
            }
        }
    }
    
    
    public void main() throws Exception
    {
        String fileNameOne = "";
        String fileNameTwo = "";
        List<Integer> binaryVectorOne;
        List<Integer> binaryVectorTwo;
        List<Integer> wordsIndexOne;
        List<Integer> wordsIndexTwo;
        wordsIndexOne = collectWords(fileNameOne);
        wordsIndexTwo = collectWords(fileNameTwo);  
        binaryVectorOne = getBinaryVector(wordsIndexOne);
        binaryVectorTwo = getBinaryVector(wordsIndexTwo);
        constructAmatrix(); 
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

