import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultiReleveCompressor {

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

    public static String extractHeader(String texteHTML) {
        int debutBody = texteHTML.indexOf("<body");
        if (debutBody == -1) {
            return "";
        }
        return texteHTML.substring(0, debutBody);
    }

    public static String extractBody(String texteHTML) {
        int debutBody = texteHTML.indexOf("<body");
        if (debutBody == -1) {
            return texteHTML;
        }
        return texteHTML.substring(debutBody);
    }

    public static String combineReleves(File folder) throws IOException {
        StringBuilder combinedHeader = new StringBuilder();
        StringBuilder combinedBody = new StringBuilder();

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".html")) {
                    String content = readFile(file.getPath());
                    combinedHeader.append(extractHeader(content)).append("\n");
                    combinedBody.append(extractBody(content)).append("\n");
                }
            }
        }

        return combinedHeader.toString() + combinedBody.toString();
    }

    public static void saveBinaryFile(String compressedText, String filePath) throws IOException {
        BitSet bitSet = new BitSet(compressedText.length());
        for (int i = 0; i < compressedText.length(); i++) {
            if (compressedText.charAt(i) == '1') {
                bitSet.set(i);
            }
        }
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(bitSet.toByteArray());
        fos.close();
    }

    public static String loadBinaryFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(fileData);
        fis.close();

        BitSet bitSet = BitSet.valueOf(fileData);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitSet.length(); i++) {
            sb.append(bitSet.get(i) ? '1' : '0');
        }
        return sb.toString();
    }

    public static void saveDecompressedFile(String text, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(text.getBytes());
        fos.close();
    }

    public static void ecrireFichierCompresse(String nomFichier, String texteCompresse) throws IOException {
        StringBuilder binaryRepresentation = new StringBuilder();
        for (char c : texteCompresse.toCharArray()) {
            binaryRepresentation.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }

        FileWriter writer = new FileWriter(nomFichier, StandardCharsets.UTF_8);
        writer.write(binaryRepresentation.toString());
        writer.close();

        System.out.println("\nLe fichier compressé a été sauvegardé sous : " + nomFichier);
    }

    public static void main(String[] args) {
        try {
            File folder = new File("src/resources/releves");

            String combinedReleves = combineReleves(folder);

            int tailleAvantCompression = combinedReleves.getBytes().length;
            System.out.println("Taille du fichier avant compression : " + tailleAvantCompression + " octets");

            Map<Character, Integer> frequencyMap = HuffmanCompression.buildFrequencyMap(combinedReleves);
            Node root = HuffmanCompression.buildHuffmanTree(frequencyMap);
            HuffmanCompression.generateCodes(root, "");

            String compressedText = HuffmanCompression.compress(combinedReleves);

            String outputFilePath = "src/resources/relevesCompressed/test.txt";
            saveBinaryFile(compressedText, outputFilePath);

            File compressedFile = new File(outputFilePath);
            long tailleApresCompression = compressedFile.length();
            System.out.println("Taille du fichier après compression : " + tailleApresCompression + " octets");

            double pourcentageCompression = ((double) (tailleAvantCompression - tailleApresCompression) / tailleAvantCompression) * 100;
            System.out.println("Pourcentage de compression : " + String.format("%.2f", pourcentageCompression) + "%");

            String loadedCompressedText = loadBinaryFile(outputFilePath);

            String decompressedText = HuffmanCompression.decompress(loadedCompressedText, root);

            String decompressedFilePath = "src/resources/relevesDecompressed/Decompressed_releves.html";
            saveDecompressedFile(decompressedText, decompressedFilePath);

            System.out.println("Fichier décompressé et sauvegardé sous : " + decompressedFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
