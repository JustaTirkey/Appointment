package com.appointment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@RestController
public class AppointmentController {

    @GetMapping("/appointments")
    public List<Appointment> getAppointments() {
        List<Appointment> list = new ArrayList<>();

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/appointment_system", "user (generally root)", "dummyPassword");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT a.id, u.name AS user, d.name AS doctor, a.appointment_date, a.appointment_time, a.status " +
                    "FROM appointments a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "JOIN doctors d ON a.doctor_id = d.id");

            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setUser(rs.getString("user"));
                a.setDoctor(rs.getString("doctor"));
                a.setDate(rs.getString("appointment_date"));
                a.setTime(rs.getString("appointment_time"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
