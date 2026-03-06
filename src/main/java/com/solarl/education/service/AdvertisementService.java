package com.solarl.education.service;

import com.solarl.education.entity.Advertisement;
import com.solarl.education.entity.Client;
import com.solarl.education.mapper.AdvertisementMapper;
import com.solarl.education.repository.AdvertisementRepository;
import com.solarl.education.repository.ClientRepository;
import com.solarl.education.request.AdvertisementRequest;
import com.solarl.education.response.AdvertisementResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final ClientRepository clientRepository;
    private final AdvertisementMapper advertisementMapper;

    @PostConstruct
    public void init() {
        System.out.println("Создали бин AdvertisementService");
    }

    public void createAdvertisement(AdvertisementRequest advertisementRequest) {
        System.out.println("Создаем объявление: " + advertisementRequest);
        if (advertisementRequest == null) {
            return;
        }
        Client client = null;
        if (advertisementRequest.getClientId() != null) {
            Optional<Client> optClient = clientRepository.findById(advertisementRequest.getClientId());
            client = optClient.orElse(null);
        }

        advertisementRepository.save(
                advertisementMapper.toAdvertisement(advertisementRequest, client)
        );
    }

    public AdvertisementResponse getAdvertisementById(Long id) {
        System.out.println("Получаем объявление по id: " + id);
        Optional<Advertisement> advertisementOptional = advertisementRepository.findById(id);
        if (advertisementOptional.isEmpty()) {
            return null;
        }
        Advertisement advertisement = advertisementOptional.get();
        return advertisementMapper.toAdvertisementResponse(advertisement);
    }

    public AdvertisementResponse updateAdvertisementById(Long id, AdvertisementRequest advertisementRequest) {
        System.out.println("Изменение объявления с id: " + id);
        return AdvertisementResponse.builder()
                .name(advertisementRequest.getName())
                .description(advertisementRequest.getDescription())
                .category(advertisementRequest.getCategory().name())
                .address(advertisementRequest.getAddress())
                .subcategory(advertisementRequest.getSubcategory())
                .build();
    }

    public void deleteAdvertisementById(Long id) {
        System.out.println("Удаляем объявление с id: " + id);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Удаляем бин AdvertisementService");
    }
}
