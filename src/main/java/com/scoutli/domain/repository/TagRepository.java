package com.scoutli.domain.repository;

import com.scoutli.domain.entity.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {
    public Tag findByName(String name) {
        return find("name", name).firstResult();
    }
}
