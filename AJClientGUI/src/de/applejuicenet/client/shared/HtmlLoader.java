package de.applejuicenet.client.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/HtmlLoader.java,v 1.29 2004/05/10 16:14:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class HtmlLoader {
    public static final int POST = 0;
    public static final int GET = 1;

    private static Logger logger = Logger.getLogger(HtmlLoader.class);

    public static String getHtmlContent(String host, int port, int method,
                                        String command) throws
        WebSiteNotFoundException {
        StringBuffer urlContent = new StringBuffer();
        try{
            try {
                InetAddress addr = InetAddress.getByName(host);
                Socket socket = new Socket(addr, port);
                PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                    new OutputStreamWriter(
                    socket.getOutputStream())));

                String methode = "";
                if (method == HtmlLoader.GET) {
                    methode = "GET ";
                }
                else if (method == HtmlLoader.POST) {
                    methode = "POST ";
                }
                else {
                    return "";
                }
                out.println(methode + command + " HTTP/1.1");
                out.println("host: " + host);
                out.println();
                out.flush();

                BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    socket.getInputStream()));

                String inputLine = in.readLine();
                if (inputLine == null) {
                    throw new WebSiteNotFoundException(WebSiteNotFoundException.
                        UNKNOWN_HOST);
                }
                while (inputLine.indexOf("Content-Type: text") != 0) {
                    inputLine = in.readLine();
                }
                int gelesen;
                while ( (gelesen = in.read()) != -1) {
                    urlContent.append( (char) gelesen);
                }
            }
            catch (SocketException sex) {
                throw new WebSiteNotFoundException(WebSiteNotFoundException.
                    AUTHORIZATION_REQUIRED, sex);
            }
            catch (IOException ioE) {
                throw new WebSiteNotFoundException(WebSiteNotFoundException.
                    AUTHORIZATION_REQUIRED, ioE);
            }
        }
        catch (WebSiteNotFoundException wnfE) {
            if (logger.isEnabledFor(Level.INFO)) {
                int index = command.indexOf("password=");
                StringBuffer commandBuffer = new StringBuffer(command);
                if (index != -1) {
                    for (int i = 0; i < 6; i++) {
                        commandBuffer.setCharAt(index + 9 + i, '*');
                    }
                }
                logger.info(
                    "WebSiteNotFound: " + host + ":" + port +
                    commandBuffer.toString(),
                    wnfE);
            }
            throw wnfE;
        }
        return urlContent.toString();
    }

    public static String getHtmlXMLContent(String host, int method,
                                           String command, boolean withResult) throws
        WebSiteNotFoundException {
        int ajPort = OptionsManagerImpl.getInstance().getRemoteSettings().
            getXmlPort();
        StringBuffer urlContent = new StringBuffer();
        try{
            try {
                InetAddress addr = InetAddress.getByName(host);
                Socket socket = new Socket(addr, ajPort);
                PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                    new OutputStreamWriter(
                    socket.getOutputStream())));

                String methode = "";
                if (method == HtmlLoader.GET) {
                    methode = "GET ";
                }
                else if (method == HtmlLoader.POST) {
                    methode = "POST ";
                }
                else {
                    return "";
                }
                command = command.replaceAll(" ", "%20");
                out.println(methode + command + " HTTP/1.1");
                out.println("host: " + host);
                out.println();
                out.flush();

                if (!withResult) {
                    return "ok";
                }

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String inputLine = readLn(in);
                if (method == HtmlLoader.GET) {
                    if (inputLine == null || inputLine.length()==0) {
                        throw new WebSiteNotFoundException(
                            WebSiteNotFoundException.
                            UNKNOWN_HOST);
                    }
                    while (inputLine.indexOf("Content-Length:") == -1) {
                        inputLine = readLn(in);
                        if (inputLine == null) {
                            throw new WebSiteNotFoundException(
                                WebSiteNotFoundException.
                                UNKNOWN_HOST);
                        }
                        if (inputLine.indexOf("/wrongpassword") != -1){
                            return "wrong password";
                        }
                        if (inputLine.indexOf("invalid id") != -1) {
                            return "";
                        }
                    }
                    long laenge = Long.parseLong(inputLine.substring(inputLine.
                        indexOf(" ") + 1));
                    if (command.indexOf("mode=zip") != -1) {
                        DataInputStream in_data = new DataInputStream(socket.
                            getInputStream());
                        in_data.skip(1);
                        byte[] allRead = new byte[ (int) laenge];
                        byte[] toRead = new byte[1];
                        int pos = 0;
                        while (in_data.read(toRead) != -1) {
                            allRead[pos] = toRead[0];
                            pos++;
                        }
                        urlContent.append(ZLibUtils.uncompress(allRead));
                    }
                    else {
                        in.skip(1);
                        byte[] toRead = null;
                        int gelesen = 0;
                        while (laenge > 0) {
                            if (laenge > Integer.MAX_VALUE) {
                                toRead = new byte[Integer.MAX_VALUE];
                            }
                            else {
                                toRead = new byte[ (int) laenge];
                            }
                            gelesen = in.read(toRead);
                            if (gelesen < toRead.length) {
                                urlContent.append(new String(toRead, 0, gelesen));
                                laenge -= gelesen;
                            }
                            else {
                                urlContent.append(new String(toRead));
                                laenge -= toRead.length;
                            }
                        }
                    }
                }
                else {
                    if (inputLine.compareToIgnoreCase("HTTP/1.1 200 OK") == 0) {
                        urlContent = new StringBuffer(inputLine);
                    }
                    else {
                        throw new WebSiteNotFoundException(
                            WebSiteNotFoundException.
                            INPUT_ERROR);
                    }
                }
            }
            catch (SocketException sex) {
                throw new WebSiteNotFoundException(WebSiteNotFoundException.
                    AUTHORIZATION_REQUIRED, sex);
            }
            catch (IOException ioE) {
                throw new WebSiteNotFoundException(WebSiteNotFoundException.
                    AUTHORIZATION_REQUIRED, ioE);
            }
        }
        catch(WebSiteNotFoundException wnfE){
            if (logger.isEnabledFor(Level.DEBUG)){
                int index = command.indexOf("password=");
                StringBuffer commandBuffer = new StringBuffer(command);
                if (index != -1){
                    for (int i=0; i<6; i++){
                        commandBuffer.setCharAt(index + 9 + i, '*');
                    }
                }
                logger.debug(
                    "WebSiteNotFound: " + host + ":" + ajPort +
                    commandBuffer.toString());
            }
            if (withResult){
                throw wnfE;
            }
        }
        String test = urlContent.toString();
        return test;
    }

    private static String readLn(DataInputStream in) {
        try {
            StringBuffer line = new StringBuffer();
            byte[] toRead = new byte[1];
            while (in.read(toRead) != -1) {
                char read = (char) toRead[0];
                line.append(read);
                if (read == '\n') {
                    break;
                }
            }
            return line.toString().trim();
        }
        catch (Exception e) {
            return "";
        }
    }

    public static String getHtmlXMLContent(String host, int method,
                                           String command) throws
        WebSiteNotFoundException {
        return getHtmlXMLContent(host, method, command, true);
    }
}
