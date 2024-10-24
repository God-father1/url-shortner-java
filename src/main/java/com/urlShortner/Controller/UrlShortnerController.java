package com.urlShortner.Controller;

import com.urlShortner.Model.UrlMapping;
import com.urlShortner.Service.UrlShortnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class UrlShortnerController {
    private final UrlShortnerService urlShortenerService;

    public UrlShortnerController(UrlShortnerService urlShortnerService) {
        this.urlShortenerService = urlShortnerService;
    }


    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("long_url");
        String customAlias = request.get("custom_alias");
        int ttlSeconds = request.get("ttl_seconds") != null ? Integer.parseInt(request.get("ttl_seconds")) : 120;

        String alias = urlShortenerService.shortenUrl(longUrl, customAlias, ttlSeconds);
        Map<String, String> response = new HashMap<>();
        if(alias==" already in use"){
            response.put("short_url", "This alias is"+ alias);
        }
        else{
            response.put("short_url", "http://localhost:8081/api/" + alias);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String alias) {
        UrlMapping urlMapping = urlShortenerService.getUrlMapping(alias);
        if (urlMapping != null) {
            urlMapping.incrementAccessCount();
            urlMapping.addAccessTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header("Location", urlMapping.getLongUrl())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/analytics/{alias}")
    public ResponseEntity<Map<String, Object>> getUrlAnalytics(@PathVariable String alias) {
        UrlMapping urlMapping = urlShortenerService.getUrlMapping(alias);
        if (urlMapping != null) {
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("alias", alias);
            analytics.put("long_url", urlMapping.getLongUrl());
            analytics.put("access_count", urlMapping.getAccessCount());
            analytics.put("access_times", urlMapping.getAccessTimes());
            return ResponseEntity.ok(analytics);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/update/{alias}")
    public ResponseEntity<String> updateUrl(@PathVariable String alias, @RequestBody Map<String, String> request) {
        if(!urlShortenerService.checkAlias(alias)){
            return new ResponseEntity<>("Invalid request.",HttpStatus.BAD_REQUEST);
        }
        String newAlias = request.get("custom_alias");
        int ttlSeconds = request.get("ttl_seconds") != null ? Integer.parseInt(request.get("ttl_seconds")) : 120;

        boolean updated = urlShortenerService.updateUrlMapping(alias, newAlias, ttlSeconds);
        if (updated) {
            return new ResponseEntity<>("Successfully updated.",HttpStatus.OK);
        }
        return new ResponseEntity<>("Alias  does not exist or has expired.",HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/delete/{alias}")
    public ResponseEntity<String> deleteUrl(@PathVariable String alias) {
        boolean deleted = urlShortenerService.deleteUrlMapping(alias);
        if (deleted) {
            return new ResponseEntity<>("Successfully deleted.",HttpStatus.OK);
        }
        return new ResponseEntity<>("Alias does not exist or has expired.\n",HttpStatus.NOT_FOUND);
    }

}
