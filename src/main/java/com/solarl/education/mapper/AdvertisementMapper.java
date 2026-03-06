package com.solarl.education.mapper;

import com.solarl.education.entity.Advertisement;
import com.solarl.education.entity.Client;
import com.solarl.education.request.AdvertisementRequest;
import com.solarl.education.response.AdvertisementResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "advertisementRequest.name")
    @Mapping(target = "client", source = "client")
    Advertisement toAdvertisement(AdvertisementRequest advertisementRequest, Client client);

    AdvertisementResponse toAdvertisementResponse(Advertisement advertisement);

    List<AdvertisementResponse> toListAdvertisementResponse(List<Advertisement> advertisements);

    @AfterMapping
    default void setDefaultValues(@MappingTarget Advertisement advertisement, AdvertisementRequest advertisementRequest) {
        if (advertisementRequest.getSubcategory() == null || advertisementRequest.getSubcategory().isEmpty()) {
            advertisement.setSubcategory("default");
        }
    }

}
