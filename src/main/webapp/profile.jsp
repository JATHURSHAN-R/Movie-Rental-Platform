<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="dsa.Stack" %>
<%@ page import="models.Movie" %>
<%@ page import="java.util.List" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");

    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Profile - MovieRental</title>
    <link rel="stylesheet" href="assets/styles/profile.css">
    <style>
        .rental-history {
            margin-top: 2rem;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .rental-item {
            background: white;
            padding: 1rem;
            margin-bottom: 1rem;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .rental-item h4 {
            color: #2c3e50;
            margin: 0 0 0.5rem 0;
        }
        
        .rental-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 0.5rem;
        }
        
        .rental-detail {
            display: flex;
            flex-direction: column;
        }
        
        .rental-detail strong {
            color: #666;
            font-size: 0.9rem;
        }
        
        .rental-detail span {
            color: #2c3e50;
            font-size: 1rem;
        }
        
        .status-active {
            color: #27ae60;
            font-weight: bold;
        }
        
        .status-returned {
            color: #7f8c8d;
        }

        /* Recently Watched Movies Styling */
        .recently-watched {
            margin-top: 2rem;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 8px;
        }

        .movie-item {
            background: white;
            padding: 1.5rem;
            margin-bottom: 1rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: transform 0.2s ease;
        }

        .movie-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }

        .movie-item h3 {
            color: #2c3e50;
            margin: 0 0 1rem 0;
            font-size: 1.2rem;
        }

        .movie-item p {
            margin: 0.5rem 0;
            color: #666;
        }

        .movie-item strong {
            color: #2c3e50;
        }
    </style>
</head>
<body>
<div class="profile-container">
    <header class="profile-header">
        <h1>Welcome, ${sessionScope.user.name}</h1>
        <a href="home.jsp" class="back-link">← Back to Home</a>
    </header>

    <div class="profile-info">
        <p><strong>Email:</strong> ${sessionScope.user.email}</p>
        <p><strong>Role:</strong> ${sessionScope.user.role}</p>
    </div>

    <div class="profile-actions">
        <a href="edit-account.jsp" class="btn btn-primary">Edit Account</a>
        <a href="delete-account.jsp" class="btn btn-danger">Delete Account</a>
        <a href="logout" class="btn btn-logout">Logout</a>
    </div>

    <h2>Recently Watched Movies</h2>
    <div class="recently-watched">
        <% 
            Stack<Movie> recentlyWatchedMovies = (Stack<Movie>) session.getAttribute("recentlyWatchedMovies");
            if (recentlyWatchedMovies != null && !recentlyWatchedMovies.isEmpty()) {
                List<Movie> movies = recentlyWatchedMovies.getAllItems();
                // Display movies in reverse order (latest first)
                for (int i = movies.size() - 1; i >= 0; i--) {
                    Movie movie = movies.get(i);
        %>
            <div class="movie-item">
                <h3><%= movie.getTitle() %></h3>
                <p><strong>Genre:</strong> <%= movie.getGenre() %></p>
                <p><strong>Price:</strong> $<%= movie.getPrice() %></p>
            </div>
        <% 
                }
            } else {
        %>
            <p>No recently watched movies.</p>
        <% } %>
    </div>
</div>
</body>
</html>
