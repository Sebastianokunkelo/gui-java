package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.exception.LanguageSelectorNotInstanciatedException;
import de.applejuicenet.client.shared.ZeichenErsetzer;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class SearchPanel extends JPanel implements LanguageListener{
  JTable searchResultTable = new JTable();
  JButton btnStartStopSearch = new JButton("Suche starten");
  JTextField suchbegriff = new JTextField();
  int anzahlSuchanfragen = 0;
  private JLabel label1 = new JLabel("Suchbegriff: ");
  private JLabel label2 = new JLabel("0 Suchanfragen in Bearbeitung");

  public SearchPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    LanguageSelector.getInstance().addLanguageListener(this);
    JPanel panel3 = new JPanel();
    JPanel leftPanel = new JPanel();
    panel3.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    panel3.add(label1, constraints);
    constraints.gridx = 1;
    panel3.add(suchbegriff, constraints);
    constraints.gridy = 1;
    panel3.add(btnStartStopSearch, constraints);
    JPanel panel2 = new JPanel();
    panel2.setLayout(new FlowLayout());
    panel2.add(label2);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 2;
    panel3.add(panel2, constraints);
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(panel3, BorderLayout.NORTH);
    add(leftPanel, BorderLayout.WEST);

    searchResultTable.setModel(new SearchResultTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(searchResultTable);
    add(aScrollPane, BorderLayout.CENTER);
  }

  public void fireLanguageChanged() {
    try {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("mainform", "searchlbl", "caption")) + ": ");
      btnStartStopSearch.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("mainform", "searchbtn", "searchcaption")));

      String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("mainform", "opensearches", "caption"));
      temp = temp.replaceAll("%d", Integer.toString(anzahlSuchanfragen));
      label2.setText(temp);
    }
    catch (LanguageSelectorNotInstanciatedException ex) {
      ex.printStackTrace();
    }
  }
}