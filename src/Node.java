public class Node implements Comparable<Node> {

    char character;
    int frequency;
    Node left, right;

    Node(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        left = null;
        right = null;
    }

    Node(Node left, Node right) {
        this.character = '-';  // Caractère fictif pour les nœuds internes
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    public int compareTo(Node other) {
        return this.frequency - other.frequency;
    }
}
