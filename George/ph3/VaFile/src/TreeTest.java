import static org.junit.Assert.*;

import java.util.NavigableSet;
import java.util.TreeMap;

import org.junit.Test;


public class TreeTest {

	@Test
	public void test() {
		TreeMap<Integer,Integer> myTree = new TreeMap<Integer,Integer>();
		myTree.put(5, 5);
		myTree.put(4, 4);
		myTree.put(6, 6);
		myTree.put(3, 3);
		myTree.put(1, 1);
		myTree.put(8, 8);
		
		int elementWeGot = myTree.get(3);
		NavigableSet<Integer> myNavSet = myTree.navigableKeySet();
		
		assertEquals(3, elementWeGot);
		int val = myNavSet.lower(10);
		assertEquals(8, val);
		
		myNavSet.higher(10);
		System.out.println(val);
		assertEquals(val, null);

	}
}
