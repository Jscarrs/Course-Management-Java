import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Superclass Person
class Person {
    private String name;
    private String email;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void displayDetails() {
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
    }
}

// Subclass Student
class Student extends Person {
    private String studentId;

    public Student(String studentId, String name, String email) {
        super(name, email);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public void displayDetails() {
        System.out.println("Student ID: " + studentId);
        super.displayDetails();
    }
}

// Subclass Instructor
class Instructor extends Person {
    private String instructorId;
    private String department;

    public Instructor(String instructorId, String name, String email, String department) {
        super(name, email);
        this.instructorId = instructorId;
        this.department = department;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public void displayDetails() {
        System.out.println("Instructor ID: " + instructorId);
        System.out.println("Department: " + department);
        super.displayDetails();
    }
}

// Class Course
class Course {
    private String courseId;
    private String courseName;
    private int credits;
    private Instructor instructor;
    private List<Student> students;

    public Course(String courseId, String courseName, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.students = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void assignInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void displayCourseDetails() {
        System.out.println("Course ID: " + courseId);
        System.out.println("Course Name: " + courseName);
        System.out.println("Credits: " + credits);
        if (instructor != null) {
            System.out.println("\nInstructor Details:");
            instructor.displayDetails();
        } else {
            System.out.println("\nNo instructor assigned yet.");
        }

        System.out.println("\nEnrolled Students:");
        if (students.isEmpty()) {
            System.out.println("No students enrolled yet.");
        } else {
            for (Student student : students) {
                student.displayDetails();
                System.out.println();
            }
        }
    }
}

// Main Class
public class CourseManagementSystem {
    public static void main(String[] args) {
        Course course = null;
        try (BufferedReader courseBr = new BufferedReader(new FileReader("course.csv"))) {
            courseBr.readLine(); // Skip header

            String line;
            while ((line = courseBr.readLine()) != null) {
                course = parseCourse(line);
            }

            courseBr.close();

        } catch (InvalidCSVFormatException e) {
            System.out.println(e);
            return;
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: File does not exists! (course.csv)");
            return;
        } catch (IOException e) {
            System.out.println("IOException: Unable to read a file! (course.csv)");
            return;
        }
        // Read Instructor Details
        try (BufferedReader instructorBr = new BufferedReader(new FileReader("instructor.csv"))) {
            instructorBr.readLine(); // Skip header

            String line;
            while ((line = instructorBr.readLine()) != null) {
                Instructor instructor = parseInstructor(line);
                course.assignInstructor(instructor);
            }

            instructorBr.close();

        } catch (InvalidCSVFormatException e) {
            // Incorrect csv format || Missing or invalid data
            System.out.println(e);
            return;
        } catch (FileNotFoundException e) {
            // File not found or can't open file (permission etc.)
            System.out.println("FileNotFoundException: File does not exists! (instructor.csv)");
            return;
        } catch (IOException e) {
            // Any other IO exceptions
            System.out.println("IOException: Unable to read a file! (instructor.csv)");
            return;
        }

        // Read Student Details
        try (BufferedReader br = new BufferedReader(new FileReader("student.csv"))) {
            br.readLine(); // Skip header

            String line;
            while ((line = br.readLine()) != null) {
                Student student = parseStudent(line);
                course.addStudent(student);
            }
            br.close();
        } catch (InvalidCSVFormatException e) {
            // Incorrect csv format || Missing or invalid data
            System.out.println(e);
            return;
        } catch (FileNotFoundException e) {
            // File not found or can't open file (permission etc.)
            System.out.println("FileNotFoundException: File does not exists! (student.csv)");
            return;
        } catch (IOException e) {
            // Any other IO exceptions
            System.out.println("IOException: Unable to read a file! (student.csv)");
            return;
        }

        // Display Course Details
        course.displayCourseDetails();
    }

    private static Instructor parseInstructor(String line) throws InvalidCSVFormatException {
        String[] data = line.split(",");

        // Incorrect CSV Format
        if (data.length != 4) {
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor data must have 4 fields!");
        }

        // Checking Missing or Invalid Data:
        if (data[0] == null || data[0].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor id is empty!");
        else if (data[1] == null || data[1].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor name is empty!");
        else if (data[2] == null || data[2].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor email is empty!");
        else if (data[2] == null || data[3].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Instructor department is empty!");

        return new Instructor(data[0], data[1], data[2], data[3]);
    }

    private static Student parseStudent(String line) throws InvalidCSVFormatException {
        String[] data = line.split(",");

        // Incorrect CSV Format
        if (data.length != 3) {
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Student data must have 3 fields!");
        }

        // Checking Missing or Invalid Data:
        if (data[0] == null || data[0].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Student id is empty!");
        else if (data[1] == null || data[1].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Student name is empty!");
        else if (data[2] == null || data[2].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Student email is empty!");

        return new Student(data[0], data[1], data[2]);
    }

    private static Course parseCourse(String line) throws InvalidCSVFormatException {
        String[] data = line.split(",");
        if (data.length != 3) {
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Course data must have 3 fields!");
        }
        if (data[0] == null || data[0].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Course id is empty!");
        else if (data[1] == null || data[1].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Course name is empty!");
        else if (data[2] == null || data[2].trim().isEmpty())
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Course credit is empty!");
        try {
            int credits = Integer.parseInt(data[2]);
            return new Course(data[0], data[1], credits);
        } catch (NumberFormatException e) {
            throw new InvalidCSVFormatException("InvalidCSVFormatException: Course credit is not a valid integer!");
        }
    }
}

// Custom exception for Invalid CSV
class InvalidCSVFormatException extends Exception {
    public InvalidCSVFormatException(String m) {
        super(m);
    }
}
