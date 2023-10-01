package com.example.shortifyme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "urls")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    @Id
    private String id;
    private String originalUrl;
    private String shortUrl;

    public Url(String originalUrl, String shortUrl) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
    }

}
