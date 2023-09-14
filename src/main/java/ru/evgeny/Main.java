package ru.evgeny;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws SQLException {

        String user = "postgres";        // логин БД!!!
        String password = "password";    //пароль БД!!!

          //Аргументы командной строки, только по одной строке за запуск
//        args = new String[]{"createXML", "10000"};  // создать тестовый файл с N записей (1-10000)
//        args = new String[]{"createSchema", user, password}; // создать схему и таблицу
//        args = new String[]{"load", user, password};   // загрузить файл в БД
//        args = new String[]{"deleteData", user, password};  // удалить данные из таблицы
//        args = new String[]{"deleteSchema", user, password}; // удалить таблицу и схему



        if (args.length > 0) {
            if (args[0].equals("createXML")) { //генерируем XML файл с тестовыми данными
                if (args.length > 1) {
                    int countOfOrganizations = Integer.parseInt(args[1]); //тут нужна проверка на то, что args[1] это число, в рамках текущей задачи не реализовывал
                    if (countOfOrganizations < 1 || countOfOrganizations > 10000) {
                        countOfOrganizations = 10;
                    }
                    CreateTestXML.createSampleFile(countOfOrganizations);
                    System.out.println("создан файл с " + countOfOrganizations + " записями");
                } else {
                    System.out.println("создан файл с 10 записями"); //если не указать количество записей в файле, то генерируем 10
                    CreateTestXML.createSampleFile(10);
                }

            } else

            if (args[0].equals("createSchema")) { //создаем схему и пустую таблицу
                checkLoginToDBInput(args);
                new PostgresDB(args[1], args[2]).createNewSchemaAndTable();

            } else
            if (args[0].equals("deleteSchema")) { //удаляем схему и таблицу
                checkLoginToDBInput(args);
                new PostgresDB(args[1], args[2]).deleteSchemaAndTable();
            } else

            if (args[0].equals("deleteData")) { //удаляем только данные из таблицы
                checkLoginToDBInput(args);
                new PostgresDB(args[1], args[2]).deleteDataFromTable();
            } else

            if (args[0].equals("load")) { // загружаем данные из файла в БД
                checkLoginToDBInput(args);

                PostgresDB postgresDB = new PostgresDB(args[1], args[2]);

                // получаем все уникальные ОГРН из БД
                Set<Long> ogrnFromDB = postgresDB.getAllOgrnFromDB();

                // читаем из XML организации, ОГРН которых нет в БД
                Map<Long, Organization> organizationsToDB = ReadXML.readFIle(ogrnFromDB);

                // записываем в БД прочитанную коллекцию организаций Map<Organization>
                postgresDB.writeToDB(organizationsToDB);

            } else {

                printHint();
            }

       } else printHint();

    }//main

    private static void checkLoginToDBInput(String[] args){
        if (args.length < 3) {
            System.out.println("вы не ввели учетные данные для подключения к БД");
            System.exit(1);
        }
    }

    private static void printHint(){
        System.out.println("*****************************************************************************");
        System.out.println("Для создания заполненного файла введите createXML N (N от 1 до 10000 записей)");
        System.out.println("Для создания новой схемы и таблицы введите createSchema user password  (логин и пароль для postgres)");
        System.out.println("Для загрузки в БД сгенерированного файла organizations_sample_data.xml ведите load user password ");
        System.out.println("Для удаления данных из таблицы ведите deleteData user password ");
        System.out.println("Для удаления схемы из БД ведите deleteSchema user password ");
        System.out.println("*****************************************************************************");
    }
}//class