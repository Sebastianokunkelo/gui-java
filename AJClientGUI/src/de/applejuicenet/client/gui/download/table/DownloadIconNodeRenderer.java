package de.applejuicenet.client.gui.download.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;

import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.components.util.IconGetter;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.Settings;

public class DownloadIconNodeRenderer extends DefaultIconNodeRenderer 
	implements DataUpdateListener {

	private Settings settings;
	private JTreeTable treeTable;

	public DownloadIconNodeRenderer(JTreeTable treeTable) {
		super();
		this.treeTable = treeTable;
		settings = Settings.getSettings();
		OptionsManagerImpl.getInstance().addSettingsListener(this);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component c = null;
		if (value.getClass() == DownloadSourceDO.class) {
			c = super.getTreeCellRendererComponent(tree,
					((DownloadSourceDO) value).getNickname() + " ("
							+ ((DownloadSourceDO) value).getFilename() + ")",
					sel, expanded, leaf, row, hasFocus);
		} else if (value.getClass() == DownloadMainNode.class) {
			c = super.getTreeCellRendererComponent(tree, value.toString(), sel,
					expanded, leaf, row, hasFocus);
		} else if (value.getClass() == DownloadDirectoryNode.class) {
			c = super.getTreeCellRendererComponent(tree,
					((DownloadDirectoryNode) value).getVerzeichnis(), sel,
					expanded, leaf, row, hasFocus);
		} else {
			c = super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
		}
		((DownloadIconNodeRenderer) c).setOpaque(true);
		if (sel) {
			c.setBackground(treeTable.getSelectionBackground());
			c.setForeground(treeTable.getSelectionForeground());
		} else {
			if (settings.isFarbenAktiv()) {
				if (value.getClass() == DownloadSourceDO.class) {
					c.setBackground(settings.getQuelleHintergrundColor());
				} else if (value.getClass() == DownloadMainNode.class
						&& ((DownloadMainNode) value).getType() == DownloadMainNode.ROOT_NODE
						&& ((DownloadMainNode) value).getDownload()
								.getStatus() == Download.FERTIG) {
					c.setBackground(settings
							.getDownloadFertigHintergrundColor());
				} else {
					c.setBackground(tree.getBackground());
				}
			}
		}
		if (value instanceof Node) {
			Icon icon = ((Node) value).getConvenientIcon();
			if (icon != null) {
				setIcon(icon);
			}
		} else {
			Icon icon = IconGetter.getConvenientIcon(value);
			if (icon != null) {
				setIcon(icon);
			}
		}
		return this;
	}

	public void fireContentChanged(int type, Object content) {
		if (type == DataUpdateListener.SETTINGS_CHANGED) {
			settings = (Settings) content;
		}
	}
}
