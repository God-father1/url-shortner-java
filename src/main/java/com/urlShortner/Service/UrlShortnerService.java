package com.urlShortner.Service;

import com.urlShortner.Model.UrlMapping;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class UrlShortnerService {
    private final Map<String, UrlMapping> urlMappings = new ConcurrentHashMap<>();

    public String shortenUrl(String longUrl, String customAlias, int ttlSeconds) {
        if(customAlias!=null && urlMappings.containsKey(customAlias)){
            return " already in use";
        }
        String alias = customAlias != null ? customAlias : generateUniqueAlias();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusSeconds(ttlSeconds);

        urlMappings.put(alias, new UrlMapping(longUrl, createdAt, expiresAt));
        return alias;
    }

    public UrlMapping getUrlMapping(String alias) {
        UrlMapping urlMapping = urlMappings.get(alias);
        if (urlMapping != null && urlMapping.getExpiresAt().isAfter(LocalDateTime.now())) {
            return urlMapping;
        }
        // Return null if not found or expired
        return null;
    }

    public boolean deleteUrlMapping(String alias) {
        return urlMappings.remove(alias) != null;
    }

    public boolean updateUrlMapping(String alias, String newAlias, int ttlSeconds) {
        UrlMapping urlMapping = urlMappings.remove(alias);
        if (urlMapping != null) {
            String updatedAlias = newAlias != null ? newAlias : alias;
            LocalDateTime newExpiresAt = LocalDateTime.now().plusSeconds(ttlSeconds);
            urlMapping.setExpiresAt(newExpiresAt);
            urlMappings.put(updatedAlias, urlMapping);
            return true;
        }
        return false;
    }

    private String generateUniqueAlias() {
        return UUID.randomUUID().toString().substring(0, 6); // Generate 6-character alias
    }

    public boolean checkAlias(String alias) {
        return urlMappings.containsKey(alias);
    }
}
