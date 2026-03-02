package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;

@RestController
@RequestMapping("/api/params")
@Tag(name = "Parameters Demo", description = "Query and Path parameters demo")
public class ParamsRestController {

    @GetMapping
    @Operation(summary = "Demo of @RequestParam")
    public Map<String, String> withQueryParam(@RequestParam(value = "name", defaultValue = "world") String name) {
        return Map.of("hello", name);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Demo of @PathVariable")
    public Map<String, Object> withPathParam(@PathVariable("id") Long id) {
        return Map.of("id", id);
    }
}
