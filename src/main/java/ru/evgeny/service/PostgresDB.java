package ru.evgeny.service;
import lombok.AllArgsConstructor;
import ru.evgeny.entity.Organization;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class PostgresDB {

    private  String user; //данные для подключения к БД устанавливаются в конструкторе при создании экземпляра
    private  String password;

    private Connection connect() { //приватный метод для подключения к БД
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

    public void createNewSchemaAndTable() throws SQLException { //метод для создания схемы и таблицы в БД (если он еще не созданы)
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            // Проверяем существование схемы перед созданием
            ResultSet schemaExistsResult = connection.getMetaData().getSchemas();
            boolean schemaExists = false;
            while (schemaExistsResult.next()) {
                String schemaName = schemaExistsResult.getString("TABLE_SCHEM");
                if ("MetaPrimeTask".equalsIgnoreCase(schemaName)) {
                    schemaExists = true;
                    break;
                }
            }

            if (!schemaExists) { // Создаем новую схему
                String createSchemaSQL = "CREATE SCHEMA MetaPrimeTask AUTHORIZATION " + user;
                statement.executeUpdate(createSchemaSQL);
                System.out.println("Новая схема \"MetaPrimeTask\" создана");
            } else {
                System.out.println("Схема \"MetaPrimeTask\" уже существует");
            }

            // Проверяем существование таблицы
            ResultSet tableExistsResult = connection.getMetaData().getTables(null, "metaprimetask", "organization", null);
            boolean tableExists = tableExistsResult.next();

            if (!tableExists) { // Создаем пустую таблицу organization если ее еще нет в БД
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
                System.out.println("Пустая таблица \"organization\" создана");
            } else {
                System.out.println("Таблица \"organization\" уже существует");
            }
        }
    }



    public Set<Long> getAllOgrnFromDB() throws SQLException { //метод для получения сета ОГРН из БД

        Set<Long> allOgrn = new HashSet<>();

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            //проверяем перед записью существует ли таблица
            ResultSet tableExistsResult = connection.getMetaData().getTables(null, "metaprimetask", "organization", null);
            boolean tableExists = tableExistsResult.next();

            if(tableExists){ // если таблица существует то читаем ОГРН
                ResultSet results = statement.executeQuery("SELECT ogrn FROM MetaPrimeTask.organization");
                while (results.next()) {
                    Long ogrn = results.getLong("ogrn");
                    allOgrn.add(ogrn);
                }
                System.out.println("Из БД прочитано: " + allOgrn.size() + "  ОГРН");
            } else {
                System.out.println("не найдена таблица organization");
                System.exit(1);
            }
            return allOgrn;
        }
    }


    public void deleteSchemaAndTable() throws SQLException { //удаление схемы и таблицы (если они существуют)

        try( Connection connection = connect();
             Statement statement = connection.createStatement()) {

            // Проверяем существование схемы перед удалением
            ResultSet schemaExistsResult = connection.getMetaData().getSchemas();
            boolean schemaExists = false;
            while (schemaExistsResult.next()) {
                String schemaName = schemaExistsResult.getString("TABLE_SCHEM");
                if ("MetaPrimeTask".equalsIgnoreCase(schemaName)) {
                    schemaExists = true;
                    break;
                }
            }
            if (schemaExists){ //если схема существует, то удаляем каскадно
                String dropSchemaSQL = "DROP SCHEMA MetaPrimeTask CASCADE";
                statement.executeUpdate(dropSchemaSQL);
                System.out.println("схема \"MetaPrimeTask\" удалена");
            }else {
                System.out.println("схемы MetaPrimeTask в БД не существует");
            }

        }
    }


    public void deleteDataFromTable() throws SQLException { // удаление всех данных из таблицы organization (если она существует)
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            //проверяем существует ли таблица
            ResultSet tableExistsResult = connection.getMetaData().getTables(null, "metaprimetask", "organization", null);
            boolean tableExists = tableExistsResult.next();

            if(tableExists){ // если таблица существует, то удаляем из нее данные
                String deleteAllDataSQL = "DELETE FROM MetaPrimeTask.organization";
                int rowsAffected = statement.executeUpdate(deleteAllDataSQL);
                System.out.println("Удалено " + rowsAffected + " записей из таблицы organization");
            } else {
                System.out.println("таблица organization не найдена");
            }
        }
    }


    public void writeToDB(Set<Organization> organizationsToDB) throws SQLException { //запись коллекции организаций Set в БД
        // Передаваемые данные в Set уже обработаны и не должны содержать ОГРН, имеющиеся в БД!

        try (Connection connection = connect()) {
            // проверяем перед записью существует ли таблица
            ResultSet tableExistsResult = connection.getMetaData().getTables(null, "metaprimetask", "organization", null);
            boolean tableExists = tableExistsResult.next();

            if (!tableExists) { //если таблица НЕ существует, то сначала создаем ее
                createNewSchemaAndTable();
                System.out.println("создана таблица organization");
            }

                String insertQuery = "INSERT INTO MetaPrimeTask.organization (ogrn, inn, name, address, director, capital, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

                //подготовка данных для записи в БД
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    for (Organization org : organizationsToDB) {
                        preparedStatement.setLong(1, org.getOgrn());
                        preparedStatement.setString(2, org.getInn());
                        preparedStatement.setString(3, org.getName());
                        preparedStatement.setString(4, org.getAddress());
                        preparedStatement.setString(5, org.getDirector());
                        preparedStatement.setInt(6, org.getCapital());
                        preparedStatement.setDate(7, new java.sql.Date(org.getDate().getTime()));
                        preparedStatement.addBatch();
                    }
                    //выполнение запроса на загрузку в БД
                    int[] insertCounts = preparedStatement.executeBatch();
                    System.out.println("В БД загружено: " + insertCounts.length + " записей");
                }
                System.out.println("не найдена таблица organization, данные не будут загружены в БД");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}





