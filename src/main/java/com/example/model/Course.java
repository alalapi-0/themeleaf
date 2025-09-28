package com.example.model;

import java.util.Objects;

public class Course {
    private final int id;
    private final String title;
    private final String level;
    private final int minAge;
    private final String requireRole;
    private final String description;

    public Course(int id, String title, String level, int minAge, String requireRole, String description) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.minAge = minAge;
        this.requireRole = requireRole;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }

    public int getMinAge() {
        return minAge;
    }

    public String getRequireRole() {
        return requireRole;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return id == course.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
