package com.team2.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.team2.model.Course;
import com.team2.model.Instructor;
import com.team2.model.Student;
import com.team2.model.StudentCourse;
import com.team2.model.StudentCourseId;

public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			return new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Instructor.class)
					.addAnnotatedClass(Student.class).addAnnotatedClass(Course.class).addAnnotatedClass(StudentCourseId.class)
					.addAnnotatedClass(StudentCourse.class).buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}