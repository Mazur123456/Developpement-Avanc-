package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint de démonstration des paramètres JAX-RS.
 *
 * - QueryParam : GET /api/params?name=Jakub → {"hello": "Jakub"}
 * - PathParam : GET /api/params/42 → {"id": 42}
 */
@Path("/params")
@Produces(MediaType.APPLICATION_JSON)
public class ParamsResource {

    /**
     * Exemple de @QueryParam.
     * Usage : GET /api/params?name=Jakub
     */
    @GET
    public Response withQueryParam(@QueryParam("name") String name) {
        Map<String, String> result = new HashMap<>();
        result.put("hello", name != null ? name : "world");
        return Response.ok(result).build();
    }

    /**
     * Exemple de @PathParam.
     * Usage : GET /api/params/42
     */
    @GET
    @Path("/{id}")
    public Response withPathParam(@PathParam("id") Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        return Response.ok(result).build();
    }
}
