package edu.msoe.examscheduler.repository;

import edu.msoe.examscheduler.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findBySemester(String semester);

    List<Exam> findByCourseCodeContainingIgnoreCase(String courseCode);

    List<Exam> findByCourseCodeContainingIgnoreCaseAndSemester(String courseCode, String semester);

    List<Exam> findByCourseNameContainingIgnoreCase(String courseName);
}
