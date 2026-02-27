package edu.msoe.examscheduler.repository;

import edu.msoe.examscheduler.model.Exam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ExamRepositoryTest {

    @Autowired
    private ExamRepository examRepository;

    private Exam dataStructures;
    private Exam algorithms;
    private Exam calculus;

    @BeforeEach
    void setUp() {
        examRepository.deleteAll();

        dataStructures = examRepository.save(new Exam(
                "CS2852", "001", "Data Structures", "Jones",
                LocalDate.of(2026, 5, 13),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                "S202", "Spring 2026", null));

        algorithms = examRepository.save(new Exam(
                "CS3851", "001", "Algorithms", "Davis",
                LocalDate.of(2026, 5, 13),
                LocalTime.of(10, 15), LocalTime.of(12, 15),
                "S211", "Spring 2026", null));

        calculus = examRepository.save(new Exam(
                "MA1120", "001", "Calculus I", "Moore",
                LocalDate.of(2026, 5, 11),
                LocalTime.of(13, 0), LocalTime.of(15, 0),
                "CC103", "Fall 2025", null));
    }

    @Test
    void findAll_returnsAllExams() {
        List<Exam> exams = examRepository.findAll();
        assertThat(exams).hasSize(3);
    }

    @Test
    void findById_returnsCorrectExam() {
        Optional<Exam> found = examRepository.findById(dataStructures.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCourseCode()).isEqualTo("CS2852");
        assertThat(found.get().getCourseName()).isEqualTo("Data Structures");
    }

    @Test
    void findByCourseCodeContainingIgnoreCase_returnsMatchingExams() {
        List<Exam> results = examRepository.findByCourseCodeContainingIgnoreCase("cs");
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Exam::getCourseCode)
                .containsExactlyInAnyOrder("CS2852", "CS3851");
    }

    @Test
    void findByCourseCodeContainingIgnoreCase_isCaseInsensitive() {
        List<Exam> lower = examRepository.findByCourseCodeContainingIgnoreCase("cs2852");
        List<Exam> upper = examRepository.findByCourseCodeContainingIgnoreCase("CS2852");
        assertThat(lower).hasSameSizeAs(upper);
    }

    @Test
    void findBySemester_returnsOnlyMatchingSemester() {
        List<Exam> spring2026 = examRepository.findBySemester("Spring 2026");
        assertThat(spring2026).hasSize(2);

        List<Exam> fall2025 = examRepository.findBySemester("Fall 2025");
        assertThat(fall2025).hasSize(1);
        assertThat(fall2025.get(0).getCourseCode()).isEqualTo("MA1120");
    }

    @Test
    void findByCourseCodeContainingIgnoreCaseAndSemester_filtersCorrectly() {
        List<Exam> results = examRepository.findByCourseCodeContainingIgnoreCaseAndSemester("CS", "Spring 2026");
        assertThat(results).hasSize(2);

        List<Exam> noMatch = examRepository.findByCourseCodeContainingIgnoreCaseAndSemester("CS", "Fall 2025");
        assertThat(noMatch).isEmpty();
    }

    @Test
    void save_persistsNewExam() {
        Exam newExam = new Exam("PH2010", "001", "Physics I", "Jackson",
                LocalDate.of(2026, 5, 11),
                LocalTime.of(15, 15), LocalTime.of(17, 15),
                "S211", "Spring 2026", null);
        Exam saved = examRepository.save(newExam);
        assertThat(saved.getId()).isNotNull();
        assertThat(examRepository.findAll()).hasSize(4);
    }

    @Test
    void delete_removesExam() {
        examRepository.deleteById(dataStructures.getId());
        assertThat(examRepository.findById(dataStructures.getId())).isEmpty();
        assertThat(examRepository.findAll()).hasSize(2);
    }
}
