package com.scoutli.event;

import com.scoutli.domain.entity.Discovery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryCreatedEvent {
    public Long id;
    public String name;
    public String description;
    public String streetAddress;
    public String city;
    public String country;
    public Double latitude;
    public Double longitude;
    public String userEmail;
    public List<String> tags;

    public static DiscoveryCreatedEvent fromDiscovery(Discovery discovery) {
        return new DiscoveryCreatedEvent(
                discovery.getId(),
                discovery.getName(),
                discovery.getDescription(),
                discovery.getStreetAddress(),
                discovery.getCity(),
                discovery.getCountry(),
                discovery.getLatitude(),
                discovery.getLongitude(),
                discovery.getUserEmail(),
                discovery.getTags().stream().map(t -> t.getName().toLowerCase()).collect(Collectors.toList())
        );
    }
}
