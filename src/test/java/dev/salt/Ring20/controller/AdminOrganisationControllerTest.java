package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.dto.admin.AdminCreateEventDto;
import dev.salt.Ring20.dto.admin.AdminOrganizationDto;
import dev.salt.Ring20.dto.admin.AdminOrganizationEventDto;
import dev.salt.Ring20.dto.organization.OrganizationCreateRequestDto;
import dev.salt.Ring20.entity.Event;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class AdminOrganisationControllerTest {

    @Mock private OrganisationService organisationService;
    @Mock private EventService eventService;

    private AdminOrganisationController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminOrganisationController(organisationService, eventService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getOrganisationsReturnsAdminContract() {
        Organisation organisation = organisation(1L);
        Event event = event(2L, organisation);
        organisation.setEvents(List.of(event));
        when(organisationService.getAllOrganisations()).thenReturn(List.of(organisation));

        ResponseEntity<List<AdminOrganizationDto>> response = controller.getOrganisations();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getFirst().organizerId());
        assertEquals(2L, response.getBody().getFirst().events().getFirst().id());
    }

    @Test
    void createOrganisationReturnsCreatedOrganisation() {
        OrganizationCreateRequestDto request =
                new OrganizationCreateRequestDto("Salt", "Training", "Stockholm", 1L, "Motivation");
        when(organisationService.createOrganisation(any(Organisation.class), eq(1L)))
                .thenReturn(organisation(3L));

        ResponseEntity<AdminOrganizationDto> response = controller.createOrganisation(request);

        ArgumentCaptor<Organisation> captor = ArgumentCaptor.forClass(Organisation.class);

        verify(organisationService).createOrganisation(captor.capture(), eq(1L));

        Organisation passed = captor.getValue();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(3L, response.getBody().id());
        assertEquals("Salt", passed.getName());
        assertEquals("Training", passed.getDescription());
    }

    @Test
    void deleteOrganisationReturnsNoContent() {
        ResponseEntity<Void> response = controller.deleteOrganisation(4L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(organisationService).deleteOrganisationById(4L);
    }

    @Test
    void createEventUsesOrganisationCityAndReturnsCreatedEvent() {
        Organisation organisation = organisation(1L);
        Event created = event(5L, organisation);
        LocalDateTime time = LocalDateTime.of(2026, 8, 4, 10, 0);
        AdminCreateEventDto dto =
                new AdminCreateEventDto(organisation.getId(), "Morning event", "Description", time);
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(eventService.createEvent(any(Event.class), eq(organisation.getId())))
                .thenReturn(created);

        ResponseEntity<AdminOrganizationEventDto> response = controller.createEvent(dto);
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

        verify(eventService).createEvent(captor.capture(), eq(1L));

        Event passed = captor.getValue();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, response.getBody().id());
        assertEquals(1L, response.getBody().organisationId());
        assertEquals("Morning event", response.getBody().name());
        assertEquals("Morning event", passed.getName());
        assertEquals("Description", passed.getDescription());
    }

    @Test
    void deleteEventReturnsNoContent() {
        ResponseEntity<Void> response = controller.deleteEvent(6L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService).deleteEventById(6L);
    }

    private Organisation organisation(Long id) {
        User organizer = new User();
        organizer.setId(1L);
        Organisation organisation =
                new Organisation("Salt", "Training", "Stockholm", organizer, "motivation");
        organisation.setId(id);
        return organisation;
    }

    private Event event(Long id, Organisation organisation) {
        Event event = new Event();
        event.setId(id);
        event.setName("Morning event");
        event.setDescription("Description");
        event.setTime(LocalDateTime.of(2026, 8, 4, 10, 0));
        event.setOrganisation(organisation);
        return event;
    }
}
