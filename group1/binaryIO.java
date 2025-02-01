package group1;

import java.io.*;

public class binaryIO {

    public static void convertCsvToBinary(String csvFilePath, String binaryFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             RandomAccessFile raf = new RandomAccessFile(binaryFilePath, "rw")) {

            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                for (String field : data) {
                    raf.writeUTF(field);
                }
            }
        }
    }

    public static void addDataFromCsv(String csvFilePath, String binaryFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             RandomAccessFile raf = new RandomAccessFile(binaryFilePath, "rw")) {

            raf.seek(raf.length()); // Move to the end of the file

            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                for (String field : data) {
                    raf.writeUTF(field);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            convertCsvToBinary("./data/students.csv", "./data/students.bin");
            convertCsvToBinary("./data/courses.csv", "./data/courses.bin");
            convertCsvToBinary("./data/instructors.csv", "./data/instructors.bin");
            System.out.println("CSV files have been converted to binary format.");

            // Add new data from another CSV file
            addDataFromCsv("./data/new_students.csv", "./data/students.bin");
            System.out.println("New data has been added to the binary file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}