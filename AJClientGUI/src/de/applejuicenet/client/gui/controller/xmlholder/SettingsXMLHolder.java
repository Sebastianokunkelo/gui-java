package de.applejuicenet.client.gui.controller.xmlholder;

import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import de.applejuicenet.client.gui.controller.WebXMLParser;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ShareEntry;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SettingsXMLHolder.java,v 1.5 2004/02/09 07:28:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SettingsXMLHolder.java,v $
 * Revision 1.5  2004/02/09 07:28:24  maj0r
 * Max. Anzahl von Quellen pro Datei kann begrenzt werden.
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2004/02/04 13:10:37  maj0r
 * Neues Linkformat zusaetzlich in den Downloadbereich eingebaut.
 *
 * Revision 1.2  2004/01/06 17:32:50  maj0r
 * Es wird nun zweimal versucht den Core erneut zu erreichen, wenn die Verbindung unterbrochen wurde.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.10  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.9  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.8  2003/09/10 13:16:28  maj0r
 * Veraltete Option "Browsen erlauben" entfernt und neue Option MaxNewConnectionsPerTurn hinzugefuegt.
 *
 * Revision 1.7  2003/08/10 21:08:18  maj0r
 * Diverse �nderungen.
 *
 * Revision 1.6  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.5  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.4  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class SettingsXMLHolder
    extends WebXMLParser {
    private AJSettings settings;
    private Logger logger;

    public SettingsXMLHolder() {
        super("/xml/settings.xml", "", false);
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        try {
            reload("", false);
            NodeList nodes = document.getElementsByTagName("nick");
            String nick = nodes.item(0).getFirstChild().getNodeValue();
            nodes = document.getElementsByTagName("port");
            long port = Long.parseLong(nodes.item(0).getFirstChild().
                                       getNodeValue());
            nodes = document.getElementsByTagName("xmlport");
            long xmlPort = Long.parseLong(nodes.item(0).getFirstChild().
                                          getNodeValue());
            nodes = document.getElementsByTagName("maxupload");
            long maxUpload = Long.parseLong(nodes.item(0).getFirstChild().
                                            getNodeValue());
            nodes = document.getElementsByTagName("maxdownload");
            long maxDownload = Long.parseLong(nodes.item(0).getFirstChild().
                                              getNodeValue());
            nodes = document.getElementsByTagName("maxconnections");
            long maxConnections = Long.parseLong(nodes.item(0).getFirstChild().
                                                 getNodeValue());
            nodes = document.getElementsByTagName("maxsourcesperfile");
            long maxSourcesPerFile = Long.parseLong(nodes.item(0).getFirstChild().
                                                 getNodeValue());
            nodes = document.getElementsByTagName("autoconnect");
            boolean autoConnect = new Boolean(nodes.item(0).getFirstChild().
                                              getNodeValue()).booleanValue();
            nodes = document.getElementsByTagName("speedperslot");
            int speedPerSlot = Integer.parseInt(nodes.item(0).getFirstChild().
                                                getNodeValue());
            nodes = document.getElementsByTagName("maxnewconnectionsperturn");
            long maxNewConnectionsPerTurn = Long.parseLong(nodes.item(0).
                getFirstChild().
                getNodeValue());
            nodes = document.getElementsByTagName("incomingdirectory");
            String incomingDir = nodes.item(0).getFirstChild().getNodeValue();
            nodes = document.getElementsByTagName("temporarydirectory");
            String tempDir = nodes.item(0).getFirstChild().getNodeValue();
            HashSet shareEntries = new HashSet();
            nodes = document.getElementsByTagName("directory");
            Element e = null;
            String dir = null;
            String shareMode = null;
            ShareEntry entry = null;
            int nodesSize = nodes.getLength();
            for (int i = 0; i < nodesSize; i++) {
                e = (Element) nodes.item(i);
                dir = e.getAttribute("name");
                shareMode = e.getAttribute("sharemode");
                entry = new ShareEntry(dir, shareMode);
                shareEntries.add(entry);
            }
            settings = new AJSettings(nick, port, xmlPort, maxUpload,
                                      maxDownload, speedPerSlot, incomingDir,
                                      tempDir,
                                      shareEntries, maxConnections, autoConnect,
                                      maxNewConnectionsPerTurn, maxSourcesPerFile);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public AJSettings getAJSettings() {
        update();
        return settings;
    }

    public AJSettings getCurrentAJSettings() {
        if (settings == null) {
            update();
        }
        return settings;
    }

}
