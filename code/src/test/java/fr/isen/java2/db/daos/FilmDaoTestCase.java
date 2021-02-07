package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDaoTestCase {
	FilmDao myFilmDao = new FilmDao();
	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS film (\r\n"
				+ "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM film");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListFilms() {
		// WHEN
		 List<Film> films = myFilmDao.listFilms();
		// THEN
		assertThat(films).hasSize(3);
		assertThat(films).extracting("id","title", "releaseDate", "genre.id", "duration", "director", "summary").containsOnly(
					tuple(1, "Title 1", LocalDate.parse("2015-11-26"),1, 120, "director 1", "summary of the first film"), 
					tuple(2, "My Title 2",  LocalDate.parse("2015-11-14"),2, 114, "director 2", "summary of the second film"),
	 				tuple(3, "Third title", LocalDate.parse("2015-12-12"), 2,176, "director 3", "summary of the third film"));
		

	 }
	
	 @Test
	 public void shouldListFilmsByGenre() {
		// WHEN
		List<Film> films = myFilmDao.listFilmsByGenre("Comedy");
		// THEN
		assertThat(films).hasSize(2);
		assertThat(films).extracting("id","title", "releaseDate", "genre.id", "duration", "director", "summary").containsOnly(
				tuple(2, "My Title 2",  LocalDate.parse("2015-11-14"),2, 114, "director 2", "summary of the second film"),
				tuple(3, "Third title", LocalDate.parse("2015-12-12"), 2,176, "director 3", "summary of the third film"));
	 }
	
	 @Test
	 public void shouldAddFilm() throws Exception {
		// WHEN 
		Film film = new Film(3,"The Big Walk",LocalDate.parse("2021-12-15"), new Genre(1,"Comedy"), 300,"Shaun Pelling","Dancing in the sun");
		myFilmDao.addFilm(film);
		// THEN
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM film WHERE title='The Big Walk'");
		assertThat(resultSet.next()).isTrue();
		assertThat(resultSet.getInt("idfilm")).isNotNull();
		assertThat(resultSet.getString("title")).isEqualTo("The Big Walk");
		assertThat(resultSet.getDate("release_date")).isEqualTo(Date.valueOf("2021-12-15"));
		assertThat(resultSet.getInt("genre_id")).isEqualTo(1);
		assertThat(resultSet.getInt("duration")).isEqualTo(300);
		assertThat(resultSet.getString("director")).isEqualTo("Shaun Pelling");
		assertThat(resultSet.getString("summary")).isEqualTo("Dancing in the sun");
		assertThat(resultSet.next()).isFalse();
		assertThat(resultSet.next()).isFalse();
		resultSet.close();
		statement.close();
		connection.close();
	}
}
