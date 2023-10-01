package com.example.shortifyme.controller;

import com.example.shortifyme.model.Url;
import com.example.shortifyme.repository.UrlRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Before
    public void setUp() {
        urlRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void testCreateShortUrl() throws Exception {
        String originalUrl = "http://www.example.com";
        mockMvc.perform(post("/api/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalUrl))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Source"))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8"))
                .andExpect(content().string(notNullValue()));
        Optional<Url> savedUrl = urlRepository.findByOriginalUrl(originalUrl);
        assertThat(savedUrl).isPresent();
    }



    @Test
    public void testCreateDuplicateUrl() throws Exception {
        String originalUrl = "http://www.example.com";
        mockMvc.perform(post("/api/url")
                        .content(originalUrl)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Source"))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8"))
                .andExpect(content().string(notNullValue()));

    }

}
