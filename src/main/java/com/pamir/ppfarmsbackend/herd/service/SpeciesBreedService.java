package com.pamir.ppfarmsbackend.herd.service;

import com.pamir.ppfarmsbackend.herd.dto.BreedRequest;
import com.pamir.ppfarmsbackend.herd.dto.SpeciesRequest;
import com.pamir.ppfarmsbackend.herd.entity.Breed;
import com.pamir.ppfarmsbackend.herd.entity.Species;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface SpeciesBreedService {
    Species createSpecies(SpeciesRequest request, CustomUserDetails userDetails);
    List<Species> getAllSpecies(CustomUserDetails userDetails);
    Species updateSpecies(UUID id, SpeciesRequest request);
    void deleteSpecies(UUID id);

    Breed createBreed(BreedRequest request, CustomUserDetails userDetails);
    List<Breed> getBreeds(UUID speciesId, CustomUserDetails userDetails);
    Breed updateBreed(UUID id, BreedRequest request, CustomUserDetails userDetails);
    void deleteBreed(UUID id, CustomUserDetails userDetails);
}
