package ru.mycrg;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, SQLException {
//        String folderPath = args[0];
        String folderPath = "/home/framzik/Загрузки/фиас/91";
        XmlParser xmlParser = new XmlParser();
        DataSourceDb dataSource = new DataSourceDb();
        Writer writer = new Writer(dataSource.getDataSource());
        File xmlFile = new File(folderPath);

        if (xmlFile.isDirectory()) {
            if (xmlFile.listFiles() != null && xmlFile.listFiles().length != 0) {
                writer.createTables();

                List<File> fileNames = Arrays.asList(xmlFile.listFiles());

                fileNames.forEach(file -> {
                    Optional<Map<String, List<String>>> oParse = xmlParser.parseData(file);
                    oParse.ifPresent(info -> {
                        writer.writeValue(info, file);
                    });
                });
            }
        } else {
            System.out.println("переданный аргумент не является директорией.");
        }
    }
}
