package com.pamir.ppfarmsbackend.herd.repository;

import com.pamir.ppfarmsbackend.herd.entity.Animal;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnimalSpecification {

    public static Specification<Animal> filterAnimals(UUID tenantId, UUID speciesId, UUID breedId, UUID shedPenId, String gender, String status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("organizationId"), tenantId));
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (speciesId != null) {
                predicates.add(criteriaBuilder.equal(root.get("species").get("id"), speciesId));
            }
            if (breedId != null) {
                predicates.add(criteriaBuilder.equal(root.get("breed").get("id"), breedId));
            }
            if (shedPenId != null) {
                predicates.add(criteriaBuilder.equal(root.get("shedPen").get("id"), shedPenId));
            }
            if (StringUtils.hasText(gender)) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender.toUpperCase()));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status.toUpperCase()));
            }
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate tagMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("tagNumber")), searchPattern);
                Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                predicates.add(criteriaBuilder.or(tagMatch, nameMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
