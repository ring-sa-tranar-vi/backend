package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.entity.Organization;
import dev.salt.Ring20.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock private OrganisationService organisationService;
    @Mock private EventService eventService;
    @Mock private UserService userService;

    @Test
    void resolvesOrganisationOnlyThroughAuthenticatedOrganizer() {
        CompanyService service = new CompanyService(organisationService, eventService, userService);
        User user = new User();
        user.setId(7L);
        Organization owned = new Organization();
        owned.setId(12L);
        owned.setName("Owned organisation");
        when(userService.getByClerkIdOrThrow("clerk-company")).thenReturn(user);
        when(organisationService.getOrganisationForUser("clerk-company"))
                .thenReturn(List.of(owned));
        when(organisationService.findOrganisationForUser("clerk-company"))
                .thenReturn(Optional.of(owned));

        assertEquals(12L, service.getManagedOrganisationForClerkId("clerk-company").getId());
        assertEquals("COMPANY", service.getCompanyMe("clerk-company").role());
        assertEquals(true, service.getCompanyMe("clerk-company").canManageOrganisation());
    }

    @Test
    void returnsUserContractWhenAuthenticatedUserHasNoOrganisation() {
        CompanyService service = new CompanyService(organisationService, eventService, userService);
        User user = new User();
        user.setId(7L);
        when(userService.getByClerkIdOrThrow("clerk-user")).thenReturn(user);
        when(organisationService.findOrganisationForUser("clerk-user"))
                .thenReturn(Optional.empty());

        var response = service.getCompanyMe("clerk-user");

        assertEquals(7L, response.userId());
        assertEquals("USER", response.role());
        assertEquals(false, response.canManageOrganisation());
        assertEquals(null, response.organisationId());
        assertEquals(null, response.organisationName());
    }
}
