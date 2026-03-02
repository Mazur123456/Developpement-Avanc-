package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meta")
@Tag(name = "Meta API", description = "Metadata for entities and dynamic fields introspection")
public class MetaRestController {

    @GetMapping("/annonces")
    @Operation(summary = "Get the list of filterable and sortable fields for Annonce using Java reflection")
    public ResponseEntity<List<Map<String, String>>> getAnnonceMetadata() {
        List<Map<String, String>> fields = Arrays.stream(Annonce.class.getDeclaredFields())
                .map(f -> Map.of("name", f.getName(), "type", f.getType().getSimpleName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(fields);
    }
}
