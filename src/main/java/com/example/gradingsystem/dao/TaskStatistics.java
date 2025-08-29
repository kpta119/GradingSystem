package com.example.gradingsystem.dao;

public record TaskStatistics(
        boolean hasResults,
        int maxScore,
        int minScore,
        String bestStudent,
        String worstStudent,
        double mean,
        double stdDev
) {}