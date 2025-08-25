package com.example.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;

@RestController
@RequestMapping("/api")
public class ProfessorController {

    @Autowired
    private EntityManager entityManager;
    
    String url = "jdbc:mysql://localhost:3306/university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    String dbUser = "root";
    String dbPass = "your_password!"; // add your password 

    @GetMapping("/professor/{profId}/schedule")
    public ResponseEntity<?> getProfessorSchedule(@PathVariable int profId) {

        String query = "SELECT date, start_time, end_time, type, status, description " +
                    "FROM professor_calendar WHERE professor_id = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> schedule = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("date", rs.getDate("date"));
                    row.put("start_time", rs.getTime("start_time"));
                    row.put("end_time", rs.getTime("end_time"));
                    row.put("type", rs.getString("type"));
                    row.put("status", rs.getString("status"));
                    row.put("description", rs.getString("description"));
                    // row.put("name", rs.getString("name"));
                    schedule.add(row);
                }
                return ResponseEntity.ok(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/professor/{profId}/requests")
    public ResponseEntity<?> getMeetingRequests(@PathVariable int profId) {
        String query = "SELECT r.request_id, s.name AS studentName, r.requested_date, r.requested_start_time, r.requested_end_time, r.status " +
                    "FROM meeting_request r " +
                    "JOIN student s ON r.student_id = s.id " +
                    "WHERE r.professor_id = ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> requests = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> req = new HashMap<>();
                    req.put("requestId", rs.getInt("request_id"));
                    req.put("studentName", rs.getString("studentName"));
                    req.put("date", rs.getString("requested_date"));
                    req.put("startTime", rs.getString("requested_start_time"));
                    req.put("endTime", rs.getString("requested_end_time"));
                    req.put("status", rs.getString("status"));
                    requests.add(req);
                }
                return ResponseEntity.ok(requests);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
        

    @PostMapping("/request/{requestId}/{action}")
    public ResponseEntity<?> updateRequest(
            @PathVariable int requestId,
            @PathVariable String action) {

        String newStatus;
        if ("accept".equalsIgnoreCase(action)) {
            newStatus = "approved";
        } else if ("reject".equalsIgnoreCase(action)) {
            newStatus = "declined";
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid action"));
        }

        String updateRequestSql = "UPDATE meeting_request SET status = ? WHERE request_id = ?";
        String selectRequestSql = "SELECT professor_id, requested_date, requested_start_time FROM meeting_request WHERE request_id = ?";
        // String updateCalendarSql = "UPDATE professor_calendar SET status = ? WHERE professor_id = ? AND date = ? AND start_time = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass)) {
            con.setAutoCommit(false);

            // 1. Update the meeting_request status
            try (PreparedStatement ps = con.prepareStatement(updateRequestSql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, requestId);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    con.rollback();
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "Request not found"));
                }
            }

            // 2. Get the slot info for updating professor_calendar
            int professorId;
            Date date;
            Time startTime;
            try (PreparedStatement ps = con.prepareStatement(selectRequestSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("message", "Request details not found"));
                    }
                    professorId = rs.getInt("professor_id");
                    date = rs.getDate("requested_date");
                    startTime = rs.getTime("requested_start_time");
                }
            }

            String updateCalendarSql = "UPDATE professor_calendar SET status = ?, description = ?, requested_by = ? " + "WHERE professor_id = ? AND date = ? AND start_time = ? ";

            String calendarStatus = newStatus.equals("approved") ? "booked" : "free";

            try (PreparedStatement ps = con.prepareStatement(updateCalendarSql)) {
                ps.setString(1, calendarStatus);   // status
                ps.setString(2, null);             // description
                ps.setString(3, null);             // requested_by
                ps.setInt(4, professorId);         // professor_id
                ps.setDate(5, date);               // date
                ps.setTime(6, startTime);          // start_time
                ps.executeUpdate();
            }

            con.commit();
            return ResponseEntity.ok(Map.of("message", "Request " + newStatus + " and calendar updated"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating request and calendar: " + e.getMessage()));
        }
    }

}
