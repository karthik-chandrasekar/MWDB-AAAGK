import java.io.*;
import java.util.*;

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
        //Load the file label hash 
        
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
        //Find the top similarity files for the given file
        //Loading file label hash
        File labelFile = new File(labelFilePath);
        loadFileLabelHash(labelFile);
        
        //System.out.println("File label hash map " + fileLabelHash);
        
        File queryFile = new File(queryFilePath);
        File[] filesInDir = getFilesInDir(inputDirectory);
        Map<String, Double> fileSimilarityMap = new HashMap<String, Double>();
        Map<String, Integer> labelCountMap = new HashMap<String, Integer>();
        
        //Find the similarity of the given file with the training data set
        double simVal = 0.0;
        SimilarityWrapper sobj = new SimilarityWrapper();
        
        for(File fileObj : filesInDir)
        {
            if (!fileLabelHash.containsKey(fileObj.getName().substring(1)))continue;
            //System.out.println("File name 1 " + queryFile.getAbsolutePath() + " File name 2 " + fileObj.getAbsolutePath());
            simVal = sobj.getSimilarityForFiles(3,queryFile.getAbsolutePath(),fileObj.getAbsolutePath());
            fileSimilarityMap.put(fileObj.getName(), simVal);//Replace 1.0 with similarity function call
        }
        
        //System.out.println("File similarity map " + fileSimilarityMap);
        
        //Sort the simValues to find out the top k similar files
        Map<String, Double> fileSimilarityTreeMap = new TreeMap<String, Double>(fileSimilarityMap);
        ValueComparator bvc =  new ValueComparator( fileSimilarityTreeMap );
        TreeMap<String,Double> sortedFileSimilarityMap = new TreeMap<String,Double>(bvc);
        sortedFileSimilarityMap.putAll(fileSimilarityMap);
        
        //Select top k similar values
        String label; int count; int loopCount = 0;
        //System.out.println("Sorted File Similarity Map " + sortedFileSimilarityMap);
        
        for(Map.Entry<String, Double> entry : sortedFileSimilarityMap.entrySet())
        {
            if(loopCount < topK)
            {
            
            
                label = fileLabelHash.get(entry.getKey().substring(1));
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
        
        //System.out.println("Label Count Map " + labelCountMap);
        
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

    String inputDirectory = "/tmp/tem/MWDB-AAAGK/Karthik/DecisionTreeInput/KNNInput/word_files";
    String queryFile = "/tmp/tem/MWDB-AAAGK/Karthik/DecisionTreeInput/KNNInput/word_files/n1.csv";
    int topK = 5;
    String labelFilePath = "/tmp/tem/MWDB-AAAGK/Karthik/DecisionTreeInput/labels.csv";
    String predictedClass;
    
    KNNClassifier knnclassifierObj = new KNNClassifier();
    
    System.out.println("Enter the label file path");
    Scanner scannerObj = new Scanner(System.in);
    labelFilePath = scannerObj.nextLine();
    System.out.println("Enter input data files directory");
    inputDirectory = scannerObj.nextLine();
    
    
        while(true)
        {
            System.out.println("Enter the parameter K ");
            topK = Integer.parseInt(scannerObj.nextLine());
            
            System.out.println("Enter query file path");
            queryFile = scannerObj.nextLine();
            predictedClass = knnclassifierObj.findTopSimilarityFiles(inputDirectory, queryFile, topK, labelFilePath);
            System.out.println("Predicted Label" + "-" + predictedClass);
        }
    }
}

