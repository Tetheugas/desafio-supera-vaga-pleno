package com.empresa.acesso.service;

import com.empresa.acesso.dto.ModuloResponse;
import com.empresa.acesso.entity.Modulo;
import com.empresa.acesso.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuloService {
    
    private final ModuloRepository moduloRepository;
    
    @Transactional(readOnly = true)
    public List<ModuloResponse> listarModulosDisponiveis() {
        return moduloRepository.findByAtivoTrue().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    private ModuloResponse toResponse(Modulo modulo) {
        return ModuloResponse.builder()
            .id(modulo.getId())
            .nome(modulo.getNome())
            .descricao(modulo.getDescricao())
            .ativo(modulo.getAtivo())
            .departamentosPermitidos(modulo.getDepartamentosPermitidos())
            .modulosIncompativeis(modulo.getModulosIncompativeis())
            .build();
    }
}
