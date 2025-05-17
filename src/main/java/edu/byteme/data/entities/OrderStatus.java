package edu.byteme.data.entities;

/*
 * OrderStatus — enum for tracking order progress
 * used by the Order entity to indicate its current state
 * should this be a repository?
 */

public enum OrderStatus {
    NEW,
    IN_PROGRESS,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}
