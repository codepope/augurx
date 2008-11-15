/**
 * Pure Java JFC (Swing 1.1) application.
 * This application realizes a windowing application.
 *
 * This file was automatically generated by
 * Omnicore CodeGuide.
 */

package com.runstate.augur;


import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.gallery.BundleManager;
import com.runstate.augur.gallery.Gallery;
import com.runstate.util.ImageCache;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class AugurX {
    public static String name="Augur";
    public static String edition="Snapshot Build";
    public static String build="svn revision 13";
//    public static String edition="Alpha Release";
//    public static String build="0.5.x.0";
    
    static boolean forcecentral=false;
    
    public static void main(String[] args) {
        
        Controller controller=Controller.getController();
           // Are we on a Mac?
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean MAC_OS_X = lcOSName.startsWith("mac os x");

        if(MAC_OS_X) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name","Augur");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            com.apple.eawt.Application app=com.apple.eawt.Application.getApplication();
            ImageIcon img=ImageCache.get("macosxbadge");
            app.setDockIconImage(img.getImage());

        }
        checkArguments(args,controller);
        
        String lookandfeel=controller.getProfile().get(Prefs.UI_LOOKANDFEEL,UIManager.getSystemLookAndFeelClassName());
        
        if(lookandfeel!=null && lookandfeel!=UIManager.getSystemLookAndFeelClassName()) {
            try {
                UIManager.setLookAndFeel(lookandfeel);
            } catch (ClassNotFoundException e) {
                System.err.println("Could Not Find Look and Feel classes");
                System.exit(1);
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Could Not Find Look and Feel classes");
                System.exit(1);
            } catch (InstantiationException e) {
                System.err.println("Could Not Instansiate Look and Feel classes");} catch (IllegalAccessException e) { System.err.println("Illegal Access to Look and Feel classes");}
        }
        
        initialiseStartup(controller);
        
        
        
        System.out.println("Platform "+System.getProperty("os.name"));
        System.out.println("Architecture "+System.getProperty("os.arch"));
        
        controller.run();
    }
    
    private static void initialiseStartup(Controller controller) {
        
        controller.setProfileName(System.getProperty("augur.profile","default"));
        Profile profile=controller.getProfile();
        
        if(profile.get(Prefs.HOMEDIR)==null) {
            profile.set(Prefs.HOMEDIR,System.getProperty("user.home")+File.separatorChar+"augurxhome"+File.separatorChar+controller.getProfileName()+File.separatorChar);
        }
        
//		forcecentral|=controller.setDefault(Prefs.HOMEDIR,System.getProperty("user.home")+File.separatorChar+"augurhome"+File.separatorChar+controller.getProfileName()+File.separatorChar);
//		forcecentral|=controller.setDefault(Prefs.UI_HTML_FONT,"SanSerif-plain-11");
        
        
        if(profile.get(Prefs.G_DBURL)==null) {
            profile.set(Prefs.G_DBURL,"jdbc:hsqldb:"+profile.get(Prefs.HOMEDIR)+"augurdb"+File.separatorChar+"augur");
        }
        
        
        
//		forcecentral|=profile.setDefault(Prefs.G_DBURL,"jdbc:hsqldb:"+profile.get(Prefs.HOMEDIR)+"augurdb"+File.separatorChar+"augur");
//		forcecentral|=controller.setDefault(Prefs.G_DBUSER,"sa");
//		forcecentral|=controller.setDefault(Prefs.G_DBPASS,"");
//		forcecentral|=controller.setDefault(Prefs.M_C_USERNAME,"");
//		forcecentral|=controller.setDefault(Prefs.M_C_PASSWORD,"");
//		forcecentral|=controller.setDefault(Prefs.UI_SPELL_FILE,"dict/UK.dic");
//		forcecentral|=controller.setDefault(Prefs.DEBUG_SSH,"false");
//		forcecentral|=controller.setDefault(Prefs.DEBUG_YMODEM,"false");
//		forcecentral|=controller.setDefault(Prefs.G_HSQL_COMPACT,"false");
//		forcecentral|=controller.setDefault(Prefs.G_HSQL_OFFERSHUTDOWN,"false");
//		forcecentral|=controller.setDefault(Prefs.UI_TEXT_ALIAS,"false");
//		forcecentral|=controller.setDefault(Prefs.UI_TREE_ALIAS,"false");
//		forcecentral|=controller.setDefault(Prefs.UI_COMPOSE_FONT,"Monospaced-PLAIN-12");
//		forcecentral|=controller.setDefault(Prefs.UI_PATHTREE_FONT,"Dialog-PLAIN-10");
//		forcecentral|=controller.setDefault(Prefs.UI_SSHCONSOLE_FONT,"Monospaced-PLAIN-10");
//		forcecentral|=controller.setDefault(Prefs.UI_THREADTREE_FONT,"Dialog-PLAIN-10");
//		forcecentral|=controller.setDefault(Prefs.M_C_MSGSIZE,"32767");
        
        File f=new File(profile.get(Prefs.HOMEDIR));
        if(!f.exists()) {
            f.mkdirs();
        }
    }
    
    static String lookandfeel=UIManager.getSystemLookAndFeelClassName();
    
    private static void checkArguments(String[] args,Controller controller) {
        Profile profile=controller.getProfile();
        
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("-profile")) {
                if(i!=0) {
                    System.err.println("-profile must be the first argument if present");
                    System.exit(1);
                }
                i++;
                if(i<args.length) {
                    //			central.setProfileName(args[i]);
                    System.setProperty("augur.profile",args[i]);
                } else {
                    System.err.println("Need to specify a profile name with -profile");
                    System.exit(1);
                }
            }
            if(args[i].equals("-prefs")) {
                forcecentral=true;
            }
            if(args[i].equals("-help")) {
                showHelp();
                System.exit(0);
            }
            if(args[i].equals("-motif")) {
                profile.override(Prefs.UI_LOOKANDFEEL,"com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } else if(args[i].equals("-windows")) {
                profile.override(Prefs.UI_LOOKANDFEEL,"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if(args[i].equals("-gtk")) {
                profile.override(Prefs.UI_LOOKANDFEEL,"com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else if(args[i].equals("-metal")) {
                profile.override(Prefs.UI_LOOKANDFEEL,"javax.swing.plaf.metal.MetalLookAndFeel");
            } else if(args[i].equals("-tonic")) {
                profile.override(Prefs.UI_LOOKANDFEEL,"com.digitprop.tonic.TonicLookAndFeel");
            } else if(args[i].equals("-lookandfeel")) {
                i++;
                if(i<args.length) {
                    profile.override(Prefs.UI_LOOKANDFEEL,args[i]);
                } else {
                    System.err.println("Need to specify an look and feel with -lookandfeel");
                    System.exit(0);
                }
            }
//			else if(args[i].equals("-browser")) {
//				i++;
//				if(i<args.length) {
//					central.override(Prefs.A_BROWSER,args[i]);
//				}
//				else {
//					System.err.println("Need to specify a browser with -browser");
//					System.exit(0);
//				}
//			}
//
        }
        
    }
    
    private static void showHelp() {
        System.out.println("Look and feel");
        System.out.println("-motif         Force Motif look and feel");
        System.out.println("-windows       Force Windows look and feel");
        System.out.println("-gtk           Force GTK look and feel");
        System.out.println("-metal         Force Metal (Java default) look and feel");
        System.out.println("-tonic         Force the Tonic look and feel");
        System.out.println("-lookandfeel x Set look and feel to x (a L&F class)");
        System.out.println("-browser xxx   Set the external browser to xxx");
    }
}

