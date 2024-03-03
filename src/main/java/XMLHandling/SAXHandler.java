package XMLHandling;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SAXHandler extends DefaultHandler {
    List<XmlRow> xmlRowList = new ArrayList<>();
    XmlRow xmlRow = null;
    String content = null;

    public List<XmlRow> getXmlRowList() {
        return xmlRowList;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("Row")) {
            xmlRow = new XmlRow();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("Row")) {
            xmlRowList.add(xmlRow);
        } else if (qName.equals("Data")) {
            xmlRow.cellList.add(content);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }
}
