package com.scoutli.opensearch.service;

import com.scoutli.opensearch.model.DiscoveryDocument;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class DiscoveryIndexerService {

    private static final String INDEX_NAME = "discoveries";

    @Inject
    RestHighLevelClient restHighLevelClient; // Injected by Quarkus Elasticsearch client

    public void createIndexIfNeeded() {
        try {
            GetIndexRequest request = new GetIndexRequest(INDEX_NAME);
            boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
            if (!exists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);
                createIndexRequest.settings(Settings.builder()
                        .put("index.number_of_shards", 1)
                        .put("index.number_of_replicas", 0)
                );
                // Define mappings if necessary
                createIndexRequest.mapping(Map.of(
                    "properties", Map.of(
                        "id", Map.of("type", "long"),
                        "name", Map.of("type", "text"),
                        "description", Map.of("type", "text"),
                        "streetAddress", Map.of("type", "text"),
                        "city", Map.of("type", "text"),
                        "country", Map.of("type", "text"),
                        "latitude", Map.of("type", "double"),
                        "longitude", Map.of("type", "double"),
                        "userEmail", Map.of("type", "keyword"),
                        "tags", Map.of("type", "keyword")
                    )
                ));
                restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                log.info("OpenSearch index '{}' created.", INDEX_NAME);
            } else {
                log.info("OpenSearch index '{}' already exists.", INDEX_NAME);
            }
        } catch (IOException e) {
            log.error("Failed to create OpenSearch index: {}", e.getMessage(), e);
        }
    }

    public void indexDiscovery(DiscoveryDocument document) {
        try {
            IndexRequest request = new IndexRequest(INDEX_NAME);
            request.id(document.getId().toString()); // Use Discovery ID as document ID
            request.source(document, XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("Discovery indexed: ID={}", document.getId());
        } catch (IOException e) {
            log.error("Failed to index discovery {}: {}", document.getId(), e.getMessage(), e);
        }
    }

    public List<DiscoveryDocument> searchDiscoveries(String query) {
        List<DiscoveryDocument> results = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // Multi-match query across relevant fields
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(query, "name", "description", "city", "country", "tags"));
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : searchResponse.getHits().getHits()) {
                // Convert Map<String, Object> source to DiscoveryDocument
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                DiscoveryDocument doc = new DiscoveryDocument();
                doc.setId(Long.valueOf(sourceAsMap.get("id").toString()));
                doc.setName((String) sourceAsMap.get("name"));
                doc.setDescription((String) sourceAsMap.get("description"));
                doc.setStreetAddress((String) sourceAsMap.get("streetAddress"));
                doc.setCity((String) sourceAsMap.get("city"));
                doc.setCountry((String) sourceAsMap.get("country"));
                doc.setLatitude((Double) sourceAsMap.get("latitude"));
                doc.setLongitude((Double) sourceAsMap.get("longitude"));
                doc.setUserEmail((String) sourceAsMap.get("userEmail"));
                // Tags might be a list of strings
                doc.setTags( (List<String>) sourceAsMap.get("tags") );
                results.add(doc);
            }
            log.info("Search for '{}' returned {} results.", query, results.size());
        } catch (IOException e) {
            log.error("Failed to search OpenSearch: {}", e.getMessage(), e);
        }
        return results;
    }
}