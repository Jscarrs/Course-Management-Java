package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidCSVFormatException;
import model.Course;
import model.Instructor;
import model.Student;

public class CSVToBinaryConverter {

	private static final String COURSE_BINARY = "./data/courses.dat";
	private static final String INSTRUCTOR_BINARY = "./data/instructors.dat";
	private static final String STUDENT_BINARY = "./data/students.dat";
	private static final String COURSE_STUDENT_BINARY = "./data/course_students.dat";
	private static final String COURSE_INSTRUCTOR_BINARY = "./data/course_instructor.dat";

	private static final String COURSE_CSV = "./data/courses.csv";
	private static final String INSTRUCTOR_CSV = "./data/instructors.csv";
	private static final String STUDENT_CSV = "./data/students.csv";

	// Delete all binary files before conversion
	public static void resetBinaryFiles() {
		deleteFile(COURSE_BINARY);
		deleteFile(INSTRUCTOR_BINARY);
		deleteFile(STUDENT_BINARY);
		deleteFile(COURSE_STUDENT_BINARY);
		deleteFile(COURSE_INSTRUCTOR_BINARY);
	}

	private static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	// Convert all CSV files to binary format
	public static void convertAllCsvToBinary() {
		resetBinaryFiles();

		convertCsvToBinary(COURSE_CSV, COURSE_BINARY, Course.class);
		convertCsvToBinary(INSTRUCTOR_CSV, INSTRUCTOR_BINARY, Instructor.class);
		convertCsvToBinary(STUDENT_CSV, STUDENT_BINARY, Student.class);
	}

	// Convert CSV file to binary
	public static <T> void convertCsvToBinary(String csvFilePath, String binaryFilePath, Class<T> targetClass) {
		File csvFile = new File(csvFilePath);
		if (!csvFile.exists())
			return;

		List<T> dataList = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
			br.readLine(); // Skip header
			String line;
			while ((line = br.readLine()) != null) {
				if (validateCSVFormat(line, targetClass)) {
					T obj = parseCSV(line, targetClass);
					if (obj != null) {
						dataList.add(obj);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binaryFilePath))) {
			oos.writeObject(dataList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Validate CSV format before parsing
	private static boolean validateCSVFormat(String line, Class<?> targetClass) {
		String[] values = line.split(",");

		if (targetClass == Course.class) {
			return values.length == 3 && isInteger(values[0]) && !values[1].trim().isEmpty() && isInteger(values[2]);
		} else if (targetClass == Instructor.class) {
			return values.length == 4 && isInteger(values[0]) && !values[1].trim().isEmpty()
					&& !values[2].trim().isEmpty() && !values[3].trim().isEmpty();
		} else if (targetClass == Student.class) {
			return values.length == 3 && isInteger(values[0]) && !values[1].trim().isEmpty()
					&& !values[2].trim().isEmpty();
		}
		return false;
	}

	// Parse CSV line into corresponding object
	private static <T> T parseCSV(String line, Class<T> targetClass) {
		try {
			if (targetClass == Course.class) {
				return (T) parseCourse(line);
			} else if (targetClass == Instructor.class) {
				return (T) parseInstructor(line);
			} else if (targetClass == Student.class) {
				return (T) parseStudent(line);
			}
		} catch (InvalidCSVFormatException e) {
			System.out.println("Invalid CSV format: " + e.getMessage());
		}
		return null;
	}

	private static Course parseCourse(String line) throws InvalidCSVFormatException {
		String[] data = line.split(",");
		if (data.length != 3) {
			throw new InvalidCSVFormatException("Course data must have 3 fields!");
		}
		if (data[0].trim().isEmpty())
			throw new InvalidCSVFormatException("Course ID is empty!");
		if (data[1].trim().isEmpty())
			throw new InvalidCSVFormatException("Course name is empty!");
		if (data[2].trim().isEmpty())
			throw new InvalidCSVFormatException("Course credit is empty!");

		try {
			return new Course(Integer.parseInt(data[0]), data[1], Integer.parseInt(data[2]));
		} catch (NumberFormatException e) {
			throw new InvalidCSVFormatException("Course credit is not a valid integer!");
		}
	}

	private static Instructor parseInstructor(String line) throws InvalidCSVFormatException {
		String[] data = line.split(",");
		if (data.length != 4) {
			throw new InvalidCSVFormatException("Instructor data must have 4 fields!");
		}
		if (data[0].trim().isEmpty())
			throw new InvalidCSVFormatException("Instructor ID is empty!");
		if (data[1].trim().isEmpty())
			throw new InvalidCSVFormatException("Instructor name is empty!");
		if (data[2].trim().isEmpty())
			throw new InvalidCSVFormatException("Instructor email is empty!");
		if (data[3].trim().isEmpty())
			throw new InvalidCSVFormatException("Instructor department is empty!");

		return new Instructor(Integer.parseInt(data[0]), data[1], data[2], data[3]);
	}

	private static Student parseStudent(String line) throws InvalidCSVFormatException {
		String[] data = line.split(",");
		if (data.length != 3) {
			throw new InvalidCSVFormatException("Student data must have 3 fields!");
		}
		if (data[0].trim().isEmpty())
			throw new InvalidCSVFormatException("Student ID is empty!");
		if (data[1].trim().isEmpty())
			throw new InvalidCSVFormatException("Student name is empty!");
		if (data[2].trim().isEmpty())
			throw new InvalidCSVFormatException("Student email is empty!");

		return new Student(Integer.parseInt(data[0]), data[1], data[2]);
	}

	private static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
