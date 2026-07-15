package dev.salt.Ring20.controller;

import dev.salt.Ring20.service.OrganisationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/organisations")
@CrossOrigin(
        origins = {"http://localhost:5173", "https://https://prod-ringsatranarvi-app.web.app/"})
public class OrganisationController {
    private OrganisationService service;

    @PostMapping
    //    private ResponseEntity<Organisation> createOrganisation(){
    //
    //    }
    //    @GetMapping
    //    private ResponseEntity<Organisation>  getAllOrganisations(){}
    //    @GetMapping
    //    private ResponseEntity<Organisation> getOrganisationById(){}
    //
    //    @DeleteMapping
    //    private ResponseEntity<Organisation> deleteOrganisation(){}
    //
    //    @PutMapping
    //    private ResponseEntity<Organisation> updateOrganisation(){}

}
