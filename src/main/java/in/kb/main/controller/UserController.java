package in.kb.main.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping
    public String user() {

        return "USER ACCESS";
    }
}
