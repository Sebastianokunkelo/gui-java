package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODPasswortPanel.java,v 1.3 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODPasswortPanel.java,v $
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ODPasswortPanel
    extends JPanel {
  private JLabel label1;
  private JLabel label2;
  private JLabel label3;
  private JTextField alt = new JTextField();
  private JTextField neu = new JTextField();
  private JTextField neu2 = new JTextField();
  private JButton btnAendern;

  public ODPasswortPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel panel1 = new JPanel(new GridBagLayout());

    LanguageSelector languageSelector = LanguageSelector.getInstance();

    label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label15",
                                  "caption"})));
    label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label16",
                                  "caption"})));

    label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label17",
                                  "caption"})));

    btnAendern = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Button3",
                                  "caption"})));

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.insets.top = 5;

    constraints.insets.left = 5;
    panel1.add(label1, constraints);
    constraints.gridy = 1;
    panel1.add(label2, constraints);
    constraints.gridy = 2;
    panel1.add(label3, constraints);

    constraints.insets.right = 5;
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 1;
    panel1.add(alt, constraints);
    constraints.gridy = 1;
    panel1.add(neu, constraints);
    constraints.gridy = 2;
    panel1.add(neu2, constraints);

    FlowLayout flowL = new FlowLayout();
    flowL.setAlignment(FlowLayout.RIGHT);
    JPanel panel2 = new JPanel(flowL);
    panel2.add(btnAendern);

    constraints.gridx = 0;
    constraints.gridwidth = 2;
    constraints.weightx = 0;
    constraints.gridy = 3;
    constraints.insets.right = 0;
    panel1.add(panel2, constraints);

    add(panel1, BorderLayout.NORTH);

  }
}