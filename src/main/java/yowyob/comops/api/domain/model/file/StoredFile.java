package yowyob.comops.api.domain.model.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {
    private UUID id;
    private String publicUrl; // URL API pour accéder au fichier (ex: /files/{id})
    private String fileName;
    private String contentType;
    private Long size;
    private Instant createdAt;
}
