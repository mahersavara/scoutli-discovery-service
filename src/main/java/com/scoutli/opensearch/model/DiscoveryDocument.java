package com.scoutli.opensearch.model;

import com.scoutli.domain.entity.Discovery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryDocument {
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

    public static DiscoveryDocument fromDiscovery(Discovery discovery) {
        DiscoveryDocument doc = new DiscoveryDocument();
        doc.id = discovery.getId();
        doc.name = discovery.getName();
        doc.description = discovery.getDescription();
        doc.streetAddress = discovery.getStreetAddress();
        doc.city = discovery.getCity();
        doc.country = discovery.getCountry();
        doc.latitude = discovery.getLatitude();
        doc.longitude = discovery.getLongitude();
        doc.userEmail = discovery.getUserEmail();
        doc.tags = discovery.getTags().stream().map(t -> t.getName().toLowerCase()).collect(Collectors.toList());
        return doc;
    }
}
