import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String json = readString("new_data.json");

        List<Employee> employees = jsonToList(json);

        if (employees != null) {
            employees.forEach(System.out::println);
        }
    }

    public static String readString(String fileName) {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            return json.toString();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return null;
        }
    }

    public static List<Employee> jsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        List<Employee> employees = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        JSONParser parser = new JSONParser();

        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            Type employeeType = new TypeToken<Employee>() {}.getType();

            for (Object obj : jsonArray) {
                Employee employee = gson.fromJson(obj.toString(), employeeType);
                employees.add(employee);
            }
            return employees;
        } catch (ParseException e) {
            System.err.println("Ошибка при парсинге JSON: " + e.getMessage());
            return null;
        }
    }
}