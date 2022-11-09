package org.example.log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientLog {

    List<String[]> logList = new ArrayList<>();
    // Создадим лог в полях, т.к к нему нужен доступ из нескольких методов

    public ClientLog () {
        logList.add(new String[] {"productNum", "amount"});
    }

    public void log (int productNum, int amount) {
        logList.add(new String[]{String.valueOf(productNum), String.valueOf(amount)});
        // Берем ArrayList, т.к. с добавлением в конец списка у него проблем (почти) нет
    }

    // Отсебятина, чтобы логировать все вводы, а не только когда 2 инта
    public void logString (String[] parts) {
        logList.add(parts);
    }

    public void exportAsCSV (File textFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(textFile))) {
            for (String[] row : logList) {
                writer.writeNext(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printLog () {
        for (String[] row : logList) {
            System.out.println(Arrays.toString(row));
        }
    }
}
