package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint de test pour vérifier que JAX-RS fonctionne.
 * Accessible via : GET /api/helloWorld
 */
@Path("/helloWorld")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    @GET
    public Response hello() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Hello World!");
        return Response.ok(result).build();
    }
}
