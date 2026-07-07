package com.emras.shared.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    // ── Cache name constants ─────────────────────────────────────────────
    public static final String PRODUCTS        = "products";
    public static final String PRODUCT_DETAIL  = "product-detail";
    public static final String CATEGORIES      = "categories";
    public static final String FEATURED        = "featured";
    public static final String SEARCH_RESULTS  = "search-results";
    public static final String SITE_CONFIG     = "site-config";
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.registerCustomCache(PRODUCTS,       buildCache(5,  100));
        manager.registerCustomCache(PRODUCT_DETAIL, buildCache(10, 500));
        manager.registerCustomCache(CATEGORIES,     buildCache(60, 50));
        manager.registerCustomCache(FEATURED,       buildCache(15, 20));
        manager.registerCustomCache(SEARCH_RESULTS, buildCache(3,  200));
        manager.registerCustomCache(SITE_CONFIG,    buildCache(30, 10));
        return manager;
    }
    /**
     * @param ttlMinutes  time-to-live in minutes
     * @param maxSize     max number of entries
     */
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildCache(
            long ttlMinutes, long maxSize) {
        return Caffeine.newBuilder()
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }
}
