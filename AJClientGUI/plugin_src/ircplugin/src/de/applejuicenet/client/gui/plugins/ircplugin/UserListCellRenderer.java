package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/UserListCellRenderer.java,v 1.9 2004/11/22 16:25:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */


public class UserListCellRenderer
    extends JLabel
    implements ListCellRenderer {

	public UserListCellRenderer(){
        setOpaque(true);
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setFont(list.getFont());
        if (value != null) {
            String inhalt = (String) value;
            setText(inhalt);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setForeground(list.getForeground());
                if (inhalt.length() != 0){
                    if (inhalt.substring(0, 1).compareTo("!") == 0) {
                        setBackground(Color.RED);
                    }
                    else if (inhalt.substring(0, 1).compareTo("@") == 0) {
                        setBackground(Color.YELLOW);
                    }
                    else if (inhalt.substring(0, 1).compareTo("%") == 0) {
                        setBackground(Color.GREEN);
                    }
                    else if (inhalt.substring(0, 1).compareTo("+") == 0) {
                        setBackground(Color.LIGHT_GRAY);
                    }
                    else {
                        setBackground(list.getBackground());
                    }
                }
                else {
                    setBackground(list.getBackground());
                }
            }
        }
        return this;
    }
}
