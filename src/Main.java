import java.util.*;
import java.io.*;


public class Main {

    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));// Ouvre le fichier en lecture
        String line;
        while ((line = br.readLine()) != null) {  // Lit le fichier ligne par ligne
            content.append(line).append("\n"); // Ajoute chaque ligne au StringBuilder avec un retour à la ligne
        }
        br.close(); // Ferme le fichier une fois la lecture terminée
        return content.toString(); // Retourne le contenu complet du fichier sous forme de chaîne de caractères
    }

    public static void saveToFile(String compressedData, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);  // Ouvre le fichier en écriture
        fos.write(compressedData.getBytes());// Écrit les données sous forme de bytes dans le fichier
        fos.close(); // Ferme le flux de sortie pour s'assurer que toutes les données sont bien écrites

    }

    public static void main(String[] args){


        try {
            // Chemins des fichiers d'entrée, de sortie pour compression, et de sortie pour décompression
            String inputFilePath = "src/resources/releves/releve1.html";
            String outputFilePath = "src/resources/relevesCompressed/HuffmanCompression_releve1.huffman";
            String outputDecompressedFilePath = "src/resources/relevesDecompressed/Decompressed_releve1.html";

            // Lecture du fichier original
            String text = readFile(inputFilePath);
            int originalSize = text.getBytes().length;


            System.out.println("Taille du fichier avant compression : " + originalSize + " octets");

            // Construction de la carte des fréquences de chaque caractère dans le texte
            Map<Character, Integer> frequencyMap = HuffmanCompression.buildFrequencyMap(text);

            // Construction de l'arbre de Huffman à partir de la carte des fréquences
            Node root = HuffmanCompression.buildHuffmanTree(frequencyMap);

            // Génération des codes de Huffman pour chaque caractère en traversant l'arbre
            HuffmanCompression.generateCodes(root, "");


            // Compression du texte à l'aide des codes de Huffman générés
            String compressedText = HuffmanCompression.compress(text);


            // Calcul de la taille du texte compressé en octets
            int compressedSize = (int) Math.ceil(compressedText.length() / 8.0);


            System.out.println("Taille du fichier après compression : " + compressedSize + " octets");


            // Calcul du pourcentage de compression
            double compressionPercentage = (1 - (double) compressedSize / originalSize) * 100;
            System.out.printf("Pourcentage de compression : %.2f%%\n", compressionPercentage);


            // Sauvegarde du fichier décompressé
            saveToFile(compressedText, outputFilePath);

            System.out.println("Fichier compressé et sauvegardé sous : " + outputFilePath);

            // Décompression du fichier
            String decompressedText = HuffmanCompression.decompress(compressedText, root);

            // Sauvegarde du fichier décompressé
            saveToFile(decompressedText, outputDecompressedFilePath);
            System.out.println("Fichier décompressé et sauvegardé sous : " + outputDecompressedFilePath);


            // Vérification de l'intégrité de la décompression
            if (decompressedText.equals(text)) {
                System.out.println("La décompression a réussi. Le fichier décompressé est identique à l'original.");
            } else {
                System.out.println("Erreur : Le fichier décompressé n'est pas identique à l'original.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}