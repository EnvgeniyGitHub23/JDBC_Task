package ru.evgeny;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReadXML {

    public static Map<Long, Organization> readFIle(Set<Long> ogrnListFromDB) {

        Map<Long, Organization> result = new HashMap<>();

        // читаем файл
        try {
            File file = new File("organizations_sample_data.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Организация");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long ogrnLong = Long.parseLong(element.getElementsByTagName("ОГРН").item(0).getTextContent()); //тут нужен TRY CATCH

                    //если такого ОГРН в БД нет, то добавляем организацию в Map
                    if (!ogrnListFromDB.contains(ogrnLong)) {

                        Organization organization = new Organization();

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
                            e.printStackTrace();
                        }
                        result.put(ogrnLong, organization);
                    }
                }
            }

            //проверка
            System.out.println("Из XML прочитано организаций: " + nodeList.getLength());
            System.out.println("Будет загружено в БД: " + result.size());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }//void read XML

}









