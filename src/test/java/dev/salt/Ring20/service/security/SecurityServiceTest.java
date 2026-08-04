package dev.salt.Ring20.service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.repository.OrganisationRepository;
import dev.salt.Ring20.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock private UserService userService;
    @Mock private OrganisationRepository organisationRepository;

    @Test
    void organizerMustOwnAnOrganisation() {
        SecurityService service = new SecurityService(userService, organisationRepository);
        when(organisationRepository.findByOrganizer_ClerkIdWithEvents("clerk-organizer"))
                .thenReturn(List.of(new Organisation()));

        assertThat(service.isOrganizer("clerk-organizer")).isTrue();
    }

    @Test
    void userWithoutAnOrganisationIsNotAnOrganizer() {
        SecurityService service = new SecurityService(userService, organisationRepository);
        when(organisationRepository.findByOrganizer_ClerkIdWithEvents("clerk-user"))
                .thenReturn(List.of());

        assertThat(service.isOrganizer("clerk-user")).isFalse();
        assertThat(service.isOrganizer(null)).isFalse();
    }
}
