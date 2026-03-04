package yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization;

import org.springframework.stereotype.Component;
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.PointOfInterestEntity;

@Component
public class PointOfInterestMapper {
    public PointOfInterest toDomain(PointOfInterestEntity entity) {
        
        if (entity == null)
            return null;

        return PointOfInterest.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .description(entity.getDescription())
                .mediaUri(entity.getMediaUri())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }

    public PointOfInterestEntity toEntity(PointOfInterest domain) {
        if (domain == null)
            return null;

        PointOfInterestEntity entity = new PointOfInterestEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setType(domain.getType());
        entity.setDescription(domain.getDescription());
        entity.setMediaUri(domain.getMediaUri());
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        // createdAt géré par la base ou mis à jour ailleurs

        return entity;
    }
}