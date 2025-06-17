package org.removeBG.api;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.removeBG.entity.ConfigApiEntity;
import org.removeBG.repository.ConfigApiRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiProvider {

    private final ConfigApiRepository configApiRepository;

    public enum key {
        LOCAL_HOST_AI_API;
    }

    public Map<String, String> APIS;

    @PostConstruct
    public void init() {
        APIS = new HashMap<>();

        List<ConfigApiEntity> all = configApiRepository.findAll();

        for (ConfigApiEntity apiEntity : all) {
            APIS.put(apiEntity.getKey(), apiEntity.getValue());
        }
    }
}
