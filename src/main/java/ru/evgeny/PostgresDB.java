package ru.evgeny;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class PostgresDB {

    private  String user;
    private  String password;

    private Connection connect() {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl, user, password);
            if (connection != null) {
                System.out.println("Соединение с БД установлено");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка соединения с БД");
            e.printStackTrace();
        }
        return connection;
    }

    public void createNewSchemaAndTable() throws SQLException {

        Connection connection = connect();

        //создаем новую схему
        Statement statement = connection.createStatement();
        String createSchemaSQL = "CREATE SCHEMA MetaPrimeTask AUTHORIZATION " + user;
        statement.executeUpdate(createSchemaSQL);
        System.out.println("новая схема \"MetaPrimeTask\" создана");

        // создаем пустую таблицу organization
        String createTable = "CREATE TABLE MetaPrimeTask.organization (" +
                "ogrn BIGINT PRIMARY KEY," +
                "inn VARCHAR(12)," +
                "name VARCHAR(20)," +
                "address VARCHAR(60)," +
                "director VARCHAR(15)," +
                "capital INT," +
                "date DATE" +
                ")";
        statement.executeUpdate(createTable);
        System.out.println("пустая таблица создана");

        statement.close();
        connection.close();
    }


    public Set<Long> getAllOgrnFromDB() throws SQLException {

        Set<Long> allOgrn = new HashSet<>();
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT ogrn FROM MetaPrimeTask.organization");
        while (results.next()) {
            Long ogrn = results.getLong("ogrn");
            allOgrn.add(ogrn);
        }
        System.out.println("Из БД прочитано: " + allOgrn.size() + "  ОГРН");

        statement.close();
        connection.close();

        return allOgrn;
    }


    public void deleteSchemaAndTable() throws SQLException { //удаляем схему с таблицей

        Connection connection = connect();
        Statement statement = connection.createStatement();
        String dropSchemaSQL = "DROP SCHEMA MetaPrimeTask CASCADE";
        statement.executeUpdate(dropSchemaSQL);
        System.out.println("схема \"MetaPrimeTask\" удалена");

        statement.close();
        connection.close();
    }


    public void deleteDataFromTable() throws SQLException { // удаление всех данных из таблицы organization
        Connection connection = connect();
        Statement statement = connection.createStatement();

        String deleteAllDataSQL = "DELETE FROM MetaPrimeTask.organization";
        int rowsAffected = statement.executeUpdate(deleteAllDataSQL);
        System.out.println("Удалено " + rowsAffected + " записей из таблицы organization");

        statement.close();
        connection.close();
    }


    public void writeToDB(Map<Long, Organization> organizationsToDB) throws SQLException {
        Connection connection = connect();

        String insertQuery = "INSERT INTO MetaPrimeTask.organization (ogrn, inn, name, address, director, capital, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                for (Map.Entry<Long, Organization> entry : organizationsToDB.entrySet()){
                    preparedStatement.setLong(  1, entry.getKey());
                    preparedStatement.setString(2, entry.getValue().getInn());
                    preparedStatement.setString(3, entry.getValue().getName());
                    preparedStatement.setString(4, entry.getValue().getAddress());
                    preparedStatement.setString(5, entry.getValue().getDirector());
                    preparedStatement.setInt(   6, entry.getValue().getCapital());
                    preparedStatement.setDate(  7, new java.sql.Date(entry.getValue().getDate().getTime()));
                    preparedStatement.addBatch();
                }

                int[] insertCounts = preparedStatement.executeBatch();
                System.out.println("В БД загружено: " + insertCounts.length + " записей");

                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection.close();
    }

}





