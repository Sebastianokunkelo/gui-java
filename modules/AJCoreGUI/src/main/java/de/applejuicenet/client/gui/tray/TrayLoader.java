/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.tray;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayLoader
{
   private String   zeigen;
   private String   verstecken;
   private TrayIcon trayIcon = null;

   public boolean makeTray( String title, final AppleJuiceDialog dialog, final JMenuItem popupShowHideMenuItem,
                           final Icon zeigenIcon, final Icon versteckenIcon, final JPopupMenu popup)
   {
      SystemTray tray = SystemTray.getSystemTray();

      IconManager im = IconManager.getInstance();

      Image image = im.getIcon("applejuice").getImage();

      trayIcon = new TrayIcon(image, title, null);

      trayIcon.setImageAutoSize(true);
      trayIcon.addMouseListener(new MouseAdapter()
         {
            public void mouseReleased(MouseEvent e)
            {
               e.consume();
               if(!dialog.isVisible())
               {
                  popupShowHideMenuItem.setText(zeigen);
                  popupShowHideMenuItem.setIcon(zeigenIcon);
               }
               else
               {
                  popupShowHideMenuItem.setText(verstecken);
                  popupShowHideMenuItem.setIcon(versteckenIcon);
               }

               if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
               {
                  AppleJuiceDialog dialog = AppleJuiceDialog.getApp();

                  if(!dialog.isVisible())
                  {
                     dialog.setVisible(true);
                     dialog.toFront();
                  }
                  else
                  {
                     dialog.setVisible(false);
                  }
               }

               if(e.getButton() == MouseEvent.BUTTON3)
               {
                  if(popup.isVisible())
                  {
                     popup.setVisible(false);
                  }
                  else
                  {
                     popup.setLocation(e.getX(), e.getY());
                     popup.setInvoker(popup);
                     popup.setVisible(true);
                  }
               }
            }
         });

      try
      {
         tray.add(trayIcon);
      }
      catch(AWTException e)
      {
         return false;
      }

      return true;
   }

   public void setTextZeigen(String zeigen)
   {
      this.zeigen = zeigen;
   }

   public void setTextVerstecken(String verstecken)
   {
      this.verstecken = verstecken;
   }

   public void showBallon(String caption, String message)
   {
      if(null == trayIcon || (null == caption && null == message))
      {
         return;
      }

      trayIcon.displayMessage(caption, message, MessageType.INFO);
   }
}
