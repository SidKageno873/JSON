import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        // Парсинг CSV
        List<Employee> list = parseCSV(columnMapping, fileName);

        // Конвертация в JSON
        String json = listToJson(list);

        // Запись JSON в файл
        writeString(json, "data.json");

        List<Employee> xmlList = parseXML("data.xml");
        String xmlJson = listToJson(xmlList);
        writeString(xmlJson, "data2.json");
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении CSV файла: " + e.getMessage());
            return null;
        }
    }

    private static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            System.out.println("JSON успешно записан в файл " + fileName);
        } catch (IOException e) {
            System.err.println("Ошибка при записи JSON файла: " + e.getMessage());
        }
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Employee employee = new Employee(
                            Long.parseLong(getTagValue("id", element)),
                            getTagValue("firstName", element),
                            getTagValue("lastName", element),
                            getTagValue("country", element),
                            Integer.parseInt(getTagValue("age", element))
                    );
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге XML: " + e.getMessage());
        }
        return employees;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}