package ru.mycrg;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser {

    private final Logger log = Logger.getLogger(XmlParser.class);

    private final DocumentBuilder documentBuilder;

    public XmlParser() throws ParserConfigurationException {
        this.documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
    }

    public void parseAndWriteData(File xmlFile, Writer writer) {
        log.info(String.format("Start parsing info from: %s", xmlFile.getName()));
        System.out.println(String.format("Start parsing info from: %s", xmlFile.getName()));

        try (InputStream inputStream = new FileInputStream(xmlFile) {
        }) {
            Document doc = documentBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("*");

            parseAndWriteData(nodeList, xmlFile.getName(), writer);
        } catch (IOException | SAXException e) {
            log.error(String.format("Can't parse file: %s ", e.getMessage()));
            System.out.println(String.format("Can't parse file: %s ", e.getMessage()));
        }
    }

    private void parseAndWriteData(NodeList nodeList, String fileName, Writer writer) {
        log.info(String.format("Writing infos from %s", fileName));
        System.out.println(String.format("Writing infos from %s", fileName));

        Map<String, List<String>> result = new HashMap<>();
        List<String> queries = new ArrayList<>();
        String tableName = initTableName(fileName);
        for (int i = 1; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            NamedNodeMap attributes = element.getAttributes();

            String query = initQuery(attributes, tableName);
            queries.add(query);

            if (i % 10000 == 0) {
                result.put(tableName, queries);
                writer.writeValue(result);

                log.info(String.format("Processing....was wrote %s raws of %s", i, nodeList.getLength()));
                System.out.println(String.format("Processing....was wrote %s raws of %s", i, nodeList.getLength()));

                result.clear();
                queries.clear();
            }
        }
        result.put(tableName, queries);
        writer.writeValue(result);

        log.info(String.format("End writing info from: %s, writing %s raws", fileName, nodeList.getLength() - 1));
        System.out.println(
                (String.format("End writing info from: %s, writing %s raws", fileName, nodeList.getLength() - 1)));
    }

    private String initQuery(NamedNodeMap attributes, String tableName) {
        String columnNames = "";
        String values = "";
        for (int j = 0; j < attributes.getLength(); j++) {
            Node node = attributes.item(j);
            String nodeName = node.getNodeName();
            nodeName = nodeName.equalsIgnoreCase("desc") ? "\"" + nodeName.toLowerCase() + "\"" : nodeName;
            String nodeValue = node.getNodeValue();

            columnNames = String.join(",", columnNames, nodeName);
            values = String.join(",'", values, nodeValue + "'");
        }
        columnNames = columnNames.substring(1);
        values = values.substring(1);

        return String.format("insert into %s (%s) values (%s) ", tableName, columnNames,
                             values);
    }

    private String initTableName(String fileName) {
        if (fileName.toLowerCase().startsWith("as_operation_types")) {
            return "fiaz.operation_types";
        } else if (fileName.toLowerCase().startsWith("as_reestr_objects")) {
            return "fiaz.reestr_objects";
        } else if (fileName.toLowerCase().startsWith("as_change_history")) {
            return "fiaz.change_history";
        } else if (fileName.toLowerCase().startsWith("as_param_types")) {
            return "fiaz.param_types";
        } else if (fileName.toLowerCase().startsWith("as_object_levels")) {
            return "fiaz.object_levels";
        } else if (fileName.toLowerCase().startsWith("as_adm_hierarchy")) {
            return "fiaz.adm_hierarchy";
        } else if (fileName.toLowerCase().startsWith("as_mun_hierarchy")) {
            return "fiaz.mun_hierarchy";
        } else if (fileName.toLowerCase().startsWith("as_carplaces")) {
            if (fileName.toLowerCase().startsWith("as_carplaces_params")) {
                return "fiaz.param";
            }

            return "fiaz.car_places";
        } else if (fileName.toLowerCase().startsWith("as_steads")) {
            if (fileName.toLowerCase().startsWith("as_steads_params")) {
                return "fiaz.param";
            }

            return "fiaz.steads";
        } else if (fileName.toLowerCase().startsWith("as_room")) {
            if (fileName.toLowerCase().startsWith("as_rooms_params")) {
                return "fiaz.param";
            } else if (fileName.toLowerCase().startsWith("as_room_types")) {
                return "fiaz.room_types";
            }

            return "fiaz.rooms";
        } else if (fileName.toLowerCase().startsWith("as_normative_docs")) {
            if (fileName.toLowerCase().startsWith("as_normative_docs_kinds")) {
                return "fiaz.normative_docs_kinds";
            } else if (fileName.toLowerCase().startsWith("as_normative_docs_types")) {
                return "fiaz.normative_docs_types";
            }

            return "fiaz.normative_docs";
        } else if (fileName.toLowerCase().startsWith("as_apartment")) {
            if (fileName.toLowerCase().startsWith("as_apartments_params")) {
                return "fiaz.param";
            } else if (fileName.toLowerCase().startsWith("as_apartment_types")) {
                return "fiaz.apartment_types";
            }

            return "fiaz.apartments";
        } else if (fileName.toLowerCase().startsWith("as_house")) {
            if (fileName.toLowerCase().startsWith("as_houses_params")) {
                return "fiaz.param";
            } else if (fileName.toLowerCase().startsWith("as_house_types")) {
                return "fiaz.house_types";
            }

            return "fiaz.houses";
        } else if (fileName.toLowerCase().startsWith("as_addr_obj")) {
            if (fileName.toLowerCase().startsWith("as_addr_obj_params")) {
                return "fiaz.param";
            } else if (fileName.toLowerCase().startsWith("as_addr_obj_types")) {
                return "fiaz.address_objects_types";
            } else if (fileName.toLowerCase().startsWith("as_addr_obj_division")) {
                return "fiaz.address_objects_division";
            }

            return "fiaz.address_objects";
        }

        return "";
    }
}
