package ch.dulce.multipart;

import ch.dulce.multipart.util.AttachmentsProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.log;

@ApplicationScoped
public class Routes extends RouteBuilder {


    @Inject
    AttachmentsProcessor attachmentsProcessor;


    @Override
    public void configure() throws Exception {

        from("platform-http:/upload?httpMethodRestrict=POST&consumes=multipart/form-data")
                .process(attachmentsProcessor)
                .removeHeaders("*", "traceId")
                .to(log("upload-processor").showExchangePattern(false).showBodyType(false))
                .marshal().json(JsonLibrary.Jackson);
    }
}
