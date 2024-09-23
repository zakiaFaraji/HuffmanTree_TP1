import java.util.*;

public class HuffmanCompression {

    public static Map<Character, String> huffmanCode = new HashMap<>();
    public static Map<Character, Integer> buildFrequencyMap(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    public static Node buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.add(new Node(left, right));
        }

        return pq.poll();
    }

    public static void generateCodes(Node root, String code) {
        if (root == null) return;

        if (root.left == null && root.right == null) {
            huffmanCode.put(root.character, code);
        }

        generateCodes(root.left, code + "0");
        generateCodes(root.right, code + "1");
    }

    public static String compress(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(huffmanCode.get(c));
        }
        return sb.toString();
    }

    public static String decompress(String binary, Node root) {
        StringBuilder sb = new StringBuilder();
        Node current = root;
        for (char bit : binary.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.left == null && current.right == null) {
                sb.append(current.character);
                current = root;
            }
        }
        return sb.toString();
    }

}
