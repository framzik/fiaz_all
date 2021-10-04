package ru.mycrg;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class Writer {

    private static final Logger log = Logger.getLogger(Writer.class);

    private DataSource dataSource;

    @Autowired
    private ApplicationContext ctx;

    public Writer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTables() throws SQLException {
        log.info("Start creating tables");
        System.out.println("Start creating tables");

        try (final Connection connection = dataSource.getConnection()) {
            EncodedResource resource = new EncodedResource(new FileSystemResource(
                    "src/main/resources/init_all_tables.sql"));
            ScriptUtils.executeSqlScript(connection, resource);

            log.info("Creating tables complete");
            System.out.println("Creating tables complete");
        } catch (Exception e) {
            log.error("Error, tables do not created: " + e.getMessage());
            System.out.println("Error, tables do not created: " + e.getMessage());
        }
    }

    public void writeValue(Map<String, List<String>> infos, File xmlFile) {
        log.info(String.format("Start writing infos from %s", xmlFile.getName()));
        System.out.println(String.format("Start writing infos from %s", xmlFile.getName()));

        int i = 0;
        int sizeInfo = 0;
        String queryUpdate = "";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            for (Map.Entry<String, List<String>> info: infos.entrySet()) {
                sizeInfo = info.getValue().size();
                String tableName = info.getKey();
                truncateDb(tableName);

                List<String> queries = info.getValue();
                for (String query: queries) {
                    if (i % 1000 == 0) {
                        log.info(String.format("Processing....was wrote %s raws of %s", i, sizeInfo));
                        System.out.println(String.format("Processing....was wrote %s raws of %s", i, sizeInfo));
                    }

                    queryUpdate = query;
                    stmt.executeUpdate(queryUpdate);
                    i++;
                }
            }
        } catch (SQLException e) {
            log.error(String.format("Не удалось записать в БД, sql:[%s],error: %s", queryUpdate, e.getMessage()));
            System.out.println(
                    String.format("Не удалось записать в БД, sql:[%s],error: %s", queryUpdate, e.getMessage()));
        }
        log.info(String.format("Was wrote %s raws of %s", i, sizeInfo));
        System.out.println(String.format("Was wrote %s raws of %s", i, sizeInfo));
    }

    public void truncateDb(String tableName) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            log.info(String.format("Таблица %s будет очищена", tableName));
            System.out.println(String.format("Таблица %s будет очищена", tableName));

            stmt.execute(String.format("truncate %s;", tableName));

            log.info(String.format("Таблица %s очищена", tableName));
            System.out.println(String.format("Таблица %s очищена", tableName));
        } catch (SQLException e) {
            log.error(String.format("Не удалось очистить таблицу %s: %s", tableName, e.getMessage()));
        }
    }
}
