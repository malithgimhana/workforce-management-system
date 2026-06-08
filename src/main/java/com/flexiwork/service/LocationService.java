package com.flexiwork.service;

import com.flexiwork.model.Job;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public boolean isWithinRadius(Job job, double targetLat, double targetLon, double radiusKm) {
        if (job.getLatitude() == null || job.getLongitude() == null) {
            return false;
        }
        double distance = haversineDistance(job.getLatitude(), job.getLongitude(), targetLat, targetLon);
        return distance <= radiusKm;
    }
}
