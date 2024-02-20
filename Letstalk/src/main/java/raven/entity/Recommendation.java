package raven.entity;

import java.sql.Timestamp;

public class Recommendation {
    private int id;
    private int userId; // Change to User type if you want to reference User directly
    private int recommendedContentId; // Change to Post type if you want to reference Post directly
    private Timestamp recommendationTime;

    // Constructors, getters, setters, and other methods
}

