package com.scoutli.opensearch.service;

import com.scoutli.opensearch.model.DiscoveryDocument;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Discovery Indexer Service - OpenSearch Integration
 * 
 * TODO: Implement OpenSearch Java Client integration
 * The old Elasticsearch REST High Level Client has been deprecated.
 * This service will be rewritten to use OpenSearch Java Client.
 * 
 * For now, methods return empty results to allow the application to build and
 * run.
 */
@ApplicationScoped
@Slf4j
public class DiscoveryIndexerService {

    private static final String INDEX_NAME = "discoveries";

    public void createIndexIfNeeded() {
        log.warn("OpenSearch indexing not yet implemented - createIndexIfNeeded() called");
        // TODO: Implement with OpenSearch Java Client
    }

    public void indexDiscovery(DiscoveryDocument document) {
        log.warn("OpenSearch indexing not yet implemented - indexDiscovery() called for ID: {}", document.getId());
        // TODO: Implement with OpenSearch Java Client
    }

    public List<DiscoveryDocument> searchDiscoveries(String query) {
        log.warn("OpenSearch search not yet implemented - searchDiscoveries() called with query: {}", query);
        // TODO: Implement with OpenSearch Java Client
        return new ArrayList<>();
    }
}