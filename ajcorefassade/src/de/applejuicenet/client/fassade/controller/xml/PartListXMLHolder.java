package de.applejuicenet.client.fassade.controller.xml;

import java.io.CharArrayWriter;
import java.io.StringReader;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.controller.dac.PartListDO;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.shared.HtmlLoader;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/PartListXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r <aj@tkl-soft.de>
 * 
 */

public class PartListXMLHolder extends DefaultHandler {

	private final CoreConnectionSettingsHolder coreHolder;

	private String xmlCommand;

	private XMLReader xr = null;

	private CharArrayWriter contents = new CharArrayWriter();

	private PartListDO partListDO;

	private String zipMode = "";

	public PartListXMLHolder(CoreConnectionSettingsHolder coreHolder) {
		this.coreHolder = coreHolder;
		try {
			if (!coreHolder.isLocalhost()) {
				zipMode = "mode=zip&";
			}
			Class parser = SAXParser.class;
			xr = XMLReaderFactory.createXMLReader(parser.getName());
			xr.setContentHandler(this);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void checkInformationAttributes(Attributes attr) {
		for (int i = 0; i < attr.getLength(); i++) {
			if (attr.getLocalName(i).equals("filesize")) {
				partListDO.setGroesse(Long.parseLong(attr.getValue(i)));
			}
		}
	}

	private void checkPartAttributes(Attributes attr) {
		long startPosition = -1;
		int type = -1;
		for (int i = 0; i < attr.getLength(); i++) {
			if (attr.getLocalName(i).equals("fromposition")) {
				startPosition = Long.parseLong(attr.getValue(i));
			} else if (attr.getLocalName(i).equals("type")) {
				type = Integer.parseInt(attr.getValue(i));
			}
		}
		partListDO.addPart(partListDO.new Part(startPosition, type));
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		contents.reset();
		if (localName.equals("fileinformation")) {
			checkInformationAttributes(attr);
		} else if (localName.equals("part")) {
			checkPartAttributes(attr);
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contents.write(ch, start, length);
	}

	private String getXMLString(String parameters)
			throws WebSiteNotFoundException {
		String xmlData = null;
		String command = xmlCommand + zipMode + "password="
				+ coreHolder.getCorePassword() + parameters;
		xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(),
				coreHolder.getCorePort(), HtmlLoader.GET, command);
		if (xmlData.length() == 0) {
			throw new IllegalArgumentException();
		}
		return xmlData;
	}

	public PartListDO getPartList(Object object)
			throws WebSiteNotFoundException {
		try {
			String xmlString;
			if (object.getClass() == DownloadSourceDO.class) {
				xmlCommand = "/xml/userpartlist.xml?";
				xmlString = getXMLString("&id="
						+ ((DownloadSourceDO) object).getId());
				partListDO = new PartListDO((DownloadSourceDO) object);
			} else {
				xmlCommand = "/xml/downloadpartlist.xml?";
				xmlString = getXMLString("&id=" + ((DownloadDO) object).getId());
				partListDO = new PartListDO((Download) object);
			}
			xr.parse(new InputSource(new StringReader(xmlString)));
			PartListDO resultPartList = partListDO;
			partListDO = null;
			return resultPartList;
		} catch (Exception e) {
			return null;
		}
	}
}
