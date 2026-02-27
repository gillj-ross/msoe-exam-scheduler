package edu.msoe.examscheduler.service;

import edu.msoe.examscheduler.model.Exam;
import edu.msoe.examscheduler.repository.ExamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    private ExamService examService;

    private Exam sampleExam;

    @BeforeEach
    void setUp() {
        examService = new ExamService(examRepository);

        sampleExam = new Exam("CS2852", "001", "Data Structures", "Jones",
                LocalDate.of(2026, 5, 13),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                "S202", "Spring 2026", "Closed-book exam");
        sampleExam.setId(1L);
    }

    // ---- getAllExams ----

    @Test
    void getAllExams_delegatesToRepository() {
        when(examRepository.findAll()).thenReturn(List.of(sampleExam));
        List<Exam> result = examService.getAllExams();
        assertThat(result).hasSize(1);
        verify(examRepository).findAll();
    }

    // ---- searchExams ----

    @Test
    void searchExams_withQueryAndSemester_usesCombinedQuery() {
        when(examRepository.findByCourseCodeContainingIgnoreCaseAndSemester("CS2852", "Spring 2026"))
                .thenReturn(List.of(sampleExam));

        List<Exam> result = examService.searchExams("CS2852", "Spring 2026");
        assertThat(result).hasSize(1);
        verify(examRepository).findByCourseCodeContainingIgnoreCaseAndSemester("CS2852", "Spring 2026");
    }

    @Test
    void searchExams_withQueryOnly_usesCourseCodeSearch() {
        when(examRepository.findByCourseCodeContainingIgnoreCase("CS2852"))
                .thenReturn(List.of(sampleExam));

        List<Exam> result = examService.searchExams("CS2852", null);
        assertThat(result).hasSize(1);
        verify(examRepository).findByCourseCodeContainingIgnoreCase("CS2852");
    }

    @Test
    void searchExams_withSemesterOnly_usesSemesterSearch() {
        when(examRepository.findBySemester("Spring 2026")).thenReturn(List.of(sampleExam));

        List<Exam> result = examService.searchExams(null, "Spring 2026");
        assertThat(result).hasSize(1);
        verify(examRepository).findBySemester("Spring 2026");
    }

    @Test
    void searchExams_withNoFilters_returnsAll() {
        when(examRepository.findAll()).thenReturn(List.of(sampleExam));

        List<Exam> result = examService.searchExams(null, null);
        assertThat(result).hasSize(1);
        verify(examRepository).findAll();
    }

    @Test
    void searchExams_withBlankQuery_treatsAsNoFilter() {
        when(examRepository.findBySemester("Spring 2026")).thenReturn(List.of(sampleExam));

        List<Exam> result = examService.searchExams("   ", "Spring 2026");
        assertThat(result).hasSize(1);
        verify(examRepository).findBySemester("Spring 2026");
    }

    // ---- getExamById ----

    @Test
    void getExamById_returnsExam_whenFound() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(sampleExam));
        Optional<Exam> result = examService.getExamById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getCourseCode()).isEqualTo("CS2852");
    }

    @Test
    void getExamById_returnsEmpty_whenNotFound() {
        when(examRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Exam> result = examService.getExamById(99L);
        assertThat(result).isEmpty();
    }

    // ---- createExam ----

    @Test
    void createExam_savesAndReturnsExam() {
        when(examRepository.save(any(Exam.class))).thenReturn(sampleExam);
        Exam created = examService.createExam(sampleExam);
        assertThat(created.getCourseCode()).isEqualTo("CS2852");
        verify(examRepository).save(sampleExam);
    }

    // ---- updateExam ----

    @Test
    void updateExam_updatesFieldsAndSaves() {
        Exam updated = new Exam("CS2852", "002", "Data Structures", "Smith",
                LocalDate.of(2026, 5, 13),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                "S211", "Spring 2026", null);

        when(examRepository.findById(1L)).thenReturn(Optional.of(sampleExam));
        when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));

        Exam result = examService.updateExam(1L, updated);
        assertThat(result.getCourseSection()).isEqualTo("002");
        assertThat(result.getInstructor()).isEqualTo("Smith");
        assertThat(result.getLocation()).isEqualTo("S211");
    }

    @Test
    void updateExam_throwsException_whenNotFound() {
        when(examRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> examService.updateExam(99L, sampleExam));
    }

    // ---- deleteExam ----

    @Test
    void deleteExam_callsRepositoryDelete() {
        examService.deleteExam(1L);
        verify(examRepository).deleteById(1L);
    }

    // ---- generateIcsContent ----

    @Test
    void generateIcsContent_containsRequiredIcsFields() {
        when(examRepository.findAllById(List.of(1L))).thenReturn(List.of(sampleExam));

        String ics = examService.generateIcsContent(List.of(1L));

        assertThat(ics).contains("BEGIN:VCALENDAR");
        assertThat(ics).contains("END:VCALENDAR");
        assertThat(ics).contains("BEGIN:VEVENT");
        assertThat(ics).contains("END:VEVENT");
        assertThat(ics).contains("SUMMARY:CS2852 Final Exam");
        assertThat(ics).contains("LOCATION:S202");
        assertThat(ics).contains("DTSTART:");
        assertThat(ics).contains("DTEND:");
        assertThat(ics).contains("UID:exam-1@msoe-exam-scheduler");
    }

    @Test
    void generateIcsContent_withNullIds_exportsAll() {
        when(examRepository.findAll()).thenReturn(List.of(sampleExam));

        String ics = examService.generateIcsContent(null);
        assertThat(ics).contains("BEGIN:VEVENT");
    }

    // ---- generateGoogleCalendarUrl ----

    @Test
    void generateGoogleCalendarUrl_containsRequiredParams() {
        String url = examService.generateGoogleCalendarUrl(sampleExam);

        assertThat(url).startsWith("https://calendar.google.com/calendar/r/eventedit");
        assertThat(url).contains("text=");
        assertThat(url).contains("CS2852");
        assertThat(url).contains("dates=");
        assertThat(url).contains("location=");
        assertThat(url).contains("S202");
    }

    // ---- getWebcalUrl ----

    @Test
    void getWebcalUrl_replacesHttpsWithWebcal() {
        String webcal = examService.getWebcalUrl("https://api.example.com");
        assertThat(webcal).startsWith("webcal://");
        assertThat(webcal).contains("/api/exams/calendar.ics");
    }

    @Test
    void getWebcalUrl_replacesHttpWithWebcal() {
        String webcal = examService.getWebcalUrl("http://localhost:8080");
        assertThat(webcal).startsWith("webcal://");
    }
}
