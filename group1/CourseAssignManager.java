package group1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CourseAssignManager implements Serializable {

	private static final long serialVersionUID = 1L;
	final static String filePathStudents = "./data/course_students.dat";
	final static String filePathInstructor = "./data/course_instructor.dat";
	private static Scanner scanner = new Scanner(System.in);

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
		System.out.print("Enter course id: ");
		int courseId = scanner.nextInt();
		System.out.print("Enter instructor id: ");
		int instructorId = scanner.nextInt();

		isFileExists(filePathInstructor);

		if (isIntructorAssignedToCourse(courseId)) {
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
	public static boolean isIntructorAssignedToCourse(int pcourseId) {
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
	
	// Used in delete functions
	public static boolean checkIntructorAssignedToCourse(int pcourseId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instId = file.readInt();
				if (courseId == pcourseId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Used in delete functions
	public static boolean checkIntructorHasCourse(int pinstructorId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instId = file.readInt();
				if (instId == pinstructorId) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Used in delete functions
	public static boolean checkStudentAssignedToCourse(int pcourse) {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (courseId == pcourse) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Used in delete functions
	public static boolean checkStudentHasCourse(int pstudentId) {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (studentId == pstudentId) {
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

//	DELETE FUNCTIONS:

	public static void checkPathDelete(boolean isCourse) {
		String strRel = isCourse ? "course - student" : "course - instructor";
		if (!new File(filePathStudents).exists()) {
			System.out.println("No records found in " + strRel + " assignments.");
			return;
		}
	}

	// Delete specific course - student relation
	public static void deleteCourseStudent() {
		checkPathDelete(true);

		System.out.print("Enter course id: ");
		int pcourseId = scanner.nextInt();
		System.out.print("Enter student id: ");
		int pstudentId = scanner.nextInt();

		List<CourseStudents> keepingList = new ArrayList<>();
		boolean isDeletedRec = false;

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (courseId == pcourseId && studentId == pstudentId) {
					isDeletedRec = true;
				} else {
					keepingList.add(new CourseStudents(courseId, studentId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isDeletedRec) {
			System.out.println("No matching record found to delete.");
			return;
		}
		overrideCourseStudentFile(keepingList);
	}

	// Delete all courses from student
	// Used in delete
	public static void deleteAllCourseFromStudent(int pstudentId ) {
		checkPathDelete(true);

		List<CourseStudents> keepingList = new ArrayList<>();
		boolean isDeletedRec = false;

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (studentId == pstudentId) {
					isDeletedRec = true;
				} else {
					keepingList.add(new CourseStudents(courseId, studentId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isDeletedRec) {
			System.out.println("Course - No matching records found to delete.");
			return;
		}

		overrideCourseStudentFile(keepingList);
	}

	// Delete all students from given course
	// Used in Delete function
	public static void deleteAllStudentFromCourse(int pcourseId) {
		checkPathDelete(true);

		List<CourseStudents> keepingList = new ArrayList<>();
		boolean isDeletedRec = false;

		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int studentId = file.readInt();
				if (courseId == pcourseId) {
					isDeletedRec = true;
				} else {
					keepingList.add(new CourseStudents(courseId, studentId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isDeletedRec) {
			System.out.println("No matching student records found to delete.");
			return;
		}

		overrideCourseStudentFile(keepingList);
	}

	// Delete specific course - instructor relation
	public static void deleteCourseInstructor() {
		checkPathDelete(false);

		System.out.print("Enter course id: ");
		int pcourseId = scanner.nextInt();
		System.out.print("Enter instructor id: ");
		int pintructorId = scanner.nextInt();

		List<CourseInstructor> keepingList = new ArrayList<>();
		boolean isDeletedRec = false;

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instructorId = file.readInt();
				if (courseId == pcourseId && instructorId == pintructorId) {
					isDeletedRec = true;
				} else {
					keepingList.add(new CourseInstructor(courseId, instructorId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isDeletedRec) {
			System.out.println("No matching record found to delete.");
			return;
		}

		overrideCourseInstructorFile(keepingList);
	}

	// Delete all assigned Courses from Instructor
	// Used in delete function
	public static void deleteAllCourseFromInstructor(int pinstructorId) {
		checkPathDelete(false);


		List<CourseInstructor> keepingList = new ArrayList<>();
		boolean isDeletedRec = false;

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instructorId = file.readInt();
				if (instructorId == pinstructorId) {
					isDeletedRec = true;
				} else {
					keepingList.add(new CourseInstructor(courseId, instructorId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isDeletedRec) {
			System.out.println("No matching records found to delete.");
			return;
		}

		overrideCourseInstructorFile(keepingList);
	}
	
	// Delete all assigned Instructors from Course
	// Used in Delete function
	public static void deleteAllInstructorFromCourse(int pcourseId) {
		checkPathDelete(false);

		List<CourseInstructor> keepingList = new ArrayList<>();
		boolean isDeletedRec = false;

		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "r")) {
			while (file.getFilePointer() < file.length()) {
				int courseId = file.readInt();
				int instructorId = file.readInt();
				if (courseId == pcourseId) {
					isDeletedRec = true;
				} else {
					keepingList.add(new CourseInstructor(courseId, instructorId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isDeletedRec) {
			System.out.println("Instructor - No matching records found to delete.");
			return;
		}

		overrideCourseInstructorFile(keepingList);
	}

	public static void overrideCourseStudentFile(List<CourseStudents> keepingList) {
		try (RandomAccessFile file = new RandomAccessFile(filePathStudents, "rw")) {
			file.setLength(0);
			for (CourseStudents record : keepingList) {
				file.writeInt(record.getCourseId());
				file.writeInt(record.getStudentId());
			}

			System.out.println("Student - Deleted records: " + keepingList.size());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void overrideCourseInstructorFile(List<CourseInstructor> keepingList) {
		try (RandomAccessFile file = new RandomAccessFile(filePathInstructor, "rw")) {
			file.setLength(0);
			for (CourseInstructor record : keepingList) {
				file.writeInt(record.getCourseId());
				file.writeInt(record.getInstructorId());
			}

			System.out.println("Instructor - Deleted records: " + keepingList.size());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
