package group1;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//created student manager class to manage insertion and listing of student data and also manage save changes to file
public class StudentManager {

    public static void addStudent() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Student Email: ");
        String email = scanner.nextLine();

        Student newStudent = new Student(id, name, email);

        // Load existing students from the binary file
        ArrayList<Student> students = Student.readStudents();
        students.add(newStudent);
        
        // Update global list in CourseManagementSystem
        CourseManagementSystem.students = students;

        // Save updated student list back to binary file
        Student.saveStudents(students);
        
        //Append new student to `students.csv`
        try (FileWriter fw = new FileWriter("./data/students.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(id + "," + name + "," + email);  
            out.flush();
            System.out.println("Student added to students.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Student added successfully!");
        scanner.close();
    }

    public static void updateStudent() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine();

        // Load existing students from the binary file
        ArrayList<Student> students = Student.readStudents();
        boolean found = false;

        for (Student student : students) {
            if (student.getStudentId().equals(id)) {
                System.out.print("Enter new Student Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter new Student Email: ");
                String email = scanner.nextLine();

                student.setName(name);
                student.setEmail(email);
                found = true;
                break;
            }
        }

        if (found) {
            // Save updated student list back to binary file
            Student.saveStudents(students);
            System.out.println("Student updated successfully!");
        } else {
            System.out.println("Student with ID " + id + " not found.");
        }
        scanner.close();
    }

    public static void listStudents() {
        ArrayList<Student> students = Student.readStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student student : students) {
                student.displayDetails();
                System.out.println("-------------------");
            }
        }
    }
}
