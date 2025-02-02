package group1;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class
 */
public class CourseManagementSystem {

    static ArrayList<Course> courses;
    static ArrayList<Instructor> instructors;
    static ArrayList<Student> students;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in); 

        // Test CSV to Binary Conversion 
        System.out.println("Would you like to convert CSV files to binary format? (yes/no)");
        String response = scan.nextLine();

        if ("yes".equalsIgnoreCase(response)) {
            System.out.println("Starting conversion...");
            CSVToBinaryConverter.convertCsvToBinary("./data/courses.csv", "data/courses.dat", Course.class);
            CSVToBinaryConverter.convertCsvToBinary("./data/instructors.csv", "data/instructors.dat", Instructor.class);
            CSVToBinaryConverter.convertCsvToBinary("./data/students.csv", "data/students.dat", Student.class);
        } else {
            System.out.println("CSV to Binary conversion skipped.");
        }

        // Read Data from Binary Files
        try {
            courses = Course.readCourses();
            instructors = Instructor.readInstructors();
            students = Student.readStudents();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // menu for inserting and listing data
        int choice;
        do {
            System.out.println("\nCourse Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Add Course");
            System.out.println("3. Add Instructor");
            System.out.println("4. List Students");
            System.out.println("5. List Courses");
            System.out.println("6. List Instructors");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            scan.nextLine(); 

            switch (choice) {
                case 1:
                    StudentManager.addStudent();
                    break;
                case 2:
                    CourseManager.addCourse();
                    break;
                case 3:
                    InstructorManager.addInstructor();
                    break;
                case 4:
                    StudentManager.listStudents();
                    break;
                case 5:
                    CourseManager.listCourses();
                    break;
                case 6:
                    InstructorManager.listInstructors();
                    break;
                case 7:
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        } while (choice != 7);

        scan.close();
        
        // Prevent IndexOutOfBoundsException
        if (!courses.isEmpty() && !instructors.isEmpty()) {
            assignInstructorToCourse(0, 0);
        }
        if (!courses.isEmpty() && !students.isEmpty()) {
            assignCourseToStudent(0, 0);
            if (students.size() > 1) assignCourseToStudent(0, 1);
            if (students.size() > 2) assignCourseToStudent(0, 2);
        }

        // Display Course Details Safely
        if (!courses.isEmpty()) {
            courses.get(0).displayCourseDetails();
        }
    }

    private static boolean assignInstructorToCourse(int courseId, int instructorId) {
        if (courseId >= courses.size() || instructorId >= instructors.size()) {
            System.out.println("Invalid indices for instructor or course.");
            return false;
        }
        Instructor instructor = instructors.get(instructorId);
        courses.get(courseId).assignInstructor(instructor);
        return true;
    }

    private static boolean assignCourseToStudent(int courseId, int studentId) {
        if (courseId >= courses.size() || studentId >= students.size()) {
            System.out.println("Invalid indices for student or course.");
            return false;
        }
        Student student = students.get(studentId);
        courses.get(courseId).addStudent(student);
        return true;
    }
}
