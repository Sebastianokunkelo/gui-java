package de.applejuicenet.client.gui.components;

import javax.swing.JTabbedPane;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJTabbedPane extends JTabbedPane {
    public AJTabbedPane() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
