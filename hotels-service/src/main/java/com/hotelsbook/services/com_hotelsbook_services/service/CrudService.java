package com.hotelsbook.services.com_hotelsbook_services.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz genérica CRUD para todos los servicios
 * @param <RequestDto>  DTO de entrada (create/update)
 * @param <ResponseDto> DTO de salida
 */
public interface CrudService<RequestDto, ResponseDto> {
    
    /**
     * Crea una nueva entidad
     * @param request DTO de petición
     * @return DTO de respuesta con la entidad creada
     */
    ResponseDto create(RequestDto request);
    
   /**
    * Devuelve todos los resultados paginados
    * @param pageable
    * @return ResonseDto
    */
    Page<ResponseDto> findAll(Pageable pageable);
    
    /**
     * Obtiene una entidad por ID
     * @param id Identificador de la entidad
     * @return DTO de respuesta
     */
    ResponseDto findById(Long id);
    
    /**
     * Actualiza una entidad existente
     * @param id      Identificador de la entidad
     * @param request DTO de petición con los nuevos datos
     * @return DTO de respuesta actualizado
     */
    ResponseDto update(Long id, RequestDto request);
    
    /**
     * Elimina una entidad por ID
     * @param id Identificador de la entidad
     */
    void deleteById(Long id);
}