import java.io.*;
import java.util.*;
import mwdb.phase2.*;

class ValueComparator implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

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
        //Loading file label hash
        File labelFile = new File(labelFilePath);
        loadFileLabelHash(labelFile);
        
        System.out.println("File label hash map " + fileLabelHash);
        
        File queryFile = new File(queryFilePath);
        File[] filesInDir = getFilesInDir(inputDirectory);
        Map<String, Double> fileSimilarityMap = new HashMap<String, Double>();
        Map<String, Integer> labelCountMap = new HashMap<String, Integer>();
        
        //Find the similarity of the given file with the training data set
        double simVal = 0.0;
        SimilarityWrapper sobj = new SimilarityWrapper();
        
        for(File fileObj : filesInDir)
        {
            if (!fileLabelHash.containsKey(fileObj.getName()))continue;
            simVal = sobj.getSimilarityForFiles(2,queryFile.getAbsolutePath(),fileObj.getAbsolutePath());
            fileSimilarityMap.put(fileObj.getName(), simVal);//Replace 1.0 with similarity function call
            simVal++;
        }
        
        System.out.println("File similarity map " + fileSimilarityMap);
        
        //Sort the simValues to find out the top k similar files
        Map<String, Double> fileSimilarityTreeMap = new TreeMap<String, Double>(fileSimilarityMap);
        ValueComparator bvc =  new ValueComparator( fileSimilarityTreeMap );
        TreeMap<String,Double> sortedFileSimilarityMap = new TreeMap<String,Double>(bvc);
        sortedFileSimilarityMap.putAll(fileSimilarityMap);
        
        //Select top k similar values
        String label; int count; int loopCount = 0;
        System.out.println("Sorted File Similarity Map " + sortedFileSimilarityMap);
        
        for(Map.Entry<String, Double> entry : sortedFileSimilarityMap.entrySet())
        {
            if(loopCount < topK)
            {
            
            
                label = fileLabelHash.get(entry.getKey());
                if(labelCountMap.containsKey(label))
                {
                    count = labelCountMap.get(label);
                    count++;
                    labelCountMap.put(label, count);
                }
                else
                {
                    labelCountMap.put(label, 1);
                }
                loopCount++;
            }
            else
            {
                break;
            }
        }
        
        System.out.println("Label Count Map " + labelCountMap);
        
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

    String inputDirectory = "/Users/karthikchandrasekar/Downloads/MWDB-Phase3/SampleData_P3_F14/Data";
    String queryFile = "/Users/karthikchandrasekar/Downloads/MWDB-Phase3/SampleData_P3_F14/Data/QueryFile/1.csv";
    int topK = 5;
    String labelFilePath = "/Users/karthikchandrasekar/Downloads/MWDB-Phase3/SampleData_P3_F14/labels.csv";
    String predictedClass;
    
    KNNClassifier knnclassifierObj = new KNNClassifier();
    predictedClass = knnclassifierObj.findTopSimilarityFiles(inputDirectory, queryFile, topK, labelFilePath);
    System.out.println("PredictedClass " + predictedClass);
    }
}

