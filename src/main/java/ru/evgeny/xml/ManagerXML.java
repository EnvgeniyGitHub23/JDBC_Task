package ru.evgeny.xml;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import ru.evgeny.entity.Organization;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/*
  Класс для работы с XML файлами.
  Содержит:
   - метод createSampleFile для создания файла с тестовыми данными;
   - метод readFIle для чтения данных об организациях из файла.
   Метод принимает список ОГРН, которые уже есть в базе, читает из файла уникальные (по ОГРН) записи организаций,
   и добавляет в результирующую коллекцию Set только те записи, которых нет в БД.
*/

public class ManagerXML {


    //метод читает уникальные значение из файла и возвразает коллекцию организаций, которых еще нет в БД
    public Set<Organization> readFIle(Set<Long> ogrnSetFromDB) {

        Set<Organization> result = new HashSet<>();

        // читаем файл
        try {
            // чтение из текущей папки файла organizations_sample_data.xml
            File file = new File("organizations_sample_data.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Организация");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            //перебираем организации
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    //читаем ОРГН организации из файла. Если такого нет в коллекции ОГРН из БД, то добавляем в результирующую коллекцию
                    long ogrnLong=0;
                    try { //проверка парсинга
                        ogrnLong = Long.parseLong(element.getElementsByTagName("ОГРН").item(0).getTextContent());
                    } catch (NumberFormatException e){
                        System.out.println("ОШИБКА парсинга ОГРН из файла в Long!");
                        e.printStackTrace();
                    }

                    //если такого ОГРН в БД нет, то добавляем организацию
                    if (!ogrnSetFromDB.contains(ogrnLong)) {

                        Organization organization = new Organization();

                        organization.setOgrn(ogrnLong); // берем ранее порлученное значение Long
                        organization.setInn(element.getElementsByTagName("ИНН").item(0).getTextContent());
                        organization.setName(element.getElementsByTagName("Название").item(0).getTextContent());
                        organization.setAddress(element.getElementsByTagName("Адрес").item(0).getTextContent());
                        organization.setDirector(element.getElementsByTagName("Директор").item(0).getTextContent());
                        organization.setCapital(Integer.parseInt(element.getElementsByTagName("Уставной_капитал").item(0).getTextContent()));

                        String dateStr = element.getElementsByTagName("Дата_регистрации").item(0).getTextContent();
                        try {
                            Date registrationDate = dateFormat.parse(dateStr);
                            organization.setDate(registrationDate);
                        } catch (ParseException e) {
                            System.out.println("ОШИБКА парсинга даты из файла!");
                            e.printStackTrace();
                        }
                        result.add(organization);
                    }
                }
            }

            //вывод информации
            System.out.println("Из XML прочитано организаций: " + nodeList.getLength());
            System.out.println("Будет загружено в БД: " + result.size());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("ОШИБКА чтения из файла!");
            e.printStackTrace();
        }

        return result;
    }//void read XML




    public static void createSampleFile(int countOfOrganizations) { //метод для создания тестового XML файла с данными

        try { //подготовка XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("organizations");
            doc.appendChild(root);

            //создание организаций
            for (int i = 1; i <= countOfOrganizations; i++) {
                Element organization = doc.createElement("Организация");

                //ОГРН 13 знаков, получаем сложением 1000000000000L + i
                Element ogrn = doc.createElement("ОГРН");
                Text ogrnText = doc.createTextNode(String.valueOf(1000000000000L + i));
                ogrn.appendChild(ogrnText);

                //ИНН 12 знаков, получаем сложением 100000000000L + i
                Element inn = doc.createElement("ИНН");
                Text innText = doc.createTextNode(String.valueOf(100000000000L + i));
                inn.appendChild(innText);

                //название организации ООО "Фирма_N"
                Element name = doc.createElement("Название");
                Text nameText = doc.createTextNode("ООО \"Фирма_" + i + "\"");
                name.appendChild(nameText);

                //адрес организации, № дома от 1 до 100, офис от 1 до 600
                Element address  = doc.createElement("Адрес");
                int buildingInRange1To100 = ((i - 1) % 100) + 1;
                int officeInRange1To600 = ((i - 1) % 600) + 1;
                Text addressText = doc.createTextNode("Город, улица Улица, д." + buildingInRange1To100 + " офис:" + officeInRange1To600 );
                address.appendChild(addressText);

                //директор: Иванов с переменными инициалами A-Ш
                Element director = doc.createElement("Директор");
                char letter = (char) ('А' + ((i - 1) % 24));
                Text directorText = doc.createTextNode("Иванов " + letter + "." + (++letter) + ".");
                director.appendChild(directorText);

                //уставной капитал
                Element capital = doc.createElement("Уставной_капитал");
                Text capitalText = doc.createTextNode(String.valueOf(10000 + i));
                capital.appendChild(capitalText);

                //дата регистрации (случайная с 01.01.2000 по наст.время)
                Element date = doc.createElement("Дата_регистрации");
                Text dateText = doc.createTextNode(generateRandomDate());
                date.appendChild(dateText);

                organization.appendChild(ogrn);
                organization.appendChild(inn);
                organization.appendChild(name);
                organization.appendChild(address);
                organization.appendChild(director);
                organization.appendChild(capital);
                organization.appendChild(date);

                root.appendChild(organization);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes"); //вкл. форматирование данных в файле (чтобы не в одну строку)

            //создание в текущей папке файла: organizations_sample_data.xml
            File xmlFile = new File("organizations_sample_data.xml");
            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String generateRandomDate() { // метод генерирует случайные даты с 01.01.2000 по наст. время

        Calendar startCalendar = new GregorianCalendar(2000, Calendar.JANUARY, 1); //начальная дата
        Calendar endCalendar = Calendar.getInstance(); //получаем текущую дату

        long startTimeInMillis = startCalendar.getTimeInMillis(); //начальное значание в миллисекундах
        long endTimeInMillis = endCalendar.getTimeInMillis();     //конечное значание в миллисекундах

        //случайное значение в миллисекундах из диапазона с 01.01.2000 по наст. время
        Random random = new Random();
        long randomTimeInMillis = startTimeInMillis + (long) (random.nextDouble() * (endTimeInMillis - startTimeInMillis));

        //форматирование даты в "dd.MM.yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormat.format(randomTimeInMillis);

        return formattedDate;
    }
}


