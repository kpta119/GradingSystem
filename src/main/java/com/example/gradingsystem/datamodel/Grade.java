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
            throw new IllegalArgumentException("Wynik musi być wiekszy od zera");
        }
    }

}
