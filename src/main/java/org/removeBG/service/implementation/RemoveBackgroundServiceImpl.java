package org.removeBG.service.implementation;

import lombok.RequiredArgsConstructor;
import org.removeBG.api.ApiProvider;
import org.removeBG.service.RemoveBackgroundService;
import org.removeBG.utility.MultipartInputStreamFileResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RemoveBackgroundServiceImpl implements RemoveBackgroundService {

    private final RestTemplate restTemplate;
    private final ApiProvider apiProvider;

    @Override
    public byte[] removeBackground(MultipartFile file) {
        try {
            String API = apiProvider.APIS.get(ApiProvider.key.LOCAL_HOST_AI_API.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(API, HttpMethod.POST, requestEntity, byte[].class);

            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Flask API", e);
        }
    }
}
