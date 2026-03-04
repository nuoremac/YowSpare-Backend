package yowyob.comops.api.domain.model.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private UUID organizationId; // Le champ est bien là !
    
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; 
    
    private String firstName;
    private String lastName;
    private UUID businessActorId;
    private boolean isActive;
    private List<String> roles;

    private UserPlan plan; 
    private OnboardingStatus onboardingStatus; 
    private int onboardingStep; 

    public enum UserPlan {
        FREE_TIER,
        FREELANCE,
        PROFESSIONAL
    }

    public enum OnboardingStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}