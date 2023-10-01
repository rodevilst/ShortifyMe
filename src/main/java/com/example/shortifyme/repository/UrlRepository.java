package com.example.shortifyme.repository;

import com.example.shortifyme.model.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
    Optional<Url> findByOriginalUrl(String originalUrl);

}
