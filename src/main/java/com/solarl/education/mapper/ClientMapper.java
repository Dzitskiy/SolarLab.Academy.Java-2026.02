package com.solarl.education.mapper;

import com.solarl.education.entity.Client;
import com.solarl.education.response.ClientView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientView toClientView(Client client);
}
