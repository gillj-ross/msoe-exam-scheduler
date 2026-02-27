package edu.msoe.examscheduler.service;

import edu.msoe.examscheduler.model.Exam;
import edu.msoe.examscheduler.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    private static final DateTimeFormatter ICS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final DateTimeFormatter GOOGLE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final ZoneId MSOE_ZONE = ZoneId.of("America/Chicago");

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    // ---- CRUD ----

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public List<Exam> searchExams(String query, String semester) {
        if (query != null && !query.isBlank() && semester != null && !semester.isBlank()) {
            return examRepository.findByCourseCodeContainingIgnoreCaseAndSemester(query, semester);
        } else if (query != null && !query.isBlank()) {
            return examRepository.findByCourseCodeContainingIgnoreCase(query);
        } else if (semester != null && !semester.isBlank()) {
            return examRepository.findBySemester(semester);
        }
        return examRepository.findAll();
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public Exam createExam(Exam exam) {
        return examRepository.save(exam);
    }

    public Exam updateExam(Long id, Exam updated) {
        return examRepository.findById(id).map(existing -> {
            existing.setCourseCode(updated.getCourseCode());
            existing.setCourseSection(updated.getCourseSection());
            existing.setCourseName(updated.getCourseName());
            existing.setInstructor(updated.getInstructor());
            existing.setExamDate(updated.getExamDate());
            existing.setStartTime(updated.getStartTime());
            existing.setEndTime(updated.getEndTime());
            existing.setLocation(updated.getLocation());
            existing.setSemester(updated.getSemester());
            existing.setNotes(updated.getNotes());
            return examRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    // ---- Calendar Export ----

    /**
     * Generates an ICS (iCalendar) file content string for the given exam IDs.
     * Falls back to all exams if ids list is null/empty.
     */
    public String generateIcsContent(List<Long> ids) {
        List<Exam> exams = (ids == null || ids.isEmpty())
                ? examRepository.findAll()
                : examRepository.findAllById(ids);

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//MSOE Exam Scheduler//EN\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");
        ics.append("METHOD:PUBLISH\r\n");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        String dtstamp = now.format(ICS_DATE_FORMAT);

        for (Exam exam : exams) {
            ics.append("BEGIN:VEVENT\r\n");
            ics.append("UID:exam-").append(exam.getId()).append("@msoe-exam-scheduler\r\n");
            ics.append("DTSTAMP:").append(dtstamp).append("\r\n");
            ics.append("DTSTART:").append(toUtcString(exam.getExamDate(), exam.getStartTime())).append("\r\n");
            ics.append("DTEND:").append(toUtcString(exam.getExamDate(), exam.getEndTime())).append("\r\n");
            ics.append("SUMMARY:").append(icsEscape(exam.getCourseCode() + " Final Exam")).append("\r\n");
            ics.append("LOCATION:").append(icsEscape(nullSafe(exam.getLocation()))).append("\r\n");
            ics.append("DESCRIPTION:").append(icsEscape(buildDescription(exam))).append("\r\n");
            ics.append("STATUS:CONFIRMED\r\n");
            ics.append("END:VEVENT\r\n");
        }

        ics.append("END:VCALENDAR\r\n");
        return ics.toString();
    }

    /**
     * Generates a Google Calendar "add event" URL for a single exam.
     * No API key required — uses Google Calendar's public event creation URL.
     */
    public String generateGoogleCalendarUrl(Exam exam) {
        String title = encode(exam.getCourseCode() + " Final Exam");
        String start = toUtcString(exam.getExamDate(), exam.getStartTime());
        String end = toUtcString(exam.getExamDate(), exam.getEndTime());
        String details = encode(buildDescription(exam));
        String location = encode(nullSafe(exam.getLocation()));

        return "https://calendar.google.com/calendar/r/eventedit"
                + "?text=" + title
                + "&dates=" + start + "/" + end
                + "&details=" + details
                + "&location=" + location;
    }

    /**
     * Returns a webcal:// URL pointing to the full ICS endpoint,
     * suitable for Apple Calendar subscription on iOS/macOS.
     */
    public String getWebcalUrl(String baseUrl) {
        String httpUrl = baseUrl + "/api/exams/calendar.ics";
        return httpUrl.replace("https://", "webcal://").replace("http://", "webcal://");
    }

    // ---- Private helpers ----

    private String toUtcString(LocalDate date, LocalTime time) {
        if (date == null) return "";
        LocalTime t = (time != null) ? time : LocalTime.of(0, 0);
        ZonedDateTime zdt = ZonedDateTime.of(date, t, MSOE_ZONE).withZoneSameInstant(ZoneId.of("UTC"));
        return zdt.format(ICS_DATE_FORMAT);
    }

    private String buildDescription(Exam exam) {
        StringBuilder desc = new StringBuilder();
        if (exam.getCourseName() != null) desc.append(exam.getCourseName());
        if (exam.getInstructor() != null) desc.append(" - ").append(exam.getInstructor());
        if (exam.getNotes() != null && !exam.getNotes().isBlank()) desc.append(" | ").append(exam.getNotes());
        return desc.toString();
    }

    private String icsEscape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace(";", "\\;").replace(",", "\\,").replace("\n", "\\n");
    }

    private String encode(String value) {
        return URLEncoder.encode(nullSafe(value), StandardCharsets.UTF_8);
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
