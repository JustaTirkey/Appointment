package com.example.models;

import jakarta.persistence.*;

@Entity
@Table(name = "instructor")

public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @Column(name = "dept_name")
    private String deptName;
    private int tot_cred;

    private double salary;
    private String username;

    public Instructor(int id, String name, String deptName, double salary, String username) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
        this.salary = salary;
        this.username = username;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDeptName() { return deptName; }
    public String getUsername() { return username; }
}
