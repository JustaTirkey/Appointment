package com.example.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")   // <-- ensure lowercase matches DB column
    private int id;

    private String name;

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "tot_cred")
    @JsonProperty("tot_cred")   // keep JSON snake_case if you want
    private int totCred;

    private String username;
    private String password;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDeptName() { return deptName; }
    public int getTotCred() { return totCred; }
    public String getUsername() { return username; }
}
