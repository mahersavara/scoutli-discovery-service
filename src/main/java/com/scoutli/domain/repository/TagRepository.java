package com.scoutli.domain.repository;

import com.scoutli.domain.entity.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.cache.CacheResult;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {
    @CacheResult(cacheName = "tags")
    public Tag findByName(String name) {
        return find("name", name).firstResult();
    }
}
