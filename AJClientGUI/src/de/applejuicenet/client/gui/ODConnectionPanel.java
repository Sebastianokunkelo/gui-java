package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import javax.swing.Icon;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODConnectionPanel.java,v 1.10 2004/01/25 10:16:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODConnectionPanel.java,v $
 * Revision 1.10  2004/01/25 10:16:42  maj0r
 * Optionenmenue ueberarbeitet.
 *
 * Revision 1.9  2004/01/03 17:29:01  maj0r
 * Dialog bei fehlgeschlagenem Verbindungsversuch �berarbeitet.
 *
 * Revision 1.8  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.7  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.6  2003/12/27 19:06:33  maj0r
 * Im Verbindungsfenster geht nun ein einfaches <Enter> (Danke an muhviestarr).
 *
 * Revision 1.5  2003/10/14 19:21:23  maj0r
 * Korrekturen zur Xml-Port-Verwendung.
 *
 * Revision 1.4  2003/10/14 15:43:52  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.3  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.2  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.1  2003/08/22 10:55:06  maj0r
 * Klassen umbenannt.
 *
 * Revision 1.4  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ODConnectionPanel
        extends JPanel implements OptionsRegister{
    private boolean dirty = false;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JTextField host = new JTextField();
    private JTextField port = new JTextField();
    private JPasswordField passwortAlt = new JPasswordField();
    private JPasswordField passwortNeu = new JPasswordField();
    private ConnectionSettings remote;
    private Logger logger;
    private boolean showPort = false;
    private QuickConnectionSettingsDialog quickConnectionSettingsDialog;
    private Icon menuIcon;
    private String menuText;

    public ODConnectionPanel(ConnectionSettings remote, QuickConnectionSettingsDialog quickConnectionSettingsDialog) {
        logger = Logger.getLogger(getClass());
        try
        {
            this.quickConnectionSettingsDialog = quickConnectionSettingsDialog;
            this.remote = remote;
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public ODConnectionPanel(ConnectionSettings remote,
                             QuickConnectionSettingsDialog quickConnectionSettingsDialog,
                             boolean showPort) {
        logger = Logger.getLogger(getClass());
        try
        {
            this.showPort = showPort;
            this.quickConnectionSettingsDialog = quickConnectionSettingsDialog;
            this.remote = remote;
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_passwort");
        JPanel panel1 = new JPanel(new GridBagLayout());
        FlowLayout flowL = new FlowLayout();
        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel2 = new JPanel(flowL);

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options", "remote",
                                                                                                     "host"})));
        label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options", "remote",
                                                                                                     "passwortAlt"})));
        label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options", "remote",
                                                                                                     "passwortNeu"})));
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
           getFirstAttrbuteByTagName(new String[]{"einstform", "pwsheet", "caption"}));
        label4 = new JLabel("Port");

        host.setText(remote.getHost());
        host.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (remote.getHost().compareTo(host.getText()) != 0)
                {
                    dirty = true;
                    remote.setHost(host.getText());
                }
            }
        });
        passwortAlt.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setOldPassword(new String(passwortAlt.getPassword()));
            }
        });
        passwortNeu.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setNewPassword(new String(passwortNeu.getPassword()));
            }
        });
        if (quickConnectionSettingsDialog != null){
            passwortNeu.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        dirty = true;
                        remote.setNewPassword(new String(passwortNeu.getPassword()));
                        quickConnectionSettingsDialog.pressOK();
                    }
                    else{
                        super.keyPressed(ke);
                    }
                }
            });
        }
        port.setDocument(new NumberInputVerifier());
        port.setText(Integer.toString(remote.getXmlPort()));
        port.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setXmlPort(Integer.parseInt(port.getText()));
            }
        });

        enableControls(true);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;

        panel1.add(label1, constraints);

        int gridy = 1;
        if (showPort){
            constraints.gridy = gridy;
            gridy++;
            panel1.add(label4, constraints);
        }
        constraints.gridy = gridy;
        gridy++;
        panel1.add(label2, constraints);

        constraints.gridy = gridy;
        gridy++;
        panel1.add(label3, constraints);

        constraints.insets.right = 5;
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(host, constraints);

        gridy = 1;
        if (showPort){
            constraints.gridy = gridy;
            gridy++;
            panel1.add(port, constraints);
        }
        constraints.gridy = gridy;
        gridy++;
        panel1.add(passwortAlt, constraints);

        constraints.gridy = gridy;
        gridy++;
        panel1.add(passwortNeu, constraints);

        constraints.gridy = gridy;
        gridy++;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        panel1.add(panel2, constraints);

        add(panel1, BorderLayout.NORTH);
        if (quickConnectionSettingsDialog != null){
            label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"einstform", "pwsheet", "caption"})));
            passwortAlt.setVisible(false);
            label2.setVisible(false);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void enableControls(boolean enable) {
        host.setEnabled(enable);
        passwortAlt.setEnabled(enable);
        passwortNeu.setEnabled(enable);
        label1.setEnabled(enable);
        label2.setEnabled(enable);
        label3.setEnabled(enable);
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }
}