package de.applejuicenet.client.gui.controller.xmlholder;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.WebXMLParser;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.Search.SearchEntry;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;
import de.applejuicenet.client.shared.Version;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.shared.dac.UploadDO;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.*;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.ConnectionSettings;
import java.io.StringReader;
import java.io.CharArrayWriter;
import java.util.HashSet;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/ModifiedXMLHolder.java,v 1.18 2004/02/18 17:24:21 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ModifiedXMLHolder.java,v $
 * Revision 1.18  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
 *
 */

public class ModifiedXMLHolder
    extends DefaultHandler  {
    private HashMap sourcenZuDownloads = new HashMap();
    private XMLReader xr = null;

    private HashMap serverMap = new HashMap();
    private HashMap downloadMap = new HashMap();
    private HashMap uploadMap = new HashMap();
    private HashMap searchMap = new HashMap();
    private NetworkInfo netInfo;
    private Information information;
    private int count = 0;
    private String filter = "";
    private String sessionKontext = null;

    private SecurerXMLHolder securerHolder = new SecurerXMLHolder();

    private int connectedWithServerId = -1;
    private int tryConnectToServer = -1;

    private boolean reloadInProgress = false;
    private int gcCounter = 0;
    private Logger logger;

    private String host;
    private String password;
    private String zipMode = "";
    private String xmlCommand;
    private long timestamp = 0;
    private CharArrayWriter contents = new CharArrayWriter();
    private static ModifiedXMLHolder instance = null;

    private ModifiedXMLHolder() {
        logger = Logger.getLogger(getClass());
        ConnectionSettings rc = PropertiesManager.getOptionsManager().
            getRemoteSettings();
        host = rc.getHost();
        password = rc.getOldPassword();
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        if (host.compareToIgnoreCase("localhost") != 0 &&
            host.compareTo("127.0.0.1") != 0) {
            zipMode = "mode=zip&";
        }
        xmlCommand = "/xml/modified.xml";
        try {
            System.setProperty("org.xml.sax.parser",
                               "org.apache.xerces.parsers.SAXParser");
            xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler( this );
        }
        catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    public static ModifiedXMLHolder getInstance(){
        if (instance == null){
            instance = new ModifiedXMLHolder();
        }
        return instance;

    }

    public HashMap getServer() {
        return serverMap;
    }

    public HashMap getUploads() {
        return uploadMap;
    }

    public HashMap getDownloads() {
        return downloadMap;
    }

    public HashMap getSearchs() {
        return searchMap;
    }

    public NetworkInfo getNetworkInfo() {
        return netInfo;
    }

    public synchronized boolean update(String sessionId) {
        if (sessionKontext == null) {
            sessionKontext = "&session=" + sessionId;
        }
        if (tryToReload()) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean tryToReload() {
        if (reloadInProgress) {
            return false;
        }
        else {
            switch (count) {
                //lazy loading
                case 0: {
                    count++;
                    filter =
                        "&filter=ids;down;uploads;server;informations;search&mode=zip";
                    break;
                }
                case 1: {
                    count++;
                    filter = "&filter=ids;informations;user;search&mode=zip";
                    break;
                }
                case 2: {
                    count++;
                    filter = ""; // kein Filter
                    break;
                }
                default: {
                    break;
                }
            }
            reload(sessionKontext + filter);
            return true;
        }
    }

    private boolean secureSession() {
        try {
            return securerHolder.secure(sessionKontext, information);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
            return false;
        }
    }

    public Information getInformation(){
        return information;
    }

    public HashMap getSpeeds() {
           HashMap speeds = new HashMap();
           if (information != null){
               speeds.put("uploadspeed",
                          new Long(information.getUploadSpeed()));
               speeds.put("downloadspeed",
                          new Long(information.getDownloadSpeed()));
               speeds.put("credits",
                          new Long(information.getCredits()));
               speeds.put("sessionupload",
                          new Long(information.getSessionUpload()));
               speeds.put("sessiondownload",
                          new Long(information.getSessionDownload()));
           }
           return speeds;
       }


    private void checkInformationAttributes(Attributes attr){
        if (information == null){
            information = new Information();
        }
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("credits")){
                information.setCredits(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sessionupload")){
                information.setSessionUpload(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sessiondownload")){
                information.setSessionDownload(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("uploadspeed")){
                information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("downloadspeed")){
                information.setDownloadSpeed(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("openconnections")){
                information.setOpenConnections(Long.parseLong(attr.getValue(i)));
            }
            else{
                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));
            }
        }
    }

    private void checkNetworkInfoAttributes(Attributes attr){
        if (netInfo == null){
            netInfo = new NetworkInfo();
        }
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("users")){
                netInfo.setAjUserGesamt(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("files")){
                netInfo.setAjAnzahlDateien(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("filesize")){
                netInfo.setAjGesamtShare(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("firewalled")){
                netInfo.setFirewalled(attr.getValue(i).equals("true"));
            }
            else if (attr.getLocalName(i).equals("ip")){
                netInfo.setExterneIP(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("tryconnecttoserver")){
                netInfo.setTryConnectToServer(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("connectedwithserverid")){
                netInfo.setConnectedWithServerId(Integer.parseInt(attr.getValue(i)));
            }
            else{
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
    }

    private void checkServerAttributes(Attributes attr){
        int id = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")) {
                id = Integer.parseInt(attr.getValue(i));
                break;
            }
        }
        if (id == -1) {
            return;
        }
        String key = Integer.toString(id);
        ServerDO serverDO;
        if (uploadMap.containsKey(key)) {
            serverDO = (ServerDO) serverMap.get(key);
        }
        else {
            serverDO = new ServerDO(id);
            serverMap.put(key, serverDO);
        }
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("name")) {
                serverDO.setName(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("host")) {
                serverDO.setHost(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("lastseen")) {
                serverDO.setTimeLastSeen(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("port")) {
                serverDO.setPort(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("connectiontry")) {
                serverDO.setVersuche(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("id")) {
                continue;
            }
            else {
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
    }

    private HashMap shareMap = null;

    private void checkUploadAttributes(Attributes attr){
        int id = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")) {
                id = Integer.parseInt(attr.getValue(i));
                break;
            }
        }
        if (id == -1) {
            return;
        }
        String key = Integer.toString(id);
        UploadDO uploadDO;
        if (uploadMap.containsKey(key)) {
            uploadDO = (UploadDO) uploadMap.get(key);
        }
        else{
            uploadDO = new UploadDO(id);
            uploadMap.put(key, uploadDO);
        }
        String versionNr = "";
        int os = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("shareid")) {
                uploadDO.setShareFileID(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("version")) {
                versionNr = attr.getValue(i);
            }
            else if (attr.getLocalName(i).equals("operatingsystem")) {
                os = Integer.parseInt(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("status")) {
                uploadDO.setStatus(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("directstate")) {
                uploadDO.setDirectState(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("priority")) {
                uploadDO.setPrioritaet(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("nick")) {
                uploadDO.setNick(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("uploadfrom")) {
                uploadDO.setUploadFrom(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("actualuploadposition")) {
                uploadDO.setActualUploadPosition(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("uploadto")) {
                uploadDO.setUploadTo(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("speed")) {
                uploadDO.setSpeed(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("id")) {
                continue;
            }
            else {
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
        if (!versionNr.equals("0.0.0.0")) {
            Version version = new Version(versionNr, os);
            uploadDO.setVersion(version);
        }
        if (shareMap == null){
            shareMap = ApplejuiceFassade.getInstance().getShare(false);
        }
        ShareDO shareDO = (ShareDO) shareMap.get(uploadDO.getShareFileIDAsString());
        if (shareDO != null) {
            uploadDO.setDateiName(shareDO.getShortfilename());
        }
        else{
            //wenns die passende Sharedatei aus irgendeinem Grund nicht geben sollte,
            //wird dieser Upload auch nicht angezeigt
            uploadMap.remove(key);
        }
    }

    private void checkUserAttributes(Attributes attr){
        int id = -1;
        int downloadId = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")) {
                id = Integer.parseInt(attr.getValue(i));
                continue;
            }
            else if (attr.getLocalName(i).equals("downloadid")) {
                downloadId = Integer.parseInt(attr.getValue(i));
                continue;
            }
        }
        if (id == -1 || downloadId == -1) {
            return;
        }
        String downloadKey = Integer.toString(downloadId);
        DownloadDO downloadDO = (DownloadDO) downloadMap.get(downloadKey);
        if (downloadDO != null) {
            DownloadSourceDO downloadSourceDO = downloadDO.getSourceById(id);
            if (downloadSourceDO == null) {
                downloadSourceDO = new DownloadSourceDO(id);
                downloadDO.addSource(downloadSourceDO);
                sourcenZuDownloads.put(Integer.toString(id),
                                       downloadDO);
                downloadSourceDO.setDownloadId(downloadId);
            }
            String versionNr = "";
            int os = -1;
            for (int i = 0; i < attr.getLength(); i++) {
                if (attr.getLocalName(i).equals("id")) {
                    continue;
                }
                else if (attr.getLocalName(i).equals("downloadid")) {
                    continue;
                }
                else if (attr.getLocalName(i).equals("status")) {
                    downloadSourceDO.setStatus(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("directstate")) {
                    downloadSourceDO.setDirectstate(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("downloadfrom")) {
                    downloadSourceDO.setDownloadFrom(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("downloadto")) {
                    downloadSourceDO.setDownloadTo(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("actualdownloadposition")) {
                    downloadSourceDO.setActualDownloadPosition(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("speed")) {
                    downloadSourceDO.setSpeed(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("version")) {
                    versionNr = attr.getValue(i);
                }
                else if (attr.getLocalName(i).equals("operatingsystem")) {
                    os = Integer.parseInt(attr.getValue(i));
                }
                else if (attr.getLocalName(i).equals("queueposition")) {
                    downloadSourceDO.setQueuePosition(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("powerdownload")) {
                    downloadSourceDO.setPowerDownload(Integer.parseInt(attr.getValue(i)));
                }
                else if (attr.getLocalName(i).equals("filename")) {
                    downloadSourceDO.setFilename(attr.getValue(i));
                }
                else if (attr.getLocalName(i).equals("nickname")) {
                    downloadSourceDO.setNickname(attr.getValue(i));
                }
            }
            if (!versionNr.equals("0.0.0.0")) {
                Version version = new Version(versionNr, os);
                downloadSourceDO.setVersion(version);
            }
        }
        else{
            //todo
            int i=0;
        }
    }

    private void checkDownloadAttributes(Attributes attr){
        int id = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")){
                id = Integer.parseInt(attr.getValue(i));
                break;
            }
        }
        if (id == -1){
            return;
        }
        String key = Integer.toString(id);
        DownloadDO downloadDO;
        if (downloadMap.containsKey(key)) {
            downloadDO = (DownloadDO) downloadMap.get(key);
        }
        else{
            downloadDO = new DownloadDO(id);
            downloadMap.put(key, downloadDO);
        }
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("shareid")) {
                downloadDO.setShareId(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("hash")) {
                downloadDO.setHash(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("size")) {
                downloadDO.setGroesse(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("status")) {
                downloadDO.setStatus(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("powerdownload")) {
                downloadDO.setPowerDownload(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("temporaryfilenumber")) {
                downloadDO.setTemporaryFileNumber(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("filename")) {
                downloadDO.setFilename(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("targetdirectory")) {
                downloadDO.setTargetDirectory(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("ready")) {
                downloadDO.setReady(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("id")) {
                continue;
            }
            else {
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
    }

    private void checkRemovedAttributes(Attributes attr){
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")){
                String id = attr.getValue(i);
                if (uploadMap.containsKey(id)) {
                    uploadMap.remove(id);
                    continue;
                }
                else if (downloadMap.containsKey(id)) {
                    DownloadDO downloadDO = (DownloadDO) downloadMap.get(
                        sourcenZuDownloads.get(id));
                    if (downloadDO != null) {
                        DownloadSourceDO[] sourcen = downloadDO.getSources();
                        if (sourcen != null) {
                            for (int y = 0; y < sourcen.length; y++) {
                                sourcenZuDownloads.remove(Integer.toString(
                                    sourcen[y].getId()));
                            }
                        }
                    }
                    downloadMap.remove(id);
                    continue;
                }
                else if (serverMap.containsKey(id)) {
                    serverMap.remove(id);
                    continue;
                }
                else if (sourcenZuDownloads.containsKey(id)) {
                    DownloadDO downloadDO = (DownloadDO) sourcenZuDownloads.get(
                        id);
                    downloadDO.removeSource(id);
                    sourcenZuDownloads.remove(id);
                    continue;
                }
                else if (searchMap.containsKey(id)) {
                    searchMap.remove(id);
                    Search.currentSearchCount = searchMap.size();
                    continue;
                }
            }
            else{
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
    }

    private void checkSearchAttributes(Attributes attr){
        int id = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")){
                id = Integer.parseInt(attr.getValue(i));
                break;
            }
        }
        if (id == -1){
            return;
        }
        String key = Integer.toString(id);
        Search aSearch;
        if (searchMap.containsKey(key)) {
            aSearch = (Search) searchMap.get(key);
        }
        else {
            aSearch = new Search(id);
            searchMap.put(key, aSearch);
        }
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("searchtext")) {
                aSearch.setSuchText(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("opensearches")) {
                aSearch.setOffeneSuchen(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("foundfiles")) {
                aSearch.setGefundenDateien(Integer.parseInt(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sumsearches")) {
                aSearch.setDurchsuchteClients(Integer.parseInt(attr.getValue(i)));
            }
            else {
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
    }

    private SearchEntry tmpSearchEntry = null;

    private void checkSearchEntryAttributes(Attributes attr){
        int searchId = -1;
        int id = -1;
        String checksum = "";
        long groesse = -1;

        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")) {
                id = Integer.parseInt(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("searchid")) {
                searchId = Integer.parseInt(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("checksum")) {
                checksum = attr.getValue(i);
            }
            else if (attr.getLocalName(i).equals("size")) {
                groesse = Long.parseLong(attr.getValue(i));
            }
            else {
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
        String key = Integer.toString(searchId);
        Search aSearch;
        if (searchMap.containsKey(key)) {
            aSearch = (Search) searchMap.get(key);
            tmpSearchEntry = aSearch.new SearchEntry(id, searchId, checksum, groesse);
            aSearch.addSearchEntry(tmpSearchEntry);
        }
        else{
            tmpSearchEntry = new Search(-1).new SearchEntry(id, searchId, checksum, groesse);
            searchEntriesToDo.add(tmpSearchEntry);
        }
    }

    private HashSet searchEntriesToDo = new HashSet();

    private void checkSearchEntryFilenameAttributes(Attributes attr){
        if (tmpSearchEntry == null){
            return;
        }
        int haeufigkeit = -1;
        String dateiName = "";

        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("name")) {
                dateiName = attr.getValue(i);
            }
            else if (attr.getLocalName(i).equals("user")) {
                haeufigkeit = Integer.parseInt(attr.getValue(i));
            }
            else {
/*                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));*/
            }
        }
        FileName filename = tmpSearchEntry.new FileName(
                                                dateiName, haeufigkeit);
        tmpSearchEntry.addFileName(filename);
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
        if (localName.equals("download")){
            checkDownloadAttributes(attr);
        }
        else if (localName.equals("upload")){
            checkUploadAttributes(attr);
        }
        else if (localName.equals("server")){
            checkServerAttributes(attr);
        }
        else if (localName.equals("information")){
            checkInformationAttributes(attr);
        }
        else if (localName.equals("networkinfo")){
            checkNetworkInfoAttributes(attr);
        }
        else if (localName.equals("object")){
            checkRemovedAttributes(attr);
        }
        else if (localName.equals("user")){
            checkUserAttributes(attr);
        }
        else if (localName.equals("search")){
            checkSearchAttributes(attr);
        }
        else if (localName.equals("searchentry")){
            checkSearchEntryAttributes(attr);
        }
        else if (localName.equals("filename")){
            checkSearchEntryFilenameAttributes(attr);
        }
        else{
/*            System.out.println("SAX Event: START ELEMENT[ " +
                               localName + " ]");
            for (int i = 0; i < attr.getLength(); i++) {
                System.out.println("   ATTRIBUTE: " + attr.getLocalName(i) +
                                   " VALUE: " + attr.getValue(i));
            }*/
        }
    }

    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (localName.equals("time")){
            timestamp = Long.parseLong(contents.toString());
        }
    }

    public void characters(char[] ch, int start, int length) throws
        SAXException {
        contents.write(ch, start, length);
    }

    private String getXMLString(String parameters) throws
        Exception {
        String xmlData = null;
        String command = xmlCommand + "?";
        if (parameters.indexOf("mode=zip") == -1) {
            command += zipMode;
        }
        command += "password=" + password + "&timestamp=" +
            timestamp + parameters;
        xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                               command);
        if (xmlData.length() == 0) {
            throw new IllegalArgumentException();
        }
        return xmlData;
    }

    private void parseRest(){
        int verbindungsStatus = Information.NICHT_VERBUNDEN;
        ServerDO serverDO = null;
        if (this.tryConnectToServer != netInfo.getTryConnectToServer()) {
            Object alterServer = serverMap.get(Integer.toString(this.
                tryConnectToServer));
            if (alterServer != null) {
                ( (ServerDO) alterServer).setTryConnect(false);
            }
            if (tryConnectToServer != -1) {
                serverDO = (ServerDO) serverMap.get(Integer.
                    toString(netInfo.getTryConnectToServer()));
                serverDO.setTryConnect(true);
                verbindungsStatus = Information.VERSUCHE_ZU_VERBINDEN;
            }
            this.tryConnectToServer = netInfo.getTryConnectToServer();
        }
        Object alterServer = serverMap.get(Integer.toString(this.
            connectedWithServerId));
        if (alterServer != null) {
            ( (ServerDO) alterServer).setConnected(false);
        }
        if (netInfo.getConnectedWithServerId() != -1) {
            serverDO = (ServerDO) serverMap.get(Integer.toString(
                netInfo.getConnectedWithServerId()));
            serverDO.setConnected(true);
            verbindungsStatus = Information.VERBUNDEN;
            information.setServer(serverDO);
        }
        this.connectedWithServerId = netInfo.getConnectedWithServerId();
        information.setServer(serverDO);
        information.setVerbindungsStatus(verbindungsStatus);
        information.setExterneIP(netInfo.getExterneIP());

        Search aSearch;
        SearchEntry searchEntry;
        if (searchEntriesToDo.size()>0){
            Iterator it = searchEntriesToDo.iterator();
            while(it.hasNext()){
                searchEntry = (SearchEntry)it.next();
                if (searchMap.containsKey(Integer.toString(searchEntry.getSearchId()))) {
                    aSearch = (Search) searchMap.get(Integer.toString(searchEntry.getSearchId()));
                    aSearch.addSearchEntry(searchEntry);
                }

            }
            searchEntriesToDo.clear();
        }
    }

    public void reload(String parameters) {
        try {
            reloadInProgress = true;
            Securer securer = new Securer();
            securer.start();
            String xmlString = getXMLString(parameters);
            xr.parse( new InputSource(
               new StringReader( xmlString )) );
            parseRest();
            if (!securer.isInterrupted()) {
                securer.interrupt();
            }
            if (!securer.isOK()) {
                SessionXMLHolder session = new SessionXMLHolder();
                session.reload("", false);
                String sessionId = session.getFirstAttrbuteByTagName(new
                    String[] {
                    "applejuice", "session", "id"}
                    , false);
                sessionKontext = "&session=" + sessionId;
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug(
                        "Neue SessionId: " + sessionId);
                }
            }
            securer = null;
            reloadInProgress = false;
        }
        catch (WebSiteNotFoundException webSiteNotFound) {
            SessionXMLHolder session = new SessionXMLHolder();
            try {
                session.reload("", false);
            }
            catch (Exception ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", ex);
                }
            }
            reloadInProgress = false;
            String sessionId = session.getFirstAttrbuteByTagName(new
                String[] {
                "applejuice", "session", "id"}
                , false);
            sessionKontext = "&session=" + sessionId;
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug(
                    "Neue SessionId: " + sessionId);
            }
            throw new RuntimeException();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateSuche() {
        try {
/*            NodeList nodes = document.getElementsByTagName("search");
            int size = nodes.getLength();
            if (size > 0) {
                Element e;
                int id;
                String suchtext;
                int offeneSuchen;
                int gefundeneDateien;
                int durchsuchteClients;
                String key;
                Search aSearch;
                String temp;
                for (int i = 0; i < size; i++) {
                    e = (Element) nodes.item(i);
                    id = Integer.parseInt(e.getAttribute("id"));
                    key = Integer.toString(id);
                    suchtext = e.getAttribute("searchtext");
                    temp = e.getAttribute("opensearchs");
                    if (temp.length() == 0) {
                        temp = e.getAttribute("opensearches");
                    }
                    offeneSuchen = Integer.parseInt(temp);
                    temp = e.getAttribute("sumsearches");
                    durchsuchteClients = Integer.parseInt(temp);
                    temp = e.getAttribute("foundfiles");
                    gefundeneDateien = Integer.parseInt(temp);
                    if (searchMap.containsKey(key)) {
                        aSearch = (Search) searchMap.get(key);
                        aSearch.setDurchsuchteClients(durchsuchteClients);
                        aSearch.setGefundenDateien(gefundeneDateien);
                        aSearch.setOffeneSuchen(offeneSuchen);
                        aSearch.setSuchText(suchtext);
                    }
                    else {
                        aSearch = new Search(id);
                        aSearch.setDurchsuchteClients(durchsuchteClients);
                        aSearch.setGefundenDateien(gefundeneDateien);
                        aSearch.setOffeneSuchen(offeneSuchen);
                        aSearch.setSuchText(suchtext);
                        searchMap.put(key, aSearch);
                    }
                }
                Search.currentSearchCount = searchMap.size();
            }
            nodes = document.getElementsByTagName("searchentry");
            size = nodes.getLength();
            if (size > 0) {
                Element e;
                int id;
                int searchid;
                String checksum;
                long groesse;
                String key;
                Search aSearch;
                Search.SearchEntry searchEntry;
                Element innerElement;
                NodeList childNodes;
                String dateiName;
                int haeufigkeit;
                Search.SearchEntry.FileName filename;
                for (int i = 0; i < size; i++) {
                    e = (Element) nodes.item(i);
                    id = Integer.parseInt(e.getAttribute("id"));
                    searchid = Integer.parseInt(e.getAttribute("searchid"));
                    key = Integer.toString(searchid);
                    checksum = e.getAttribute("checksum");
                    groesse = Long.parseLong(e.getAttribute("size"));
                    aSearch = (Search) searchMap.get(key);
                    if (aSearch != null) {
                        searchEntry = aSearch.new SearchEntry(id, checksum,
                            groesse);
                        childNodes = nodes.item(i).getChildNodes();
                        int nodesSize = childNodes.getLength();
                        for (int y = 0; y < nodesSize; y++) {
                            if (childNodes.item(y).getNodeType() ==
                                Node.ELEMENT_NODE) {
                                innerElement = (Element) childNodes.item(y);
                                if (innerElement.getNodeName().
                                    compareToIgnoreCase("filename") == 0) {
                                    dateiName = innerElement.getAttribute(
                                        "name");
                                    haeufigkeit = Integer.parseInt(innerElement.
                                        getAttribute("user"));
                                    filename = searchEntry.new FileName(
                                        dateiName, haeufigkeit);
                                    searchEntry.addFileName(filename);
                                }
                            }
                        }
                        aSearch.addSearchEntry(searchEntry);
                    }
                }
            }*/
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private class Securer
        extends Thread {
        private boolean ok = true;

        public void run() {
            while (!interrupted()) {
                try {
                    sleep(5000);
                    if (!secureSession()) {
                        ok = false;
                        interrupt();
                    }
                }
                catch (InterruptedException ex) {
                    interrupt();
                }
            }
        }

        public boolean isOK() {
            return ok;
        }
    }

}
