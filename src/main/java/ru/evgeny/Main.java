package ru.evgeny;

import ru.evgeny.xml.ManagerXML;
import ru.evgeny.service.PostgresDB;
import java.sql.SQLException;

/*
    Главный класс с методом main.
    Принимает на вход параметры для:
    1) создания тестового XML файла;
    2) загрузки файла в БД (требуется логин и пароль для БД). Автоматически создается новая схема и таблица organization;
    3) удаление данных из таблицы organization;
    4) удаление схемы и талицы;
*/


public class Main {
    public static void main(String[] args) throws SQLException {

        String user = "postgres";        // логин БД!!!
        String password = "SQLRootPass";    //пароль БД!!!

          //Аргументы командной строки, только по одной строке за запуск
//        args = new String[]{"createXML", "20"};  // создать тестовый файл с N записей (1-10000)
        args = new String[]{"load", user, password};   // загрузить файл в БД
//        args = new String[]{"deleteData", user, password};  // удалить данные из таблицы
//        args = new String[]{"deleteSchema", user, password}; // удалить таблицу и схему

        //если переданы аргументы
        if (args.length > 0) {

            if (args[0].equals("createXML")) { //генерируем XML файл с тестовыми данными
                if (args.length > 1) {
                    int countOfOrganizations = Integer.parseInt(args[1]); //тут нужна проверка на то, что args[1] это число, в рамках текущей задачи не реализовывал
                    if (countOfOrganizations < 1 || countOfOrganizations > 10000) {
                        countOfOrganizations = 10;
                    }
                    ManagerXML.createSampleFile(countOfOrganizations);
                    System.out.println("создан файл с " + countOfOrganizations + " записями");
                } else {
                    System.out.println("создан файл с 10 записями"); //если не указать количество записей в файле, то генерируем 10
                    ManagerXML.createSampleFile(10);
                }
            }  else


            if (args[0].equals("deleteSchema")) { //удаляем схему и таблицу
                checkLoginToDBInput(args); //проверка, что переданы логин и пароль для БД
                new PostgresDB(args[1], args[2]).deleteSchemaAndTable();
            } else


            if (args[0].equals("deleteData")) { //удаляем только данные из таблицы
                checkLoginToDBInput(args); //проверка, что переданы логин и пароль для БД
                new PostgresDB(args[1], args[2]).deleteDataFromTable();
            } else


            if (args[0].equals("load")) { // загружаем данные из файла в БД
                checkLoginToDBInput(args); //проверка, что переданы логин и пароль для БД
                new PostgresDB(args[1], args[2]).loadDataToDB(new ManagerXML());
            } else {
                printHint(); //если ничего не подошло то вывод подсказки
            }


       } else printHint();   //если нет аргументов то вывод подсказки

    }//main

    private static void checkLoginToDBInput(String[] args){ //метод проверяет что введены учетные данные для подключения к БД
        if (args.length < 3) {
            System.out.println("вы не ввели учетные данные для подключения к БД");
            System.exit(1);
        }
    }

    private static void printHint(){  //вывод подсказки
        System.out.println("*****************************************************************************");
        System.out.println("Для создания заполненного файла введите createXML N (N от 1 до 10000 записей)");
        System.out.println("Для загрузки в БД сгенерированного файла organizations_sample_data.xml ведите load user password ");
        System.out.println("Для удаления данных из таблицы ведите deleteData user password ");
        System.out.println("Для удаления схемы из БД ведите deleteSchema user password ");
        System.out.println("*****************************************************************************");
    }
}