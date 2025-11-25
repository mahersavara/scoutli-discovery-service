package com.scoutli.service;

import com.scoutli.api.dto.DiscoveryDTO;
import com.scoutli.domain.entity.Discovery;
import com.scoutli.domain.entity.Tag;
import com.scoutli.domain.repository.DiscoveryRepository;
import com.scoutli.domain.repository.TagRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class DiscoveryService {

    @Inject
    DiscoveryRepository discoveryRepository;

    @Inject
    TagRepository tagRepository;

    @Inject
    GeocodingService geocodingService;

    public List<DiscoveryDTO> getAllDiscoveries() {
        log.debug("Fetching all discoveries");
        return discoveryRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiscoveryDTO createDiscovery(DiscoveryDTO.CreateRequest request, String userEmail) {
        log.info("Creating discovery: {} for user: {}", request.name, userEmail);

        Discovery discovery = new Discovery();
        discovery.setName(request.name);
        discovery.setDescription(request.description);
        discovery.setStreetAddress(request.streetAddress);
        discovery.setCity(request.city);
        discovery.setCountry(request.country);
        discovery.setUserEmail(userEmail);

        // Geocoding
        String fullAddress = request.streetAddress + ", " + request.city + ", " + request.country;
        double[] coords = geocodingService.getCoordinates(fullAddress);
        discovery.setLatitude(coords[0]);
        discovery.setLongitude(coords[1]);

        // Tags
        if (request.tags != null) {
            for (String tagName : request.tags) {
                Tag tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tagRepository.persist(tag);
                }
                discovery.getTags().add(tag);
            }
        }

        discoveryRepository.persist(discovery);
        log.info("Discovery created with ID: {}", discovery.getId());
        return toDTO(discovery);
    }

    private DiscoveryDTO toDTO(Discovery discovery) {
        DiscoveryDTO dto = new DiscoveryDTO();
        dto.id = discovery.getId();
        dto.name = discovery.getName();
        dto.description = discovery.getDescription();
        dto.streetAddress = discovery.getStreetAddress();
        dto.city = discovery.getCity();
        dto.country = discovery.getCountry();
        dto.latitude = discovery.getLatitude();
        dto.longitude = discovery.getLongitude();
        dto.userEmail = discovery.getUserEmail();
        dto.tags = discovery.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        return dto;
    }
}
