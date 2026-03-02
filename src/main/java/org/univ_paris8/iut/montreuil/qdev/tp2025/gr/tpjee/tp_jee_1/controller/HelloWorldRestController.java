package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;

@RestController
@RequestMapping("/api/helloWorld")
@Tag(name = "Hello World", description = "Test endpoint")
public class HelloWorldRestController {

    @GetMapping
    @Operation(summary = "Returns Hello World")
    public Map<String, String> hello() {
        return Map.of("message", "Hello World!");
    }
}
