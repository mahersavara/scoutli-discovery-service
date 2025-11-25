package com.scoutli.api.controller;

import com.scoutli.api.dto.DiscoveryDTO;
import com.scoutli.service.DiscoveryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

@Path("/api/discoveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiscoveryController {

    @Inject
    DiscoveryService discoveryService;

    @GET
    public List<DiscoveryDTO> list() {
        return discoveryService.getAllDiscoveries();
    }

    @POST
    @RolesAllowed({ "MEMBER", "ADMIN" })
    public Response create(DiscoveryDTO.CreateRequest request, @Context SecurityContext securityContext) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        String email = userPrincipal.getName();
        DiscoveryDTO created = discoveryService.createDiscovery(request, email);
        return Response.status(201).entity(created).build();
    }
}
