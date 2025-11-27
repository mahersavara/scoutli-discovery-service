package com.scoutli.client;

import com.scoutli.api.dto.UserDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import io.smallrye.mutiny.Uni;

@RegisterRestClient(configKey="auth-service-api")
@Path("/api/users")
public interface AuthServiceRestClient {

    @GET
    @Path("/me")
    Uni<UserDTO> getMyUserDetails();
}
