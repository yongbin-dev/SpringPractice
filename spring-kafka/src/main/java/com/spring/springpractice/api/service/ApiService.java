package com.spring.springpractice.api.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spring.springpractice.conf.exception.BizException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;


@Service
public class ApiService {

    private ResponseEntity<String> callRestAPI(UriComponents uriBuilder) throws BizException {
        ResponseEntity<String>  entity =  null;
        try {

            entity = new RestTemplate().exchange(uriBuilder.toUriString(), HttpMethod.GET, null , String.class);

            String str = entity.getBody()  ;
            JsonElement json = JsonParser.parseString(str);
            entity = ResponseEntity.status(HttpStatus.OK).body(json.toString());

        }catch(Exception e){
            throw new BizException("Error");
        }

        return entity;
    }


    public ResponseEntity<String> callTourListAPI()  throws BizException{

        final String API_URL = "https://apis.data.go.kr/B551011/PhotoGalleryService1/galleryList1";
        final String ACCESS_TOKEN = "RiRhW5gyS8VRPtTKn0oYUJwUF3JFl3rkxKBDj1XiVMzmthbnyxqLKvdedcAhXYFPtT61VQxtmRkyGcmDdaEJIw%3D%3D";

        UriComponents UriComponents = UriComponentsBuilder
                .fromUriString(API_URL)
                .queryParam("serviceKey", ACCESS_TOKEN)
                .queryParam("numOfRows", 50)
                .queryParam("pageNo", 1 )
                .queryParam("MobileOS", "ETC" )
                .queryParam("MobileApp", "AppTest" )
                .queryParam("arrange", "A" )
                .queryParam("_type",  "json")
                .encode()
                .build();

        return this.callRestAPI(UriComponents);
    }

}
