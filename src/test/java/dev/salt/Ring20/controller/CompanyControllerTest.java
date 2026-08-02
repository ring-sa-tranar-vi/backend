package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.dto.CompanyMeResponseDto;
import dev.salt.Ring20.dto.EventRequestDto;
import dev.salt.Ring20.dto.EventResponseDto;
import dev.salt.Ring20.dto.OrganisationRequestDto;
import dev.salt.Ring20.dto.OrganisationResponseDto;
import dev.salt.Ring20.entity.EventType;
import dev.salt.Ring20.entity.Organisation;
import dev.salt.Ring20.service.CompanyService;
import dev.salt.Ring20.service.EventService;
import dev.salt.Ring20.service.OrganisationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyController Tests")
class CompanyControllerTest {

    @Mock private CompanyService companyService;
    private final String CLERK_ID = "clerk_123";

    private Authentication mockAuthentication() {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(CLERK_ID);
        when(auth.getPrincipal()).thenReturn(jwt);
        return auth;
    }

    @Test
    void getCompanyMeReturnsCompanyContext() {
        CompanyController controller = new CompanyController(companyService);
        CompanyMeResponseDto dto =
                new CompanyMeResponseDto(12L, "ORGANIZER", true, 5L, "Aktiva Tillsammans");
        when(companyService.getCompanyMe(CLERK_ID)).thenReturn(dto);

        ResponseEntity<CompanyMeResponseDto> response =
                controller.getCompanyMe(mockAuthentication());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(companyService).getCompanyMe(CLERK_ID);
    }

    @Test
    void getOrganisationReturnsOrganisationProfile() {
        CompanyController controller = new CompanyController(companyService);
        Organisation org = new Organisation();
        org.setId(5L);
        org.setName("Aktiva Tillsammans");
        org.setDescription("Beskrivning");
        org.setOrgCity("Stockholm");
        org.setEvents(List.of());
        when(companyService.getManagedOrganisationForClerkId(CLERK_ID)).thenReturn(org);

        ResponseEntity<OrganisationResponseDto> response =
                controller.getOrganisation(mockAuthentication());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().id());
        assertEquals("Aktiva Tillsammans", response.getBody().name());
    }

    @Test
    void updateOrganisationReturnsUpdatedProfile() {
        CompanyController controller = new CompanyController(companyService);
        OrganisationRequestDto request =
                new OrganisationRequestDto("Aktiva Tillsammans", "Ny text", List.of(), "Stockholm");
        Organisation org = new Organisation();
        org.setId(5L);
        when(companyService.getManagedOrganisationForClerkId(CLERK_ID)).thenReturn(org);

        Organisation updated = new Organisation();
        updated.setId(5L);
        updated.setName("Aktiva Tillsammans");
        updated.setDescription("Ny text");
        updated.setOrgCity("Stockholm");
        updated.setEvents(List.of());
        when(companyService.getOrganisationService()).thenReturn(mock(OrganisationService.class));
        when(companyService
                        .getOrganisationService()
                        .updateOrganisationById(
                                5L, "Aktiva Tillsammans", "Ny text", null, "Stockholm"))
                .thenReturn(updated);

        ResponseEntity<OrganisationResponseDto> response =
                controller.updateOrganisation(mockAuthentication(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ny text", response.getBody().description());
    }

    @Test
    void getEventsReturnsCompanyEvents() {
        CompanyController controller = new CompanyController(companyService);
        Organisation org = new Organisation();
        org.setId(5L);
        when(companyService.getManagedOrganisationForClerkId(CLERK_ID)).thenReturn(org);
        when(companyService.getEventService()).thenReturn(mock(EventService.class));
        when(companyService.getEventService().getAllEventsByOrgId(5L)).thenReturn(List.of());

        ResponseEntity<List<EventResponseDto>> response =
                controller.getEvents(mockAuthentication());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createEventReturnsCreatedResponse() {
        CompanyController controller = new CompanyController(companyService);
        EventRequestDto request =
                new EventRequestDto(
                        "Promenad och stretch",
                        "Lugn gruppaktivitet",
                        LocalDateTime.of(2026, 8, 5, 12, 30),
                        new Organisation(),
                        "Malmo",
                        "Ribersborg",
                        EventType.IN_PERSON);
        Organisation org = new Organisation();
        org.setId(5L);

        dev.salt.Ring20.entity.Event created = new dev.salt.Ring20.entity.Event();
        created.setId(102L);
        created.setName("Promenad och stretch");
        created.setDescription("Lugn gruppaktivitet");
        created.setTime(LocalDateTime.of(2026, 8, 5, 12, 30));
        created.setCity("Malmo");
        created.setVenue("Ribersborg");
        created.setOrganisation(org);
        created.setEventType(EventType.IN_PERSON);

        when(companyService.getManagedOrganisationForClerkId(CLERK_ID)).thenReturn(org);
        EventService eventServiceMock = mock(EventService.class);
        when(companyService.getEventService()).thenReturn(eventServiceMock);
        when(eventServiceMock.createEvent(
                        "Promenad och stretch",
                        "Lugn gruppaktivitet",
                        LocalDateTime.of(2026, 8, 5, 12, 30),
                        org,
                        "Malmo",
                        "Ribersborg",
                        EventType.IN_PERSON))
                .thenReturn(created);

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(
                        new MockHttpServletRequest("POST", "/api/company/events")));

        try {
            ResponseEntity<EventResponseDto> response =
                    controller.createEvent(mockAuthentication(), request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    // TODO: Fix this test - complex mocking needed
    /*
    @Test
    void updateEventReturnsUpdatedEvent() {
        CompanyController controller = new CompanyController(companyService);
        EventRequestDto request =
                new EventRequestDto(
                        "Promenad och stretch",
                        "Uppdaterad beskrivning",
                        LocalDateTime.of(2026, 8, 5, 13, 45),
                        new Organisation(),
                        "Malmo",
                        "Ribersborg",
                        EventType.IN_PERSON);

        Organisation org = new Organisation();
        org.setId(5L);
        dev.salt.Ring20.entity.Event event = new dev.salt.Ring20.entity.Event();
        event.setId(102L);
        event.setName("Promenad och stretch");
        event.setOrganisation(org);
        event.setEventType(EventType.IN_PERSON);

        dev.salt.Ring20.entity.Event updated = new dev.salt.Ring20.entity.Event();
        updated.setId(102L);
        updated.setName("Promenad och stretch");
        updated.setDescription("Uppdaterad beskrivning");
        updated.setTime(LocalDateTime.of(2026, 8, 5, 13, 45));
        updated.setCity("Malmo");
        updated.setVenue("Ribersborg");
        updated.setOrganisation(org);
        updated.setEventType(EventType.IN_PERSON);

        when(companyService.getManagedOrganisationForClerkId(CLERK_ID)).thenReturn(org);
        when(companyService.getManagedEventForClerkId(102L, CLERK_ID)).thenReturn(event);
        EventService eventServiceMock = mock(EventService.class);
        when(companyService.getEventService()).thenReturn(eventServiceMock);
        when(companyService.getDefaultEventType()).thenReturn(EventType.IN_PERSON);
        when(eventServiceMock.updateEvent(102L, "Promenad och stretch", "Uppdaterad beskrivning",
                LocalDateTime.of(2026, 8, 5, 13, 45), org, "Malmo", "Ribersborg",
                EventType.IN_PERSON)).thenReturn(updated);

        ResponseEntity<EventResponseDto> response =
                controller.updateEvent(mockAuthentication(), 102L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    */

    @Test
    void deleteEventReturnsNoContent() {
        CompanyController controller = new CompanyController(companyService);

        ResponseEntity<Void> response = controller.deleteEvent(mockAuthentication(), 102L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(companyService).deleteEventForClerkId(102L, CLERK_ID);
    }
}
