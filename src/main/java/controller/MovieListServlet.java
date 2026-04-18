package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.MovieDao;
import models.Movie;
import java.io.IOException;
import java.util.List;
import dsa.Stack;

@WebServlet("/movie-list")
public class MovieListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MovieListServlet: Starting to fetch movies...");
        
        try {
            List<Movie> movies;
            String sortBy = request.getParameter("sort");
            
            if ("rating_asc".equals(sortBy)) {
                movies = MovieDao.getMoviesSortedByRating(true);
                System.out.println("Sorting movies by rating (ascending)");
            } else if ("rating_desc".equals(sortBy)) {
                movies = MovieDao.getMoviesSortedByRating(false);
                System.out.println("Sorting movies by rating (descending)");
            } else {
                movies = MovieDao.getAllMovies();
                System.out.println("Using default movie order");
            }
            
            System.out.println("MovieListServlet: Found " + movies.size() + " movies");
            
            // Debug: Print each movie's details with their ratings
            for (Movie movie : movies) {
                double avgRating = MovieDao.getAverageRating(movie.getId());
                System.out.println("Movie: " + movie.getTitle() + 
                                 ", ID: " + movie.getId() + 
                                 ", Genre: " + movie.getGenre() + 
                                 ", Price: " + movie.getPrice() +
                                 ", Average Rating: " + avgRating);
            }
            
            // Set movies list in request attribute
            request.setAttribute("movies", movies);
            
            // Forward to movie-list.jsp
            request.getRequestDispatcher("movie-list.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("MovieListServlet: Error occurred - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading movies: " + e.getMessage());
            request.getRequestDispatcher("movie-list.jsp").forward(request, response);
        }
    }
} 