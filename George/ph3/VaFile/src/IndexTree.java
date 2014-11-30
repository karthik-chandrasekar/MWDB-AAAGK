
public class IndexTree {

	Node root;
	int noOfElements;
	
	public IndexTree() {
		super();
	}

	public IndexTree(Node root) {
		super();
		this.root = root;
		this.noOfElements = 1;
	}

	void addNode(Node elem) {
		
		if (root == null) {
			root = elem;
			return;
		}
		
		Node prev = root;
		Node next = root;
		boolean left = false;
		while(next != null) {
			if(elem.getKey() == prev.getKey()) {
				return; // Do not add to tree
			}
			if(elem.getKey() < prev.getKey()) {
				prev = next;
				next = next.left;
				left = true;
			} else if(elem.getKey() > prev.getKey()) {
				prev = next;
				next = next.right;
				left = false;
			}
			if (left == true) {
				prev.left = elem;
			} else {
				prev.right = elem;
			}
		}
		this.noOfElements ++;
	}
	
	Node find(int key) {

		Node prev = root;
		Node next = root;
		int cost = 0;
		while(next != null) {
			cost++;
			if(key == prev.getKey()) {
				break;
			} else if(key < prev.getKey()) {
				prev = next;
				next = next.left;
			} else if(key > prev.getKey()) {
				prev = next;
				next = next.right;
			}
		}
		prev.setCost(cost);
		return prev;
	}

	@Override
	public String toString() {
		return "IndexTree [root=" + root + ", noOfElements=" + noOfElements
				+ "]";
	}
}
