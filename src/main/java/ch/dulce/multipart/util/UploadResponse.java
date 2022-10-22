package ch.dulce.multipart.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponse {
    private String traceId;
    private String status;
    private String message;
}
