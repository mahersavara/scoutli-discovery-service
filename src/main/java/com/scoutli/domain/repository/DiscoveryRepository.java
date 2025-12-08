package com.scoutli.domain.repository;

import com.scoutli.domain.entity.Discovery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DiscoveryRepository implements PanacheRepository<Discovery> {
    public List<Discovery> findByTag(String tagName) {
        return list("select d from Discovery d join d.tags t where t.name = ?1", tagName);
    }
}
