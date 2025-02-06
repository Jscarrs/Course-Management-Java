package service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CourseAssignService implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String filePathStudents = "./data/course_students.dat";
	private static final String filePathInstructor = "./data/course_instructor.dat";
	private static final Scanner scanner = new Scanner(System.in);

	public static void assignInstructorToCourse() {
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter instructor id: ");
		int instructorId = scanner.nextInt();

		if (isIntructorAssignedToCourse(courseId)) {
			System.out.println("Instructor is already assigned to this course.");
			return;
		}

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "rw")) {
			file.seek(file.length());
			file.writeInt(courseId);
			file.writeInt(instructorId);
			System.out.println("Instructor " + instructorId + " assigned to Course " + courseId + " successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void assignStudentToCourse() {
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter student id: ");
		int studentId = scanner.nextInt();

		if (isStudentCourseAssigned(courseId, studentId)) {
			System.out.println("Student is already assigned to this course.");
			return;
		}

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "rw")) {
			file.seek(file.length());
			file.writeInt(courseId);
			file.writeInt(studentId);
			System.out.println("Student " + studentId + " assigned to Course " + courseId + " successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isIntructorAssignedToCourse(int courseId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int storedCourseId = file.readInt();
				file.readInt(); // Skip instructor ID
				if (storedCourseId == courseId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isStudentCourseAssigned(int courseId, int studentId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int storedCourseId = file.readInt();
				int storedStudentId = file.readInt();
				if (storedCourseId == courseId && storedStudentId == studentId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkStudentAssignedToCourse(int courseId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int assignedCourseId = file.readInt();
				file.readInt(); // Skip studentId
				if (assignedCourseId == courseId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkStudentHasCourse(int studentId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				file.readInt(); // Skip courseId
				int assignedStudentId = file.readInt();
				if (assignedStudentId == studentId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkIntructorAssignedToCourse(int courseId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int assignedCourseId = file.readInt();
				file.readInt(); // Skip instructorId
				if (assignedCourseId == courseId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkIntructorHasCourse(int instructorId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				file.readInt(); // Skip courseId
				int assignedInstructorId = file.readInt();
				if (assignedInstructorId == instructorId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void deleteAllStudentFromCourse(int courseId) {
		deleteFromFile(filePathStudents, courseId, true);
	}

	public static void deleteAllInstructorFromCourse(int courseId) {
		deleteFromFile(filePathInstructor, courseId, true);
	}

	public static void deleteAllCourseFromInstructor(int instructorId) {
		deleteFromFile(filePathInstructor, instructorId, false);
	}

	public static void deleteAllCourseFromStudent(int studentId) {
		deleteFromFile(filePathStudents, studentId, false);
	}

	public static void deleteCourseStudent() {
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter student id: ");
		int studentId = scanner.nextInt();
		deleteFromFile(filePathStudents, courseId, studentId);
	}

	public static void deleteCourseInstructor() {
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter instructor id: ");
		int instructorId = scanner.nextInt();
		deleteFromFile(filePathInstructor, courseId, instructorId);
	}

	private static void deleteFromFile(String filePath, int firstId, int secondId) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("No records found in file: " + filePath);
			return;
		}

		List<Integer> remainingRecords = new ArrayList<>();
		boolean deleted = false;

		try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
			while (raf.getFilePointer() < raf.length()) {
				int storedFirstId = raf.readInt();
				int storedSecondId = raf.readInt();
				if (storedFirstId == firstId && storedSecondId == secondId) {
					deleted = true;
				} else {
					remainingRecords.add(storedFirstId);
					remainingRecords.add(storedSecondId);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!deleted) {
			System.out.println("No matching records found.");
			return;
		}

		try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
			raf.setLength(0);
			for (int i = 0; i < remainingRecords.size(); i += 2) {
				raf.writeInt(remainingRecords.get(i));
				raf.writeInt(remainingRecords.get(i + 1));
			}
			System.out.println("Records updated successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
