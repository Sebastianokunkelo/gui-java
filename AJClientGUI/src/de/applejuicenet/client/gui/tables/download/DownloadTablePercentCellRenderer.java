package de.applejuicenet.client.gui.tables.download;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadTablePercentCellRenderer.java,v 1.6 2004/03/09 16:50:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DownloadTablePercentCellRenderer
    implements TableCellRenderer, DataUpdateListener {
    private Settings settings;

    public DownloadTablePercentCellRenderer() {
        super();
        settings = Settings.getSettings();
        OptionsManagerImpl.getInstance().addSettingsListener(this);
    }

    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {

        Object node = ( (TreeTableModelAdapter) table.getModel()).nodeForRow(
            row);
        if (node.getClass() == DownloadSourceDO.class) {
            return getComponentForSource( (DownloadSourceDO) node, table, value,
                                         isSelected, hasFocus, row, column);
        }
        else if (node.getClass() == DownloadMainNode.class) {
            return getComponentForDownload( (DownloadMainNode) node, table,
                                           value, isSelected, hasFocus, row,
                                           column);
        }
        else if (node.getClass() == DownloadDirectoryNode.class) {
            return getComponentForDirectory( (DownloadDirectoryNode) node,
                                            table, value, isSelected, hasFocus,
                                            row, column);
        }
        else {
            return new JLabel("");
        }
    }

    public Component getComponentForDownload(DownloadMainNode downloadMainNode,
                                             JTable table,
                                             Object value,
                                             boolean isSelected,
                                             boolean hasFocus,
                                             int row,
                                             int column) {
        Component c = downloadMainNode.getProgressbarComponent(table, value);
        DownloadDO downloadDO = downloadMainNode.getDownloadDO();
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        }
        else {
            if (downloadMainNode.getType() == DownloadMainNode.ROOT_NODE &&
                downloadDO.getStatus() == DownloadDO.FERTIG &&
                settings.isFarbenAktiv()) {
                c.setBackground(settings.
                                     getDownloadFertigHintergrundColor());
            }
            else {
                c.setBackground(table.getBackground());
            }
        }
        return c;
    }

    public Component getComponentForSource(DownloadSourceDO downloadSourceDO,
                                           JTable table,
                                           Object value,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row,
                                           int column) {
        Component c = downloadSourceDO.getProgressbarComponent(table, value);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        }
        else {
            if (settings.isFarbenAktiv()) {
                c.setBackground(settings.getQuelleHintergrundColor());
            }
            else {
                c.setBackground(table.getBackground());
            }
        }
        return c;
    }

    public Component getComponentForDirectory(DownloadDirectoryNode downloadDirectoryNode,
                                              JTable table,
                                              Object value,
                                              boolean isSelected,
                                              boolean hasFocus,
                                              int row,
                                              int column) {
        Component c = downloadDirectoryNode.getProgressbarComponent(table, value);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        }
        else {
            c.setBackground(table.getBackground());
        }
        return c;
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED) {
            settings = (Settings) content;
        }
    }
}
