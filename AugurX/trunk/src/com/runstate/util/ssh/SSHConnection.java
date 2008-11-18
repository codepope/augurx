/**
 * SSHConnection.java
 *
 * AugurX is under the Apache Licence.
 *
 * AugurX is (C) Dj Walker-Morgan 2000-2008
 *
 * 
 **/
package com.runstate.util.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import javax.swing.JOptionPane;

/**
 * SSHConnection manages connections to SSH hosts for Augur code.
 *
 * @author dj
 *
 **/
public class SSHConnection {

    WritableInputStream mis;
    ReadableOutputStream mos;
    JSch jsch;
    Session session;
    Channel channel;
    String host;
    String initialLogin;
    boolean debug = Controller.getProfile().getBool(Prefs.DEBUG_SSH, false);

    /**
     * SSHConnection constructor for backwards compatibility
     *
     * @param host host to connect to
     * @throws com.runstate.util.ssh.SSHConnectionException
     */
    public SSHConnection(String host) throws SSHConnectionException {
        if (host.startsWith("twix")) {
            setupConnection(host, "twix");
        } else if (host.startsWith("cix")) {
            setupConnection(host, "qix");
        }
    }

    /**
     * SSHConnection contructor takes host name and initial Login to be used on
     * hosts where CoSY is supported under one user login account
     *
     * @param host host to connect to
     * @param initialLogin login string to send
     * @throws com.runstate.util.ssh.SSHConnectionException
     */
    public SSHConnection(String host, String initialLogin) throws SSHConnectionException {
        setupConnection(host, initialLogin);
    }

    private void setupConnection(String host, String initialLogin) throws SSHConnectionException {
        this.host = host;
        this.initialLogin = initialLogin;

        jsch = new JSch();


        try {

            jsch.setKnownHosts(Controller.getProfile().get(Prefs.HOMEDIR) + "known_hosts");

            session = jsch.getSession(initialLogin, host, 22);

            UserInfo ui = new CosyUserInfo();

            session.setUserInfo(ui);

            mis = new WritableInputStream();
            mos = new ReadableOutputStream();

            session.setInputStream(mis);
            session.setOutputStream(mos);

            session.connect();

            channel = session.openChannel("shell");

            channel.connect();


        } catch (JSchException e) {
            throw new SSHConnectionException("Failed to connect", e);
        }

    }

    /**
     * Make SSHConnection reestablish if required
     *
     * @throws com.runstate.util.ssh.SSHConnectionException
     */
    public void connect() throws SSHConnectionException {
        if (session == null) {
            try {
                session = jsch.getSession(initialLogin, host, 22);

                UserInfo ui = new CosyUserInfo();

                session.setUserInfo(ui);

                mis = new WritableInputStream();
                mos = new ReadableOutputStream();

                session.setInputStream(mis);
                session.setOutputStream(mos);

                session.connect();

                channel = session.openChannel("shell");

                channel.connect();
            } catch (JSchException e) {
                throw new SSHConnectionException("Failed to connect", e);
            }

        }
    }

    /**
     * Wait for a string to appear in the incoming stream.
     *
     * Will return false if interrupted.
     *
     * @param s string to wait for
     * @return boolean, was string found
     */
    public boolean waitFor(String s) {
        boolean done = false;
        StringBuffer sb = new StringBuffer(1024);

        while (!done) {
            sb.setLength(0);

            if (mos.available() > 0) {

                int i = mos.available();

                while (i > 0) {
                    int c = mos.read();
                    sb.append((char) c);
                    i--;
                }


                if (debug) {
                    System.out.println("Current read is '" + sb.toString() + "' (" + sb.length() + ") waiting for " + s);
                }

                if (sb.toString().indexOf(s) != -1) {
                    if (debug) {
                        System.out.println("Found it");
                    }
                    return true;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

        }

        return false;
    }

    /**
     * Waits for string and returns the matched string when found
     * @param s string to wait for
     * @return found string
     */
    public String waitForAndReturn(String s) {
        boolean done = false;
        StringBuffer sb = new StringBuffer(1024);
        while (!done) {
            sb.setLength(0);

            if (mos.available() > 0) {

                int i = mos.available();

                while (i > 0) {
                    int c = mos.read();
                    sb.append((char) c);
                    i--;
                }


                if (debug) {
                    System.out.println("Current read is '" + sb.toString() + "' (" + sb.length() + ") waiting for " + s);
                }

                if (sb.toString().indexOf(s) != -1) {
                    if (debug) {
                        System.out.println("Found it");
                    }

                    //  if(debug) System.out.println(sb);

                    return sb.toString();
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

        }

        return null;
    }

    /**
     * Waits for string and returns everything read up to and including the
     * matched text
     * @param s string to wait for
     * @return found string
     */
    public String waitForAndReturnAll(String s) {
        boolean done = false;
        StringBuilder sb = new StringBuilder(1024);
        StringBuilder bb = new StringBuilder();
        sb.setLength(0);

        while (!done) {
            bb.append(sb.toString());
            sb.setLength(0);

            if (mos.available() > 0) {

                int i = mos.available();

                while (i > 0) {
                    int c = mos.read();
                    sb.append((char) c);
                    i--;
                }


                if (debug) {
                    System.out.println("Current read is '" + sb.toString() + "' (" + sb.length() + ") waiting for " + s);
                }

                if (sb.toString().indexOf(s) != -1) {
                    if (debug) {
                        System.out.println("Found it");
                    }

                    //  if(debug) System.out.println(sb);
                    bb.append(sb);
                    return bb.toString();
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { }

        }

        return null;
    }

    /**
     * Waits for a selection of strings and returns index of string matched
     * to or -1 to denote an interruptException or other error occured.
     *
     * @param s String array of things we can match to
     * @return integer index of s or -1
     */
    public int waitFor(String[] s) {
        boolean done = false;
        StringBuffer sb = new StringBuffer(1024);

        while (!done) {
            sb.setLength(0);

            if (mos.available() > 0) {

                int i = mos.available();


                while (i > 0) {
                    int c = mos.read();
                    sb.append((char) c);
                    i--;
                }


                if (debug) {
                    System.out.print("Current read is '" + sb.toString() + "' (" + sb.length() + ") waiting for ");
                    for (String st : s) {
                        System.out.println("'" + st + "'");
                    }
                }

                for (int j = 0; j < s.length; j++) {
                    if (sb.toString().indexOf(s[j]) != -1) {
                        if (debug) {
                            System.out.println("Got " + j);
                        }
                        return j;
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

        }

        return -1;
    }


    /**
     * Flush input - Currently a noop
     */
    public void flushInput() {
    }

    /**
     * Flush output - Currently a noop
     */
    public void flushOutput() {
    }
    ;

    /**
     * Is this SSHConnection actually connected
     * @return boolean, true/false
     */
    public boolean isConnected() {
        if (session == null) {
            return false;
        }

        return session.isConnected();
    }

    /**
     * return number of bytes on input stream available to consume
     * @return integer
     */
    public int available() {
        return mos.available();
    }

    /**
     * Write a byte to the output stream
     * @param b byte to write
     */
    public void write(byte b) {
        mis.write(b);
    }

    /**
     * Write a string to the output stream
     * @param s string to write
     */
    public void write(String s) {
        mis.write(s);
    }

    /**
     * Read a byte from the input stream
     * @return integer representing a character
     */public int read() {
        return mos.read();
    }

    /**
     * Close SSH connection
     */
     public void close() {
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    /**
     * SSH class to manage the user connection dialog
     */

     static class CosyUserInfo implements UserInfo {

        @Override
        public String getPassword() {
            return "";
        }

        @Override
        public String getPassphrase() {
            return "";
        }

        @Override
        public void showMessage(String p1) {
            return;
        }

        @Override
        public boolean promptYesNo(String p1) {

            int i = JOptionPane.showOptionDialog(null, p1, "SSH Query", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

            if (i == JOptionPane.YES_OPTION) {
                return true;
            }

            return false;
        }

        @Override
        public boolean promptPassword(String p1) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String str) {
            return true;
        }

        public String getPassphrase(String message) {
            return null;
        }
    }
}
