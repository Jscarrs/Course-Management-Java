package com.team2.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import com.team2.exceptions.ConstraintException;
import com.team2.model.Student;
import com.team2.util.HibernateUtil;

public class StudentDAO {

	public static List<Student> getStudents() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<Student> query = session.createQuery("FROM Student", Student.class);
			List<Student> students = query.list();
			return students;
		}
	}

	public static Student getStudentById(int id) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Student student = session.get(Student.class, id);
			return student;
		}
	}

	@SuppressWarnings("deprecation")
	public static void insertStudent(Student student) {
		Transaction tx = null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			session.save(student);

			tx.commit();
			System.out.println("Student added successfully!");
		} catch (Exception e) {
			if (tx != null && tx.getStatus().canRollback()) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean updateStudent(Student student) {
		Transaction tx = null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			session.update(student);

			tx.commit();
			System.out.println("Student updated successfully!");
			return true;
		} catch (Exception e) {
			if (tx != null && tx.getStatus().canRollback()) {
				tx.rollback();
			}
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean deleteStudent(int studentId) throws ConstraintException {
		Transaction tx = null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Student student = session.get(Student.class, studentId);
			if (student != null) {
				session.delete(student);
				tx.commit();
				System.out.println("Student deleted successfully!");
				return true;
			} else {
				System.out.println("Student with ID " + studentId + " not found.");
				return false;
			}
		} catch (ConstraintViolationException e) {
			if (tx != null && tx.getStatus().canRollback()) {
				tx.rollback();
			}
			throw new ConstraintException("Cannot delete student. The student is enrolled in courses.");
		} catch (Exception e) {
			if (tx != null && tx.getStatus().canRollback()) {
				tx.rollback();
			}
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
}
