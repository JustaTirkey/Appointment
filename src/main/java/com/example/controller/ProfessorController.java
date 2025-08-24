package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import jakarta.persistence.EntityManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfessorController {

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/professor/{profId}/schedule")
    public ResponseEntity<?> getProfessorSchedule(@PathVariable int profId) {
        String url = "jdbc:mysql://localhost:3306/university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String dbUser = "root";
        String dbPass = "your password"; // add your password 

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
}
