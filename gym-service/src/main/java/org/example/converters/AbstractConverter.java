package org.example.converters;

import lombok.RequiredArgsConstructor;
import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractConverter<E, D> {

    protected final ModelMapper modelMapper;

    public E toEntity(D dto) {
        return modelMapper.map(dto, getEntity());
    }

    public D toDto(E entity) {
        return modelMapper.map(entity, getDto());
    }

    public List<E> toEntityList(List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }

    public List<D> toDtoList(List<E> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    protected abstract Class<E> getEntity();
    protected abstract Class<D> getDto();

    public AuthDto toAuthDto(String username, String password) {
        return new AuthDto(username, password);
    }
    public CriteriaDto toCriteriaDto(LocalDate from, LocalDate to, String trainingType) {
        return new CriteriaDto(from, to, trainingType);
    }

    public AuthDto fromPasswordChangeDtoToAuthDto(PasswordChangeDto passwordChangeDto) {
        String username = passwordChangeDto.getUsername();
        String password = passwordChangeDto.getOldPassword();
        return new AuthDto(username, password);
    }
}
