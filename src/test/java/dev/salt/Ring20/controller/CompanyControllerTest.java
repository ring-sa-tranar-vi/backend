package dev.salt.Ring20.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.salt.Ring20.service.CompanyService;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyController Tests")
class CompanyControllerTest {

    @Mock private CompanyService companyService;

    @Test
    void getCompanyMeReturnsCompanyContext() {
        CompanyController controller = new CompanyController(companyService);
        CompanyMeResponseDto dto =
                new CompanyMeResponseDto(12L, "ORGANIZER", true, 5L, "Aktiva Tillsammans");
        when(companyService.getCompanyMe()).thenReturn(dto);

        ResponseEntity<CompanyMeResponseDto> response = controller.getCompanyMe();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(companyService).getCompanyMe();
    }

    @Test
    void getOrganisationReturnsOrganisationProfile() {
        CompanyController controller = new CompanyController(companyService);
        CompanyOrganisationResponseDto dto =
                new CompanyOrganisationResponseDto(
                        5L, "Aktiva Tillsammans", "Beskrivning", "Stockholm");
        when(companyService.getOrganisation()).thenReturn(dto);

        ResponseEntity<CompanyOrganisationResponseDto> response = controller.getOrganisation();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(companyService).getOrganisation();
    }

    @Test
    void updateOrganisationReturnsUpdatedProfile() {
        CompanyController controller = new CompanyController(companyService);
        CompanyOrganisationRequestDto request =
                new CompanyOrganisationRequestDto("Aktiva Tillsammans", "Ny text", "Stockholm");
        CompanyOrganisationResponseDto dto =
                new CompanyOrganisationResponseDto(
                        5L, "Aktiva Tillsammans", "Ny text", "Stockholm");
        when(companyService.updateOrganisation(request)).thenReturn(dto);

        ResponseEntity<CompanyOrganisationResponseDto> response =
                controller.updateOrganisation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(companyService).updateOrganisation(request);
    }

    @Test
    void getEventsReturnsCompanyEvents() {
        CompanyController controller = new CompanyController(companyService);
        List<CompanyEventResponseDto> events =
                List.of(
                        new CompanyEventResponseDto(
                                101L,
                                "Lugn cirkelträning",
                                "Pass för alla nivåer",
                                LocalDateTime.of(2026, 8, 1, 18, 0),
                                "Stockholm",
                                "Stadshagen",
                                12));
        when(companyService.getEvents()).thenReturn(events);

        ResponseEntity<List<CompanyEventResponseDto>> response = controller.getEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(companyService).getEvents();
    }

    @Test
    void createEventReturnsCreatedResponse() {
        CompanyController controller = new CompanyController(companyService);
        CompanyEventRequestDto request =
                new CompanyEventRequestDto(
                        "Promenad och stretch",
                        "Lugn gruppaktivitet",
                        LocalDateTime.of(2026, 8, 5, 12, 30),
                        "Malmo",
                        "Ribersborg");
        CompanyEventResponseDto dto =
                new CompanyEventResponseDto(
                        102L,
                        "Promenad och stretch",
                        "Lugn gruppaktivitet",
                        LocalDateTime.of(2026, 8, 5, 12, 30),
                        "Malmo",
                        "Ribersborg",
                        0);
        when(companyService.createEvent(request)).thenReturn(dto);
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(
                        new MockHttpServletRequest("POST", "/api/company/events")));

        try {
            ResponseEntity<CompanyEventResponseDto> response = controller.createEvent(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(dto, response.getBody());
            assertNotNull(response.getHeaders().getLocation());
            assertEquals("/api/company/events/102", response.getHeaders().getLocation().getPath());
            verify(companyService).createEvent(request);
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    void updateEventReturnsUpdatedEvent() {
        CompanyController controller = new CompanyController(companyService);
        CompanyEventRequestDto request =
                new CompanyEventRequestDto(
                        "Promenad och stretch",
                        "Uppdaterad beskrivning",
                        LocalDateTime.of(2026, 8, 5, 13, 45),
                        "Malmo",
                        "Ribersborg");
        CompanyEventResponseDto dto =
                new CompanyEventResponseDto(
                        102L,
                        "Promenad och stretch",
                        "Uppdaterad beskrivning",
                        LocalDateTime.of(2026, 8, 5, 13, 45),
                        "Malmo",
                        "Ribersborg",
                        8);
        when(companyService.updateEvent(102L, request)).thenReturn(dto);

        ResponseEntity<CompanyEventResponseDto> response = controller.updateEvent(102L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(companyService).updateEvent(102L, request);
    }

    @Test
    void deleteEventReturnsNoContent() {
        CompanyController controller = new CompanyController(companyService);

        ResponseEntity<Void> response = controller.deleteEvent(102L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(companyService).deleteEvent(102L);
    }
}
