package yowyob.comops.api.infrastructure.adapter.in.web.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.file.StoredFile;
import yowyob.comops.api.domain.port.out.file.FileStoragePort;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Gestion du stockage et de la récupération de fichiers")
public class FileController {
    private final FileStoragePort fileStorage;
    private final SecurityUtils securityUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader un fichier", description = "Stocke un fichier et retourne ses métadonnées. Le fichier est automatiquement associé à l'organisation de l'utilisateur (via X-Tenant-ID).", requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object", properties = {
    }))))
    @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoredFile.class)))
    public Mono<ResponseEntity<StoredFile>> uploadFile(@RequestPart("file") FilePart file) {
        // Tente de récupérer l'ID de l'org. Si l'utilisateur n'en a pas encore
        // (onboarding), onErrorReturn(null)
        // permet de stocker le fichier comme "personnel" (non-lié à une org).
        Mono<UUID> organizationIdMono = securityUtils.getCurrentOrganizationId()
                .onErrorResume(e -> Mono.justOrEmpty(null));

        return Mono.zip(securityUtils.getCurrentUserId(), organizationIdMono)
                .flatMap(tuple -> {
                    UUID userId = tuple.getT1();
                    UUID orgId = tuple.getT2(); // Peut être null
                    return fileStorage.store(file, userId, orgId);
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Télécharger ou afficher un fichier", description = "Récupère le contenu binaire d'un fichier stocké par son ID.")
    @ApiResponse(responseCode = "200", description = "Contenu du fichier", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<Resource>> downloadFile(@PathVariable UUID id) {
        return fileStorage.getMetadata(id)
                .flatMap(metadata -> fileStorage.loadAsResource(id)
                        .map(resource -> ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                        "inline; filename=\"" + metadata.getFileName() + "\"")
                                .header(HttpHeaders.CONTENT_TYPE, metadata.getContentType())
                                .body(resource)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}