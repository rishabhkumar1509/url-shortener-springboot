package dev.rishabhsajwal.urlShortener.service;

import dev.rishabhsajwal.urlShortener.model.Url;
import dev.rishabhsajwal.urlShortener.model.UrlDto;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public interface UrlService {
    public Url generateShortLink(UrlDto urlDto);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public void deleteShortLink(Url url);
}
