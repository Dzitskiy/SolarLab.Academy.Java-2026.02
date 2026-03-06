package com.solarl.education.service;

import com.solarl.education.entity.Client;
import com.solarl.education.repository.ClientRepository;
import com.solarl.education.request.ClientRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public void createClient(ClientRequest clientRequest) {
        System.out.println("Создание клиента: " + clientRequest);
        clientRepository.save(Client.builder()
                .name(clientRequest.getName())
                .email(clientRequest.getEmail())
                .build());
    }

    public void getClient(Long id) {
        clientRepository.findById(id);
    }

}
