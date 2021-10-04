package ru.mycrg;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class Writer {

    private static final Logger log = Logger.getLogger(Writer.class);

    private DataSource dataSource;

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

    public void writeValue(Map<String, List<String>> infos) {
        String queryUpdate = "";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            for (Map.Entry<String, List<String>> info: infos.entrySet()) {

                List<String> queries = info.getValue();
                for (String query: queries) {
                    queryUpdate = query;
                    stmt.executeUpdate(queryUpdate);
                }
            }
        } catch (SQLException e) {
            log.error(String.format("Не удалось записать в БД, sql:[%s],error: %s", queryUpdate, e.getMessage()));
            System.out.println(
                    String.format("Не удалось записать в БД, sql:[%s],error: %s", queryUpdate, e.getMessage()));
        }
    }
}
