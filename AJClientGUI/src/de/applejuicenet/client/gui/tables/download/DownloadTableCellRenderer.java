package de.applejuicenet.client.gui.tables.download;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.gui.tables.download.DownloadNode;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadTableCellRenderer.java,v 1.7 2003/08/09 10:56:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadTableCellRenderer.java,v $
 * Revision 1.7  2003/08/09 10:56:38  maj0r
 * DownloadTabelle weitergef�hrt.
 *
 * Revision 1.6  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.5  2003/07/04 06:43:51  maj0r
 * Diverse �nderungen am DownloadTableModel.
 *
 * Revision 1.4  2003/07/03 19:11:16  maj0r
 * DownloadTable �berarbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett �berarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class DownloadTableCellRenderer
    implements TableCellRenderer {
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    DownloadNode node = (DownloadNode) ( (TreeTableModelAdapter) table.getModel()).
        nodeForRow(row);
    switch (node.getNodeType()){
        case DownloadNode.SOURCE_NODE:
            {
                return getComponentForSource(node, table, value, isSelected, hasFocus, row, column);
            }
        case DownloadNode.DOWNLOAD_NODE:
            {
                return getComponentForDownload(node, table, value, isSelected, hasFocus, row, column);
            }
        case DownloadNode.DIRECTORY_NODE:
            {
                return getComponentForDirectory(node, table, value, isSelected, hasFocus, row, column);
            }
        default:
            {
                return new JLabel("");
            }
    }
  }

  public Component getComponentForDownload(DownloadNode node, JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column){
    DownloadDO downloadDO = node.getDownloadDO();
    if (column == 6) {
        String prozent = downloadDO.getProzentGeladenAsString();
        String wert = null;
        int i;
        if ( (i = prozent.indexOf(".")) != -1) {
          wert = prozent.substring(0, i);
        }
        else{
          wert = prozent;
        }
        JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                                                 100);
        progress.setValue(Integer.parseInt(wert));
        progress.setString(prozent + " %");
        progress.setStringPainted(true);
        return progress;
      }
    else {
      JLabel label1 = new JLabel();
      label1.setOpaque(true);
      label1.setFont(table.getFont());
      label1.setText((String)value);
      if (isSelected) {
        label1.setBackground(table.getSelectionBackground());
        label1.setForeground(table.getSelectionForeground());
      }
      else {
          if (downloadDO.getStatus()==DownloadDO.FERTIGSTELLEN)
            label1.setBackground(DownloadNode.DOWNLOAD_FERTIG_COLOR);
          else
            label1.setBackground(table.getBackground());
          label1.setForeground(table.getForeground());
      }
      return label1;
    }
  }

    public Component getComponentForSource(DownloadNode node, JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column){
        DownloadSourceDO downloadSourceDO = node.getDownloadSourceDO();
        Color background = DownloadNode.SOURCE_NODE_COLOR;
        Color foreground = table.getForeground();
        if (column == 6) {
            String prozent = downloadSourceDO.getDownloadPercentAsString();
            JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                                                     100);
            int pos = prozent.indexOf('.');
            String balken = prozent;
            if (pos!=-1)
                balken = balken.substring(0, pos);
            progress.setValue(Integer.parseInt(balken));
            progress.setString(prozent + " %");
            progress.setStringPainted(true);
            return progress;
          }
        else if (column == 9) {
          JPanel returnPanel = new JPanel(new BorderLayout());
          JLabel image = new JLabel();

          long aktuelleZeit = System.currentTimeMillis();
          long tag = 24 * 60 * 60 * 1000;
          JLabel serverName = new JLabel();

          if (isSelected) {
            returnPanel.setBackground(table.getSelectionBackground());
            returnPanel.setForeground(table.getSelectionForeground());
            image.setBackground(table.getSelectionBackground());
            serverName.setBackground(table.getSelectionBackground());
            image.setForeground(table.getSelectionForeground());
            serverName.setBackground(table.getSelectionForeground());
          }
          else {
            returnPanel.setBackground(background);
            returnPanel.setForeground(foreground);
            image.setBackground(table.getBackground());
            serverName.setBackground(table.getBackground());
            image.setForeground(table.getForeground());
            serverName.setBackground(table.getForeground());
          }

          if (downloadSourceDO.getVersion() == null) {
            return returnPanel;
          }
          else {
              switch (downloadSourceDO.getVersion().getBetriebsSystem()){
                  case Version.WIN32:
                      {
                          image.setIcon(IconManager.getInstance().getIcon("winsymbol"));
                          break;
                      }
                  case Version.LINUX:
                      {
                          image.setIcon(IconManager.getInstance().getIcon("linuxsymbol"));
                          break;
                      }
                  default:
                      {
                          image.setIcon(IconManager.getInstance().getIcon("linuxsymbol"));
                      }

              }
          }
          serverName.setText("  " + downloadSourceDO.getVersion().getVersion());
          serverName.setFont(table.getFont());
          returnPanel.add(image, BorderLayout.WEST);
          returnPanel.add(serverName, BorderLayout.CENTER);
          return returnPanel;
        }
      else {
        JLabel label1 = new JLabel();
        label1.setText((String)value);
        label1.setFont(table.getFont());
        label1.setOpaque(true);
        if (isSelected) {
          label1.setBackground(table.getSelectionBackground());
          label1.setForeground(table.getSelectionForeground());
        }
        else {
          label1.setBackground(background);
          label1.setForeground(foreground);
        }
        return label1;
      }
    }

    public Component getComponentForDirectory(DownloadNode node, JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column){
        JLabel label1 = new JLabel();
        label1.setText((String)value);
        label1.setFont(table.getFont());
        label1.setOpaque(true);
        if (isSelected) {
          label1.setBackground(table.getSelectionBackground());
          label1.setForeground(table.getSelectionForeground());
        }
        else {
          label1.setBackground(table.getBackground());
          label1.setForeground(table.getForeground());
        }
        return label1;
    }

}