package edu.msoe.examscheduler.controller;

import edu.msoe.examscheduler.model.Exam;
import edu.msoe.examscheduler.service.ExamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    // ---- Read ----

    /**
     * GET /api/exams
     * Optional params: ?search=CS2852  ?semester=Spring+2026
     */
    @GetMapping
    public ResponseEntity<List<Exam>> getExams(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String semester) {
        return ResponseEntity.ok(examService.searchExams(search, semester));
    }

    /**
     * GET /api/exams/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable Long id) {
        return examService.getExamById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
    }

    // ---- Admin CRUD ----

    /**
     * POST /api/exams
     */
    @PostMapping
    public ResponseEntity<Exam> createExam(@Valid @RequestBody Exam exam) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(exam));
    }

    /**
     * PUT /api/exams/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        return ResponseEntity.ok(examService.updateExam(id, exam));
    }

    /**
     * DELETE /api/exams/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Calendar Export ----

    /**
     * GET /api/exams/export/ics?ids=1,2,3
     * Downloads an ICS file for the specified exam IDs.
     * If no ids provided, exports all exams.
     */
    @GetMapping("/export/ics")
    public ResponseEntity<byte[]> exportIcs(@RequestParam(required = false) List<Long> ids) {
        String icsContent = examService.generateIcsContent(ids);
        byte[] bytes = icsContent.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "msoe-exams.ics");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * GET /api/exams/calendar.ics
     * Full calendar ICS for webcal:// subscription (Apple Calendar).
     */
    @GetMapping("/calendar.ics")
    public ResponseEntity<byte[]> fullCalendar() {
        String icsContent = examService.generateIcsContent(null);
        byte[] bytes = icsContent.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * GET /api/exams/{id}/google-calendar-url
     * Returns the pre-filled Google Calendar event creation URL.
     */
    @GetMapping("/{id}/google-calendar-url")
    public ResponseEntity<Map<String, String>> getGoogleCalendarUrl(@PathVariable Long id) {
        Exam exam = examService.getExamById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
        String url = examService.generateGoogleCalendarUrl(exam);
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * GET /api/exams/webcal-url
     * Returns the webcal:// subscription URL for Apple Calendar.
     */
    @GetMapping("/webcal-url")
    public ResponseEntity<Map<String, String>> getWebcalUrl(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443
                        ? ":" + request.getServerPort() : "");
        String url = examService.getWebcalUrl(baseUrl);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
