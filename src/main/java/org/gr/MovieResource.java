package org.gr;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Path("/movies")
@Tag(name = "Movie Resource" , description = "Movie REST APIs")
public class MovieResource {

    public static List<Movie> movies = new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "getMovies",
                summary = "Get movies" ,
                description = "Get all movies from List")

    @APIResponse(responseCode = "200",
                description = "Operation completed",
                content = @Content(mediaType = MediaType.APPLICATION_JSON))
    public Response getMovies() {
        return Response.ok(movies).build();
    }

    @GET
    @Path("/size")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(operationId = "countMovies",
            summary = "Count movies" ,
            description = "Count all movies from List")

    @APIResponse(responseCode = "200",
            description = "Operation completed",
            content = @Content(mediaType = MediaType.TEXT_PLAIN))
    public Integer countMovies() {
        return movies.size();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON )
    @Operation(operationId = "createMovies",
            summary = "Create new movie" ,
            description = "Create a new movie and add in List")

    @APIResponse(responseCode = "201",
            description = "Movie created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))

    public Response createMovie(@RequestBody(description = "movie to create",
                                required = true ,
                                content = @Content(schema = @Schema(implementation = Movie.class))) Movie newMovie) {
        movies.add(newMovie);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{id}/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "updateMovies",
            summary = "Update exist movie" ,
            description = "Update  movie and add in List")

    @APIResponse(responseCode = "200",
            description = "Movie updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    public Response updateMovie(
            @Parameter(
                    description = "Movie id ",
                    required = true
            )
            @PathParam("id") Long id,
            @Parameter(
                    description = "Movie title ",
                    required = true
            )
            @PathParam("title") String title
    ) {
        if (movies == null || movies.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Movies list is empty").build();
        }
        AtomicBoolean movieUpdated = new AtomicBoolean(false);

        movies = movies.stream().map(movie -> {
            if (movie.getId().equals(id)) {
                movie.setTitle(title);
                movieUpdated.set(true);
            }
            return movie;
        }).collect(Collectors.toList());

        if (!movieUpdated.get()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Movie with ID " + id + " not found").build();
        }

        return Response.ok(movies).build();
    }


    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "deleteMovies",
            summary = "Delete exist movie" ,
            description = "Delete  movie from a List")
    @APIResponse(responseCode = "204",
            description = "Movie deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "400",
            description = "Movie not valid",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response deleteMovie(@PathParam("id") Long id){
    Optional <Movie> movieToDelete =  movies.stream().filter(movie -> movie.getId().equals(id)).findFirst();

    boolean isRemoved = false;
    if(movieToDelete.isPresent()){
            isRemoved = movies.remove(movieToDelete.get());
        }
    if (isRemoved) {
        return Response.noContent().build();
    }
    return Response.status(Response.Status.BAD_REQUEST).build();
    }
}