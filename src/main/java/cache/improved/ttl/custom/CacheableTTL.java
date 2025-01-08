package cache.improved.ttl.custom;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(cacheResolver = TTLCacheResolver.BEAN_NAME, cacheNames = TTLCacheResolver.NO_NAME)
public @interface CacheableTTL {

  long ttl();

  ChronoUnit unit();

}
