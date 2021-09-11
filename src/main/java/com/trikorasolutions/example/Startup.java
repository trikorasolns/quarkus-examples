package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class Startup {
    private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

    @Inject
    LaunchMode launchMode;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting... {}, {}", launchMode, launchMode.isDevOrTest());
        if (launchMode.isDevOrTest()) {
            Panache.withTransaction(() -> new Fruit("pear", "Pear").persist()).await().atMost(Duration.ofSeconds(30));
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping... {}, {}", launchMode, launchMode.isDevOrTest());
    }
}
