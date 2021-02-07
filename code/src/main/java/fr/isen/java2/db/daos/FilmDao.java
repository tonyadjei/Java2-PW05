package fr.isen.java2.db.daos;

import java.util.List;


import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FilmDao {

	public List<Film> listFilms() {
		List<Film> listOfFilm = new ArrayList<>();
	    try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
	        try (Statement preStatement = connection.createStatement()) {
	            try (ResultSet results = preStatement.executeQuery("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")) {
	                while (results.next()) {
	                	listOfFilm.add(new Film(results.getInt("idfilm"),
	                            results.getString("title"),
	                            results.getDate("release_date").toLocalDate(),
	                            new Genre(results.getInt("genre_id"),results.getString("name")),
	                            results.getInt("duration"),
	                            results.getString("director"),
	                            results.getString("summary")));
	                }	
	            }
	            preStatement.close();
	           
	        }
	        connection.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return listOfFilm;
	}

	public List<Film> listFilmsByGenre(String genreName) {
		List<Film> listOfFilm = new ArrayList<>();
		 try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		        try (PreparedStatement preStatement = connection.prepareStatement(
		                    "SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name =?")) {
		            preStatement.setString(1, genreName);
		            try (ResultSet results = preStatement.executeQuery()) {
		                while(results.next()) {
		                    listOfFilm.add(new Film(results.getInt("idfilm"),
	                            results.getString("title"),
	                            results.getDate("release_date").toLocalDate(),
	                            new Genre(results.getInt("genre_id"),results.getString("name")),
	                            results.getInt("duration"),
	                            results.getString("director"),
	                            results.getString("summary")));
		                }
		            }
		            preStatement.close();
		        }
		        connection.close();
		    } catch (SQLException e) {
		        // Manage Exception
		        e.printStackTrace();
		    }
		    return listOfFilm;
	}

	public Film addFilm(Film film) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
	        String sqlQuery = "INSERT INTO film(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
	        try (PreparedStatement statement = connection.prepareStatement(
	                        sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
	            statement.setString(1, film.getTitle());
	            statement.setDate(2,Date.valueOf(film.getReleaseDate()));
	            statement.setInt(3,film.getGenre().getId() );
	            statement.setInt(4, film.getDuration());
	            statement.setString(5,film.getDirector());
	            statement.setString(6, film.getSummary());
	            statement.executeUpdate();
	            ResultSet ids = statement.getGeneratedKeys();
	            if (ids.next()) {
	                return new Film(ids.getInt(1),film.getTitle(),film.getReleaseDate(),film.getGenre(),film.getDuration(),film.getDirector(),
	                		film.getSummary());
	            }
	           
	            statement.close();
	        }
	        connection.close();
	    }catch (SQLException e) {
	        // Manage Exception
	        e.printStackTrace();
	    }
		return null;
	}

}

