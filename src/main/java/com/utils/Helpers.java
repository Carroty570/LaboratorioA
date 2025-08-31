package com.utils;

import com.service.FeedbackService;

public class Helpers {

    public Helpers(){

    }

    public static String avgSuffix(String micId) {
        
        try {
            double avg = FeedbackService.averageStars(micId);
            return avg > 0 ? String.format("  (★%.1f)", avg) : "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String safe(String s) {
         
        return (s == null) ? "" : s.trim(); 
    }

    public static String truncate(String s, int maxLen) {

        if (s == null) return "";
        if (maxLen <= 3) return s.length() <= maxLen ? s : s.substring(0, maxLen);
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }

    public static Integer parseIntOrNull(String s) {

        try {
            return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean eq(String a, String b) {
        
        return safe(a).equalsIgnoreCase(safe(b));
    }

    
    
}
