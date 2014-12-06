package updated2;
public class Node {

	int key;
	int cost;
	VaFileEntry vaFileEntry;
	Node left;
	Node right;
	
	public Node(int key, int cost, VaFileEntry vaFileEntry, Node left,
			Node right) {
		super();
		this.key = key;
		this.cost = cost;
		this.vaFileEntry = vaFileEntry;
		this.left = left;
		this.right = right;
	}
	
	public Node(int key, VaFileEntry vaFileEntry) {
		super();
		this.key = key;
		this.vaFileEntry = vaFileEntry;
		this.left = null;
		this.right = null;
	}

	public Node(int key, Node left, Node right) {
		super();
		this.key = key;
		this.left = left;
		this.right = right;
		this.cost = 0;
	}
		
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	
	public void setKeyFromCode(String code) {
		this.key = Integer.parseInt(code, 2);
	}
	
	public VaFileEntry getVaFileEntry() {
		return vaFileEntry;
	}
	
	public void setVaFileEntry(VaFileEntry vaFileEntry) {
		this.vaFileEntry = vaFileEntry;
	}

	@Override
	public String toString() {
		return "Node [key=" + key + ", cost=" + cost + ", vaFileEntry="
				+ vaFileEntry + ", left=" + left + ", right=" + right + "]";
	}
	
}
