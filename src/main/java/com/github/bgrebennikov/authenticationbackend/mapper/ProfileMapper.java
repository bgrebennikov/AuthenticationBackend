package com.github.bgrebennikov.authenticationbackend.mapper;

import com.github.bgrebennikov.authenticationbackend.data.dto.profile.ProfileResponse;
import com.github.bgrebennikov.authenticationbackend.data.dto.profile.UpdateProfileRequest;
import com.github.bgrebennikov.authenticationbackend.data.entity.Profile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileResponse toResponse(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateProfileFromDto(UpdateProfileRequest request, @MappingTarget Profile profile);
}