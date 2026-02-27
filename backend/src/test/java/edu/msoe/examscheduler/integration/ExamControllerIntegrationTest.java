package edu.msoe.examscheduler.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.msoe.examscheduler.model.Exam;
import edu.msoe.examscheduler.repository.ExamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExamRepository examRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Exam savedExam;

    @BeforeEach
    void setUp() {
        examRepository.deleteAll();
        savedExam = examRepository.save(new Exam(
                "CS2852", "001", "Data Structures", "Jones",
                LocalDate.of(2026, 5, 13),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                "S202", "Spring 2026", null));
    }

    // ---- GET /api/exams ----

    @Test
    void getExams_returnsOkAndJsonArray() throws Exception {
        mockMvc.perform(get("/api/exams"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].courseCode", is("CS2852")));
    }

    @Test
    void getExams_withSearchParam_filtersResults() throws Exception {
        mockMvc.perform(get("/api/exams").param("search", "CS2852"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseCode", is("CS2852")));
    }

    @Test
    void getExams_withSemesterParam_filtersResults() throws Exception {
        mockMvc.perform(get("/api/exams").param("semester", "Spring 2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].semester", is("Spring 2026")));
    }

    // ---- GET /api/exams/{id} ----

    @Test
    void getExamById_returnsExam() throws Exception {
        mockMvc.perform(get("/api/exams/{id}", savedExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode", is("CS2852")))
                .andExpect(jsonPath("$.courseName", is("Data Structures")));
    }

    @Test
    void getExamById_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/exams/999999"))
                .andExpect(status().isNotFound());
    }

    // ---- POST /api/exams ----

    @Test
    void createExam_returnsCreatedExam() throws Exception {
        Exam newExam = new Exam("MA1120", "001", "Calculus I", "Moore",
                LocalDate.of(2026, 5, 11),
                LocalTime.of(13, 0), LocalTime.of(15, 0),
                "CC103", "Spring 2026", null);

        mockMvc.perform(post("/api/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newExam)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseCode", is("MA1120")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createExam_returns400WhenCourseCodeMissing() throws Exception {
        Exam invalid = new Exam(null, "001", "Test", "Prof",
                LocalDate.of(2026, 5, 11), null, null, null, "Spring 2026", null);

        mockMvc.perform(post("/api/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ---- PUT /api/exams/{id} ----

    @Test
    void updateExam_returnsUpdatedExam() throws Exception {
        savedExam.setInstructor("Dr. Smith");
        savedExam.setLocation("S211");

        mockMvc.perform(put("/api/exams/{id}", savedExam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedExam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instructor", is("Dr. Smith")))
                .andExpect(jsonPath("$.location", is("S211")));
    }

    // ---- DELETE /api/exams/{id} ----

    @Test
    void deleteExam_returns204() throws Exception {
        mockMvc.perform(delete("/api/exams/{id}", savedExam.getId()))
                .andExpect(status().isNoContent());
    }

    // ---- GET /api/exams/export/ics ----

    @Test
    void exportIcs_returnsTextCalendarContentType() throws Exception {
        mockMvc.perform(get("/api/exams/export/ics")
                        .param("ids", savedExam.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/calendar")))
                .andExpect(content().string(containsString("BEGIN:VCALENDAR")))
                .andExpect(content().string(containsString("BEGIN:VEVENT")))
                .andExpect(content().string(containsString("CS2852")));
    }

    // ---- GET /api/exams/{id}/google-calendar-url ----

    @Test
    void getGoogleCalendarUrl_returnsUrlString() throws Exception {
        mockMvc.perform(get("/api/exams/{id}/google-calendar-url", savedExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", startsWith("https://calendar.google.com")))
                .andExpect(jsonPath("$.url", containsString("CS2852")));
    }

    // ---- GET /api/exams/calendar.ics ----

    @Test
    void fullCalendar_returnsFullIcs() throws Exception {
        mockMvc.perform(get("/api/exams/calendar.ics"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/calendar")))
                .andExpect(content().string(containsString("BEGIN:VCALENDAR")));
    }
}
