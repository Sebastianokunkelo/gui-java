package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/PartListDO.java,v 1.12 2004/02/25 11:16:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: PartListDO.java,v $
 * Revision 1.12  2004/02/25 11:16:29  maj0r
 * Bug #241 gefixt (Danke an computer.ist.org)
 * Farbgebung war genau umgekehrt. Nun gilt wirklich: je dunkler, desto mehr Quellen gefunden.
 *
 * Revision 1.11  2004/02/20 14:55:02  maj0r
 * Speicheroptimierungen.
 *
 * Revision 1.10  2004/02/12 18:10:27  maj0r
 * Farben etwas geaendert.
 *
 * Revision 1.9  2004/02/12 16:36:58  maj0r
 * Anzeige der Teile, die zurzeit uebertragen werden (weiss bis dunkelgelb).
 *
 * Revision 1.8  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.5  2003/10/04 15:30:26  maj0r
 * Userpartliste hinzugefuegt.
 *
 * Revision 1.4  2003/09/01 18:00:15  maj0r
 * Wo es ging, DO auf primitiven Datentyp umgebaut.
 * Status "geprueft" eingefuehrt.
 *
 * Revision 1.3  2003/09/01 06:27:35  maj0r
 * Ueberarbeitet.
 *
 * Revision 1.2  2003/08/11 19:41:09  maj0r
 * HashSet ggn ArrayList getauscht, um die Reihenfolge der Parts zu behalten.
 *
 * Revision 1.1  2003/08/11 16:52:22  maj0r
 * DownloadPartList eingefuegt.
 *
 *
 */

import java.util.ArrayList;

import java.awt.Color;

public class PartListDO {
    public static final Color COLOR_TYPE_UEBERPRUEFT = Color.GREEN;
    public static final Color COLOR_TYPE_OK = Color.BLACK;
    public static final Color COLOR_TYPE_0 = Color.RED;
    public static final Color COLOR_TYPE_9 = new Color(25, 25, 250);
    public static final Color COLOR_TYPE_8 = new Color(50, 50, 250);
    public static final Color COLOR_TYPE_7 = new Color(75, 75, 250);
    public static final Color COLOR_TYPE_6 = new Color(100, 100, 250);
    public static final Color COLOR_TYPE_5 = new Color(125, 125, 250);
    public static final Color COLOR_TYPE_4 = new Color(150, 150, 250);
    public static final Color COLOR_TYPE_3 = new Color(175, 175, 250);
    public static final Color COLOR_TYPE_2 = new Color(200, 200, 250);
    public static final Color COLOR_TYPE_1 = new Color(225, 225, 250);
    public static final Color COLOR_TYPE_10 = Color.BLUE;

    public static final Color COLOR_READY_10 = new Color(255, 255, 132);
    public static final Color COLOR_READY_30 = new Color(255, 255, 98);
    public static final Color COLOR_READY_50 = new Color(255, 255, 0);
    public static final Color COLOR_READY_70 = new Color(210, 210, 0);
    public static final Color COLOR_READY_90 = new Color(176, 176, 0);
    public static final Color COLOR_READY_100 = new Color(157, 157, 0);

    public static final int MAIN_PARTLIST = 0;
    public static final int SOURCE_PARTLIST = 1;

    private Object valueHolderDO;
    private long groesse;
    private ArrayList parts = new ArrayList();
    private int type;

    public PartListDO(DownloadDO downloadDO) {
        valueHolderDO = downloadDO;
        type = MAIN_PARTLIST;
    }

    public PartListDO(DownloadSourceDO downloadSourceDO) {
        valueHolderDO = downloadSourceDO;
        type = SOURCE_PARTLIST;
    }

    public int getPartListType() {
        return type;
    }

    public long getGroesse() {
        return groesse;
    }

    public void addPart(Part aPart) {
        parts.add(aPart);
    }

    public void removeAllParts(){
        parts.clear();
    }

    public Part[] getParts() {
        return (Part[]) parts.toArray(new Part[parts.size()]);
    }

    public void setGroesse(long groesse) {
        this.groesse = groesse;
    }

    public Object getValueDO() {
        return valueHolderDO;
    }

    public class Part {
        private long fromPosition;
        private int type;

        public Part(long fromPosition, int type) {
            this.fromPosition = fromPosition;
            this.type = type;
        }

        public long getFromPosition() {
            return fromPosition;
        }

        public int getType() {
            return type;
        }
    }
}
