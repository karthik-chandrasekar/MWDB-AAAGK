import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

class DataNormalizer{
    
    public void getFilesInDir(String path, Logger logger) throws Exception
    {
        File dirObj = new File(path);
        File[] inputFiles = dirObj.listFiles();
        for(File inputFile:inputFiles)
        {
            readCsv(inputFile, logger);
        }
    }
    
    public void readCsv(File inputFile, Logger logger) throws Exception
    {
        List<String> headerList = new ArrayList<String>();
        List<List<String>> valuesList;
        
        Double maxValue;
        
        Scanner scannerObj = new Scanner(inputFile);
        for(String header : scannerObj.nextLine().split(","))
        {
            headerList.add(header);
        }
        maxValue = findMax(scannerObj);     
        logger.info(inputFile.getName() + "  " + "MaxValue " + maxValue);
        valuesList = normalizeData(inputFile, maxValue);
        formHeaderValueHash(headerList, valuesList, inputFile, logger);    
    }
    
    public HashMap<String, List<String>> formHeaderValueHash(List<String> headerList, List<List<String>> valueList, File inputFile, Logger logger) throws Exception
    {
        HashMap<String, List<String>> keyValueHash = new HashMap<String, List<String>>();

        for(int i=0;i<headerList.size();i++)
        {
            keyValueHash.put(headerList.get(i), valueList.get(i));
        }

        dumpInFile(headerList, valueList, inputFile.getName());
        return keyValueHash;
    }
    
    public void dumpInFile(List<String> headerList, List<List<String>> valuesList, String outputFileName) throws Exception
    {
        String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        PrintWriter writer = new PrintWriter(outputDir+outputFileName+"_Normalized", "UTF-8");
        String outputString = "";
        for(String header: headerList)
        {
            outputString = outputString + header + ',';
        }
        writer.println(outputString);

        for(List<String> valueList: valuesList)
        {
            outputString = "";
            for(String dataVal: valueList)
            {
                outputString = outputString + dataVal + ',';
            }
            writer.println(outputString);
        }
        writer.close();
    }

    public double findMax(Scanner scannerObj)
    {
        double maxValue =0.0;
        while(scannerObj.hasNextLine())
        {
            int count = 0;
            
            for(String value : scannerObj.nextLine().split(","))
            {
                count ++;
                if (count > 2)
                {
                    if(Double.parseDouble(value) > maxValue)
                    {
                        maxValue = Double.parseDouble(value);
                    }
                }
            }
        }
        return maxValue;
    }
    
    public List<List<String>> normalizeData(File inputFile, double maxValue) throws Exception
    {
        List<List<String>> valuesList = new ArrayList(new ArrayList<String>());
        List<String> valueList;
        Scanner scannerObj = new Scanner(inputFile);
        scannerObj.nextLine();
        
        while(scannerObj.hasNextLine())
        {
            int count = 0;
            valueList = new ArrayList<String>();
            
            for(String value : scannerObj.nextLine().split(","))
            {
                count ++;
                if (count > 2)
                {
                    valueList.add(String.valueOf((Double.valueOf(value))/maxValue));
                }
                else
                {
                    valueList.add(value);
                }
            }
            valuesList.add(valueList);
        }
        return valuesList;
    }
    
    public void main(Logger logger)
    {
        try
        {
            String dirPath = "/Users/karthikchandrasekar/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets";
            getFilesInDir(dirPath, logger);
        }
        catch(Exception e)
        {
            
        }
    }
    
}

public class EpidemicWordGenerator {

public static void main(String args[]) throws Exception
    {
        Logger logger = Logger.getLogger("MyLogger");
        FileHandler fh;

        fh = new FileHandler("EpidemicWordGenerator.log");
        logger.addHandler(fh);

        logger.info("Logger starts");

        //Data Normalizer
        DataNormalizer dn = new DataNormalizer();
        dn.main(logger);
    }   
}

