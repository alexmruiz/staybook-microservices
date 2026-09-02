package com.hotelsbook.services.com_hotelsbook_services.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.service.CityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cities")
@Tag(name = "Ciudades", description = "Endpoints para la gestión del catálogo de ciudades")
public class CityController {
    
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una ciudad", description = "Registra una nueva ciudad en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ciudad creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos")
    })
    public CityResponseDto create(@Valid @RequestBody CityRequestDto cityRequestDto) {
        return cityService.create(cityRequestDto);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las ciudades", description = "Devuelve una lista con todas las ciudades registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de ciudades obtenida con éxito")
    public ResponseEntity<List<CityResponseDto>> findAll() {
        return ResponseEntity.ok(cityService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ciudad por ID", description = "Busca los detalles de una ciudad por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciudad encontrada"),
        @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public ResponseEntity<CityResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una ciudad", description = "Actualiza los datos de una ciudad existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciudad actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de petición no válidos"),
        @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public ResponseEntity<CityResponseDto> update(
            @PathVariable Long id, 
            @Valid @RequestBody CityRequestDto request) {
        return ResponseEntity.ok(cityService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una ciudad", description = "Elimina una ciudad del sistema a partir de su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ciudad eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public void delete(@PathVariable Long id) {
        cityService.deleteById(id);
    }
}
