package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres(){
		List<Genre> genres = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM genre")){
				try(ResultSet resultSet = preStatement.executeQuery()){
					while(resultSet.next()){
						genres.add(new Genre(resultSet.getInt("idgenre"), resultSet.getString("name")));
					}
				}
				preStatement.close();
			}
			connection.close();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		return genres;
	}

	public Genre getGenre(String name) {
		Genre genre = new Genre();
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")){
				preStatement.setString(1, name);
				try(ResultSet result = preStatement.executeQuery()){
					if(result.next()) {
						genre.setId(result.getInt("idgenre"));
						genre.setName(result.getString("name"));
					}
					else {
						genre = null;
					}
				}
				preStatement.close();
			}
			connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return genre;
	}

	public void addGenre(String name) {
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(PreparedStatement preStatement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS)){
				preStatement.setString(1, name);
				preStatement.executeUpdate();
				preStatement.close();
			}
			connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
