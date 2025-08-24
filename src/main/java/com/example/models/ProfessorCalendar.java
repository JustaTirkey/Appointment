package com.example.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "professor_calendar")
public class ProfessorCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Integer calendarId;

    @Column(name = "professor_id", nullable = false)
    private Integer professorId; // or map as @ManyToOne to Instructor if you prefer

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Convert(converter = CalendarTypeConverter.class)
    @Column(name = "type", nullable = false)
    private CalendarType type;

    @Convert(converter = StatusConverter.class)
    @Column(name = "status", nullable = false)
    private Status status = Status.FREE;

    @Column(name = "description")
    private String description;

    // ---- Constructors ----
    public ProfessorCalendar() {}

    public ProfessorCalendar(Integer calendarId, Integer professorId, LocalDate date,
                             LocalTime startTime, LocalTime endTime,
                             CalendarType type, Status status, String description) {
        this.calendarId = calendarId;
        this.professorId = professorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    // ---- Getters & Setters ----
    public Integer getCalendarId() { return calendarId; }
    public void setCalendarId(Integer calendarId) { this.calendarId = calendarId; }

    public Integer getProfessorId() { return professorId; }
    public void setProfessorId(Integer professorId) { this.professorId = professorId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public CalendarType getType() { return type; }
    public void setType(CalendarType type) { this.type = type; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // ---- Enums (Java-friendly) ----
    public enum CalendarType {
        CLASS("class"),
        MEETING("meeting"),
        CONFERENCE("conference"),
        OTHER("other");

        private final String dbValue;
        CalendarType(String v) { this.dbValue = v; }
        public String getDbValue() { return dbValue; }
        public static CalendarType fromDb(String v) {
            for (CalendarType t : values()) if (t.dbValue.equals(v)) return t;
            throw new IllegalArgumentException("Unknown CalendarType: " + v);
        }
    }

    public enum Status {
        FREE("free"),
        BOOKED("booked");

        private final String dbValue;
        Status(String v) { this.dbValue = v; }
        public String getDbValue() { return dbValue; }
        public static Status fromDb(String v) {
            for (Status s : values()) if (s.dbValue.equals(v)) return s;
            throw new IllegalArgumentException("Unknown Status: " + v);
        }
    }

    // ---- Converters to store lowercase enum strings in MySQL ENUM columns ----
    @Converter(autoApply = false)
    public static class CalendarTypeConverter implements AttributeConverter<CalendarType, String> {
        @Override public String convertToDatabaseColumn(CalendarType attribute) {
            return attribute == null ? null : attribute.getDbValue();
        }
        @Override public CalendarType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : CalendarType.fromDb(dbData);
        }
    }

    @Converter(autoApply = false)
    public static class StatusConverter implements AttributeConverter<Status, String> {
        @Override public String convertToDatabaseColumn(Status attribute) {
            return attribute == null ? null : attribute.getDbValue();
        }
        @Override public Status convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Status.fromDb(dbData);
        }
    }
}
