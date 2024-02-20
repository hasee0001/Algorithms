package raven.entity;

import java.sql.Timestamp;

public class Interaction {
    private int id;
    private int userId; // Change to User type if you want to reference User directly
    private int contentId; // Change to Post type if you want to reference Post directly
    private InteractionType interactionType;
    private Timestamp interactionTime;

    // Constructors, getters, setters, and other methods
}
