package com.NamePending;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameDatabase {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private String databaseFile;

	public void writeDataBase(String name, int score) throws Exception {
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
	
	public void readDataBase() throws Exception {
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
			resultSet = statement
					.executeQuery("select * from highscores");
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
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String name = resultSet.getString("Name");
			Integer score = resultSet.getInt("Score");
			System.out.printf("Name: %s   Score: %d\n", name, score);
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