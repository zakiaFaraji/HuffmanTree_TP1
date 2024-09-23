import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultiReleveCompressor {

    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();  // Utilise un StringBuilder pour concaténer efficacement les lignes
        BufferedReader br = new BufferedReader(new FileReader(filePath));  // Ouvre le fichier en mode lecture
        String line;
        while ((line = br.readLine()) != null) {  // Lit chaque ligne du fichier
            content.append(line).append("\n");  // Ajoute la ligne lue au contenu
        }
        br.close();  // Ferme le fichier après lecture
        return content.toString();
    }

    public static String extractHeader(String texteHTML) {
        int debutBody = texteHTML.indexOf("<body");
        if (debutBody == -1) {
            return "";  // Retourne une chaîne vide si la balise <body> est absente
        }
        return texteHTML.substring(0, debutBody);  // Retourne tout ce qui est avant la balise <body>
    }

    public static String extractBody(String texteHTML) {
        int debutBody = texteHTML.indexOf("<body");
        if (debutBody == -1) {
            return texteHTML;  // Retourne le texte complet si la balise <body> est absente
        }
        return texteHTML.substring(debutBody);  // Retourne le contenu à partir de la balise <body>
    }


    public static String combineReleves(File folder) throws IOException {
        StringBuilder combinedHeader = new StringBuilder();  // Contient les en-têtes combinés
        StringBuilder combinedBody = new StringBuilder();  // Contient les corps combinés

        File[] files = folder.listFiles();  // Liste tous les fichiers du dossier
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".html")) {  // Vérifie si c'est un fichier HTML
                    String content = readFile(file.getPath());  // Lit le fichier HTML
                    combinedHeader.append(extractHeader(content)).append("\n");  // Ajoute l'en-tête extrait
                    combinedBody.append(extractBody(content)).append("\n");  // Ajoute le corps extrait
                }
            }
        }

        return combinedHeader.toString() + combinedBody.toString();  // Retourne la combinaison des en-têtes et des corps
    }

    public static void saveBinaryFile(String compressedText, String filePath) throws IOException {
        BitSet bitSet = new BitSet(compressedText.length());  // Crée un BitSet pour stocker les bits
        for (int i = 0; i < compressedText.length(); i++) {
            if (compressedText.charAt(i) == '1') {
                bitSet.set(i);  // Définit les bits correspondants à '1'
            }
        }
        FileOutputStream fos = new FileOutputStream(filePath);  // Ouvre le fichier en mode écriture
        fos.write(bitSet.toByteArray());  // Écrit le tableau d'octets dans le fichier
        fos.close();
    }

    public static String loadBinaryFile(String filePath) throws IOException {
        File file = new File(filePath);  // Crée un objet File pour le fichier binaire
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);  // Ouvre le fichier en mode lecture
        fis.read(fileData);
        fis.close();

        BitSet bitSet = BitSet.valueOf(fileData);  // Convertit les bytes en BitSet
        StringBuilder sb = new StringBuilder();  // Utilise un StringBuilder pour stocker les bits sous forme de texte
        for (int i = 0; i < bitSet.length(); i++) {
            sb.append(bitSet.get(i) ? '1' : '0');
        }
        return sb.toString();
    }

    public static void saveDecompressedFile(String text, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);  // Ouvre le fichier en mode écriture
        fos.write(text.getBytes());
        fos.close();
    }


    public static void main(String[] args) {
        try {
            // Dossier contenant les fichiers à traiter
            File folder = new File("src/resources/releves");

            // Combine le contenu des fichiers HTML dans le dossier
            String combinedReleves = combineReleves(folder);

            int tailleAvantCompression = combinedReleves.getBytes().length;
            System.out.println("Taille du fichier avant compression : " + tailleAvantCompression + " octets");

            // Compression à l'aide de l'algorithme de Huffman
            Map<Character, Integer> frequencyMap = HuffmanCompression.buildFrequencyMap(combinedReleves);
            Node root = HuffmanCompression.buildHuffmanTree(frequencyMap);
            HuffmanCompression.generateCodes(root, "");

            String compressedText = HuffmanCompression.compress(combinedReleves);

            // Sauvegarde du fichier compressé sous forme binaire
            String outputFilePath = "src/resources/relevesCompressed/test.txt";
            saveBinaryFile(compressedText, outputFilePath);

            File compressedFile = new File(outputFilePath);
            long tailleApresCompression = compressedFile.length();
            System.out.println("Taille du fichier après compression : " + tailleApresCompression + " octets");

            // Calcul et affichage du pourcentage de compression
            double pourcentageCompression = ((double) (tailleAvantCompression - tailleApresCompression) / tailleAvantCompression) * 100;
            System.out.println("Pourcentage de compression : " + String.format("%.2f", pourcentageCompression) + "%");

            // Chargement du fichier compressé et décompression
            String loadedCompressedText = loadBinaryFile(outputFilePath);

            String decompressedText = HuffmanCompression.decompress(loadedCompressedText, root);

            // Sauvegarde du fichier décompressé
            String decompressedFilePath = "src/resources/relevesDecompressed/Decompressed_releves.html";
            saveDecompressedFile(decompressedText, decompressedFilePath);

            System.out.println("Fichier décompressé et sauvegardé sous : " + decompressedFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
