package com.example.gradingsystem.datamodel;

public class Grade {
    private int score;

    public Grade(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score >= 0) {
            this.score = score;
        } else {
            throw new IllegalArgumentException("Result cannot be less than 0");
        }
    }

    @Override
    public String toString() {
        return "Score= " + score;
    }
}
