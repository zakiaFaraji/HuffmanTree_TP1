import java.util.*;
import java.io.*;


public class Main {

    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            content.append(line).append("\n");
        }
        br.close();
        return content.toString();
    }

    public static void saveToFile(String compressedData, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(compressedData.getBytes());
        fos.close();
    }

    public static void main(String[] args){

        try {
            String inputFilePath = "src/resources/releves/releve1.html";
            String outputFilePath = "src/resources/relevesCompressed/HuffmanCompression_releve1.huffman";
            String outputDecompressedFilePath = "src/resources/relevesDecompressed/Decompressed_releve1.html";

            String text = readFile(inputFilePath);
            int originalSize = text.getBytes().length;

            System.out.println("Taille du fichier avant compression : " + originalSize + " octets");

            Map<Character, Integer> frequencyMap = HuffmanCompression.buildFrequencyMap(text);

            Node root = HuffmanCompression.buildHuffmanTree(frequencyMap);

            HuffmanCompression.generateCodes(root, "");

            String compressedText = HuffmanCompression.compress(text);


            int compressedSize = (int) Math.ceil(compressedText.length() / 8.0);


            System.out.println("Taille du fichier après compression : " + compressedSize + " octets");

            double compressionPercentage = (1 - (double) compressedSize / originalSize) * 100;
            System.out.printf("Pourcentage de compression : %.2f%%\n", compressionPercentage);

            saveToFile(compressedText, outputFilePath);

            System.out.println("Fichier compressé et sauvegardé sous : " + outputFilePath);

            String decompressedText = HuffmanCompression.decompress(compressedText, root);

            saveToFile(decompressedText, outputDecompressedFilePath);
            System.out.println("Fichier décompressé et sauvegardé sous : " + outputDecompressedFilePath);

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