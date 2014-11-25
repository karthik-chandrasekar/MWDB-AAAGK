import java.io.*;
import java.util.*;
import java.util.logging.Logger;


class KNNClassifier
{
    
    Map<String, String> fileLabelHash;
    
    public File[] getFilesInDir(String path) throws Exception
    {
            //Return list of files present in a given directory path
            File dirObj = new File(path);
            File[] inputFiles = dirObj.listFiles();
            return inputFiles;
    }
     
    
    
    void loadFileLabelHash(File labelFile) throws Exception
    {
        fileLabelHash = new HashMap<String, String>();
        Scanner scannerObj = new Scanner(labelFile);
        int count = 0;
        String[] lineValue;
        
        while(scannerObj.hasNextLine())
        {
            if (count == 0)
            {
                count = 1;
                continue;
            }
            
            lineValue = scannerObj.nextLine().split(",");
            fileLabelHash.put(lineValue[0], lineValue[1]);
        }   
    }
    
    String findTopSimilarityFiles(String inputDirectory, String queryFilePath, int topK, String labelFilePath) throws Exception
    {
        File labelFile = new File(labelFilePath);
        loadFileLabelHash(labelFile);
        
        
        File queryFile = new File(queryFilePath);
        File[] filesInDir = getFilesInDir(inputDirectory);
        List<Double> simValues = new ArrayList<Double>();
        Map<String, Double> fileSimilarityMap = new HashMap<String, Double>();
        Map<String, Integer> labelCountMap = new HashMap<String, Integer>();
        
        //Find the similarity of the given file with the training data set
        for(File fileObj : filesInDir)
        {
            fileSimilarityMap.put(fileObj.getName(), 1.0);//Replace 1.0 with similarity function call
        }
        
        //Sort the simValues to find out the top k similar files
        Map<String, Double> sortedFileSimilarityMap = new TreeMap<String, Double>(fileSimilarityMap);
        
        String label; int count; int loopCount = 0;
        for(Map.Entry<String, Double> entry : sortedFileSimilarityMap.entrySet())
        {
            if(loopCount > topK)break;
            
            loopCount++;
            
            label = fileLabelHash.get(entry.getKey());
            if(labelCountMap.containsKey(label))
            {
                count = labelCountMap.get(label);
                count++;
                labelCountMap.put(label, count);
            }
            else
            {
                labelCountMap.put(label, 0);
            }
        }
        
        //Find the label with highest count and return it
        int max=0; String maxLabel="";
        
        for(Map.Entry<String, Integer> entry : labelCountMap.entrySet())
        {
            if (entry.getValue() > max)
            {
                max = entry.getValue();
                maxLabel = entry.getKey();
            }
        }
        
        return maxLabel;
    }
    
}

public class Classifier {
public static void main(String args[]) throws Exception {

    String inputDirectory = "";
    String queryFile = "";
    int topK = 5;
    String labelFilePath = "";
    String predictedClass;
    
    KNNClassifier knnclassifierObj = new KNNClassifier();
    predictedClass = knnclassifierObj.findTopSimilarityFiles(inputDirectory, queryFile, topK, labelFilePath);
    System.out.println("PredictedClass " + predictedClass);
    }
}

