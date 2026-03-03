package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("stored_files")
public class StoredFileEntity implements Persistable<UUID> {
    @Id
    private UUID id;
    
    @Column("organization_id")
    private UUID organizationId;

    @Column("file_name")
    private String fileName; // Nom sur le disque (ex: UUID.ext)

    @Column("original_file_name")
    private String originalFileName;

    @Column("content_type")
    private String contentType;

    @Column("file_size")
    private Long fileSize;

    @Column("storage_path")
    private String storagePath;

    @Column("uploaded_by")
    private UUID uploadedBy;

    @Column("created_at")
    private Instant createdAt;

    @Transient
    private boolean isNew = false;

    @Override
    @Transient
    public boolean isNew() {
        return this.isNew || this.id == null;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
