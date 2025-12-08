package com.scoutli.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discoveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discovery {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String streetAddress;
    private String city;
    private String country;

    private Double latitude;
    private Double longitude;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "discovery_tags", joinColumns = @JoinColumn(name = "discovery_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();
}
