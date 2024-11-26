package com.fusionalmerefc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FusionAlmereFcController {
     private static final Logger log = LoggerFactory.getLogger(FusionAlmereFcController.class);

    @GetMapping("/")
    public String index() {
        log.info("Home page is loaded");
        return "index.html";
    }

    @GetMapping("/admin/roles")
    public String getRolesPage() {
        return "forward:/index.html"; 
    }

    @GetMapping("/admin/permissions")
    public String getPemissionsPage() {
        return "forward:/index.html"; 
    }

    @GetMapping("/admin/users")
    public String getUsersPage() {
        return "forward:/index.html"; 
    }

     @GetMapping(value = {"/users/**", "/home", "/about", "/contact"})
    public String getUserProfilePage(HttpServletRequest request) {
        // Forward only non-API paths to the frontend
        if (!request.getRequestURI().startsWith("/api")) {
            return "forward:/index.html";
        }
        return "404"; // Return error if incorrect configuration
    }
    
    @GetMapping("/{path:[^\\.]*}")
    public String redirectToIndex() {
        log.info("Unknown page. Redirected to the Home page");
        return "forward:/index.html";
    }
}
