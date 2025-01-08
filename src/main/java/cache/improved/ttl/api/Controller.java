package cache.improved.ttl.api;

import cache.improved.ttl.custom.CacheableTTL;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
public class Controller {

    @GetMapping("/timestamp")
    @CacheableTTL(ttl = 10, unit = ChronoUnit.SECONDS)
    public Object timestamp() {
        return Map.of("time", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
    }

}
