package XMLHandling;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlReader {
    public static SAXHandler getSAXHandler() {
        try {
            File inputFile = new File("src/main/Spreadsheet/kjndgbksnfdb-1.xmla");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SAXHandler userhandler = new SAXHandler();
            saxParser.parse(inputFile, userhandler);
            return userhandler;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

class XmlRow {
    ArrayList<String> cellList = new ArrayList<>();

    @Override
    public String toString() {
        return cellList.toString();
    }
}

