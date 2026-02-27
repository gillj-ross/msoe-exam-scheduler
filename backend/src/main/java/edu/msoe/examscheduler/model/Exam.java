package edu.msoe.examscheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course code is required")
    @Column(nullable = false)
    private String courseCode;

    private String courseSection;

    private String courseName;

    private String instructor;

    @NotNull(message = "Exam date is required")
    @Column(nullable = false)
    private LocalDate examDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    private String semester;

    private String notes;

    // ---- Constructors ----

    public Exam() {}

    public Exam(String courseCode, String courseSection, String courseName, String instructor,
                LocalDate examDate, LocalTime startTime, LocalTime endTime,
                String location, String semester, String notes) {
        this.courseCode = courseCode;
        this.courseSection = courseSection;
        this.courseName = courseName;
        this.instructor = instructor;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.semester = semester;
        this.notes = notes;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseSection() { return courseSection; }
    public void setCourseSection(String courseSection) { this.courseSection = courseSection; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
