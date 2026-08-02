package dev.salt.Ring20.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.dto.CompanyEventRequestDto;
import dev.salt.Ring20.dto.CompanyOrganisationRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.EventType;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyService Tests")
class CompanyServiceTest {

    @Mock private UserRepository userRepository;

    @Mock private OrganisationService organisationService;

    @Mock private EventService eventService;

    @Mock private UserService userService;

    @InjectMocks private CompanyService companyService;

    private final String CLERK_ID = "clerk_1";

    // TODO: Fix these tests after service refactoring
    /*
    @Test
    void getCompanyMeReturnsUserAndManagedOrganisation() {
        Organisation org = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        org.setEvents(List.of());
        User user = new User("Ada", 2, "context", CLERK_ID);
        user.setId(12L);
        when(userService.getByClerkIdOrThrow(CLERK_ID)).thenReturn(user);
        when(organisationService.getAllOrganisations()).thenReturn(List.of(org));

        var response = companyService.getCompanyMe(CLERK_ID);

        assertEquals(12L, response.userId());
        assertEquals("ORGANIZER", response.role());
        assertEquals(5L, response.organisationId());
        assertEquals("Aktiva Tillsammans", response.organisationName());
    }
    */

    @Test
    void getManagedOrganisationForClerkIdReturnsOrganisationWhenUserIsTrainer() {
        User user = new User("Ada", 2, "context", CLERK_ID);
        user.setId(12L);
        user.setTrainerId(5L);
        Organisation org = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        Event event = event(1L, org, LocalDateTime.of(2026, 8, 1, 18, 0));
        org.setEvents(List.of(event));
        when(userService.getByClerkIdOrThrow(CLERK_ID)).thenReturn(user);
        when(organisationService.getAllOrganisations()).thenReturn(List.of(org));

        var result = companyService.getManagedOrganisationForClerkId(CLERK_ID);

        assertEquals(5L, result.getId());
    }

    @Test
    void updateOrganisationUsesManagedOrganisationId() {
        Organisation existing = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        existing.setEvents(List.of());
        when(organisationService.getAllOrganisations()).thenReturn(List.of(existing));
        when(organisationService.updateOrganisationById(
                        5L, "Aktiva Tillsammans", "Ny text", List.of(), "Uppsala"))
                .thenReturn(organisation(5L, "Aktiva Tillsammans", "Uppsala", "Ny text"));

        var response =
                companyService.updateOrganisation(
                        new CompanyOrganisationRequestDto(
                                "Aktiva Tillsammans", "Ny text", "Uppsala"));

        assertEquals(5L, response.id());
        assertEquals("Ny text", response.description());
        assertEquals("Uppsala", response.orgCity());
    }

    @Test
    void getEventsReturnsManagedOrganisationEventsSortedByTime() {
        Organisation org = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        Event later = event(2L, org, LocalDateTime.of(2026, 8, 5, 13, 45));
        later.setName("Later");
        Event earlier = event(1L, org, LocalDateTime.of(2026, 8, 1, 18, 0));
        earlier.setName("Earlier");
        when(organisationService.getAllOrganisations()).thenReturn(List.of(org));
        when(eventService.getAllEventsByOrgId(5L)).thenReturn(List.of(later, earlier));

        var response = companyService.getEvents();

        assertEquals(2, response.size());
        assertEquals("Earlier", response.get(0).name());
        assertEquals("Later", response.get(1).name());
    }

    @Test
    void createEventUsesManagedOrganisationAndDefaultType() {
        Organisation org = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        Event created = event(102L, org, LocalDateTime.of(2026, 8, 5, 12, 30));
        created.setName("Promenad och stretch");
        created.setDescription("Lugn gruppaktivitet");
        created.setCity("Malmo");
        created.setVenue("Ribersborg");
        created.setUsersAttending(0);
        when(organisationService.getAllOrganisations()).thenReturn(List.of(org));
        when(eventService.createEvent(
                        "Promenad och stretch",
                        "Lugn gruppaktivitet",
                        LocalDateTime.of(2026, 8, 5, 12, 30),
                        org,
                        "Malmo",
                        "Ribersborg",
                        EventType.IN_PERSON))
                .thenReturn(created);

        var response =
                companyService.createEvent(
                        new CompanyEventRequestDto(
                                "Promenad och stretch",
                                "Lugn gruppaktivitet",
                                LocalDateTime.of(2026, 8, 5, 12, 30),
                                "Malmo",
                                "Ribersborg"));

        assertEquals(102L, response.id());
        assertEquals(0, response.attendeesCount());
    }

    // TODO: Fix these tests after service refactoring
    /*
    @Test
    void getManagedEventForClerkIdThrowsWhenEventNotInManagedOrganisation() {
        Organisation managedOrg = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        Organisation otherOrg = organisation(9L, "Other", "Goteborg");
        Event event = event(102L, otherOrg, LocalDateTime.of(2026, 8, 5, 12, 30));
        User user = new User("Ada", 2, "context", CLERK_ID);
        when(userService.getByClerkIdOrThrow(CLERK_ID)).thenReturn(user);
        when(organisationService.getAllOrganisations()).thenReturn(List.of(managedOrg));
        when(eventService.getEventById(102L)).thenReturn(event);

        NoSuchElementException ex =
                assertThrows(
                        NoSuchElementException.class,
                        () -> companyService.getManagedEventForClerkId(102L, CLERK_ID));

        assertEquals("Event not found with id: 102", ex.getMessage());
    }
    */

    // TODO: Fix these tests after service refactoring
    /*
    @Test
    void deleteEventForClerkIdDelegatesWhenEventBelongsToManagedOrganisation() {
        Organisation org = organisation(5L, "Aktiva Tillsammans", "Stockholm");
        Event event = event(102L, org, LocalDateTime.of(2026, 8, 5, 12, 30));
        User user = new User("Ada", 2, "context", CLERK_ID);
        when(userService.getByClerkIdOrThrow(CLERK_ID)).thenReturn(user);
        when(organisationService.getAllOrganisations()).thenReturn(List.of(org));
        when(eventService.getEventById(102L)).thenReturn(event);

        companyService.deleteEventForClerkId(102L, CLERK_ID);

        verify(eventService).deleteEventById(102L);
    }
    */

    @Test
    void getManagedOrganisationThrowsWhenMissing() {
        when(organisationService.getAllOrganisations()).thenReturn(List.of());

        NoSuchElementException ex =
                assertThrows(NoSuchElementException.class, () -> companyService.getOrganisation());

        assertEquals("Company organisation not found", ex.getMessage());
    }

    private Organisation organisation(Long id, String name, String city) {
        return organisation(id, name, city, "Beskrivning");
    }

    private Organisation organisation(Long id, String name, String city, String description) {
        Organisation organisation = new Organisation();
        organisation.setId(id);
        organisation.setName(name);
        organisation.setOrgCity(city);
        organisation.setDescription(description);
        return organisation;
    }

    private Event event(Long id, Organisation organisation, LocalDateTime time) {
        Event event = new Event();
        event.setId(id);
        event.setOrganisation(organisation);
        event.setTime(time);
        event.setEventType(EventType.IN_PERSON);
        return event;
    }
}
