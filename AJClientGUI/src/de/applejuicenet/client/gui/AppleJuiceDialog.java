package de.applejuicenet.client.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.jeans.trayicon.SwingTrayPopup;
import com.jeans.trayicon.WindowsTrayIcon;
import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.tools.MemoryMonitorDialog;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ServerDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/AppleJuiceDialog.java,v 1.121 2004/06/01 14:26:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 *
 */

public class AppleJuiceDialog
    extends JFrame
    implements LanguageListener, DataUpdateListener {

    private static Logger logger = Logger.getLogger(AppleJuiceDialog.class);

    private RegisterPanel registerPane;
    private JLabel[] statusbar = new JLabel[6];
    private JMenu sprachMenu;
    private JMenu optionenMenu;
    private JMenu themesMenu = null;
    private JMenu coreMenu;
    private Set plugins;
    private JMenuItem menuItemOptionen = new JMenuItem();
    private JMenuItem menuItemDateiliste = new JMenuItem();
    private JMenuItem menuItemCheckUpdate = new JMenuItem();
    private JMenuItem menuItemCoreBeenden = new JMenuItem();
    private JMenuItem menuItemUeber = new JMenuItem();
    private JMenuItem menuItemDeaktivieren = new JMenuItem();
    private JMenuItem menuItemAktivieren = new JMenuItem();
    private JMenuItem popupOptionenMenuItem = new JMenuItem("Optionen");
    private JMenuItem popupAboutMenuItem = new JMenuItem("&Info");
    private JMenuItem popupShowHideMenuItem = new JMenuItem("%Show");
    private JMenuItem popupCheckUpdateMenuItem = new JMenuItem("Update");
    private JButton sound = new JButton();
    private JButton memory = new JButton();
    private String keinServer = "";
    public static boolean rewriteProperties = false;
    private boolean firstChange = true;
    private MemoryMonitorDialog memoryMonitorDialog;
    private static Map themes = new HashMap();
    private String themeSupportTitel;
    private String themeSupportNachricht;
    private boolean automaticPwdlEnabled = false;
    private String titel;
    private static boolean themesInitialized = false;
    private String bestaetigung = "";
    private int desktopHeight;
    private int desktopWidth;
    private boolean maximized = false;
    private Dimension lastFrameSize;
    private Point lastFrameLocation;

    private static boolean useTrayIcon = false;
    private String zeigen = "";
    private String verstecken = "";
    private WindowsTrayIcon trayIcon;
    private Icon versteckenIcon = null;
    private Icon zeigenIcon = null;

    private static AppleJuiceDialog theApp;

    public static void initThemes() {
        try {
            themesInitialized = true;
            if (OptionsManagerImpl.getInstance().isThemesSupported()) {
                HashSet themesDateien = new HashSet();
                File themesPath = new File(System.getProperty("user.dir") +
                                           File.separator + "themes");
                if (!themesPath.isDirectory()) {
                    if (logger.isEnabledFor(Level.INFO)) {
                        logger.info(
                            "Der Ordner" +
                                          " fuer die Themes zip-Dateien ist nicht vorhanden." +
                                          "\r\nappleJuice wird beendet.");
                    }
                    closeWithErrormessage("Der Ordner" +
                                          " fuer die Themes zip-Dateien ist nicht vorhanden." +
                                          "\r\nappleJuice wird beendet.", false);
                }
                File[] themeFiles = themesPath.listFiles();
                for (int i = 0; i < themeFiles.length; i++) {
                    if (themeFiles[i].isFile() &&
                        themeFiles[i].getName().indexOf(".zip") != -1) {
                        //testen, ob es wirklich ein skinfile ist
                        ZipFile jf = new ZipFile(themeFiles[i]);
                        ZipEntry entry = jf.getEntry("skinlf-themepack.xml");
                        if (entry!=null){
                            themesDateien.add(themeFiles[i].toURL());
                        }
                    }
                }
                Iterator it = themesDateien.iterator();
                Skin standardSkin = null;
                Skin aSkin = null;
                String temp;
                String shortName = "";
                String defaultTheme = OptionsManagerImpl.getInstance().
                    getDefaultTheme();
                while (it.hasNext()) {
                    URL skinUrl = (URL) it.next();
                    temp = skinUrl.getFile();
                    int index1 = temp.lastIndexOf('/');
                    int index2 = temp.lastIndexOf(".zip");
                    if (index1 == -1 || index2 == -1) {
                        continue;
                    }
                    shortName = temp.substring(index1 + 1, index2);
                    aSkin = SkinLookAndFeel.loadThemePack(skinUrl);
                    themes.put(shortName, aSkin);
                    if (shortName.compareToIgnoreCase(defaultTheme) == 0) {
                        standardSkin = aSkin;
                    }
                }
                if (standardSkin == null) {
                    standardSkin = aSkin;
                }
                SkinLookAndFeel.setSkin(standardSkin);
                SkinLookAndFeel.enable();
            }
            else {
                LookAFeel defaultlookandfeel = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();
                if (defaultlookandfeel != null) {
                    UIManager.setLookAndFeel(defaultlookandfeel.getClassName());
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public AppleJuiceDialog() {
        super();
        try {
            theApp = this;
            init();
            pack();
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void addPluginToHashSet(PluginConnector plugin) {
        plugins.add(plugin);
    }

    public PluginConnector[] getPlugins() {
        return (PluginConnector[]) plugins.toArray(new PluginConnector[plugins.
            size()]);
    }

    public static AppleJuiceDialog getApp() {
        return theApp;
    }

    private void init() throws Exception {
        titel = "appleJuice Client (GUI " + ApplejuiceFassade.GUI_VERSION + ")";
        IconManager im = IconManager.getInstance();
        Image image = im.getIcon("applejuice").getImage();
        setTitle(titel);
        String osName = System.getProperty("os.name");
        plugins = new HashSet();
        setIconImage(image);
        menuItemOptionen.setIcon(im.getIcon("optionen"));
        menuItemUeber.setIcon(im.getIcon("info"));
        menuItemCoreBeenden.setIcon(im.getIcon("skull"));
        menuItemDateiliste.setIcon(im.getIcon("speichern"));
        menuItemCheckUpdate.setIcon(im.getIcon("update"));

        setJMenuBar(createMenuBar());
        if (OptionsManagerImpl.getInstance().isThemesSupported()) {
            SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
        }

        String path = System.getProperty("user.dir") + File.separator +
            "language" +
            File.separator;
        path += OptionsManagerImpl.getInstance().getSprache() + ".xml";
        if (AppleJuiceClient.splash != null){
            AppleJuiceClient.splash.setProgress(25, "Initialisiere Sprache...");
        }
        LanguageSelector languageSelector = LanguageSelector.getInstance(path);
        if (AppleJuiceClient.splash != null){
            AppleJuiceClient.splash.setProgress(30, "Erstelle Register...");
        }
        registerPane = new RegisterPanel(this);
        languageSelector.fireLanguageChanged();
        if (AppleJuiceClient.splash != null){
            AppleJuiceClient.splash.setProgress(95, "Register erstellt..");
        }
        addWindowListener(
            new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        desktopWidth = screenSize.width;
        desktopHeight = screenSize.height;
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int x = getWidth();
                int y = getHeight();
                if (x == desktopWidth && y == desktopHeight) {
                    if (maximized) {
                        demaximize();
                    }
                    else {
                        maximize();
                    }
                }
                else {
                    if (!maximized) {
                        lastFrameSize = getSize();
                        lastFrameLocation = getLocation();
                    }
                    super.componentResized(e);
                }
            }

            public void componentMoved(ComponentEvent e) {
                if (!maximized) {
                    lastFrameLocation = getLocation();
                }
                super.componentMoved(e);
            }
        });

        if (osName.toLowerCase().indexOf("win") != -1) {
            try {
                if (!WindowsTrayIcon.isRunning(titel)) {
                    useTrayIcon = true;
                    WindowsTrayIcon.initTrayIcon(titel);
                    trayIcon = new WindowsTrayIcon(image, 16, 16);
                    trayIcon.setVisible(true);
                    trayIcon.setToolTipText(titel);
                    trayIcon.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent evt) {
                            if (!isVisible()) {
                                popupShowHideMenuItem.setText(zeigen);
                                popupShowHideMenuItem.setIcon(zeigenIcon);
                            }
                            else {
                                popupShowHideMenuItem.setText(verstecken);
                                popupShowHideMenuItem.setIcon(versteckenIcon);
                            }
                            if ( (evt.getModifiers() & MouseEvent.BUTTON1_MASK) !=
                                0 &&
                                evt.getClickCount() == 2) {
                                AppleJuiceDialog dialog = AppleJuiceDialog.
                                    getApp();
                                if (!isVisible()) {
                                    dialog.setVisible(true);
                                    dialog.requestFocus();
                                }
                                else {
                                    setVisible(false);
                                }
                            }
                        }
                    });
                    SwingTrayPopup popup = makeSwingPopup();
                    popup.setTrayIcon(trayIcon);
                }
            }
            catch (UnsatisfiedLinkError error) {
                languageSelector = LanguageSelector.
                    getInstance();
                String fehlerTitel = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.getFirstAttrbuteByTagName(".root.mainform.caption"));

                String fehlerNachricht = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.getFirstAttrbuteByTagName(".root.javagui.startup.trayfehler"));

                JOptionPane.showMessageDialog(this, fehlerNachricht,
                                              fehlerTitel,
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
        getContentPane().setLayout(new BorderLayout());
        registerPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RegisterI register = (RegisterI) registerPane.
                    getSelectedComponent();
                register.registerSelected();
            }
        });
        getContentPane().add(registerPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());

        for (int i = 0; i < statusbar.length; i++) {
            statusbar[i] = new JLabel("            ");
            statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
            statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
        }
        memory.setIcon(IconManager.getInstance().getIcon("mmonitor"));
        memory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (memoryMonitorDialog == null) {
                    memoryMonitorDialog = new MemoryMonitorDialog(
                        AppleJuiceDialog.this);
                    Point loc = memory.getLocationOnScreen();
                    loc.setLocation(loc.getX() - memoryMonitorDialog.getWidth(),
                                    loc.getY() - memoryMonitorDialog.getHeight());
                    memoryMonitorDialog.setLocation(loc);
                }
                if (!memoryMonitorDialog.isVisible()) {
                    memoryMonitorDialog.show();
                }
            }
        });

        sound.addActionListener(new ActionListener() {
            {
                if (OptionsManagerImpl.getInstance().isSoundEnabled()) {
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                }
                else {
                    sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
                }
            }

            public void actionPerformed(ActionEvent ae) {
                OptionsManager om = OptionsManagerImpl.getInstance();
                om.enableSound(!om.isSoundEnabled());
                if (om.isSoundEnabled()) {
                    sound.setIcon(IconManager.getInstance().getIcon("soundon"));
                }
                else {
                    sound.setIcon(IconManager.getInstance().getIcon("soundoff"));
                }
            }
        });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(statusbar[0], constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(statusbar[1], constraints);
        constraints.weightx = 0;
        constraints.gridx = 2;
        panel.add(statusbar[2], constraints);
        constraints.gridx = 3;
        panel.add(statusbar[3], constraints);
        constraints.gridx = 4;
        panel.add(statusbar[4], constraints);
        constraints.gridx = 5;
        panel.add(statusbar[5], constraints);
        constraints.gridx = 6;
        panel.add(memory, constraints);
        constraints.gridx = 7;
        panel.add(sound, constraints);
        getContentPane().add(panel, BorderLayout.SOUTH);

        //Tooltipps einstellen
        ToolTipManager.sharedInstance().setInitialDelay(1);
        ToolTipManager.sharedInstance().setDismissDelay(50000);
        fireLanguageChanged();
        ApplejuiceFassade dm = ApplejuiceFassade.getInstance();
        dm.addDataUpdateListener(this,
                                 DataUpdateListener.INFORMATION_CHANGED);
        dm.startXMLCheck();
    }

    private void demaximize() {
        setSize(lastFrameSize);
        setLocation(lastFrameLocation);
        maximized = false;
    }

    private void maximize() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        Insets insets = tk.getScreenInsets(getGraphicsConfiguration());
        screenSize.width -= (insets.left + insets.right);
        screenSize.height -= (insets.top + insets.bottom);
        setSize(screenSize);
        setLocation(insets.left, insets.top);
        maximized = true;
    }

    private static void einstellungenSpeichern() {
        try {
            String sprachText = LanguageSelector.getInstance().
                getFirstAttrbuteByTagName(".root.Languageinfo.name");
            OptionsManagerImpl.getInstance().setSprache(sprachText);
            int[] downloadWidths = DownloadPanel.getInstance().getColumnWidths();
            int[] uploadWidths = UploadPanel.getInstance().getColumnWidths();
            int[] serverWidths = ServerPanel.getInstance().getColumnWidths();
            int[] shareWidths = SharePanel.getInstance().getColumnWidths();
            Dimension dim = AppleJuiceDialog.getApp().getSize();
            Point p = AppleJuiceDialog.getApp().getLocationOnScreen();
            PositionManager pm = PositionManagerImpl.getInstance();
            pm.setMainXY(p);
            pm.setMainDimension(dim);
            pm.setDownloadWidths(downloadWidths);
            pm.setUploadWidths(uploadWidths);
            pm.setServerWidths(serverWidths);
            pm.setShareWidths(shareWidths);
            pm.save();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void informAutomaticPwdlEnabled(boolean enabled) {
        if (enabled != automaticPwdlEnabled) {
            automaticPwdlEnabled = enabled;
            setTitle(titel);
            repaint();
        }
    }

    private void closeDialog(WindowEvent evt) {
        ApplejuiceFassade.getInstance().stopXMLCheck();
        if (rewriteProperties) {
            restorePropertiesXml();
        }
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        if (logger.isEnabledFor(Level.INFO)) {
            logger.info(nachricht);
        }
        System.out.println(nachricht);
        if (!rewriteProperties) {
            einstellungenSpeichern();
        }
        setVisible(false);
        if (useTrayIcon) {
            WindowsTrayIcon.cleanUp();
        }
        System.exit(0);
    }

    public static void closeWithErrormessage(String error,
                                             boolean speichereEinstellungen) {
        JOptionPane.showMessageDialog(theApp, error, "appleJuice Client",
                                      JOptionPane.OK_OPTION);
        if (rewriteProperties) {
            restorePropertiesXml();
        }
        else {
            ApplejuiceFassade.getInstance().stopXMLCheck();
        }
        String nachricht = "appleJuice-Core-GUI wird beendet...";
        Logger aLogger = Logger.getLogger(AppleJuiceDialog.class.getName());
        if (aLogger.isEnabledFor(Level.INFO)) {
            aLogger.info(nachricht);
        }
        System.out.println(nachricht);
        if (speichereEinstellungen && !rewriteProperties) {
            einstellungenSpeichern();
        }
        System.out.println("Fehler: " + error);
        if (useTrayIcon) {
            WindowsTrayIcon.cleanUp();
        }
        System.exit( -1);
    }

    protected JMenuBar createMenuBar() {
        try {
            if (!themesInitialized) {
                AppleJuiceDialog.initThemes();
            }
            String path = System.getProperty("user.dir") + File.separator +
                "language" +
                File.separator;
            File languagePath = new File(path);
            if (!languagePath.isDirectory()) {
                if (logger.isEnabledFor(Level.INFO)) {
                    logger.info("Der Ordner " + path +
                                      " f\uFFFDr die Sprachauswahl xml-Dateien ist nicht vorhanden." +
                                      "\r\nappleJuice wird beendet.");
                }
                closeWithErrormessage("Der Ordner " + path +
                                      " f\uFFFDr die Sprachauswahl xml-Dateien ist nicht vorhanden." +
                                      "\r\nappleJuice wird beendet.", false);
            }
            String[] tempListe = languagePath.list();
            HashSet sprachDateien = new HashSet();
            for (int i = 0; i < tempListe.length; i++) {
                if (tempListe[i].indexOf(".xml") != -1) {
                    sprachDateien.add(tempListe[i]);
                }
            }
            if (sprachDateien.size() == 0) {
                if (logger.isEnabledFor(Level.INFO)) {
                    logger.info(
                        "Es sind keine xml-Dateien fuer die Sprachauswahl im Ordner " +
                        path + " vorhanden." +
                        "\r\nappleJuice wird beendet.");
                }
                closeWithErrormessage(
                    "Es sind keine xml-Dateien fuer die Sprachauswahl im Ordner " +
                    path + " vorhanden." +
                    "\r\nappleJuice wird beendet.", false);
            }

            JMenuBar menuBar = new JMenuBar();
            optionenMenu = new JMenu("Extras");
            menuItemOptionen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showOptionsDialog();
                }
            });
            menuItemDateiliste.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dateiListeImportieren();
                }
            });
            menuItemCheckUpdate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    checkAndDisplayUpdate();
                }
            });
            menuItemCoreBeenden.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.
                        getApp(), bestaetigung,
                        "appleJuice Client", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        ApplejuiceFassade.getInstance().exitCore();
                    }
                }
            });
            menuItemUeber.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showAboutDialog();
                }
            });
            optionenMenu.add(menuItemOptionen);
            optionenMenu.add(menuItemDateiliste);
            optionenMenu.add(menuItemCheckUpdate);
            optionenMenu.add(menuItemUeber);
            menuBar.add(optionenMenu);

            sprachMenu = new JMenu("Sprache");
            menuBar.add(sprachMenu);
            ButtonGroup lafGroup = new ButtonGroup();

            Iterator it = sprachDateien.iterator();
            while (it.hasNext()) {
                String sprachText = LanguageSelector.getInstance(path +
                    (String) it.next()).
                    getFirstAttrbuteByTagName(".root.Languageinfo.name");
                JCheckBoxMenuItem rb = new JCheckBoxMenuItem(sprachText);
                if (OptionsManagerImpl.getInstance().getSprache().
                    equalsIgnoreCase(sprachText)) {
                    rb.setSelected(true);
                }
                Image img = Toolkit.getDefaultToolkit().getImage(path +
                    sprachText.toLowerCase() + ".gif");
                ImageIcon result = new ImageIcon();
                result.setImage(img);
                rb.setIcon(result);

                sprachMenu.add(rb);
                rb.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ae) {
                        JCheckBoxMenuItem rb2 = (JCheckBoxMenuItem) ae.
                            getSource();
                        if (rb2.isSelected()) {
                            String path = System.getProperty("user.dir") +
                                File.separator +
                                "language" + File.separator;
                            String dateiName = path + rb2.getText().toLowerCase() +
                                ".xml";
                            LanguageSelector.getInstance(dateiName);
                        }
                    }
                });
                lafGroup.add(rb);
            }
            themesMenu = new JMenu("Themes");
            if (OptionsManagerImpl.getInstance().isThemesSupported()) {
                HashSet themesDateien = new HashSet();
                File themesPath = new File(System.getProperty("user.dir") +
                                           File.separator + "themes");
                if (!themesPath.isDirectory()) {
                    if (logger.isEnabledFor(Level.INFO)) {
                        logger.info("Der Ordner " + path +
                            " fuer die Themes zip-Dateien ist nicht vorhanden." +
                            "\r\nappleJuice wird beendet.");
                    }
                    closeWithErrormessage("Der Ordner " + path +
                                          " fuer die Themes zip-Dateien ist nicht vorhanden." +
                                          "\r\nappleJuice wird beendet.", false);
                }
                File[] themeFiles = themesPath.listFiles();
                for (int i = 0; i < themeFiles.length; i++) {
                    if (themeFiles[i].isFile() &&
                        themeFiles[i].getName().indexOf(".zip") != -1) {
                        //testen, ob es wirklich ein skinfile ist
                        ZipFile jf = new ZipFile(themeFiles[i]);
                        ZipEntry entry = jf.getEntry("skinlf-themepack.xml");
                        if (entry!=null){
                            themesDateien.add(themeFiles[i].toURL());
                        }
                    }
                }
                it = themesDateien.iterator();
                ButtonGroup lafGroup2 = new ButtonGroup();
                String temp;
                String shortName = "";
                String defaultTheme = OptionsManagerImpl.getInstance().
                    getDefaultTheme();
                while (it.hasNext()) {
                    URL skinUrl = (URL) it.next();
                    temp = skinUrl.getFile();
                    int index1 = temp.lastIndexOf('/');
                    int index2 = temp.lastIndexOf(".zip");
                    if (index1 == -1 || index2 == -1) {
                        continue;
                    }
                    shortName = temp.substring(index1 + 1, index2);
                    final JCheckBoxMenuItem rb = new JCheckBoxMenuItem(
                        shortName);
                    if (shortName.compareToIgnoreCase(defaultTheme) == 0) {
                        rb.setSelected(true);
                    }
                    rb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent ae) {
                            if (rb.isSelected()) {
                                activateLaF(rb.getText());
                            }
                        }
                    });
                    lafGroup2.add(rb);
                    themesMenu.add(rb);
                }
                themesMenu.add(new JSeparator());
                menuItemDeaktivieren.setText("deaktivieren");
                menuItemDeaktivieren.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ce) {
                        activateThemeSupport(false);
                    }
                });
                themesMenu.add(menuItemDeaktivieren);
            }
            else {
                final LookAFeel[] feels = OptionsManagerImpl.getInstance().getLookAndFeels();
                LookAFeel defaultlookandfeel = OptionsManagerImpl.getInstance().getDefaultLookAndFeel();
                ButtonGroup lafGroup2 = new ButtonGroup();
                for (int i=0; i<feels.length; i++){
                    final JCheckBoxLookAndFeelMenuItem lookAndFeelMenuItem = new JCheckBoxLookAndFeelMenuItem(feels[i]);
                    lafGroup2.add(lookAndFeelMenuItem);
                    themesMenu.add(lookAndFeelMenuItem);
                    if (defaultlookandfeel != null && feels[i].getName().equals(defaultlookandfeel.getName())) {
                        lookAndFeelMenuItem.setSelected(true);
                    }
                    else {
                        lookAndFeelMenuItem.setSelected(false);
                    }
                    lookAndFeelMenuItem.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent ae) {
                            if (lookAndFeelMenuItem.isSelected()) {
                                activateLaF(lookAndFeelMenuItem.getText());
                            }
                        }
                    });
                }
                menuItemAktivieren.setText("aktivieren");
                menuItemAktivieren.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ce) {
                        activateThemeSupport(true);
                    }
                });
                themesMenu.add(new JSeparator());
                themesMenu.add(menuItemAktivieren);
            }
            menuBar.add(themesMenu);
            coreMenu = new JMenu("Core");
            coreMenu.add(menuItemCoreBeenden);
            menuBar.add(coreMenu);
            return menuBar;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return null;
        }
    }

    private void showOptionsDialog() {
        OptionsDialog od = new OptionsDialog(getApp());
        Dimension optDimension = od.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        od.setLocation( (screenSize.width -
                         optDimension.width) / 2,
                       (screenSize.height -
                        optDimension.height) / 2);
        od.show();
    }

    private void showAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(getApp(), true);
        Dimension appDimension = aboutDialog.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        aboutDialog.setLocation( (screenSize.width -
                                  appDimension.width) / 2,
                                (screenSize.height -
                                 appDimension.height) / 2);
        aboutDialog.show();
    }

    private void activateThemeSupport(boolean enable) {
        int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.this,
            themeSupportNachricht, themeSupportTitel, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            OptionsManagerImpl.getInstance().
                enableThemeSupport(enable);
            closeDialog(null);
        }
    }

    private void activateLaF(String laf) {
        try {
            // theme???
            Skin aSkin = (Skin) themes.get(laf);
            if (aSkin != null) {
                SkinLookAndFeel.setSkin(aSkin);
                SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
                OptionsManagerImpl.getInstance().setDefaultTheme(laf);
                return;
            }
            // laf???
            final LookAFeel[] feels = OptionsManagerImpl.getInstance().
                getLookAndFeels();
            if (feels != null && laf != null) {
                for (int i = 0; i < feels.length; i++) {
                    if (laf.equals(feels[i].getName())) {
                        OptionsManagerImpl.getInstance().setDefaultLookAndFeel(
                            feels[i]);
                        return;
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void setTitle(String newTitle) {
        if (!automaticPwdlEnabled) {
            super.setTitle(newTitle);
        }
        else {
            super.setTitle(newTitle + " - Autopilot");
        }
        titel = newTitle;
    }

    private void dateiListeImportieren() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new TxtFileFilter());
        fileChooser.setDialogTitle(menuItemDateiliste.getText());
        fileChooser.setMultiSelectionEnabled(false);
        int i = fileChooser.showOpenDialog(this);
        if (i == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            if (file.isFile()) {
                new Thread(){
                    public void run(){
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new
                                                        FileReader(file));
                            String line = "";
                            while ( (line = reader.readLine()) != null) {
                                if (line.compareTo("100") == 0) {
                                    break;
                                }
                            }
                            String size = "";
                            String filename = "";
                            String checksum = "";
                            String link = "";
                            ApplejuiceFassade af = ApplejuiceFassade.getInstance();
                            while ( (line = reader.readLine()) != null) {
                                filename = line;
                                checksum = reader.readLine();
                                size = reader.readLine();
                                if (size != null && checksum != null) {
                                    link = "ajfsp://file|" + filename + "|" + checksum +
                                        "|" + size + "/";
                                    af.processLink(link);
                                }
                            }
                        }
                        catch (FileNotFoundException ex) {
                            ;
                            //nix zu tun
                        }
                        catch (IOException ex1) {
                            ;
                            //nix zu tun
                        }
                    }
                }.start();
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            keinServer = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.mainform.keinserver"));
            themeSupportTitel = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.caption"));
            themeSupportNachricht = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.mainform.themesupportnachricht"));
            sprachMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.einstform.languagesheet.caption")));
            menuItemOptionen.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.optbtn.caption")));
            menuItemOptionen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.optbtn.hint")));
            menuItemCoreBeenden.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.corebeenden")));
            menuItemCoreBeenden.setToolTipText(ZeichenErsetzer.
                                               korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.corebeendenhint")));
            menuItemUeber.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.aboutbtn.caption")));
            menuItemCheckUpdate.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.checkupdate.caption")));
            menuItemCheckUpdate.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.checkupdate.hint")));
            menuItemDeaktivieren.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.deaktivieren")));
            optionenMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.extras")));
            menuItemDateiliste.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.dateiliste")));
            menuItemDateiliste.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.dateilistehint")));
            themesMenu.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.themes")));
            bestaetigung = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.bestaetigung"));
            menuItemAktivieren.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.aktivieren")));
            menuItemDeaktivieren.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.menu.deaktivieren")));

            if (useTrayIcon) {
                popupAboutMenuItem.setText(menuItemUeber.getText());
                popupAboutMenuItem.setToolTipText(menuItemUeber.getToolTipText());
                zeigen = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.
                    getFirstAttrbuteByTagName(".root.javagui.menu.zeigen"));
                verstecken = ZeichenErsetzer.korrigiereUmlaute(
                    languageSelector.
                    getFirstAttrbuteByTagName(".root.javagui.menu.verstecken"));
                popupOptionenMenuItem.setText(menuItemOptionen.getText());
                popupOptionenMenuItem.setToolTipText(menuItemOptionen.
                    getToolTipText());
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.INFORMATION_CHANGED) {
                if (firstChange) {
                    firstChange = false;
                    SoundPlayer.getInstance().playSound(SoundPlayer.GESTARTET);
                    String versionsNr = ApplejuiceFassade.getInstance().getCoreVersion().
                        getVersion();
                    titel = ZeichenErsetzer.korrigiereUmlaute(LanguageSelector.getInstance().
                        getFirstAttrbuteByTagName(".root.mainform.caption")) +
                        " (Core " + versionsNr +
                        " - GUI " + ApplejuiceFassade.GUI_VERSION + ")";
                    setTitle(titel);
                    if (useTrayIcon) {
                        trayIcon.setToolTipText(titel);
                    }
                    repaint();
                }
                Information information = (Information) content;
                statusbar[0].setText(information.getVerbindungsStatusAsString());
                if (information.getVerbindungsStatus() ==
                    Information.NICHT_VERBUNDEN) {
                    statusbar[1].setText(keinServer);
                }
                else {
                    String tmp = information.getServerName();
                    if (tmp == null || tmp.length() == 0 ){
                        ServerDO serverDO = information.getServerDO();
                        if (serverDO != null){
                            tmp = serverDO.getHost() + ":" +
                                serverDO.getPort();
                        }
                    }
                    statusbar[1].setText(tmp);
                }
                statusbar[2].setText(information.getUpDownAsString());
                statusbar[3].setText(information.getUpDownSessionAsString());
                statusbar[4].setText(information.getExterneIP());
                statusbar[5].setText(information.getCreditsAsString());
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public SwingTrayPopup makeSwingPopup() {
        final SwingTrayPopup popup = new SwingTrayPopup();
        popupShowHideMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!isVisible()) {
                    setVisible(true);
                    toFront();
                    requestFocus();
                }
                else {
                    setVisible(false);
                }
            }
        });
        popup.add(popupShowHideMenuItem);
        popupOptionenMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showOptionsDialog();
            }
        });

        popup.add(popupOptionenMenuItem);
        IconManager im = IconManager.getInstance();
        versteckenIcon = im.getIcon("hide");
        zeigenIcon = im.getIcon("applejuice");
        Icon aboutIcon = im.getIcon("about");
        popupOptionenMenuItem.setIcon(im.getIcon("optionen"));
        popupAboutMenuItem.setIcon(aboutIcon);
        popupAboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showAboutDialog();
            }
        });
        popup.add(popupAboutMenuItem);
        new Thread() {
            public void run() {
                final AJSettings ajSettings = ApplejuiceFassade.getInstance().
                    getAJSettings();
                if ( ajSettings != null){
                    final JSlider uploadSlider = new JSlider(JSlider.VERTICAL,
                        0,
                        50, (int) ajSettings.getMaxUploadInKB());
                    final JSlider downloadSlider = new JSlider(JSlider.VERTICAL,
                        0,
                        300, (int) ajSettings.getMaxDownloadInKB());
                    uploadSlider.setPaintLabels(true);
                    uploadSlider.setPaintTicks(true);
                    uploadSlider.setPaintTrack(true);
                    uploadSlider.setSnapToTicks(true);
                    downloadSlider.setPaintLabels(true);
                    downloadSlider.setPaintTicks(true);
                    downloadSlider.setPaintTrack(true);
                    downloadSlider.setSnapToTicks(true);
                    final JMenu uploadMenu = new JMenu("Upload");
                    final JMenu downloadMenu = new JMenu("Download");
                    JPanel uploadPanel = new JPanel(new BorderLayout());
                    JPanel downloadPanel = new JPanel(new BorderLayout());
                    final JLabel label1 = new JLabel("50 kb/s");
                    final JLabel label2 = new JLabel("50 kb/s");
                    label1.setText(Long.toString(ajSettings.getMaxUploadInKB()) +
                                   " kb/s");
                    label2.setText(Long.toString(ajSettings.getMaxDownloadInKB()) +
                                   " kb/s");
                    uploadPanel.add(label1, BorderLayout.NORTH);
                    uploadPanel.add(uploadSlider, BorderLayout.SOUTH);
                    uploadMenu.add(uploadPanel);
                    downloadPanel.add(label2, BorderLayout.NORTH);
                    downloadPanel.add(downloadSlider, BorderLayout.SOUTH);
                    downloadMenu.add(downloadPanel);
                    uploadSlider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            JSlider slider = (JSlider) e.getSource();
                            label1.setText(Integer.toString(slider.getValue()) +
                                           " kb/s");
                        }
                    });
                    downloadSlider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            JSlider slider = (JSlider) e.getSource();
                            label2.setText(Integer.toString(slider.getValue()) +
                                           " kb/s");
                        }
                    });
                    uploadSlider.addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            if (uploadSlider.getValue() <
                                uploadSlider.getMaximum()
                                && uploadSlider.getValue() > 0) {
                                long down = downloadSlider.getValue() * 1024;
                                long up = uploadSlider.getValue() * 1024;
                                ApplejuiceFassade.getInstance().setMaxUpAndDown(
                                    up,
                                    down);
                            }
                            else {
                                uploadSlider.setValue( (int) ajSettings.
                                    getMaxUploadInKB());
                            }
                        }
                    });
                    downloadSlider.addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            if (downloadSlider.getValue() <
                                downloadSlider.getMaximum()
                                && downloadSlider.getValue() > 0) {
                                long down = downloadSlider.getValue() * 1024;
                                long up = uploadSlider.getValue() * 1024;
                                ApplejuiceFassade.getInstance().setMaxUpAndDown(
                                    up,
                                    down);
                            }
                            else {
                                downloadSlider.setValue( (int) ajSettings.
                                    getMaxDownloadInKB());
                            }
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            popup.add(uploadMenu);
                            popup.add(downloadMenu);
                        }
                    });
                }
            }
        }

        .start();
        return popup;
    }

    public static void restorePropertiesXml() {
        String dateiname = ApplejuiceFassade.getPropertiesPath();
        StringBuffer xmlData = new StringBuffer();

        xmlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        xmlData.append("<root>\r\n");
        xmlData.append(
            "    <options dialogzeigen=\"true\" firststart=\"true\" sound=\"true\" sprache=\"deutsch\" ");
        xmlData.append(
            "themes=\"false\" defaulttheme=\"toxicthemepack\" loadplugins=\"true\" ");
        xmlData.append(
            "linklistenerport=\"8768\" versionsinfo=\"1\" >\r\n");
        xmlData.append(
            "        <remote host=\"localhost\" passwort=\"\"  port=\"9851\"/>\r\n");
        xmlData.append(
            "        <remote0 host=\"localhost\" port=\"9851\"/>\r\n");
        xmlData.append(
            "        <remote1 host=\"\" port=\"0\"/>\r\n");
        xmlData.append(
            "        <remote2 host=\"\" port=\"0\"/>\r\n");
        xmlData.append("        <logging level=\"INFO\"/>\r\n");
        xmlData.append("        <download uebersicht=\"true\"/>\r\n");
        xmlData.append("        <farben aktiv=\"true\">\r\n");
        xmlData.append(
            "            <hintergrund downloadFertig=\"-13382656\" quelle=\"-205\"/>\r\n");
        xmlData.append("        </farben>\r\n");
        xmlData.append("                <lookandfeels>\r\n");
        xmlData.append("                    <laf1 name=\"JGoodies Plastic\" value=\"com.jgoodies.plaf.plastic.Plastic3DLookAndFeel\"/>\r\n");
        int index = 2;
        if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1) {
            xmlData.append("                    <laf" + index + " name=\"JGoodies Windows\" value=\"com.jgoodies.plaf.windows.ExtWindowsLookAndFeel\"/>\r\n");
            index++;
        }
        LookAndFeelInfo[] feels = UIManager.getInstalledLookAndFeels();
        LookAndFeel currentFeel = UIManager.getLookAndFeel();
        for (int i=0; i<feels.length; i++){
            try{
                UIManager.setLookAndFeel(feels[i].getClassName());
                xmlData.append("                    <laf" + index + " name=\"" + feels[i].getName() + "\" value=\"" + feels[i].getClassName() + "\"/>\r\n");
                index++;
            }
            catch(Exception e){
                //unsupported
            }
        }
        try {
            UIManager.setLookAndFeel(currentFeel);
        }
        catch (Exception ex) {
            //muss klappen
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
        xmlData.append("                    <default name=\"JGoodies Plastic\"/>\r\n");
        xmlData.append("                </lookandfeels>\r\n");
        xmlData.append(
            "        <location height=\"\" width=\"\" x=\"\" y=\"\"/>\r\n");
        xmlData.append("        <columns>\r\n");
        xmlData.append("                <download>\r\n");
        xmlData.append(
            "                        <column0 width=\"80\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"80\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"80\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"80\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"81\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                        <column5 width=\"80\" visibility=\"true\" index=\"5\" />\r\n");
        xmlData.append("                        <column6 width=\"81\" visibility=\"true\" index=\"6\" />\r\n");
        xmlData.append("                        <column7 width=\"80\" visibility=\"true\" index=\"7\" />\r\n");
        xmlData.append("                        <column8 width=\"81\" visibility=\"true\" index=\"8\" />\r\n");
        xmlData.append("                        <column9 width=\"80\" visibility=\"true\" index=\"9\" />\r\n");
        xmlData.append("                </download>\r\n");
        xmlData.append("                <upload>\r\n");
        xmlData.append(
            "                        <column0 width=\"136\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"111\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"111\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"111\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"111\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                        <column5 width=\"112\" visibility=\"true\" index=\"5\" />\r\n");
        xmlData.append("                        <column6 width=\"111\" visibility=\"true\" index=\"6\" />\r\n");
        xmlData.append("                </upload>\r\n");
        xmlData.append("                <server>\r\n");
        xmlData.append(
            "                        <column0 width=\"175\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"175\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"175\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"175\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"175\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                </server>\r\n");
        xmlData.append("                <search>\r\n");
        xmlData.append(
            "                        <column0 width=\"103\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"103\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"103\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                        <column3 width=\"103\" visibility=\"true\" index=\"3\" />\r\n");
        xmlData.append("                        <column4 width=\"103\" visibility=\"true\" index=\"4\" />\r\n");
        xmlData.append("                        <column5 width=\"103\" visibility=\"true\" index=\"5\" />\r\n");
        xmlData.append("                </search>\r\n");
        xmlData.append("                <share>\r\n");
        xmlData.append(
            "                        <column0 width=\"194\" index=\"0\" />\r\n");
        xmlData.append("                        <column1 width=\"195\" visibility=\"true\" index=\"1\" />\r\n");
        xmlData.append("                        <column2 width=\"194\" visibility=\"true\" index=\"2\" />\r\n");
        xmlData.append("                </share>\r\n");
        xmlData.append("        </columns>\r\n");
        xmlData.append("        <browser file=\"\"/>\r\n");
        xmlData.append(
            "        <proxy host=\"\" port=\"\" use=\"false\" userpass=\"=\"/>\r\n");
        xmlData.append("    </options>\r\n");
        xmlData.append("</root>");

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(dateiname);
            fileWriter.write(xmlData.toString());
            fileWriter.close();
        }
        catch (IOException ioE) {
            logger.error(ioE);
        }
    }

    public static void showInformation(String information) {
        JOptionPane.showMessageDialog(theApp, information, "appleJuice Client",
                                      JOptionPane.OK_OPTION);
    }

    private class TxtFileFilter
        extends FileFilter {
        public boolean accept(File file) {
            if (!file.isFile()) {
                return true;
            }
            else {
                String name = file.getName();
                return (name.toLowerCase().endsWith(".ajl"));
            }
        }

        public String getDescription() {
            return "AJL-Dateien";
        }
    }

    private class JCheckBoxLookAndFeelMenuItem extends JCheckBoxMenuItem{
        private final LookAFeel lookAFeel;

        public JCheckBoxLookAndFeelMenuItem(LookAFeel lookAFeelToUse){
            super(lookAFeelToUse.getName());
            this.lookAFeel = lookAFeelToUse;
            addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ae) {
                    if (isSelected()) {
                        try {
                            UIManager.setLookAndFeel(lookAFeel.getClassName());
                            SwingUtilities.updateComponentTreeUI(AppleJuiceDialog.this);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    private void checkAndDisplayUpdate() {
        Thread versionWorker = new Thread() {
            public void run() {
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("VersionWorkerThread gestartet. " + this);
                }
                try {
                    String downloadData = WebsiteContentLoader.
                        getWebsiteContent("http://www.tkl-soft.de", 80,
                                          "/applejuice/version.txt");

                    if (downloadData.length() > 0) {
                        int pos1 = downloadData.indexOf("|");
                        String aktuellsteVersion = downloadData.substring(0,
                            pos1);
                        StringTokenizer token1 = new StringTokenizer(
                            aktuellsteVersion, ".");
                        StringTokenizer token2 = new StringTokenizer(
                            ApplejuiceFassade.GUI_VERSION, ".");
                        if (token1.countTokens() != 3 ||
                            token2.countTokens() != 3) {
                            return;
                        }
                        String[] versionInternet = new String[3];
                        String[] aktuelleVersion = new String[3];
                        for (int i = 0; i < 3; i++) {
                            versionInternet[i] = token1.nextToken();
                            aktuelleVersion[i] = token2.nextToken();
                        }
                        if ((versionInternet[0].compareTo(aktuelleVersion[0]) > 0) ||
                            (versionInternet[1].compareTo(aktuelleVersion[1]) > 0) ||
                            (versionInternet[2].compareTo(aktuelleVersion[2]) > 0)) {
                            int pos2 = downloadData.lastIndexOf("|");
                            String winLink = downloadData.substring(pos1 +
                                1, pos2);
                            String sonstigeLink = downloadData.substring(
                                pos2 + 1);
                            UpdateInformationDialog updateInformationDialog =
                                new UpdateInformationDialog(AppleJuiceDialog.getApp(),
                                aktuellsteVersion, winLink, sonstigeLink);
                            updateInformationDialog.show();
                        } else {
                            LanguageSelector languageSelector = LanguageSelector.getInstance();
                            String fehlerTitel = ZeichenErsetzer.korrigiereUmlaute(
                                languageSelector.getFirstAttrbuteByTagName(".root.mainform.caption"));

                            String fehlerNachricht = ZeichenErsetzer.korrigiereUmlaute(
                                languageSelector.getFirstAttrbuteByTagName(".root.javagui.checkupdate.keineNeueVersion"));

                            JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), fehlerNachricht,
                                fehlerTitel,
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.INFO)) {
                        logger.info(
                            "Aktualisierungsinformationen konnten nicht geladen werden. Server down?");
                    }
                }
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("VersionWorkerThread beendet. " + this);
                }
            }
        };
        versionWorker.start();
    }
}
