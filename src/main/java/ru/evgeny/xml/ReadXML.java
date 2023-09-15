package ru.evgeny.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.evgeny.entity.Organization;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReadXML {

    //метод читает уникальные значение из файла и возвразает коллекцию организаций, которых еще нет в БД
    public static Set<Organization> readFIle(Set<Long> ogrnSetFromDB) {

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
                    try {
                         ogrnLong = Long.parseLong(element.getElementsByTagName("ОГРН").item(0).getTextContent());
                    } catch (NumberFormatException e){
                        System.out.println("ОШИБКА парсинга ОГРН из файла в Long!");
                        e.printStackTrace();
                    }

                    //если такого ОГРН в БД нет, то добавляем организацию
                    if (!ogrnSetFromDB.contains(ogrnLong)) {

                        Organization organization = new Organization();

                        organization.setOgrn(Long.parseLong(element.getElementsByTagName("ОГРН").item(0).getTextContent())); //нужна проверка try catch
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

}









