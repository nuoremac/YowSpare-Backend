package yowyob.comops.api.domain.port.out.file;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.file.StoredFile;
import java.util.UUID;

public interface FileStoragePort {
    /**
     * Stocke un fichier et retourne ses métadonnées.
     * 
     * @param filePart       Le fichier uploadé
     * @param uploaderId     L'ID de l'utilisateur qui upload
     * @param organizationId L'ID de l'organisation (peut être null si user n'a pas
     *                       encore d'org)
     */
    Mono<StoredFile> store(FilePart filePart, UUID uploaderId, UUID organizationId);

    Mono<Resource> loadAsResource(UUID fileId);

    Mono<StoredFile> getMetadata(UUID fileId);
}