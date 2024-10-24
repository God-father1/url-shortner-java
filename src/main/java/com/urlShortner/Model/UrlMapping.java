package com.urlShortner.Model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class UrlMapping {
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int accessCount;
    private List<LocalDateTime> accessTimes;

    public UrlMapping(String longUrl, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.accessCount = 0;
        this.accessTimes = new LinkedList<>();
    }

    public String getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public List<LocalDateTime> getAccessTimes() {
        return accessTimes;
    }

    public void incrementAccessCount() {
        accessCount++;
    }

    public void addAccessTime(LocalDateTime time) {
        accessTimes.add(time);
        if (accessTimes.size() > 10) {
            accessTimes.remove(0);
        }
    }
}
