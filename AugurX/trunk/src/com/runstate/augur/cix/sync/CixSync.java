/**
 * CixSy.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.sync;

import java.io.*;
import com.runstate.augur.cix.AmeolScratchpadParser;
import com.runstate.augur.cix.commands.CixCommand;
import com.runstate.augur.cix.filetransfer.FileSpec;
import com.runstate.augur.cix.filetransfer.TransferProtocol;
import com.runstate.augur.cix.filetransfer.TransferProtocolUser;
import com.runstate.augur.cix.filetransfer.YModemG2Protocol;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.Sync;
import com.runstate.augur.gallery.SyncEvent;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.util.ssh.SSHConnection;
import com.runstate.util.ssh.SSHConnectionException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CixSync extends Sync implements TransferProtocolUser,Runnable {
    TransferProtocol transpol;
    int progress;
    int max;
    boolean exitnow=false;
    SSHConnection sshconnection=null;
    ArrayList<Command> commands=null;
    
    boolean readytoroll=false;
    
    Profile profile=Controller.getProfile();
    
    private int sessioncnt=0;
    private int sessionnew=0;
    
    public CixSync(Door door) {
        super(door);
    }
    
    public void exit() {
        exitnow=true;
    }
    
    public String getMenuName() {
        return "Cix Sync";
    }
    
    public String getTitle() {
        return "Cix Sync";
    }
    
//	public AugurPanel getStatusUI()
//	{
//		return new CixSyncUI(this);
//	}
    
    private String getHost() {
        return "cix.co.uk";
    }
    
    private String getUsername() {
        return getDoor().getMyNativeUser();
    }
    
    private String getPassword() {
        return profile.get(Prefs.M_C_PASSWORD);
    }
    
    private Gallery getGallery() {
        return Controller.getGallery();
    }
    
    
    public void go() {
        readytoroll=true;
    }
    
    public void run() {
        while(!readytoroll) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        
        while(!exitnow) {
            if(!sessionActive) {
                if(isAlwaysOn()) {
                    if(isConnected()) {
                        // We're online, so let's see
                        
                        if(!isBusy())	{
                            if(isPendingInline()) {
                                info();
                            }
                            
                            if(hasTimePassed(1)) {
                                collect();
                            }
                        }
                    } else {
                        connect();
                    }
                    
                } else // On demand
                {
                    if(isConnected()) {
                        if(!isBusy()) {
                            disconnect();
                        }
                    } else {
                        if(sshconnection!=null) {
                            sshconnection.close();
                            sshconnection=null;
                            fireSyncMsg(SyncEvent.OFFLINE,"Offline");
                        }
                    }
                }
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            
        }
        
        if(isConnected()) {
            while(isBusy()) {
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {}
            }
            
            disconnect();
        }
        
    }
    
    public boolean canConnect() {
        if(!isConnected()) return true;
        return false;
    }
    
    
    public boolean canSession() {
        if(!isBusy()) return true;
        return false;
    }
    
    public boolean canDisconnect() {
        if(isConnected() && !isBusy()) return true;
        return false;
    }
    
    public boolean canInfo() {
        if(!isBusy()) return true;
        return false;
    }
    
    public boolean canCollect() {
        if(!isBusy()) return true;
        return false;
    }
    
    public boolean canSendAndCollect() {
        if(!isBusy()) return true;
        return false;
    }
    
    
    private boolean isAlwaysOn() {
        return profile.getBool(Prefs.M_C_ALWAYSON,false);
    }
    
    boolean sessionActive=false;
    
    public int session() {
        fireSyncMsg(SyncEvent.SESSION,"Cix session started");
        
        sessioncnt=0;
        sessionnew=0;
        
        while(isBusy()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        
        sessionActive=true;
        
        boolean success=resync();
        
        if(!success || sshconnection==null) {
            fireSyncMsg(SyncEvent.FAILED_TO_CONNECT,"Failed to connect");
            sessionActive=false;
            return -1;
        }
        
        info();
        
        sendAndCollect();
        
        if(!isAlwaysOn()) disconnect();
        
        sessionActive=false;
        
        fireSyncMsg(SyncEvent.SESSION_DONE,"Cix session done "+sessioncnt+" read with "+sessionnew+" new");
        
        return sessionnew;
    }
    
    private void updatePending() {
        commands=getGallery().getPendingCommands(getDoor().getDoorid());
    }
    
    private boolean isPendingInline() {
        updatePending();
        
        Iterator<Command> i=commands.iterator();
        
        if(i.hasNext()) {
            while(i.hasNext()) {
                Command rc=i.next();
                if(rc instanceof CixCommand && ((CixCommand)rc).isInfo()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isConnected() {
        if(sshconnection!=null) return sshconnection.isConnected();
        
        return false;
    }
    
    public boolean info() {
        fireSyncMsg(SyncEvent.INFO,"Getting information");
        
        if(!sessionActive) {
            setBusy(true);
            if(!resync()) return false;
        } else {
            if(isBusy()) return false;
        }
        
        updatePending();
        
        Iterator<Command> i=commands.iterator(); // Reset the iterator
        
        if(i.hasNext()) {
            while(i.hasNext()) {
                Command rc=i.next();
                
                if(rc instanceof CixCommand) {
                    CixCommand cc=(CixCommand)rc;
                    if(cc.isInfo()) {
                        boolean success=cc.executeCommand(getDoor(),this,getGallery(),sshconnection);
                        if(success) deleteDoneCommand(rc,getGallery());
                    }
                }
            }
            
        }
        
        updatePending();
        
        if(!sessionActive) {
            setBusy(false);
            if(!isAlwaysOn()) {
                disconnect();
            }
        }
        
        return true;
    }
    
    public boolean sendAndCollect() {
        if(!sessionActive) {
            if(!resync()) return false;
            setBusy(true);
        } else {
            if(isBusy()) return false;
        }
        
        fireSyncMsg(SyncEvent.INFOMSG,"Send And Collect");
        
        
        updatePending();
        
        Iterator<Command> i=commands.iterator();
        
        ArrayList<Command> todelete=new ArrayList<Command>();
        StringBuffer scriptbuffer=new StringBuffer();
        
        while(i.hasNext()) {
            Command rc=i.next();
            if(rc instanceof CixCommand) {
                CixCommand cc=(CixCommand)rc;
                if(!cc.isInfo()) {
                    scriptbuffer.append(cc.batchCommand(getDoor()));
                    todelete.add(cc);
                }
            }
            
        }
        
        if(scriptbuffer.length()>0) {
            try {
                DataOutputStream dos=new DataOutputStream(new FileOutputStream(profile.get(Prefs.HOMEDIR)+"lastscript"));
                
                dos.write(scriptbuffer.toString().getBytes());
                
                dos.close();
            } catch (IOException e) {}
            
            boolean b=sendScript(scriptbuffer, sshconnection);
            
            if(b) {
                deleteDoneCommands(todelete,getGallery());
            } else {
                System.out.println("failed upload....");
            }
        }
        
        sshconnection.write("echo\n");
        
        boolean b=collect();
        
        if(!sessionActive) {
            setBusy(false);
            if(!isAlwaysOn()) {
                disconnect();
            }
        }
        
        return true;
    }
    
    public boolean collect() {
        if(!sessionActive) {
            if(!resync()) return false;
            setBusy(true);
        } else {
            if(isBusy()) return false;
        }
        
        fireSyncMsg(SyncEvent.COLLECTING,"Collecting messages");
        
        sshconnection.write("file read all\n");
        
        int rslt=sshconnection.waitFor(new String[] { "Scratchpad is 53 bytes.", "M:", "R:" });
        File downloadedFile=null;
        
        if(rslt==0) {
            sshconnection.write("kills\n");
            sshconnection.waitFor("M:");
        } else {
            if(rslt==2) {
                sshconnection.write("q\n");
                sshconnection.waitFor("M:");
            }
            
            sshconnection.write("download\n");
            
            sshconnection.waitFor("Sending in Batch Mode");
            
            byte[] downloaded=download();
            //transpol.startDownload();
            
            try {
                downloadedFile=new File(profile.get(Prefs.HOMEDIR)+"lastscratch");
                
                DataOutputStream dos=new DataOutputStream(new FileOutputStream(downloadedFile));
                
                dos.write(downloaded);
                
                dos.close();
                
//				parseFile(f);
            } catch (IOException e) {}
            
            sshconnection.waitFor("OK to delete the downloaded scratchpad-file? (y/n)? N"+(char)0x08);
            
            sshconnection.write("y\n");
            
            boolean restore=profile.getBool(Prefs.M_C_ALWAYSRESTORE,false);
            
            if(restore) {
                System.out.println("Restoring");
                sshconnection.waitFor("M:");
                sshconnection.write("restore\n");
                sshconnection.waitFor("M:");
            }
        }
        sshconnection.write("echo\n");
        sshconnection.waitFor("M:");
        
        if(!sessionActive) {
            setBusy(false);
            if(!isAlwaysOn()) {
                disconnect();
            }
            
            
        }
        
        fireSyncMsg(SyncEvent.PARSING,"Parsing");
        
        if(downloadedFile!=null) parseFile(downloadedFile);
        
        fireSyncMsg(SyncEvent.COLLECTING_DONE,"Collect Done");
        
        
        return true;
    }
    
    boolean busy=false;
    long lastBusy=0L;
    
    private boolean hasTimePassed(int mins) {
        if(busy) return false;
        
        long now=(System.currentTimeMillis()/1000)/60;
        
        return (now-lastBusy>=mins);
    }
    
    private void setBusy(boolean b) {
        busy=b;
        lastBusy=(System.currentTimeMillis()/1000)/60;
    }
    
    public boolean isBusy() { return busy; }
    
    private String[] resyncers=new String[] { "You were disconnected","M:" };
    
    private boolean resync() {
        fireSyncMsg(SyncEvent.RESYNC,"Resyncing");
        
        if(!isConnected()) {
            return connect();
        }
        
        sshconnection.write("echo\n");
        int ret=sshconnection.waitFor(resyncers);
        if(ret==0) {
            return connect();
            }
        
        return true;
    }
    
    
    
    
    public boolean connect() {
        fireSyncMsg(SyncEvent.CONNECTING,"Connecting");
        
        setBusy(true);
        
        if(getUsername()==null || getPassword()==null || getUsername().length()==0 || getPassword().length()==0) {
            fireSyncMsg(SyncEvent.ERROR,"Unable to start - Cix username/password not set");
            
            setBusy(false);
            
            return false;
        }
        
        
        //SSHConnection sshconnection;
        
        try {
            if(sshconnection==null) {
                sshconnection=new SSHConnection(getHost());
            } else {
                sshconnection.close();
                sshconnection.connect();
            }
        } catch (SSHConnectionException e) {
//			fireMissionDone("Failed to connect");
            e.printStackTrace();
            return false;
        }
        
        //Gallery gallery=Gallery.getInstance();
        
        transpol=new YModemG2Protocol(this,sshconnection);
        
        boolean success=cixlogin(sshconnection);
        
        if(!success) {
            sshconnection.close();
            fireSyncMsg(SyncEvent.ERROR,"Unable to sync - bad password");
            setBusy(false);
            return false;
        }
        
        setBusy(false);
        
        fireSyncMsg(SyncEvent.CONNECTED,"Connected");
        
        return true;
    }
    
    
    public boolean disconnect() {
        if(isBusy()) {
            System.out.println("Dropped because busy");
            fireSyncMsg(SyncEvent.ERROR,"Disconnect request dropped as busy");
            return false;
        }
        
        fireSyncMsg(SyncEvent.DISCONNECTING,"Disconnecting");
        
        setBusy(true);
        
        sshconnection.write("bye y\n");
        
        sshconnection.waitFor("!!!HANGUP NOW!!!");
        
        sshconnection.close();
        
        setBusy(false);
        
        sshconnection=null;
        
        fireSyncMsg(SyncEvent.DISCONNECTED,"Disconnected");
        
        return true;
    }
    
    private void parseFile(File f) {
        fireSyncMsg(SyncEvent.PARSING,"Parsing downloaded scratchpad");
        
        AmeolScratchpadParser asp=null;
        int ncnt=0;
        int cnt=0;
        
        try {
            asp=new AmeolScratchpadParser(f);
        } catch (IOException e) { return; }
        
        max=max+asp.getEstimatedCount();
        
        ArrayList a=new ArrayList();
        
        while(asp.hasNext()) {
            String c=asp.next();
            Msg cm=new Msg(getDoor(),c);
            if(getDoor().isMsgHot(cm)) {
                cm.setHot(true);
            }
            
            cm.setUnread(true);
            cnt++;
            
            if(getGallery().newMsg(cm,getDoor().getDoorid())) {
                ncnt++;
            }
        }
        
        sessioncnt=cnt;
        sessionnew=ncnt;
        
        fireSyncMsg(SyncEvent.PARSING_DONE,"Parsing done");
        
    }
    
    private void deleteDoneCommands(ArrayList<Command> todelete, Gallery gallery) {
        Iterator<Command> iter = todelete.iterator();
        while (iter.hasNext()) {
            deleteDoneCommand(iter.next(), gallery);
        }
    }
    
    private void deleteDoneCommand(Command cmd,Gallery gallery) {
        try {
            if(!cmd.isKeep()) {
                cmd.setDone(true);
                cmd.setWhendone(new Date());
                gallery.updateCommand(cmd);
            }
        } catch (GalleryException e) { e.printStackTrace(); }
    }
    
    
    
    private boolean cixlogin(SSHConnection sshconnection) {
        int i=profile.getInt(Prefs.M_C_TIMEOUT,10);
        
        sshconnection.waitFor("user)");
        sshconnection.write(getUsername()+"\n");
        sshconnection.waitFor("Password:");
        sshconnection.write(getPassword()+"\n");
        int result=sshconnection.waitFor(new String[] { "Error", ":" });
        if(result==0) {
            return false;
        }
        
        sshconnection.write("opt bit8 y scratchpad 65000000 file s y terse unix y d y u y comp y term pag 0 term width 255 ref n auto y edit q q\n");
        sshconnection.waitFor("M:");
        sshconnection.write("opt timeout 1 q\n");
        sshconnection.waitFor("M:");
        sshconnection.write("opt messagesize "+profile.getInt(Prefs.M_C_MSGSIZE,32767)+" q\n");
        sshconnection.waitFor("M:");
        sshconnection.write("store\n");
        sshconnection.waitFor("M:");
        
        return true;
    }
    
    
    
    private boolean sendScript(StringBuffer sb, SSHConnection sshconnection) {
//		System.out.println(sb.toString());
        String delim="$$$$AugurScriptDone";
        String[] donestr=new String[] { delim,"M:" };
        
        sb.append("echo "+delim+"\n");
        sshconnection.write("upload\n");
        sshconnection.waitFor("receive.");
        boolean b=transpol.startUpload(new FileSpec[] { new FileSpec("scratchpad",sb.toString().getBytes()) } );
        if(b) {
            sshconnection.waitFor("M:");
            sshconnection.write("scput script\n");
            sshconnection.waitFor("M:");
            sshconnection.write("script\n");
            boolean done=false;
            while(!done) {
                int i=sshconnection.waitFor(donestr);
                
                if(i==0) {
                    done=true;
                }
            }
            
            sshconnection.write("echo\n");
            sshconnection.waitFor("M:");
            return true;
        } else {
            System.out.println("upload failed");
            return false;
        }
    }
    
//	private void createSkipback(StringBuffer sb,CixSkipBackCommand sbc)
//	{
//		sb.append("order first cixnews\n");
//		sb.append("macro fred file hea skip to back "+sbc.getSkipback()+" killsc tnext fred\n");
//		sb.append("j cixnews/information\n");
//		sb.append("fred\n");
//		sb.append("killsc\n");
//	}
    
    public byte[] download() {
        try {
            transpol.startDownload();
        } catch (IOException e) { return null; }
        
        byte[] downloadbytes=downloaded.toByteArray();
        downloaded=null;
        return downloadbytes;
    }
    
    ByteArrayOutputStream downloaded=null;
    
    /**
     * Method transferUploadStart
     *
     * @param    filename            a  String
     * @param    filesize            an int
     *
     */
    public void transferUploadStart(String filename, int filesize) {
        // TODOy
    }
    
    /**
     * Method transferComplete
     *
     */
    public void transferComplete() {
        // TODO
    }
    
    /**
     * Method transferMessage
     *
     * @param    p0                  a  String
     *
     */
    public void transferMessage(String p0) {
        // TODO
    }
    
    /**
     * Method transferProgress
     *
     * @param    sizesofar           a  long
     * @param    p1                  a  boolean
     *
     */
    public void transferProgress(long sizesofar, boolean p1) {
        // TODO
    }
    
    /**
     * Method transferFileStart
     *
     * @param    currentfilename     a  String
     * @param    currentfilesize     a  long
     *
     * @return   a  ByteArrayOutputStream
     */
    public OutputStream transferFileStart(String currentfilename, long currentfilesize) {
        downloaded=new ByteArrayOutputStream();
        return downloaded;
    }
    
}

