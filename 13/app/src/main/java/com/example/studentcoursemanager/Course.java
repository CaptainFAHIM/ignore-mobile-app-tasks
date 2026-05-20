package com.example.studentcoursemanager;

import java.io.Serializable;

public class Course implements Serializable {
    private final String id;
    private final String name;
    private final String code;
    private final String instructor;
    private final int creditHours;
    private final String schedule;
    private final String room;
    private final String semester;

    public Course() {
        this("", "", "", "", 1, "", "", "");
    }

    public Course(String id, String name, String code, String instructor, int creditHours, String schedule, String room, String semester) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.instructor = instructor;
        this.creditHours = creditHours;
        this.schedule = schedule;
        this.room = room;
        this.semester = semester;
    }

    public Course(String name, String code, String instructor, int creditHours, String schedule, String room, String semester) {
        this("", name, code, instructor, creditHours, schedule, room, semester);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getInstructor() {
        return instructor;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getRoom() {
        return room;
    }

    public String getSemester() {
        return semester;
    }

    public Course withId(String newId) {
        return new Course(newId, name, code, instructor, creditHours, schedule, room, semester);
    }
}