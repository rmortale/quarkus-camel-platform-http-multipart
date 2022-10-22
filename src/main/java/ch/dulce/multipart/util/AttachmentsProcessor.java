package ch.dulce.multipart.util;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.attachment.AttachmentMessage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.activation.DataHandler;
import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;


@ApplicationScoped
public class AttachmentsProcessor implements Processor {

    @ConfigProperty(name = "file.store.dir", defaultValue = "./")
    String fileDir;

    @Override
    public void process(Exchange e) throws Exception {
        final AttachmentMessage am = e.getMessage(AttachmentMessage.class);
        String uuid = UUID.randomUUID().toString();
        if (am.hasAttachments()) {
            for (Map.Entry<String, DataHandler> entry : am.getAttachments().entrySet()) {
                try (InputStream in = entry.getValue().getInputStream()) {
                    Files.copy(in, Path.of(fileDir, uuid + "-" + entry.getKey()), StandardCopyOption.REPLACE_EXISTING);
                    //throw new IOException("testing");
                }
            }
            setBody(e, String.format("Successfully processed %s file(s).", am.getAttachments().size()), uuid);
        } else {
            setBody(e, "No files found to process!", uuid);
        }
    }

    private void setBody(Exchange e, String message, String uuid) {
        e.getMessage().setHeader("traceId", uuid);
        e.getMessage().setBody(UploadResponse.builder()
                .traceId(uuid)
                .message(message)
                .status(HttpResponseStatus.OK.reasonPhrase())
                .build());
    }
}
