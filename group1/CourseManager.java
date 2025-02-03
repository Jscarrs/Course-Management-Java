package group1;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

//created course manager class to manage insertion and listing of course data and also manage save changes to file
public class CourseManager {
    private static final String COURSE_FILE = "./data/courses.dat";
    private static final String COURSE_CSV = "./data/courses.csv";  

    public static void addCourse() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Enter Course ID: ");
            String courseId = reader.readLine();
            System.out.print("Enter Course Name: ");
            String courseName = reader.readLine();
            System.out.print("Enter Credits: ");
            int credits = Integer.parseInt(reader.readLine());

            Course newCourse = new Course(courseId, courseName, credits);
            ArrayList<Course> courses = readBinaryFile();
            courses.add(newCourse);
            writeBinaryFile(courses);

            // Append course to CSV file
            appendToCSV(courseId, courseName, credits);

            System.out.println("Course added successfully!");
        } catch (IOException e) {
            System.out.println("Error reading input!");
        }
    }
    
        public static void updateCourse() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Course ID to update: ");
        String id = scanner.nextLine();

        // Load existing courses from the binary file
        ArrayList<Course> courses = readBinaryFile();
        boolean found = false;

        for (Course course : courses) {
            if (course.getCourseId().equals(id)) {
                System.out.print("Enter new Course Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter new Credits: ");
                int credits = Integer.parseInt(scanner.nextLine());

                course.setCourseName(name);
                course.setCredits(credits);
                found = true;
                break;
            }
        }

        if (found) {
            // Save updated course list back to binary file
            writeBinaryFile(courses);
            System.out.println("Course updated successfully!");
        } else {
            System.out.println("Course with ID " + id + " not found.");
        }
//        scanner.close();
    } 

    public static void listCourses() {
        ArrayList<Course> courses = readBinaryFile();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        System.out.println("\nList of Courses:");
        for (Course course : courses) {
            System.out.println("Course ID: " + course.getCourseId());
            System.out.println("Course Name: " + course.getCourseName());
            System.out.println("Credits: " + course.getCredits());
            System.out.println("----------------------------");
        }
    }

    public static void listCourseInstructor() {
		// TODO: Read it from file

//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter Course ID to find instructor: ");
//        String id = scanner.nextLine();
//
//        ArrayList<Course> courses = readBinaryFile();
//        boolean found = false;
//
//        for (Course course : courses) {
//            if (course.getCourseId().equals(id)) {
//                System.out.println("Instructor for Course ID " + id + ": " + course.getInstructor());
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            System.out.println("Course with ID " + id + " not found.");
//        }
//        scanner.close();
    }

    public static void listCourseStudent() {
		// TODO: Read it from file

//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter Course ID to find students: ");
//        String id = scanner.nextLine();
//
//        ArrayList<Course> courses = readBinaryFile();
//        boolean found = false;
//
//        for (Course course : courses) {
//            if (course.getCourseId().equals(id)) {
//                ArrayList<Student> students = course.getStudents();
//                if (students.isEmpty()) {
//                    System.out.println("No students enrolled in Course ID " + id);
//                } else {
//                    System.out.println("Students enrolled in Course ID " + id + ":");
//                    for (Student student : students) {
//                        System.out.println(student.getName());
//                    }
//                }
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            System.out.println("Course with ID " + id + " not found.");
//        }
//        scanner.close();
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Course> readBinaryFile() {
        ArrayList<Course> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COURSE_FILE))) {
            list = (ArrayList<Course>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing course records found.");
        }
        return list;
    }

    private static void writeBinaryFile(ArrayList<Course> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COURSE_FILE))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.out.println("Error saving course data!");
        }
    }
    
    private static void appendToCSV(String courseId, String courseName, int credits) {
        try (FileWriter fw = new FileWriter(COURSE_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            out.println(courseId + "," + courseName + "," + credits);  // Write new record
            
        } catch (IOException e) {
            System.out.println("Error appending course to CSV!");
        }
    }
}
