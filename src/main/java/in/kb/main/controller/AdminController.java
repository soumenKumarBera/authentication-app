package in.kb.main.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping
    public String admin() {

        return "ADMIN ACCESS";
    }
}