package group1;

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVToBinaryConverter {
	
    // Convert CSV to Binary File
    public static <T> void convertCsvToBinary(String csvFilePath, String binaryFilePath, Class<T> clazz) {
    	 List<T> dataList = new ArrayList<>();

    	 try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
             String line;
             boolean headerSkipped = false;

             while ((line = br.readLine()) != null) {
                 line = line.trim(); // Remove leading and trailing whitespace
                 if (line.isEmpty()) continue;  // Skip empty lines

                 if (!headerSkipped) {
                     headerSkipped = true;  // Skip the header line
                     continue;
                 }

                 String[] values = line.split(",");  // Use comma for splitting values

                 // Check if the correct number of values is provided
                 if (values.length == 3 && clazz == Course.class) {
                     T obj = createObjectFromCSV(values, clazz);
                     if (obj != null) {
                         dataList.add(obj);
                     }
                 } else if (values.length == 4 && clazz == Instructor.class) {
                     T obj = createObjectFromCSV(values, clazz);
                     if (obj != null) {
                         dataList.add(obj);
                     }
                 } else if (values.length == 3 && clazz == Student.class) {
                     T obj = createObjectFromCSV(values, clazz);
                     if (obj != null) {
                         dataList.add(obj);
                     }
                 } else {
                     System.out.println("Invalid CSV format: " + line);
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }

         // Serialize objects to binary file
         try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binaryFilePath))) {
             oos.writeObject(dataList);
             System.out.println("Data saved to binary file: " + binaryFilePath);
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    // Helper method to create objects dynamically
    private static <T> T createObjectFromCSV(String[] values, Class<T> clazz) {
        try {
            if (clazz == Course.class) {
                
            	return clazz.getConstructor(String.class, String.class, int.class)
                        .newInstance(values[0], values[1], Integer.parseInt(values[2]));
            } else if (clazz == Instructor.class) {
             
            	return clazz.getConstructor(String.class, String.class, String.class, String.class)
                        .newInstance(values[0], values[1], values[2], values[3]);

            } else if (clazz == Student.class) {
               
            	return clazz.getConstructor(String.class, String.class, String.class)
                        .newInstance(values[0], values[1], values[2]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask user if they want to convert CSV to Binary
        System.out.println("Would you like to convert CSV files to binary format? (yes/no)");
        String response = scanner.nextLine();

        if ("yes".equalsIgnoreCase(response)) {
            System.out.println("Starting conversion...");

            // Call the method for each CSV file
            convertCsvToBinary("./data/courses.csv", "./data/courses.dat", Course.class);
            convertCsvToBinary("./data/instructors.csv", "./data/instructors.dat", Instructor.class);
            convertCsvToBinary("./data/students.csv", "./data/students.dat", Student.class);
        } else {
            System.out.println("CSV to Binary conversion skipped.");
        }

        scanner.close();
    }
}
