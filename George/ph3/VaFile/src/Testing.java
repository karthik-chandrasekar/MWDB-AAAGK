import java.security.KeyStore.Entry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


	
public class Testing {

    public static void main(String[] args) {

        Map<String,Double> map = new HashMap<String,Double>();

        map.put("A",99.5);
        map.put("B",67.4);
        map.put("C",67.4);
        map.put("D",67.3);
        sort(map);
        System.out.println("unsorted map: "+map);
    }
    
    public static void sort(Map<String, Double> map) {
    	
    	// Create a reverse Map Double to String
    	Map<Double, String> revMap = new TreeMap<Double, String>();
    	
    	for(java.util.Map.Entry<String, Double> entry : map.entrySet()) {
    		revMap.put(entry.getValue(), entry.getKey());
    	}
    	
    	Iterator<java.util.Map.Entry<Double, String>> iterator = revMap.entrySet().iterator();
    	while(iterator.hasNext()) {
    		System.out.println(iterator.next());
    	}
    }
}

