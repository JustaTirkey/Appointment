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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        String url = "jdbc:mysql://localhost:3306/university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String dbUser = "root";
        String dbPass = "your_password!"; // add your password 

        // ✅ Fixed queries: fetch whole row
        String studentQuery = "SELECT * FROM student WHERE username = ? AND password = ?";
        String instructorQuery = "SELECT * FROM instructor WHERE username = ? AND password = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Try student login
            try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
                 PreparedStatement ps = con.prepareStatement(studentQuery)) {

                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Map<String, String> res = new HashMap<>();
                        res.put("role", "student");
                        res.put("username", username);
                        res.put("name", rs.getString("name")); // only "name" as before
                        return ResponseEntity.ok(res);
                    }
                }
            }

            // Try instructor login
            try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
                 PreparedStatement ps = con.prepareStatement(instructorQuery)) {

                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Map<String, String> res = new HashMap<>();
                        res.put("role", "professor");
                        res.put("username", username);
                        res.put("name", rs.getString("name")); // only "name" as before
                        return ResponseEntity.ok(res);
                    }
                }
            }

            // Neither matched
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));

        } catch (ClassNotFoundException cnfe) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "JDBC Driver not found: " + cnfe.getMessage()));
        } catch (SQLException sqle) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error: " + sqle.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
