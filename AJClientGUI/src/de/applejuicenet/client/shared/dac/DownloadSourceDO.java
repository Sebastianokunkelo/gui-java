package de.applejuicenet.client.shared.dac;

import java.util.*;

import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadSourceDO.java,v 1.16 2003/12/30 13:55:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadSourceDO.java,v $
 * Revision 1.16  2003/12/30 13:55:20  maj0r
 * Neuen DownloadSourceStatus indirekteVerbindungAbgelehnt eingebaut.
 *
 * Revision 1.15  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.14  2003/12/16 14:52:16  maj0r
 * An Schnittstellenerweiterung angepasst.
 *
 * Revision 1.13  2003/10/18 18:44:16  maj0r
 * Neuen Userstatus "Warteschlange voll" hinzugefuegt.
 *
 * Revision 1.12  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.11  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.10  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.9  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.8  2003/07/03 19:11:16  maj0r
 * DownloadTable �berarbeitet.
 *
 * Revision 1.7  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class DownloadSourceDO {
    //Status - IDs
    public static final int UNGEFRAGT = 1;
    public static final int VERSUCHE_ZU_VERBINDEN = 2;
    public static final int GEGENSTELLE_HAT_ZU_ALTE_VERSION = 3;
    public static final int GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN = 4;
    public static final int IN_WARTESCHLANGE = 5;
    public static final int KEINE_BRAUCHBAREN_PARTS = 6;
    public static final int UEBERTRAGUNG = 7;
    public static final int NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE = 8;
    public static final int FERTIGGESTELLT = 9;
    public static final int KEINE_VERBINDUNG_MOEGLICH = 11;
    public static final int VERSUCHE_INDIREKT = 12;
    public static final int PAUSIERT = 13;
    public static final int WARTESCHLANGE_VOLL = 14;
    public static final int EIGENES_LIMIT_ERREICHT = 15;
    public static final int INDIREKTE_VERBINDUNG_ABGELEHNT = 16;

    //directstate - IDs
    public static final int UNBEKANNT = 0;
    public static final int DIREKTE_VERBINDUNG = 1;
    public static final int INDIREKTE_VERBINDUNG_UNBESTAETIGT = 2;
    public static final int INDIREKTE_VERBINDUNG = 3;

    private final int id;
    private int status;
    private int directstate;
    private int downloadFrom;
    private int downloadTo;
    private int actualDownloadPosition;
    private int speed;
    private Version version;
    private int queuePosition;
    private int powerDownload;
    private String filename;
    private String nickname;
    private int downloadId;

    public DownloadSourceDO(int id, int status, int directstate, int downloadFrom, int downloadTo,
                            int actualDownloadPosition, int speed, Version version, int queuePosition,
                            int powerDownload, String filename, String nickname, int downloadId) {
        this.id = id;
        this.status = status;
        this.directstate = directstate;
        this.downloadFrom = downloadFrom;
        this.downloadTo = downloadTo;
        this.actualDownloadPosition = actualDownloadPosition;
        this.speed = speed;
        this.version = version;
        this.queuePosition = queuePosition;
        this.powerDownload = powerDownload;
        this.filename = filename;
        this.nickname = nickname;
        this.downloadId = downloadId;
    }

    public int getStatus() {
        return status;
    }

    public int getSize(){
        if (downloadTo==-1 || downloadFrom==-1)
            return 0;
        return downloadTo - downloadFrom;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDownloadPercentAsString(){
        if (actualDownloadPosition==-1 || downloadFrom==-1)
            return "0";
        double temp = actualDownloadPosition - downloadFrom;
        temp =  temp * 100 / getSize();
        String result = Double.toString(temp);
        if (result.indexOf(".") + 3 < result.length())
        {
            result = result.substring(0, result.indexOf(".") + 3);
        }
        return result;
    }

    public String getRestZeitAsString(){
        if (speed==0 || speed==-1)
            return "";
        int restZeit = getNochZuLaden() / speed;
        int tage = restZeit / 86400;
        restZeit -= tage * 86400;
        int stunden = restZeit / 3600;
        restZeit -= stunden * 3600;
        int minuten = restZeit / 60;
        restZeit -= minuten * 60;

        StringBuffer temp = new StringBuffer();
        if (tage<10)
            temp.append('0');
        temp.append(Integer.toString(tage));
        temp.append(':');
        if (stunden<10)
            temp.append('0');
        temp.append(Integer.toString(stunden));
        temp.append(':');
        if (minuten<10)
            temp.append('0');
        temp.append(Integer.toString(minuten));
        temp.append(':');
        if (restZeit<10)
            temp.append('0');
        temp.append(Integer.toString(restZeit));
        return temp.toString();
    }

    public int getBereitsGeladen(){
        if (actualDownloadPosition==-1 || downloadFrom==-1)
            return 0;
        return actualDownloadPosition - downloadFrom;
    }

    public int getNochZuLaden(){
        if (downloadTo==-1 || actualDownloadPosition==-1)
            return 0;
        return downloadTo - actualDownloadPosition;
    }

    public int getDirectstate() {
        return directstate;
    }

    public void setDirectstate(int directstate) {
        this.directstate = directstate;
    }

    public int getDownloadFrom() {
        return downloadFrom;
    }

    public void setDownloadFrom(int downloadFrom) {
        this.downloadFrom = downloadFrom;
    }

    public int getDownloadTo() {
        return downloadTo;
    }

    public void setDownloadTo(int downloadTo) {
        this.downloadTo = downloadTo;
    }

    public int getActualDownloadPosition() {
        return actualDownloadPosition;
    }

    public void setActualDownloadPosition(int actualDownloadPosition) {
        this.actualDownloadPosition = actualDownloadPosition;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public int getPowerDownload() {
        return powerDownload;
    }

    public void setPowerDownload(int powerDownload) {
        this.powerDownload = powerDownload;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }
}