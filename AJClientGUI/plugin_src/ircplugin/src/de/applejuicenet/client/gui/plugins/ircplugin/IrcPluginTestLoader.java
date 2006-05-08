package de.applejuicenet.client.gui.plugins.ircplugin;

import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

class IrcPluginTestLoader extends TestLoader
{
   @Override
   protected String getPath()
   {
      return "ircplugin";
   }

   @Override
   protected PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map<String, XMLValueHolder> languageFiles,
      ImageIcon icon, Map<String, ImageIcon> availableIcons)
   {
      return new IrcPlugin(pluginsPropertiesXMLHolder, languageFiles, icon, availableIcons);
   }
}
