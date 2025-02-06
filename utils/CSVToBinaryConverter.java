package utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CSVToBinaryConverter {

	// Convert CSV to Binary File
	public static <T> void convertCsvToBinary(String csvFilePath, String binaryFilePath, Class<T> entityClass) {
		List<T> dataList = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
			br.readLine(); // Skipping header

			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				String[] values = line.split(",");
				T obj = createObjectFromCSV(values, entityClass);
				if (obj != null) {
					dataList.add(obj);
				} else {
					System.out.println("Skipping invalid data: " + line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error processing CSV file: " + e.getMessage());
		}

		// Write valid objects to binary file
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binaryFilePath))) {
			oos.writeObject(dataList);
			System.out.println("Data saved to binary file: " + binaryFilePath);
		} catch (IOException e) {
			System.err.println("Error writing to binary file: " + e.getMessage());
		}
	}

	// Helper method to create objects dynamically
	private static <T> T createObjectFromCSV(String[] values, Class<T> entityClass) {
		try {
			if (entityClass == model.Course.class) {
				return entityClass.getConstructor(String.class, String.class, int.class).newInstance(values[0],
						values[1], Integer.parseInt(values[2]));
			} else if (entityClass == model.Instructor.class) {
				return entityClass.getConstructor(String.class, String.class, String.class, String.class)
						.newInstance(values[0], values[1], values[2], values[3]);
			} else if (entityClass == model.Student.class) {
				return entityClass.getConstructor(String.class, String.class, String.class).newInstance(values[0],
						values[1], values[2]);
			}
		} catch (Exception e) {
			System.err.println("Error creating object: " + e.getMessage());
		}
		return null;
	}
}
