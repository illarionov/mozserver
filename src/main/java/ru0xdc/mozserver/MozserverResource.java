package ru0xdc.mozserver;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import ru0xdc.mozserver.model.Saying;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by alexey on 03.02.14.
 */
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class MozserverResource {
    private final String template = "Hello, %s";
    private final String defaultName = "user";
    private final AtomicLong counter;

    public MozserverResource() {
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        return new Saying(counter.incrementAndGet(),
                String.format(template, name.or(defaultName)));
    }
}
