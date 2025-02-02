package group1;
import java.io.*;
import java.util.ArrayList;

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
