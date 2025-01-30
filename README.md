# **📚 University Course Management System**  
_A Java-based system for managing university courses, instructors, and students using binary files with random access._  

---

## **🚀 Features**
✅ Store and retrieve data in **binary format** for efficient random access.  
✅ Support for **students, courses, and instructors** with CRUD operations.  
✅ **Assign** instructors to courses and students to courses.  
✅ **Prevent duplicate assignments and ensure relationship integrity.**  
✅ **Prompt to update Instructors for Courses that already have an assignment.**  
✅ **Delete records while maintaining relational consistency.**  
✅ **Reuse deleted space** to optimize storage.  
✅ **Text-based menu** for user-friendly interaction.  

---

## **📌 Table of Contents**
1️⃣ [CSV to Binary File Conversion](#1️⃣-csv-to-binary-file-conversion)  
2️⃣ [Adding New Records](#2️⃣-adding-new-records)  
3️⃣ [Updating Records](#3️⃣-updating-records)  
4️⃣ [Assigning an Instructor to a Course](#4️⃣-assigning-an-instructor-to-a-course-with-update-option)  
5️⃣ [Assigning a Student to a Course](#5️⃣-assigning-a-student-to-a-course)  
6️⃣ [Deleting Records](#6️⃣-deleting-records)  
7️⃣ [Listing and Searching Records](#7️⃣-listing-and-searching-records)  
8️⃣ [Menu Implementation & Code Integration](#8️⃣-menu-implementation--code-integration)  
9️⃣ [Optimizing Insert & Delete (Reuse Space)](#9️⃣-optimizing-insert--delete-reuse-space)  

---

## **1️⃣ CSV to Binary File Conversion**
- If a **CSV file exists**, prompt the user to **convert it into a binary file**.  
- This ensures data is stored in **binary format** for efficient random access.  
- **Function:** `convertCsvToBinary()`

---

## **2️⃣ Adding New Records**
- **Check if the binary file exists:**
  - If not, ask for confirmation to **create it** before writing data.
- **Check for duplicate records:**
  - If a record already exists, display its details instead of inserting.
- **Functions:**  
  - `addStudent()`, `addCourse()`, `addInstructor()`

---

## **3️⃣ Updating Records**
- **Verify if the record exists:**
  - If found, **update** the corresponding fields.
  - If not found, display **“Record not found”** and ask if the user wants to create a new record.
- **Functions:**  
  - `updateStudent()`, `updateCourse()`, `updateInstructor()`

---

## **4️⃣ Assigning an Instructor to a Course (With Update Option)**
### **✅ Steps:**
1. **Verify Instructor & Course Existence:**  
   - Check if both `Instructor` and `Course` exist in their respective binary files.  

2. **Enforce One Instructor Per Course Rule:**  
   - **Before assigning, check if the Course is already assigned to an Instructor.**  
   - If assigned, prompt the user:  
     ```plaintext
     ⚠️ This Course is already assigned to Instructor XYZ. 
     Do you want to update the Instructor? (Y/N)
     ```
   - **If "Yes" → Update the assigned Instructor**  
   - **If "No" → Keep the existing assignment unchanged.**  

3. **Allow One Instructor to Teach Multiple Courses:**  
   - An Instructor can be assigned to **multiple courses**, so only **Course-to-Instructor conflicts** need to be checked.

4. **Prevent Duplicate Assignments:**  
   - If the exact **Course-Instructor pair** already exists, notify the user and avoid duplication.

5. **Handling Binary File:**  
   - If `course_instructor.bin` does not exist, create it.  
   - **If updating, overwrite the existing Instructor ID for that Course.**  
   - Append new assignments **only if the Course does not already have an Instructor or if the user confirms an update.**  

### **✅ Function:**  
- `assignInstructorToCourse()`
  - **Ensures each Course has only one Instructor.**
  - **Allows one Instructor to be assigned to multiple Courses.**
  - **Prompts for Instructor update if the Course already has an assigned Instructor.**

---

## **5️⃣ Assigning a Student to a Course**
- **Validation Before Assigning:**
  - Ensure both **Student** and **Course** exist before proceeding.
- **Prevent Duplicate Assignments:**
  - If an assignment already exists, notify the user.
- **Handling Binary File:**
  - If `student_course.bin` does not exist, create it.
  - Append new assignments if the file exists.
- **Function:**  
  - `assignStudentToCourse()`

---

## **6️⃣ Deleting Records**
- **Check for assigned relationships first:**  
  - If assignments exist, prompt the user:  
    _“This record has linked assignments. Do you want to delete them as well?”_  
  - If confirmed, delete both the assigned relationships and the main record.
- **Affects:**  
  - `student.bin`, `course.bin`, `instructor.bin`  
  - `course_instructor.bin`, `student_course.bin`  
- **Functions:**  
  - `deleteStudent()`, `deleteCourse()`, `deleteInstructor()`  
  - `deleteCourseInstructorRelationship()`, `deleteStudentCourseRelationship()`

---

## **7️⃣ Listing and Searching Records**
- **Display all records of a specific type**  
  - Show all **students, courses, instructors, or assignment relationships**.
- **Search by ID**  
  - Retrieve a specific record using an **ID-based lookup**.
- **Functions:**  
  - `listStudents()`, `listCourses()`, `listInstructors()`  
  - `listCourseInstructors()`, `listStudentCourses()`  
  - `searchStudentById()`, `searchCourseById()`, `searchInstructorById()`

---

## **8️⃣ Menu Implementation & Code Integration**
- Create a **text-based menu** allowing the user to:
  1. Convert CSV to Binary  
  2. Add a new record (Student, Course, Instructor)  
  3. Update a record  
  4. Assign an Instructor to a Course  
  5. Assign a Student to a Course  
  6. Delete a record  
  7. Display all records or search by ID  
  8. Exit  
- **Merge all codes and integrate functions into the menu system.**
- **Functions:**  
  - `displayMenu()`, `integrateFunctions()`

---

## **9️⃣ Optimizing Insert & Delete (Reuse Space)**
- **Improve Insert Function:**  
  - Before appending a new record, check if a **deleted space exists** in the binary file.  
  - If a deleted record is found, overwrite that space instead of increasing file size.
- **Improve Delete Function:**  
  - Instead of physically removing a record, **mark it as deleted** using a flag (`1 = deleted`).  
  - **Periodically compact** the binary file to remove unused space.
- **Function:**  
  - `optimizeInsertAndDelete()`

---

## **🔧 Technologies Used**
- **Java** (`RandomAccessFile`)
- **Binary File Storage**
- **Serialization & Deserialization**
- **Exception Handling**
- **Custom Data Structures**

---
