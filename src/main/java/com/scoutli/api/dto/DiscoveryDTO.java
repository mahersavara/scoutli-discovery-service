package com.scoutli.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryDTO {
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
    // averageRating removed for now, will be fetched from interaction-service later

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        public String name;
        public String description;
        public String streetAddress;
        public String city;
        public String country;
        public List<String> tags;
    }
}
