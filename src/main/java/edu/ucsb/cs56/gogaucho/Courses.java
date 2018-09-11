package edu.ucsb.cs56.gogaucho;

import lombok.Data;

@Data
public class Courses {
    private String courseName;
    private String prerequisite;
    private String time;
    private int date;
    private int unit;

    public Courses(String courseName, String prerequisite, String time, int date, int unit){
        this.courseName = courseName;
        this.prerequisite = prerequisite;
        this.time = time;
        this.date = date;
        this.unit = unit;
    }
    public Courses() {
        this.courseName = "N/A";
        this.prerequisite = "N/A";
        this.time = "N/A";
        this.date = 0;
        this.unit = 0;
    }
}
