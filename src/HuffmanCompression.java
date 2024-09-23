import java.util.*;

public class HuffmanCompression {

    // Map stockant les codes de Huffman générés pour chaque caractère
    public static Map<Character, String> huffmanCode = new HashMap<>();

    public static Map<Character, Integer> buildFrequencyMap(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    public static Node buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        // Utilise une file de priorité pour stocker les nœuds triés par fréquence
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Crée un nœud pour chaque caractère dans la carte des fréquences et l'ajoute à la file
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Fusionne les nœuds jusqu'à ce qu'il ne reste qu'un seul arbre
        while (pq.size() > 1) {
            Node left = pq.poll();  // Récupère le nœud avec la plus petite fréquence
            Node right = pq.poll(); // Récupère le second nœud avec la plus petite fréquence
            pq.add(new Node(left, right));  // Crée un nouveau nœud avec ces deux nœuds comme enfants
        }

        return pq.poll();  // Retourne la racine de l'arbre de Huffman
    }

    public static void generateCodes(Node root, String code) {
        if (root == null) return;  // Si l'arbre est vide, il n'y a pas de codes à générer

        // Si un nœud feuille est atteint (un caractère), associe-le au code courant
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.character, code);
        }

        // Récursion pour parcourir l'arbre à gauche (ajout d'un '0') et à droite (ajout d'un '1')
        generateCodes(root.left, code + "0");
        generateCodes(root.right, code + "1");
    }

    public static String compress(String text) {
        StringBuilder sb = new StringBuilder();
        // Remplace chaque caractère du texte par son code de Huffman
        for (char c : text.toCharArray()) {
            sb.append(huffmanCode.get(c));
        }
        return sb.toString();  // Retourne la chaîne binaire compressée
    }

    public static String decompress(String binary, Node root) {
        StringBuilder sb = new StringBuilder();
        Node current = root;

        for (char bit : binary.toCharArray()) {
            // Déplace vers la gauche ou la droite selon la valeur du bit (0 ou 1)
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            // Si un nœud feuille est atteint, ajoute le caractère correspondant au texte décompressé
            if (current.left == null && current.right == null) {
                sb.append(current.character);
                current = root;  // Retourne à la racine pour commencer le décodage du caractère suivant
            }
        }
        return sb.toString();
    }

}
