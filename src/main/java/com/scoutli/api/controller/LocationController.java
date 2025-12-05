package com.scoutli.api.controller;

import com.scoutli.domain.entity.Location;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationController {

    @GET
    public List<Location> getAll() {
        return Location.listAll();
    }

    @GET
    @Path("/{id}")
    public Location getById(@PathParam("id") Long id) {
        return Location.findById(id);
    }

    @POST
    @Transactional
    public Response create(Location location) {
        location.persist();
        return Response.status(Response.Status.CREATED).entity(location).build();
    }
}
