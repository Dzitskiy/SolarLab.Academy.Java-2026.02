package com.solarl.education.service;

import com.solarl.education.entity.Client;
import com.solarl.education.mapper.ClientMapper;
import com.solarl.education.repository.ClientRepository;
import com.solarl.education.request.ClientRequest;
import com.solarl.education.response.ClientView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientView createClient(ClientRequest clientRequest) {
        System.out.println("Создание клиента: " + clientRequest);
        Client client = clientRepository.save(Client.builder()
                .name(clientRequest.getName())
                .email(clientRequest.getEmail())
                .build());
        return clientMapper.toClientView(client);
    }

    public ClientView getClient(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toClientView)
                .orElse(null);
    }

}
