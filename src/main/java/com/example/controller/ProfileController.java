package com.example.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class ProfileController {

    private final String url = "jdbc:mysql://localhost:3306/university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String dbUser = "root";
    private final String dbPass = "your_password!"; // add your password 

    @GetMapping("/student/{username}")
    public ResponseEntity<?> getStudentProfile(@PathVariable String username) {
        String query = "SELECT * FROM student WHERE username = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> student = new HashMap<>();
                    student.put("id", rs.getInt("id"));
                    student.put("username", rs.getString("username"));
                    student.put("name", rs.getString("name"));
                    student.put("deptName", rs.getString("dept_name")); // match DB column
                    student.put("totCred", rs.getInt("tot_cred"));
                    return ResponseEntity.ok(student);
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error: " + e.getMessage());
        }
    }

    @GetMapping("/professor/{username}")
    public ResponseEntity<?> getProfessorProfile(@PathVariable String username) {
        String query = "SELECT * FROM instructor WHERE username = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> instructor = new HashMap<>();
                    instructor.put("id", rs.getInt("id"));
                    instructor.put("username", rs.getString("username"));
                    instructor.put("name", rs.getString("name"));
                    instructor.put("deptName", rs.getString("dept_name"));
                    instructor.put("id", rs.getString("id"));
                    // add other columns if needed
                    return ResponseEntity.ok(instructor);
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Professor not found");

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error: " + e.getMessage());
        }
    }

    @PostMapping("/professor/{profId}/requestSlot")
    public ResponseEntity<?> requestSlot(@PathVariable int profId, @RequestBody Map<String, Object> payload) {
        // Extract data from the request payload
        int studentId = (int) payload.get("studentId");
        String date = (String) payload.get("date");
        String startTime = (String) payload.get("start_time");
        String endTime = (String) payload.get("end_time");
        String description = (String) payload.get("description"); // Assuming a description is sent

        // SQL Statements
        String insertRequestSql = "INSERT INTO meeting_request (student_id, professor_id, requested_date, " +
                                "requested_start_time, requested_end_time, status) VALUES (?, ?, ?, ?, ?, 'pending')";

        String updateCalendarSql = "UPDATE professor_calendar SET status = 'requested', requested_by = ?, description = ? " +
                                "WHERE professor_id = ? AND date = ? AND start_time = ? AND status = 'free'";

        Connection con = null;
        try {
                con = DriverManager.getConnection(url, dbUser, dbPass);
                // Start transaction
                con.setAutoCommit(false);
                
                // 1. Insert into meeting_request table
                try (PreparedStatement psInsert = con.prepareStatement(insertRequestSql)) {
                        psInsert.setInt(1, studentId);
                        psInsert.setInt(2, profId);
                        psInsert.setDate(3, java.sql.Date.valueOf(date));
                        psInsert.setTime(4, java.sql.Time.valueOf(startTime));
                        psInsert.setTime(5, java.sql.Time.valueOf(endTime));
                        psInsert.executeUpdate();
                }
                
                // 2. Update the professor_calendar table
                try (PreparedStatement psUpdate = con.prepareStatement(updateCalendarSql)) {
                  psUpdate.setInt(1, studentId);         // requested_by = ?
                  psUpdate.setString(2, description);    // description = ?
                  psUpdate.setInt(3, profId);            // professor_id = ?
                  psUpdate.setDate(4, java.sql.Date.valueOf(date)); // date = ?
                  psUpdate.setTime(5, java.sql.Time.valueOf(startTime)); // start_time = ?

                  int rowsUpdated = psUpdate.executeUpdate();
                
                   // If no rows were updated, it means the slot wasn't free.
                   if (rowsUpdated == 0) {
                        con.rollback(); // Important: Undo the insert
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Slot is not available or already booked."));
                    }
                }

                // If both operations succeeded, commit the transaction
                con.commit();
                
                return ResponseEntity.ok(Map.of("success", true, "message", "Request submitted successfully."));

        } catch (SQLException e) {
                // If anything goes wrong, roll back all changes
                if (con != null) {
                try {
                        con.rollback();
                } catch (SQLException ex) {
                        ex.printStackTrace();
                }
                }
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        } finally {
                if (con != null) {
                try {
                        con.setAutoCommit(true); // Restore default behavior
                        con.close();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                }
        }
    }
}
