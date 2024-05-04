package com.rmacd.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
class PageController {

    @GetMapping("/")
    String index(Principal principal) {
        return "pages/home";
    }

    @GetMapping("/lob")
    String lob(Principal principal) {
        return "pages/lob";
    }

}
