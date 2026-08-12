package com.pamir.ppfarmsbackend.herd.service.impl;

import com.pamir.ppfarmsbackend.herd.dto.BreedRequest;
import com.pamir.ppfarmsbackend.herd.dto.SpeciesRequest;
import com.pamir.ppfarmsbackend.herd.entity.Breed;
import com.pamir.ppfarmsbackend.herd.entity.Species;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.herd.repository.BreedRepository;
import com.pamir.ppfarmsbackend.herd.repository.SpeciesRepository;
import com.pamir.ppfarmsbackend.herd.service.SpeciesBreedService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeciesBreedServiceImpl implements SpeciesBreedService {

    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final AnimalRepository animalRepository;

    @Override
    @Transactional
    public Species createSpecies(SpeciesRequest request, CustomUserDetails userDetails) {
        log.info("[SPECIES CREATE] Admin {} creating species {}", userDetails.getUsername(), request.getName());
        Species species = Species.builder()
                .name(request.getName().trim())
                .gestationDays(request.getGestationDays() != null ? request.getGestationDays() : 150)
                .heatCycleDays(21)
                .build();
        return speciesRepository.save(species);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Species> getAllSpecies() {
        return speciesRepository.findAll();
    }

    @Override
    @Transactional
    public Species updateSpecies(UUID id, SpeciesRequest request) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Species not found with id " + id));

        species.setName(request.getName().trim());
        if (request.getGestationDays() != null) {
            species.setGestationDays(request.getGestationDays());
        }
        return speciesRepository.save(species);
    }

    @Override
    @Transactional
    public void deleteSpecies(UUID id) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Species not found with id " + id));

        if (animalRepository.existsBySpeciesIdAndDeletedAtIsNull(id)) {
            throw new BadRequestException("Cannot delete species '" + species.getName() + "'. Active animals are currently linked to this species.");
        }

        if (breedRepository.existsBySpeciesId(id)) {
            throw new BadRequestException("Cannot delete species '" + species.getName() + "'. Registered breeds are linked to this species. Delete or reassign the breeds first.");
        }

        speciesRepository.delete(species);
        log.info("[SPECIES DELETE] Purged species {} (ID: {})", species.getName(), id);
    }

    @Override
    @Transactional
    public Breed createBreed(BreedRequest request, CustomUserDetails userDetails) {
        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Species not found with id " + request.getSpeciesId()));

        Breed breed = Breed.builder()
                .organizationId(userDetails.getTenantId())
                .species(species)
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
        return breedRepository.save(breed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Breed> getBreeds(UUID speciesId, CustomUserDetails userDetails) {
        UUID tenantId = userDetails != null ? userDetails.getTenantId() : null;
        return breedRepository.findBySpeciesAndOrganization(speciesId, tenantId);
    }

    @Override
    @Transactional
    public Breed updateBreed(UUID id, BreedRequest request, CustomUserDetails userDetails) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found with id " + id));

        boolean isSuperAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!isSuperAdmin && breed.getOrganizationId() != null && !breed.getOrganizationId().equals(userDetails.getTenantId())) {
            throw new BadRequestException("Access denied: You can only modify breeds registered by your farm.");
        }

        if (request.getSpeciesId() != null) {
            Species species = speciesRepository.findById(request.getSpeciesId())
                    .orElseThrow(() -> new ResourceNotFoundException("Species not found with id " + request.getSpeciesId()));
            breed.setSpecies(species);
        }

        breed.setName(request.getName().trim());
        breed.setDescription(request.getDescription());
        return breedRepository.save(breed);
    }

    @Override
    @Transactional
    public void deleteBreed(UUID id, CustomUserDetails userDetails) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found with id " + id));

        boolean isSuperAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!isSuperAdmin && breed.getOrganizationId() != null && !breed.getOrganizationId().equals(userDetails.getTenantId())) {
            throw new BadRequestException("Access denied: You can only delete breeds registered by your farm.");
        }

        if (animalRepository.existsByBreedIdAndDeletedAtIsNull(id)) {
            throw new BadRequestException("Cannot delete breed '" + breed.getName() + "'. Active animals in your herd are assigned to this breed.");
        }

        breedRepository.delete(breed);
        log.info("[BREED DELETE] Deleted breed {} (ID: {}) for Tenant: {}", breed.getName(), id, userDetails.getTenantId());
    }
}
