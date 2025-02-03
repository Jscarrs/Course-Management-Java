package group1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CourseAssignManager implements Serializable{

	private static final long serialVersionUID = 1L;
	final static String filePathStudents = "./data/course_students.dat";
	final static String filePathInstructor = "./data/course_instructor.dat";

	
	private static void isFileExists(String filePath) {
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created new file: " + filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void assignInstructorToCourse() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter instructor id: ");
		int instructorId = scanner.nextInt();

		isFileExists(filePathInstructor);

		if (isIntructorAssigned(courseId)) {
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
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter student id: ");
		int studentId = scanner.nextInt();

		isFileExists(filePathStudents);

		if (isStudentCourseAssigned(courseId, studentId)) {
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

	/*
	 * One course only can have one instructor, but instructor can have multiple
	 * courses
	 */
	public static boolean isIntructorAssigned(int pcourseId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instId = file.readInt();
				if (courseId == pcourseId) {
					System.out.println("Course " + pcourseId + " has already assigned to instructor: " + instId);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Course and Student has many to many relation but we need to avoid duplicate
	 * records
	 */
	public static boolean isStudentCourseAssigned(int pcourseId, int pstudentId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (courseId == pcourseId && studentId == pstudentId) {
					System.out.println("Course " + pcourseId + " has already assigned to student: " + pstudentId);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static List<CourseInstructor> getAllInstructorAssignments() {
		isFileExists(filePathInstructor);
		List<CourseInstructor> assignments = new ArrayList<>();
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instructorId = file.readInt();
				assignments.add(new CourseInstructor(courseId, instructorId));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return assignments;
	}

	public static List<CourseStudents> getAllStudentAssignments() {
		isFileExists(filePathStudents);
		List<CourseStudents> students = new ArrayList<>();
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				students.add(new CourseStudents(courseId, studentId));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return students;
	}

	public static void displayAllInstructorAssignments() {
		List<CourseInstructor> assignments = getAllInstructorAssignments();
		if (assignments.isEmpty()) {
			System.out.println("No instructor assignments found.");
			return;
		}
		System.out.println("Course-Instructor Assignments:");
		for (CourseInstructor assignment : assignments) {
			System.out.println("Course " + assignment.getCourseId() + " - Instructor " + assignment.getInstructorId());
		}
	}

	public static void displayAllStudentAssignments() {
		List<CourseStudents> students = getAllStudentAssignments();
		if (students.isEmpty()) {
			System.out.println("No student assignments found.");
			return;
		}
		System.out.println("Course-Instructor Assignments:");
		for (CourseStudents assignment : students) {
			System.out.println("Course " + assignment.getCourseId() + " - Student " + assignment.getStudentId());
		}
	}
}
