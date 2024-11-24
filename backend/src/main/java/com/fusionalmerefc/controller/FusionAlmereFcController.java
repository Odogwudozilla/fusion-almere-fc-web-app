package com.fusionalmerefc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
    
    @GetMapping("/{path:[^\\.]*}")
    public String redirectToIndex() {
        log.info("Unknown page. Redirected to the Home page");
        return "forward:/index.html";
    }
}
