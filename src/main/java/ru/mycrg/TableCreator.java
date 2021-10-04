package ru.mycrg;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TableCreator {

    private static final Logger log = Logger.getLogger(TableCreator.class);

    private final DataSource dataSource;

    public TableCreator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createSchemas(List<File> schemas) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            log.info(String.format("Попытка создания схему:"));
            System.out.println(String.format("Попытка создания схемы:"));

            stmt.execute(String.format("truncate %s;", "TABLE"));

        } catch (SQLException e) {
            log.error(String.format("Не удалось создать схему: %s", e.getMessage()));
        }
    }

//    CREATE TABLE IF NOT EXISTS public.fias_address
//(
//    id            bigserial NOT NULL,
//    objectid      bigint    NOT NULL,
//    name          character varying(250),
//    typename      character varying(50),
//    level         character varying(250),
//    oktmo         character varying(11),
//    objectguid    character varying(36),
//    isactive      boolean,
//    parentobjid   bigint,
//    apart_number  character varying(50),
//    aparttype     integer,
//    housenum      character varying(50),
//    addnum1       character varying(50),
//    addnum2       character varying(50),
//    housetype     integer,
//    addtype1      integer,
//    addtype2      integer,
//    steads_number character varying(250)
//)
//    TABLESPACE pg_default;
//
//ALTER TABLE public.fias_address
//    OWNER to fiz;
}
