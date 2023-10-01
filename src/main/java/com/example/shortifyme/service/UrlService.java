package com.example.shortifyme.service;

import com.example.shortifyme.model.Url;
import com.example.shortifyme.repository.UrlRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final StringRedisTemplate redisTemplate;

    public UrlService(UrlRepository urlRepository, StringRedisTemplate redisTemplate) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
    }

    public ResponseEntity<String> shortenUrl(String url) {
        String shortUrl = redisTemplate.opsForValue().get(url);
        if (shortUrl != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Source", "Redis");
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(shortUrl);
        }
        Optional<Url> optionalOriginalUrl = urlRepository.findByOriginalUrl(url);
        if (optionalOriginalUrl.isPresent()) {
            Url url1 = optionalOriginalUrl.get();
            shortUrl = url1.getShortUrl();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Source", "MongoDB");
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(shortUrl);
        }
        String shortId = generateShortId(url);
        shortUrl = getShortUrlPrefix(url) + shortId;
        redisTemplate.opsForValue().set(shortUrl, url);
        Url newUrl = new Url(url, shortUrl);
        Url savedUrl = urlRepository.save(newUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Source", "Generated");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(savedUrl.getShortUrl());
    }



    private String getShortUrlPrefix(String originalUrl) {
        if (originalUrl.startsWith("http://")) {
            return "http://";
        } else if (originalUrl.startsWith("https://")) {
            return "https://";
        }
        throw new RuntimeException("Unsupported URL format: " + originalUrl);
    }

    private String generateShortId(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating shortId");
        }
    }
}
