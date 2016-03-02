package com.NamePending;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GameDatabase {
	
	public class HighScore {
		private String name;
		private int score;
		
		public HighScore(String n, int s)
		{
			name = n;
			score = s;
		}
		public String getName()
		{
			return name;
		}
		public int getScore()
		{
			return score;
		}
	}
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private String databaseFile;
	public ArrayList<HighScore> highscores = null;

	public void writeOptions(String key, int value) throws Exception {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			databaseFile = current + "\\game.db";

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlite://" +
							   databaseFile);
			// Statements allow to issue SQL queries to the database
			String tmpValue = String.format("%d", value);
			preparedStatement = connect
					.prepareStatement("UPDATE options SET " + key +
							"=" + tmpValue +
							" WHERE " + key + "!=" + tmpValue);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println("Database connection errror");
			throw e;
		} finally {
			close();
		}
	}
	
	public void writeOptions(String key, String value) throws Exception {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			databaseFile = current + "\\game.db";

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlite://" +
							   databaseFile);
			// Statements allow to issue SQL queries to the database
			preparedStatement = connect
					.prepareStatement("UPDATE options SET " + key +
							"=" + value +
							" WHERE " + key + "!=" + value);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println("Database connection errror");
			throw e;
		} finally {
			close();
		}		
	}
	
	public int readOptions(String key, int i) throws Exception {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			databaseFile = current + "\\game.db";

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlite://" +
							   databaseFile);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from options");
			while (resultSet.next()) {
				Integer value = resultSet.getInt(key);
				return value;
			}

		} catch (Exception e) {
			System.out.println("Database connection errror");
			throw e;
		} finally {
			close();
		}		
		return -1; /* make sure -1 is always and error */
	}
	
	public String readOptions(String key) throws Exception {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			databaseFile = current + "\\game.db";

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlite://" +
							   databaseFile);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("select * from options");
			while (resultSet.next()) {
				String value = resultSet.getString(key);
				return value;
			}

		} catch (Exception e) {
			System.out.println("Database connection errror");
			throw e;
		} finally {
			close();
		}		
		return ""; /* make sure empty string is always and error */		
	}
	
	public void writeHighScore(String name, int score) throws Exception {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			databaseFile = current + "\\game.db";

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlite://" +
							   databaseFile);

			// Statements allow to issue SQL queries to the database
			String strScore = String.format("%d", score);
			preparedStatement = connect
					.prepareStatement("INSERT INTO highscores VALUES (\"" +
							name + "\", " + strScore + ")");
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println("Database connection errror");
			throw e;
		} finally {
			close();
		}		
	}
	
	public void readHighScore() throws Exception {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			databaseFile = current + "\\game.db";

			System.out.printf("readDataBase: %s\n", databaseFile);
			
			// This will load the MySQL driver, each DB has its own driver
			//Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlite://" +
							   databaseFile);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			// getting highscores in descending order (highest score first)
			resultSet = statement
					.executeQuery("SELECT * FROM highscores ORDER BY Score DESC");
			writeResultSet(resultSet);
		} catch (Exception e) {
			System.out.println("Database connection errror");
			throw e;
		} finally {
			close();
		}
	}

	private void writeMetaData(ResultSet resultSet) throws SQLException {
		//   Now get some metadata from the database
		// Result set get the result of the SQL query

		System.out.println("The columns in the table are: ");

		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
			System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		if (highscores == null)
			highscores = new ArrayList<HighScore>();
		
		highscores.clear();
		
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String name = resultSet.getString("Name");
			Integer score = resultSet.getInt("Score");
			System.out.printf("Name: %s   Score: %d\n", name, score);
			
			highscores.add(new HighScore(name, score));
		}
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

} 