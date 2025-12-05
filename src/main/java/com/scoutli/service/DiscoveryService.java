package com.scoutli.service;

import com.scoutli.api.dto.DiscoveryDTO;
import com.scoutli.api.dto.UserDTO;
import com.scoutli.client.AuthServiceRestClient;
import com.scoutli.domain.entity.Discovery;
import com.scoutli.domain.entity.Tag;
import com.scoutli.domain.repository.DiscoveryRepository;
import com.scoutli.domain.repository.TagRepository;
import com.scoutli.opensearch.model.DiscoveryDocument;
import com.scoutli.opensearch.service.DiscoveryIndexerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;
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

    @Inject
    @RestClient // Inject the REST Client
    AuthServiceRestClient authServiceRestClient;

    @Inject
    DiscoveryIndexerService discoveryIndexerService; // Inject OpenSearch indexer

    public List<DiscoveryDTO> getAllDiscoveries() {
        log.debug("Fetching all discoveries");
        return discoveryRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiscoveryDTO createDiscovery(DiscoveryDTO.CreateRequest request, String userEmail) {
        log.info("Creating discovery: {} for user: {}", request.name, userEmail);

        // Example of inter-service communication: Fetching User details from Auth
        // Service
        // Using Uni<T> and blocking for simplicity in a @Transactional method
        Optional<UserDTO> userDetailsOptional = authServiceRestClient.getMyUserDetails()
                .onFailure()
                .invoke(failure -> log.warn("Could not fetch user details from Auth Service: {}", failure.getMessage()))
                .onItem().transform(Optional::of)
                .await().indefinitely(); // Block until Uni emits an item or fails

        if (userDetailsOptional.isPresent()) {
            UserDTO userDetails = userDetailsOptional.get();
            log.info("Fetched user details from Auth Service: {}", userDetails);
            // You can now use userDetails.id, userDetails.role etc.
        } else {
            log.warn(
                    "Proceeding with discovery creation using only userEmail due to failure to fetch user details from Auth Service.");
        }

        Discovery discovery = new Discovery();
        discovery.setName(request.name);
        discovery.setDescription(request.description);
        discovery.setStreetAddress(request.streetAddress);
        discovery.setCity(request.country); // Typo fixed: city to city
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

    public List<DiscoveryDocument> searchDiscoveries(String query) {
        log.info("Searching discoveries for query: {}", query);
        return discoveryIndexerService.searchDiscoveries(query);
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

    // New method for EventBridge triggered cleanup
    @Transactional
    public void cleanupOldDiscoveries() {
        log.info("Running EventBridge triggered task: Cleaning up old discoveries (example).");
        // Implement actual cleanup logic here
        // For example, delete discoveries older than X months from DB and OpenSearch
    }
}
