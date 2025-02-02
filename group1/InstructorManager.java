package group1;
import java.io.*;
import java.util.ArrayList;

//created instructor manager class to manage insertion and listing of instructor data and also manage save changes to file
public class InstructorManager {
    private static final String INSTRUCTOR_FILE = "./data/instructors.dat";
    private static final String INSTRUCTOR_CSV = "./data/instructors.csv"; 

    public static void addInstructor() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Enter Instructor ID: ");
            String instructorId = reader.readLine();
            System.out.print("Enter Instructor Name: ");
            String name = reader.readLine();
            System.out.print("Enter Instructor Email: ");
            String email = reader.readLine();
            System.out.print("Enter Department: ");
            String department = reader.readLine();

            Instructor newInstructor = new Instructor(instructorId, name, email, department);
            
            ArrayList<Instructor> instructors = readBinaryFile();
            instructors.add(newInstructor);
            writeBinaryFile(instructors);
            
            // Append instructor to CSV file
            appendToCSV(instructorId, name, email, department);
            
            System.out.println("Instructor added successfully!");
        } catch (IOException e) {
            System.out.println("Error reading input!");
        }
    }

    public static void listInstructors() {
        ArrayList<Instructor> instructors = readBinaryFile();
        if (instructors.isEmpty()) {
            System.out.println("No instructors found.");
            return;
        }

        System.out.println("\nList of Instructors:");
        for (Instructor instructor : instructors) {
            System.out.println("Instructor ID: " + instructor.getInstructorId());
            System.out.println("Name: " + instructor.getName());
            System.out.println("Email: " + instructor.getEmail());
            System.out.println("Department: " + instructor.getDepartment());
            System.out.println("----------------------------");
        }
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Instructor> readBinaryFile() {
        ArrayList<Instructor> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INSTRUCTOR_FILE))) {
            list = (ArrayList<Instructor>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing instructor records found.");
        }
        return list;
    }

    private static void writeBinaryFile(ArrayList<Instructor> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INSTRUCTOR_FILE))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.out.println("Error saving instructor data!");
        }
    }
 // ✅ Append new instructor to CSV file
    private static void appendToCSV(String instructorId, String name, String email, String department) {
        try (FileWriter fw = new FileWriter(INSTRUCTOR_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println();  // Ensure newline before adding new record
            out.println(instructorId + "," + name + "," + email + "," + department);  // Write new record
        } catch (IOException e) {
            System.out.println("Error appending instructor to CSV!");
        }
    }
}
