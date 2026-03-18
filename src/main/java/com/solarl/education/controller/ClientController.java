package com.solarl.education.controller;

import com.solarl.education.request.ClientRequest;
import com.solarl.education.response.ClientView;
import com.solarl.education.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("v1/clients")
@Tag(name = "Сервис клиентов", description = "API доступа к клиентам")
@Validated
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @PostMapping()
    //@PreAuthorize("hasRole('create_entity')")
    @Operation(summary = "Создание нового клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успех"),
            @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ClientView createClient(
            @Parameter(description = "Запрос на отправку уведомления")
            @RequestBody @Valid ClientRequest clientRequest) {
        log.info("createClient security context: {}", SecurityContextHolder.getContext());
        return clientService.createClient(clientRequest);
    }

    @GetMapping("{id}")
    //@PreAuthorize("hasRole('read_entity')")
    @Operation(summary = "Получение клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех"),
            @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ClientView getClient(
            @Parameter(description = "Идентификатор клиента")
            @PathVariable @PositiveOrZero Long id) {
        log.info("getClient security context: {}", SecurityContextHolder.getContext());
        return clientService.getClient(id);
    }

}
