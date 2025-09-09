package com.example.gradingsystem.dtos;

public record TaskStatistics(
        boolean hasResults,
        int maxScore,
        int minScore,
        String bestStudent,
        String worstStudent,
        double mean,
        double stdDev
) {}