package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/InvalidPasswordException.java,v 1.2 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: InvalidPasswordException.java,v $
 * Revision 1.2  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class InvalidPasswordException
    extends Exception {

  public InvalidPasswordException() {
    super("Ung�ltiges Kennwort");
  }
}