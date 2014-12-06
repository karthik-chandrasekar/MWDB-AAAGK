package updated2;

import static org.junit.Assert.*;
import org.junit.Test;


public class TreeTest {

	@Test
	public void test() {
		Node root = new Node(5, null, null);
		IndexTree myTree = new IndexTree(root);
		myTree.addNode(new Node(4, null, null));
		myTree.addNode(new Node(6, null, null));
		
		Node elem = myTree.find(3);		
		assertEquals(4, elem.getKey());
		assertEquals(myTree.noOfElements, 3);
		assertEquals(elem.cost, 2);
	}
}
