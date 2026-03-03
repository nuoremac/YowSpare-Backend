package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.file;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.file.StoredFile;
import yowyob.comops.api.domain.port.out.file.FileStoragePort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.file.StoredFileEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.file.R2dbcFileRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalStorageAdapter implements FileStoragePort {
    private final R2dbcFileRepository repository;
    
    @Value("${app.storage.location:uploads}")
    private String storageLocation;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(this.rootLocation);
            log.info("Storage location initialized at: {}", this.rootLocation.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public Mono<StoredFile> store(FilePart filePart, UUID uploaderId, UUID organizationId) {
        UUID fileId = UUID.randomUUID();
        String extension = getExtension(filePart.filename());
        String storedFileName = fileId.toString() + (extension.isEmpty() ? "" : "." + extension);
        Path destinationFile = this.rootLocation.resolve(Paths.get(storedFileName)).normalize().toAbsolutePath();

        return filePart.transferTo(destinationFile)
                .then(Mono.defer(() -> {
                    StoredFileEntity entity = new StoredFileEntity();
                    entity.setId(fileId);
                    entity.setNew(true); // Pour forcer l'INSERT avec Persistable

                    entity.setOrganizationId(organizationId);
                    entity.setFileName(storedFileName);
                    entity.setOriginalFileName(filePart.filename());
                    entity.setContentType(
                            filePart.headers().getContentType() != null ? filePart.headers().getContentType().toString()
                                    : "application/octet-stream");
                    entity.setFileSize(0L); // Impossible à avoir de façon fiable ici
                    entity.setStoragePath(destinationFile.toString());
                    entity.setUploadedBy(uploaderId);
                    entity.setCreatedAt(Instant.now());

                    return repository.save(entity);
                }))
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Resource> loadAsResource(UUID fileId) {
        return repository.findById(fileId)
                .map(entity -> {
                    Path file = Paths.get(entity.getStoragePath());
                    return new FileSystemResource(file);
                });
    }

    @Override
    public Mono<StoredFile> getMetadata(UUID fileId) {
        return repository.findById(fileId).map(this::mapToDomain);
    }

    private StoredFile mapToDomain(StoredFileEntity entity) {
        return StoredFile.builder()
                .id(entity.getId())
                .fileName(entity.getOriginalFileName())
                .contentType(entity.getContentType())
                .size(entity.getFileSize())
                .createdAt(entity.getCreatedAt())
                .publicUrl("/files/" + entity.getId())
                .build();
    }

    private String getExtension(String filename) {
        if (filename == null)
            return "";
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i + 1) : "";
    }
}