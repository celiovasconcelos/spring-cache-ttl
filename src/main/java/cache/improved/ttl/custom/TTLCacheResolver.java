package cache.improved.ttl.custom;


import com.hazelcast.config.MapConfig;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TTLCacheResolver extends SimpleCacheResolver {

    public static final String BEAN_NAME = "TTLCacheResolver";
    public static final String NO_NAME = "NO_NAME";

    public TTLCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    /**
     * It creates names for unnamed cached.
     * You are not obligated to invent cache names anymore.
     * If unnamed, the name of the cache will be the full signature of the method.
     * The NO_NAME attribute is just to prevent the following IDE warnings when using @CacheableTTL:
     * 'At least one non-empty cache name is required per cache operation.'
     */
    @Override
    protected @NonNull Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        Set<String> cacheNames = context.getOperation().getCacheNames().stream()
                .filter(cn -> cn.equals(BEAN_NAME)).collect(Collectors.toSet());
        return cacheNames.isEmpty() ? Set.of(context.getMethod().toString()) : cacheNames;
    }

    /**
     * It recognizes the @CacheableTTL and configure the appropriate timeout.
     * It works for both class and method levels.
     */
    @Override
    public @NonNull Collection<? extends Cache> resolveCaches(@NonNull CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = this.getCacheNames(context);
        Collection<Cache> result = new ArrayList<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            if (!this.getCacheManager().getCacheNames().contains(cacheName)) {
                CacheableTTL cacheableTTL = context.getMethod().getAnnotation(CacheableTTL.class);
                if (cacheableTTL == null)
                    cacheableTTL = context.getTarget().getClass().getAnnotation(CacheableTTL.class);
                Duration duration = Duration.of(cacheableTTL.ttl(), cacheableTTL.unit());
                addToCacheManager(cacheName, duration);
            }
            Cache cache = this.getCacheManager().getCache(cacheName);
            if (cache == null)
                throw new IllegalArgumentException("Cannot find cache named '" + cacheName + "' for " + context.getOperation());
            result.add(cache);
        }
        return result;
    }

    /**
     * It creates the true TTL cache according to your cache provider.
     * This sample use Hazelcast as a cache provider.
     */
    private void addToCacheManager(String cacheName, Duration duration) {
        System.out.println("Adding cache '" + cacheName + "' for " + duration);
        HazelcastCacheManager cacheManager = (HazelcastCacheManager) this.getCacheManager();
        MapConfig cacheConfig = new MapConfig().setName(cacheName).setTimeToLiveSeconds((int) duration.toSeconds());
        cacheManager.getHazelcastInstance().getConfig().addMapConfig(cacheConfig);
    }

}
