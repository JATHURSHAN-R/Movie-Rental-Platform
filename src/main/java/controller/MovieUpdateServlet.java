package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.MovieDao;
import models.Movie;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/update-movie")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class MovieUpdateServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            request.setAttribute("error", "Only admins can update movies.");
            request.getRequestDispatcher("manage-movies.jsp").forward(request, response);
            return;
        }

        String movieId = request.getParameter("movieId");
        String title = request.getParameter("title");
        String genre = request.getParameter("genre");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");

        if (movieId == null || title == null || title.trim().isEmpty() ||
            genre == null || genre.trim().isEmpty() || priceStr == null || priceStr.trim().isEmpty()) {
            request.setAttribute("error", "Movie ID, title, genre, and price are required.");
            request.getRequestDispatcher("manage-movies.jsp").forward(request, response);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException("Price cannot be negative");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price format.");
            request.getRequestDispatcher("manage-movies.jsp").forward(request, response);
            return;
        }

        // Get existing movie to preserve image if no new image uploaded
        Movie existingMovie = MovieDao.getMovieById(movieId);
        String imageFileName = existingMovie != null ? existingMovie.getImageFileName() : null;

        // Check if a new image was uploaded
        Part filePart = request.getPart("movieImage");
        if (filePart != null && filePart.getSize() > 0) {
            String submittedFileName = filePart.getSubmittedFileName();
            String fileExtension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
            imageFileName = UUID.randomUUID().toString() + fileExtension;

            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            String filePath = uploadPath + File.separator + imageFileName;
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(filePath));
            }
        }

        try {
            MovieDao.updateMovie(movieId, title, genre, description, imageFileName, price);
            request.setAttribute("success", "Movie updated successfully!");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to update movie: " + e.getMessage());
        }

        response.sendRedirect("add-movie");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String movieId = request.getParameter("movieId");
        if (movieId == null || movieId.trim().isEmpty()) {
            response.sendRedirect("add-movie");
            return;
        }
        Movie movie = MovieDao.getMovieById(movieId);
        if (movie == null) {
            response.sendRedirect("add-movie");
            return;
        }
        request.setAttribute("movie", movie);
        request.getRequestDispatcher("edit-movie.jsp").forward(request, response);
    }
}
