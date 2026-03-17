package com.solarl.education.service;

import com.solarl.education.entity.Advertisement;
import com.solarl.education.entity.Client;
import com.solarl.education.enums.CategoryEnum;
import com.solarl.education.mapper.AdvertisementMapper;
import com.solarl.education.repository.AdvertisementRepository;
import com.solarl.education.repository.ClientRepository;
import com.solarl.education.request.AdvertisementRequest;
import com.solarl.education.response.AdvertisementResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @InjectMocks
    private AdvertisementService advertisementService;

    @Test
    void createAdvertisementShouldDoNothingWhenRequestIsNull() {
        advertisementService.createAdvertisement(null);

        verify(clientRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(advertisementMapper, never()).toAdvertisement(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull());
        verify(advertisementRepository, never()).save(org.mockito.ArgumentMatchers.any(Advertisement.class));
    }

    @Test
    void createAdvertisementShouldSaveAdvertisementWithoutClientLookupWhenClientIdIsNull() {
        AdvertisementRequest request = createRequest();
        request.setClientId(null);
        Advertisement advertisement = new Advertisement();

        when(advertisementMapper.toAdvertisement(request, null)).thenReturn(advertisement);

        advertisementService.createAdvertisement(request);

        verify(clientRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(advertisementMapper).toAdvertisement(request, null);
        verify(advertisementRepository).save(advertisement);
    }

    @Test
    void createAdvertisementShouldUseFoundClientWhenClientExists() {
        AdvertisementRequest request = createRequest();
        Client client = Client.builder().id(15L).name("Alex").build();
        Advertisement advertisement = new Advertisement();

        when(clientRepository.findById(15L)).thenReturn(Optional.of(client));
        when(advertisementMapper.toAdvertisement(request, client)).thenReturn(advertisement);

        advertisementService.createAdvertisement(request);

        verify(clientRepository).findById(15L);
        verify(advertisementMapper).toAdvertisement(request, client);
        verify(advertisementRepository).save(advertisement);
    }

    @Test
    void createAdvertisementShouldSaveWithNullClientWhenClientIsNotFound() {
        AdvertisementRequest request = createRequest();
        Advertisement advertisement = new Advertisement();

        when(clientRepository.findById(15L)).thenReturn(Optional.empty());
        when(advertisementMapper.toAdvertisement(request, null)).thenReturn(advertisement);

        advertisementService.createAdvertisement(request);

        verify(clientRepository).findById(15L);
        verify(advertisementMapper).toAdvertisement(request, null);
        verify(advertisementRepository).save(advertisement);
    }

    @Test
    void getAdvertisementByIdShouldReturnNullWhenAdvertisementDoesNotExist() {
        when(advertisementRepository.findById(77L)).thenReturn(Optional.empty());

        AdvertisementResponse actual = advertisementService.getAdvertisementById(77L);

        assertThat(actual).isNull();
        verify(advertisementRepository).findById(77L);
        verify(advertisementMapper, never()).toAdvertisementResponse(org.mockito.ArgumentMatchers.any(Advertisement.class));
    }

    @Test
    void getAdvertisementByIdShouldReturnMappedResponseWhenAdvertisementExists() {
        Advertisement advertisement = new Advertisement();
        advertisement.setId(7L);
        AdvertisementResponse response = AdvertisementResponse.builder().id(7L).name("Phone").build();

        when(advertisementRepository.findById(7L)).thenReturn(Optional.of(advertisement));
        when(advertisementMapper.toAdvertisementResponse(advertisement)).thenReturn(response);

        AdvertisementResponse actual = advertisementService.getAdvertisementById(7L);

        assertThat(actual).isSameAs(response);
        verify(advertisementRepository).findById(7L);
        verify(advertisementMapper).toAdvertisementResponse(advertisement);
    }

    @Test
    void updateAdvertisementByIdShouldBuildResponseFromRequest() {
        AdvertisementRequest request = createRequest();

        AdvertisementResponse actual = advertisementService.updateAdvertisementById(100L, request);

        assertThat(actual.getName()).isEqualTo(request.getName());
        assertThat(actual.getDescription()).isEqualTo(request.getDescription());
        assertThat(actual.getCategory()).isEqualTo(request.getCategory().name());
        assertThat(actual.getAddress()).isEqualTo(request.getAddress());
        assertThat(actual.getSubcategory()).isEqualTo(request.getSubcategory());
        assertThat(actual.getId()).isNull();
        assertThat(actual.getCost()).isNull();
        assertThat(actual.getCreateDateTime()).isNull();
    }

    private AdvertisementRequest createRequest() {
        AdvertisementRequest request = new AdvertisementRequest();
        request.setName("Phone");
        request.setCategory(CategoryEnum.ELECTRONICS);
        request.setSubcategory("Smartphones");
        request.setCost(1000);
        request.setAddress("Bangkok");
        request.setClientId(15L);
        request.setDescription("Selling phone");
        request.setCreateDateTime(LocalDateTime.now());
        return request;
    }
}
