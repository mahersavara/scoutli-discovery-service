package com.scoutli.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeocodingService {
    public double[] getCoordinates(String address) {
        // Mock implementation
        // In real app, call OpenStreetMap or Google Maps API
        return new double[] { 21.0285, 105.8542 }; // Hanoi coordinates
    }
}
