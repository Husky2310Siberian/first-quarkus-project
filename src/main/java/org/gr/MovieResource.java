package org.gr;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Path("/movies")
public class MovieResource {

    public static List<Movie> movies = new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovies() {
        return Response.ok(movies).build();
    }

    @GET
    @Path("/size")
    @Produces(MediaType.TEXT_PLAIN)
    public Integer countMovies() {
        return movies.size();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON )
    public Response createMovie(Movie newMovie) {
        movies.add(newMovie);
        return Response.ok(movies).build();
    }

    @PUT
    @Path("{id}/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMovie(
            @PathParam("id") Long id,
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