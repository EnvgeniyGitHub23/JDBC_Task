package ru.evgeny;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;


public class CreateTestXML {
    public static void createSampleFile(int countOfOrganizations) { //метод для создания тестового XML файла с данными

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("organizations");
            doc.appendChild(root);

            for (int i = 1; i <= countOfOrganizations; i++) {
                Element organization = doc.createElement("Организация");

                Element ogrn = doc.createElement("ОГРН");
                Text ogrnText = doc.createTextNode(String.valueOf(1000000000000L + i));
                ogrn.appendChild(ogrnText);

                Element inn = doc.createElement("ИНН");
                Text innText = doc.createTextNode(String.valueOf(100000000000L + i));
                inn.appendChild(innText);

                Element name = doc.createElement("Название");
                Text nameText = doc.createTextNode("ООО \"Фирма_" + i + "\"");
                name.appendChild(nameText);

                Element address  = doc.createElement("Адрес");
                int buildingInRange1To100 = ((i - 1) % 100) + 1;
                int officeInRange1To600 = ((i - 1) % 600) + 1;
                Text addressText = doc.createTextNode("Город, улица Улица, д." + buildingInRange1To100 + " офис:" + officeInRange1To600 );
                address.appendChild(addressText);

                Element director = doc.createElement("Директор");
                char letter = (char) ('А' + ((i - 1) % 25));
                Text directorText = doc.createTextNode("Иванов " + letter + "." + (++letter) + ".");
                director.appendChild(directorText);

                Element capital = doc.createElement("Уставной_капитал");
                Text capitalText = doc.createTextNode(String.valueOf(10000 + i));
                capital.appendChild(capitalText);

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

            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes"); //вкл. форматирование данных в файле

            File xmlFile = new File("organizations_sample_data.xml");
            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String generateRandomDate() { // случайные даты с 01.01.2000 по наст. время

        Calendar startCalendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Calendar endCalendar = Calendar.getInstance();

        long startTimeInMillis = startCalendar.getTimeInMillis();
        long endTimeInMillis = endCalendar.getTimeInMillis();

        Random random = new Random();
        long randomTimeInMillis = startTimeInMillis + (long) (random.nextDouble() * (endTimeInMillis - startTimeInMillis));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormat.format(randomTimeInMillis);

        return formattedDate;
    }
}


