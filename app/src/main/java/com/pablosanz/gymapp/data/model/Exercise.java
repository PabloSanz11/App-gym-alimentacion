package com.pablosanz.gymapp.data.model;

public class Exercise {
    private String name;
    private int dayType;
    private int setsTarget;
    private String repsTarget;

    public Exercise(String name, int dayType, int setsTarget, String repsTarget) {
        this.name = name;
        this.dayType = dayType;
        this.setsTarget = setsTarget;
        this.repsTarget = repsTarget;
    }

    public String getName() { return name; }
    public int getDayType() { return dayType; }
    public int getSetsTarget() { return setsTarget; }
    public String getRepsTarget() { return repsTarget; }
}
