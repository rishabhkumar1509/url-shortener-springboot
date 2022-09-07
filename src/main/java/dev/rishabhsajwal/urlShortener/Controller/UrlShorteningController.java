package dev.rishabhsajwal.urlShortener.Controller;

import dev.rishabhsajwal.urlShortener.model.Url;
import dev.rishabhsajwal.urlShortener.model.UrlDto;
import dev.rishabhsajwal.urlShortener.model.UrlErrorResponseDto;
import dev.rishabhsajwal.urlShortener.model.UrlResponseDto;
import dev.rishabhsajwal.urlShortener.repository.UrlRepository;
import dev.rishabhsajwal.urlShortener.service.UrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@CrossOrigin(origins = "https://url-shortener-frontend-react.herokuapp.com/")
@RestController
public class UrlShorteningController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto)
    {
        Url urlToRet = urlService.generateShortLink(urlDto);

        if(urlToRet != null)
        {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortLink(urlToRet.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
        }

        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("There was an error processing your request. Try again later!");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink,  HttpServletResponse response) throws  IOException
    {
        if(StringUtils.isEmpty(shortLink))
        {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid URL!");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }
        Url urlToRet = urlService.getEncodedUrl(shortLink);
        if(urlToRet == null)
        {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL does not exist or it might have expired!");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }
        if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            urlService.deleteShortLink(urlToRet);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL expired. Please try generating a fresh one!");
            urlErrorResponseDto.setStatus("200");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }
        response.sendRedirect(urlToRet.getOriginalUrl());
        return null;
    }
}
