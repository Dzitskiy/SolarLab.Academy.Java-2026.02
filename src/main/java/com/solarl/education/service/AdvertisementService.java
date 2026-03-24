package com.solarl.education.service;

import com.solarl.education.entity.Advertisement;
import com.solarl.education.entity.Client;
import com.solarl.education.enums.CategoryEnum;
import com.solarl.education.mapper.AdvertisementMapper;
import com.solarl.education.repository.AdvertisementRepository;
import com.solarl.education.repository.ClientRepository;
import com.solarl.education.request.AdvertisementRequest;
import com.solarl.education.response.AdvertisementResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = "adv")
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final ClientRepository clientRepository;
    private final AdvertisementMapper advertisementMapper;

    private final CacheManager cacheManager;

    @PostConstruct
    public void init() {
        System.out.println("Создали бин AdvertisementService");
    }

    @Cacheable()
    public AdvertisementResponse createAdvertisement(AdvertisementRequest advertisementRequest) {
        System.out.println("Создаем объявление: " + advertisementRequest);
        if (advertisementRequest == null) {
            return null;
        }
        Client client = null;
        if (advertisementRequest.getClientId() != null) {
            Optional<Client> optClient = clientRepository.findById(advertisementRequest.getClientId());
            client = optClient.orElse(null);
        }

        return advertisementMapper.toAdvertisementResponse(advertisementRepository.save(
                advertisementMapper.toAdvertisementWithClient(advertisementRequest, client)
        ));
    }

    @Cacheable(key = "#id", condition = "#id != 6", unless = "#result == null")
    public AdvertisementResponse getAdvertisementById(Long id) {
        System.out.println("Получаем объявление по id: " + id);
        Optional<Advertisement> advertisementOptional = advertisementRepository.findById(id);
        if (advertisementOptional.isEmpty()) {
            return null;
        }
        Advertisement advertisement = advertisementOptional.get();
        return advertisementMapper.toAdvertisementResponse(advertisement);
    }

    @CachePut(key = "#id")
    public AdvertisementResponse updateAdvertisementById(Long id, AdvertisementRequest advertisementRequest) {
        System.out.println("Изменение объявления с id: " + id);

        return advertisementMapper.toAdvertisementResponse(
                advertisementRepository.save(advertisementMapper.toAdvertisement(advertisementRequest)));
    }

    @Cacheable(key = "#category + '_' + #cost", unless = "#result.size > 1000")
    public List<AdvertisementResponse> getAdvertisementsByCategoryAndCost(CategoryEnum category, Integer cost) {
        log.info("Получение объявлений из БД по цене больше чем {} и категории {} ", cost, category);

        List<Advertisement> advertisements = advertisementRepository
                .findByCategoryAndCostGreaterThanEqual(category, cost);

        if(advertisements.isEmpty()) {
            log.error("Нет объявлений, удовлетворяющих указанные условия");
            return Collections.emptyList();
        }

        return advertisementMapper.toListAdvertisementResponse(advertisements);
    }

    @Caching(
            cacheable = {
                    @Cacheable(key = "#id"),
                    @Cacheable(value = "basket", key = "#id")
            }
    )
    public AdvertisementResponse addAdvertisementToBasket(Long id) {
        log.info("Добавление объявления в корзину: " + id);
        Optional<Advertisement> advertisement = advertisementRepository.findById(id);
        if(advertisement.isEmpty()) {
            log.error("Объявления не существует: " + id);
            return null;
        }

        log.info("Кладём в корзину");
        return advertisementMapper.toAdvertisementResponse(advertisement.get());
    }

    @Caching(
            cacheable = @Cacheable(keyGenerator = "customKeyGenerator"),
            evict = {
                 @CacheEvict(allEntries = true),
                 @CacheEvict(value = "basket", allEntries = true)
            }
    )
    public List<AdvertisementResponse> payPurchasesFromBasket(List<Long> ids) {
        log.info("Оплата объявлений из корзины: " + ids.size());
        List<Advertisement> advertisements = advertisementRepository.findByIdIn(ids);
        if(advertisements.isEmpty()) {
            log.error("Объявления не найдены");
            return null;
        }
        log.info("Оплачиваем объявления из корзины, удаляем объявления из списка");
        advertisementRepository.deleteAll(advertisements);
        return advertisementMapper.toListAdvertisementResponse(advertisements);
    }

    @CacheEvict(value = "adv", key = "#id")
    public void deleteAdvertisementById(Long id) {
        System.out.println("Удаляем объявление с id: " + id);

        advertisementRepository.deleteById(id);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Удаляем бин AdvertisementService");
    }
}
