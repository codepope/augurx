/**
 * Gallery.java
 *
 * @author Dj
 */

package com.runstate.augur.gallery;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.gallery.*;
import com.runstate.augur.gallery.events.*;
import com.runstate.augur.gallery.listeners.*;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import java.sql.*;
import java.util.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.commands.Command;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public  class Gallery {
    private static Logger log=Logger.getLogger("Gallery");
    
    BundleManager bundlemanager;
    
    private Connection conn;
    private Statement stmt;
    
    private String jdbcdriver;
    
    private String dbusername;
    private String dbpassword;
    private String dburl;
    
    private PreparedStatement insertMsgPstmt;
    private PreparedStatement getMsgByBundleidSpidPstmt;
    
    private PreparedStatement getMsgsPstmt;
    private PreparedStatement updateMsgPstmt;
    private PreparedStatement getMsgStrandidPstmt;
    private PreparedStatement deleteMsgsByBundleidPstmt;
    
    private PreparedStatement insertFulltextPstmt;
    private PreparedStatement getFulltextPstmt;
    private PreparedStatement deleteFulltextPstmt;
    
    private PreparedStatement insertBundlePstmt;
    
    private PreparedStatement deleteBundlePstmt;
    private PreparedStatement updateBundlePstmt;
    
    private PreparedStatement insertCommandPstmt;
    private PreparedStatement updateCommandsPstmt;
    private PreparedStatement getCommandsPstmt;
    private PreparedStatement getPendingCommandsPstmt;
    private PreparedStatement getPendingCommandCountPstmt;
    private PreparedStatement deleteCommandPstmt;
    
    private PreparedStatement insertPoolablePstmt;
    private PreparedStatement updatePoolablePstmt;
    private PreparedStatement getPoolablePstmt;
    private PreparedStatement getPoolKeysPstmt;
    private PreparedStatement deletePoolablePstmt;
    
    
    
    private PreparedStatement getKnotIdsOfChildren;
    private PreparedStatement updateStrandidPstmt;
    private PreparedStatement getMsgrootsPstmt;
    private PreparedStatement getMsgsInStrandPstmt;
    
    private PreparedStatement insertStrandPstmt;
    
    private PreparedStatement getStrandPstmt;
    
    private PreparedStatement getStrandsPstmt;
    
    private PreparedStatement updateStrandPstmt;
    private PreparedStatement deleteStrand_pstmt;
    private PreparedStatement getCommentingStrandsPstmt;
    private PreparedStatement setStrandMsgsUnread_pstmt;
    
    private PreparedStatement getMsgAttributesPstmt;
    
    private PreparedStatement setMsgFirstComment;
    private PreparedStatement setMsgUnread;
    private PreparedStatement setMsgHot;
    private PreparedStatement setMsgTagged;
    private PreparedStatement setMsgIgnore;
    private PreparedStatement getRootids;
    
    private PreparedStatement getCommandIdsPstmt;
    private PreparedStatement getCommandIdsForDoorIdPstmt;
    private PreparedStatement getCommandPstmt;
    
    private PreparedStatement getTag;
    private PreparedStatement getTags;
    private PreparedStatement updateTag;
    private PreparedStatement insertTag;
    private PreparedStatement deleteTag;
    
    private PreparedStatement searchPstmt;
    
    /**
     * Method startDoorSync
     *
     */
    public void startDoorSync() {
        Door[] d=getDoors();
        for(int i=0;i<d.length;i++) d[i].startDoorSync();
    }
    
    private Controller getController() { return Controller.getController(); }
    
    public BundleManager getBundlemanager() {
        return bundlemanager;
    }
    
    private Profile getProfile() { return getController().getProfile(); }
    
    public Gallery() throws GalleryException {
    }
    
    public void startGallery() {
        long startedat=System.currentTimeMillis();
        
        bundlemanager=new BundleManager(this);
        
        try {
            jdbcdriver=getProfile().get(Prefs.G_JDBCDRIVER,"org.hsqldb.jdbcDriver");
            dbusername=getProfile().get(Prefs.G_DBUSER,"sa");
            dbpassword=getProfile().get(Prefs.G_DBPASS,"");
            dburl=getProfile().get(Prefs.G_DBURL,"jdbc:hsqldb:augurdb/augur");
            Class.forName(jdbcdriver).newInstance();
        } catch (ClassNotFoundException e) { e.printStackTrace(); System.exit(1); } catch (InstantiationException e) { e.printStackTrace(); System.exit(1); } catch (IllegalAccessException e) { e.printStackTrace(); System.exit(1); }
        
        sendGalleryEvent(new GalleryEvent("Opening Gallery"));
        
        try {
            conn=DriverManager.getConnection(dburl,dbusername,dbpassword);
            
            stmt=conn.createStatement();
            
            checkDB();
            
            initialiseStatements();
            
            loadDoors();
            
            loadBundleManager();
        } catch (SQLException e) { throw new GalleryException("Unable to open db connection",e); }
        
        long duration=System.currentTimeMillis()-startedat;
        
        sendGalleryEvent(new GalleryEvent(GalleryEvent.OPEN,"Opened in "+((float)duration/1000)+" seconds"));
    }
    
    static final String T_MSGINDEX="msgindex";
    static final String T_POOL="poolstore";
    static final String T_COMMANDS="commands";
    static final String T_BUNDLES="bundles";
    static final String T_FULLTEXT="fulltext";
    static final String T_DOORS="doors";
    static final String T_STRANDS="strands";
    static final String T_TAGS="tags";
    static final String T_TAGMSG="tagmsg";
    
    private void checkDB() throws SQLException {
        
        boolean found=false;
        
//		stmt.executeUpdate("drop table "+T_MSGINDEX+";");
//		stmt.executeUpdate("drop table "+T_COMMANDS+";");
//		stmt.executeUpdate("drop table "+T_PATHINDEX+";");
//		stmt.executeUpdate("drop table "+T_POOL+";");
//		stmt.executeUpdate("drop table "+T_FULLTEXT+";");
        
        DatabaseMetaData dbmd=conn.getMetaData();
        ResultSet rs=dbmd.getTables(null,null,"%",null);
        
        
        while(rs.next()) {
            String s=rs.getString(3);
            if(s.equalsIgnoreCase(T_MSGINDEX)) {
                found=true;
            }
        }
        
        if(found) return;
        
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_COMMANDS+" ("+
                "id VARCHAR(255) NOT NULL,"+
                "doorid INT,"+
                "date DATETIME,"+
                "hold BIT,"+
                "keep BIT,"+
                "done BIT,"+
                "donedate DATETIME,"+
                "command OTHER )");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_BUNDLES+" ("+
                "doorid INT,"+
                "bundleid INT NOT NULL,"+
                "name VARCHAR(255) NOT NULL,"+
                "unread INT,"+
                "total INT,"+
                "warmunread INT,"+
                "container BIT,"+
                "parentid INT,"+
                "PRIMARY KEY(bundleid),UNIQUE(bundleid))");
        
        stmt.executeUpdate("INSERT INTO "+T_BUNDLES+" VALUES (0,0,'',0,0,0,1,-1);");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_POOL+" ("+
                "pool VARCHAR(255) NOT NULL,"+
                "key VARCHAR(255) NOT NULL,"+
                "poolable OTHER,"+
                "UNIQUE(pool,key), PRIMARY KEY(pool,key))");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_FULLTEXT+" ( msgid INT IDENTITY, text LONGVARCHAR,PRIMARY KEY(msgid) );");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_MSGINDEX+" ("+
                "msgid INT NOT NULL,"+
                "bundleid INT NOT NULL,"+
                "spid INT NOT NULL,"+
                "msgdate DATETIME,"+
                "subject VARCHAR(255),"+
                "author VARCHAR(255),"+
                "commentto INT,"+
                "sourceid VARCHAR(255) NOT NULL,"+
                "unread BIT,"+
                "ignore BIT,"+
                "keep BIT,"+
                "hot BIT,"+
                "deleted BIT,"+
                "actionable BIT,"+
                "bodystart INT,"+
                "rootid INT,"+
                "firstcomment INT,"+
                "PRIMARY KEY(bundleid,spid),"+
                "UNIQUE (bundleid,spid),"+
                "FOREIGN KEY (msgid) REFERENCES "+T_FULLTEXT+" (msgid) ON DELETE CASCADE,"+
                "FOREIGN KEY (bundleid) REFERENCES "+T_BUNDLES+" (bundleid)"+
                ");");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_STRANDS+" ( "+
                "bundleid INT,"+
                "rootid INT,"+
                "unread INT,"+
                "hot INT,"+
                "action INT,"+
                "keep INT,"+
                "ignore INT,"+
                "deleted INT,"+
                "total INT,"+
                "commentto INT,"+
                "PRIMARY KEY(bundleid,rootid),"+
                "UNIQUE (bundleid,rootid));");
        
        stmt.executeUpdate("CREATE INDEX strandindex ON "+T_STRANDS+" (bundleid,commentto);");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_DOORS+" ("+
                "doorid INT IDENTITY,"+
                "doorname VARCHAR(255),"+
                "doorclass VARCHAR(255))");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_TAGS+" ("+
                "tagid INT NOT NULL,"+
                "tagname VARCHAR(32),"+
                "tagdescription VARCHAR(255),"+
                "PRIMARY KEY (tagid),"+
                "UNIQUE (tagid));");
        
        stmt.executeUpdate("CREATE CACHED TABLE "+T_TAGMSG+" ("+
                "msgid INT NOT NULL,"+
                "tagid INT NOT NULL);");
        
        stmt.executeUpdate("INSERT INTO "+T_DOORS+" VALUES ( NULL, 'cix', 'com.runstate.augur.cix.CixDoor');");
        
    }
    
//	long bundleidgen=1000;
    
    private void initialiseStatements() throws SQLException {
        insertMsgPstmt=conn.prepareStatement("insert into "+T_MSGINDEX+" (msgid,bundleid,spid,msgdate,subject,author,commentto,sourceid,unread,ignore,keep,hot,actionable,bodystart,rootid,deleted,firstcomment)  VALUES (  ? , ? , ? , ? , ? , ? , ? ,  ? , ? , ? , ? , ? , ? , ? , ? , ? , ? );");
        
        updateMsgPstmt=conn.prepareStatement("update "+T_MSGINDEX+" SET unread=?,ignore=?,keep=?,hot=?,actionable=?,rootid=?,deleted=?,firstcomment=? where bundleid=? and spid=?;");
        getKnotIdsOfChildren=conn.prepareStatement("select spid from "+T_MSGINDEX+" where bundleid=? and commentto=?;");
        updateStrandidPstmt=conn.prepareStatement("update "+T_MSGINDEX+" set rootid=? where bundleid=? and rootid=?;");
        setStrandMsgsUnread_pstmt=conn.prepareStatement("update "+T_MSGINDEX+" set unread=? where bundleid=? and rootid=? and unread=?;");
        
        getMsgByBundleidSpidPstmt=conn.prepareStatement("select * from "+T_MSGINDEX+" where bundleid=? and spid=?;");
        getMsgrootsPstmt=conn.prepareStatement("select * from "+T_MSGINDEX+" where bundleid=? and spid=rootid order by spid;");
        
        getMsgsInStrandPstmt=conn.prepareStatement("select * from "+T_MSGINDEX+" where bundleid=? and rootid=? order by spid;");
        
        getMsgStrandidPstmt=conn.prepareStatement("select rootid from "+T_MSGINDEX+" where bundleid=? and spid=?;");
        
        getFulltextPstmt=conn.prepareStatement("select text from "+T_FULLTEXT+" where msgid=?;");
        insertFulltextPstmt=conn.prepareStatement("insert into "+T_FULLTEXT+" ( msgid,text ) VALUES ( NULL , ? );");
        deleteFulltextPstmt=conn.prepareStatement("delete from "+T_FULLTEXT+" where msgid=?;");
        
        getMsgsPstmt=conn.prepareStatement("select * from "+T_MSGINDEX+" where bundleid=? order by spid asc;");
        deleteMsgsByBundleidPstmt=conn.prepareStatement("delete from "+T_MSGINDEX+" where bundleid=?;");
        
        insertPoolablePstmt=conn.prepareStatement("INSERT INTO "+T_POOL+" VALUES ( ? , ? , ? );");
        updatePoolablePstmt=conn.prepareStatement("UPDATE "+T_POOL+" set poolable=? where pool=? and key=?;");
        getPoolablePstmt=conn.prepareStatement("select poolable from "+T_POOL+" where pool like ? and key like ?;");
        getPoolKeysPstmt=conn.prepareStatement("select key from "+T_POOL+" where pool like ? order by key asc;");
        deletePoolablePstmt=conn.prepareStatement("delete from "+T_POOL+" where pool=? and key=?;");
        
        insertCommandPstmt=conn.prepareStatement("INSERT INTO "+T_COMMANDS+"  VALUES (  ? , ? , ? , ? , ? , ? , ?, ? );");
        updateCommandsPstmt=conn.prepareStatement("UPDATE "+T_COMMANDS+" set doorid=?,date=?,command=?,hold=?,keep=?,done=?,donedate=? where id=?;");
        getCommandsPstmt=conn.prepareStatement("select command from "+T_COMMANDS+" order by date asc;");
        getCommandIdsPstmt=conn.prepareStatement("select id from "+T_COMMANDS+" order by date asc;");
        getCommandIdsForDoorIdPstmt=conn.prepareStatement("select id from "+T_COMMANDS+" where doorid=? order by date asc;");
        getCommandPstmt=conn.prepareStatement("select command from "+T_COMMANDS+" where id=?;");
        
        getPendingCommandsPstmt=conn.prepareStatement("select command from "+T_COMMANDS+" where hold=0 and done=0 order by date asc;");
        getPendingCommandCountPstmt=conn.prepareStatement("select count(*) from "+T_COMMANDS+" where hold=0 and done=0;");
        
        deleteCommandPstmt=conn.prepareStatement("delete from "+T_COMMANDS+" where id=?;");
        
        insertBundlePstmt=conn.prepareStatement("INSERT INTO "+T_BUNDLES+" (doorid,bundleid,name,unread,total,warmunread,container,parentid) VALUES (  ? , ? , ? , ? , ? , ? , ? , ? );");
        updateBundlePstmt=conn.prepareStatement("update "+T_BUNDLES+" set unread=?,total=?,warmunread=?,parentid=? where bundleid=?;");
        deleteBundlePstmt=conn.prepareStatement("delete from "+T_BUNDLES+" where bundleid=?;");
        
        
        insertStrandPstmt=conn.prepareStatement("INSERT INTO "+T_STRANDS+" (bundleid,rootid,unread,hot,action,keep,ignore,deleted,total,commentto) VALUES (  ? , ? , ? , ? , ? , ? , ?, ? , ? , ? );");
        getStrandPstmt=conn.prepareStatement("select bundleid,rootid,unread,hot,action,keep,ignore,deleted,total,commentto from "+T_STRANDS+" where bundleid=? and rootid=? order by rootid;");
        getStrandsPstmt=conn.prepareStatement("select bundleid,rootid,unread,hot,action,keep,ignore,deleted,total,commentto from "+T_STRANDS+" where bundleid=?;");
        
        getCommentingStrandsPstmt=conn.prepareStatement("select bundleid,rootid,unread,hot,action,keep,ignore,deleted,total,commentto from "+T_STRANDS+" where bundleid=? and commentto=?;");
        
        updateStrandPstmt=conn.prepareStatement("update "+T_STRANDS+" set total=?,unread=?,hot=?,ignore=?,action=? where bundleid=? and rootid=?;");
        
        deleteStrand_pstmt=conn.prepareStatement("delete from "+T_STRANDS+" where bundleid=? and rootid=?;");
        
        getMsgAttributesPstmt=conn.prepareStatement("select unread,ignore,keep,hot,actionable,commentto from "+T_MSGINDEX+" where bundleid=? and spid=?;");
        
        setMsgFirstComment=conn.prepareStatement("update "+T_MSGINDEX+" set firstcomment=? where bundleid=? and spid=?;");
        setMsgUnread=conn.prepareStatement("update "+T_MSGINDEX+" set unread=? where bundleid=? and spid=?;");
        setMsgHot=conn.prepareStatement("update "+T_MSGINDEX+" set hot=? where bundleid=? and spid=?;");
        setMsgTagged=conn.prepareStatement("update "+T_MSGINDEX+" set actionable=? where bundleid=? and spid=?;");
        setMsgIgnore=conn.prepareStatement("update "+T_MSGINDEX+" SET ignore=? where bundleid=? and spid=?;");
        getRootids=conn.prepareStatement("select rootid from "+T_STRANDS+" where bundleid=? order by rootid asc;");
        
        getTag=conn.prepareStatement("select tagid,tagname,tagdescription from "+T_TAGS+" where tagid=?;");
        getTags=conn.prepareStatement("select tagid from "+T_TAGS+";");
        updateTag=conn.prepareStatement("update "+T_TAGS+" SET tagname=?,tagdescription=? WHERE tagid=?;");
        insertTag=conn.prepareStatement("insert into "+T_TAGS+" values (tagid=?,tagname=?,tagdescription=?);");
        deleteTag=conn.prepareStatement("delete from "+T_TAGS+" where tagid=?");
        
        searchPstmt=conn.prepareStatement("select * from "+T_MSGINDEX+","+T_FULLTEXT+" where bundleid=? and "
                    +T_MSGINDEX+".msgid="+T_FULLTEXT+".msgid and text LIKE ?;");
    }
    
    
    public void shutdown() {
        sendGalleryEvent(new GalleryEvent("Gallery Closing"));
        
        try {
            
            boolean shutdown=true;
            
            if(dburl.startsWith("jdbc:hsqldb:hsql:")) {
                if(getProfile().getBool(Prefs.G_HSQL_OFFERSHUTDOWN,false) ) {
                    shutdown=false;
                    int result=JOptionPane.showConfirmDialog(null,"Shutdown remote database?","Gallery",JOptionPane.YES_NO_OPTION);
                    if(result==JOptionPane.YES_OPTION) {
                        shutdown=true;
                    }
                } else {
                    shutdown=false;
                }
            }
            
            
            if(stmt!=null) {
                if(shutdown) {
                    if(getProfile().getBool(Prefs.G_HSQL_COMPACT,false))
                        
                    {
                        stmt.execute("SHUTDOWN COMPACT;");
                    } else {
                        stmt.execute("SHUTDOWN;");
                    }
                }
                stmt.close();
                conn.close();
            }
            
        } catch (SQLException e) {}
        try {
            if(conn!=null) conn.close();
        } catch (SQLException e) { e.printStackTrace(); }
        
    }
    
    private void loadBundleManager() {
        try {
            
            ResultSet rs=stmt.executeQuery("select doorid,bundleid,name,unread,total,warmunread,container,parentid from "+T_BUNDLES+" order by bundleid;");
            
            while (rs.next()) {
                Long doorid=longToLong(rs.getLong(1));
                Long bundleid=longToLong(rs.getLong(2));
                String name=rs.getString(3);
                int unread=rs.getInt(4);
                int total=rs.getInt(5);
                int warmunread=rs.getInt(6);
                boolean container=rs.getBoolean(7);
                Long parentid=longToLong(rs.getLong(8));
                
                Bundle bundle=new Bundle(doorid,bundleid,name,total,unread,warmunread,container,parentid);
                
                bundlemanager.addBundle(bundle);
                
                
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
    }
    
    
    public void persistStrand(Strand strand) {
//		log.info("Write Strand "+strand);
        
        try {
            insertStrandPstmt.setLong(1,LongTolong(strand.getBundleid()));
            insertStrandPstmt.setLong(2,LongTolong(strand.getRootid()));
            insertStrandPstmt.setInt(3,strand.getUnread());
            insertStrandPstmt.setInt(4,strand.getHot());
            insertStrandPstmt.setInt(5,strand.getTagged());
            insertStrandPstmt.setInt(6,strand.getKeep());
            insertStrandPstmt.setInt(7,strand.getIgnore());
            insertStrandPstmt.setInt(8,strand.getDeleted());
            insertStrandPstmt.setInt(9,strand.getTotal());
            insertStrandPstmt.setLong(10,LongTolong(strand.getCommentto()));
            insertStrandPstmt.executeUpdate();
            
            fireCoreEvent(new StrandEvent(strand.getBundleid(),strand.getRootid(),StrandEvent.NEW));
        } catch (SQLException e) { throw new GalleryException("writeAugurThread failed",e); }
    }
    
    public void updateStrand(Long bundleid,Long strandid,int total,int unread, int hot, int ignore,int tagged) {
//		log.info("Update Strand "+strand);
        try {
            updateStrandPstmt.setInt(1,total);
            updateStrandPstmt.setInt(2,unread);
            updateStrandPstmt.setInt(3,hot);
            updateStrandPstmt.setInt(4,ignore);
            updateStrandPstmt.setInt(5,tagged);
            updateStrandPstmt.setLong(6,LongTolong(bundleid));
            updateStrandPstmt.setLong(7,LongTolong(strandid));
            
            updateStrandPstmt.executeUpdate();
        } catch (SQLException e) { throw new GalleryException("updateStrand failed",e); }
        
    }
    
    private long LongTolong(Long l) {
        if(l==null) return -1;
        
        return l.longValue();
    }
    
    private Long longToLong(long l) {
        if(l==-1) return null;
        
        return new Long(l);
    }
    
    public boolean updateStrandForSingleMsg(Strand at) {
        
        try {
            updateStrandPstmt.setInt(1,at.getUnread());
            updateStrandPstmt.setInt(2,at.getHot());
            updateStrandPstmt.setInt(3,at.getTagged());
            updateStrandPstmt.setInt(4,at.getKeep());
            updateStrandPstmt.setInt(5,at.getIgnore());
            updateStrandPstmt.setInt(6,at.getDeleted());
            updateStrandPstmt.setInt(7,at.getTotal());
            updateStrandPstmt.setLong(8,LongTolong(at.getCommentto()));
            updateStrandPstmt.setLong(9,LongTolong(at.getBundleid()));
            updateStrandPstmt.setLong(10,LongTolong(at.getRootid()));
            
            int i=updateStrandPstmt.executeUpdate();
            
            fireCoreEvent(new StrandEvent(at.getBundleid(),at.getRootid(),StrandEvent.UPDATESINGLE));
            
            if(i>0) return true;
            
            return false;
        } catch (SQLException e) { throw new GalleryException("updateAugurThread failed",e); }
        
    }
    
    /**
     * Method coalesce
     *
     * @param    bundleid            a  Long
     * @param    domid               a  Long
     * @param    subid               a  Long
     *
     */
    public void coalesce(Long bundleid, Long domid, Long subid) {
        Msg dommsg=getMsg(bundleid,domid);
        Strand domstrand=getStrand(bundleid,dommsg.getRootKnotId());
        Msg submsg=getMsg(bundleid,subid);
        Strand substrand=getStrand(bundleid,subid);
        
        deleteStrand(bundleid,substrand.getRootid());
        
        domstrand.setTotal(domstrand.getTotal()+substrand.getTotal());
        domstrand.setUnread(domstrand.getUnread()+substrand.getUnread());
        domstrand.setHot(domstrand.getHot()+substrand.getHot());
        domstrand.setIgnore(domstrand.getIgnore()+substrand.getIgnore());
        domstrand.setTagged(domstrand.getTagged()+substrand.getTagged());
        
        updateStrand(bundleid,domid,domstrand.getTotal(),domstrand.getUnread(),domstrand.getHot(),domstrand.getIgnore(),domstrand.getTagged());
        
        Long commentto=submsg.getCommentto();
        
        if(commentto.equals(domid)) {
            if(dommsg.getFirstcomment()==null) {
                setFirstComment(bundleid,domid,subid);
            }
        } else {
            Msg actpa=getMsg(bundleid,commentto);
            if(actpa.getFirstcomment()==null) {
                setFirstComment(bundleid,commentto,subid);
            }
        }
        
        updateRootid(bundleid,dommsg.getRootKnotId(),subid);
    }
    
    
    
    public ArrayList<Msg> searchStrand(Long bundleid,String term)
    {
        try {
            searchPstmt.setLong(1,bundleid.longValue());
            searchPstmt.setString(2,"%"+term+"%");
            ResultSet rs=searchPstmt.executeQuery();
            if(rs.next())
            {
               return makeResultsToMsgArrayList(rs);
            }
        }
        catch(SQLException sqe)
        {
            sqe.printStackTrace();
        }
        return new ArrayList<Msg>();
    }
    
    public synchronized Strand getStrand(Long bundleid, Long rootid) {
        try {
            getStrandPstmt.setLong(1,bundleid.longValue());
            getStrandPstmt.setLong(2,rootid.longValue());
            ResultSet rs=getStrandPstmt.executeQuery();
            
            if(rs.next()) {
                return makeResultsToStrand(rs);
            }
            
//			log.error("Gallery failed to retrieve "+bundleid+","+rootid);
            return null;
        } catch (SQLException e) { throw new GalleryException("getStrand failed",e); }
        
    }
    
    public ArrayList<Long> getRootids(Long bundleid) {
        try {
            getRootids.setLong(1,bundleid.longValue());
            ResultSet rs=getRootids.executeQuery();
            ArrayList<Long> al=new ArrayList<Long>();
            while(rs.next()) {
                al.add(longToLong(rs.getLong(1)));
            }
            
            return al;
        } catch (SQLException e) { throw new GalleryException("getRootids failed",e); }
    }
    
    public void updateRootid(Long bundleid, Long newrootid,Long oldrootid) {
        try {
            updateStrandidPstmt.setLong(1,LongTolong(newrootid));
            updateStrandidPstmt.setLong(2,LongTolong(bundleid));
            updateStrandidPstmt.setLong(3,LongTolong(oldrootid));
            
            updateStrandidPstmt.execute();
        } catch (SQLException e) { throw new GalleryException("updateRootid failed",e); }
        
    }
    
    public void setMsgUnread(Long bundleid,Long spid,boolean b) {
        try {
            setMsgUnread.setBoolean(1,b);
            setMsgUnread.setLong(2,LongTolong(bundleid));
            setMsgUnread.setLong(3,LongTolong(spid));
            setMsgUnread.executeUpdate();
            
            fireCoreEvent(new MsgEvent(MsgEvent.UPDATE,bundleid,spid));
        } catch(SQLException sqe) { throw new GalleryException("setMsgUnread failed",sqe);
        }
    }
    
    public void setMsgHot(Long bundleid,Long spid,boolean b) {
        try {
            setMsgHot.setBoolean(1,b);
            setMsgHot.setLong(2,LongTolong(bundleid));
            setMsgHot.setLong(3,LongTolong(spid));
            setMsgHot.executeUpdate();
            
            fireCoreEvent(new MsgEvent(MsgEvent.UPDATE,bundleid,spid));
        } catch(SQLException sqe) { throw new GalleryException("setMsgHot failed",sqe);
        }
    }
    
    public void setMsgTagged(Long bundleid,Long spid,boolean b) {
        try {
            setMsgTagged.setBoolean(1,b);
            setMsgTagged.setLong(2,LongTolong(bundleid));
            setMsgTagged.setLong(3,LongTolong(spid));
            setMsgTagged.executeUpdate();
            
            fireCoreEvent(new MsgEvent(MsgEvent.UPDATE,bundleid,spid));
        } catch(SQLException sqe) { throw new GalleryException("setMsgTagged failed",sqe);
        }
    }
    
    public void setMsgIgnore(Long bundleid,Long spid,boolean b) {
        try {
            setMsgIgnore.setBoolean(1,b);
            setMsgIgnore.setLong(2,LongTolong(bundleid));
            setMsgIgnore.setLong(3,LongTolong(spid));
            setMsgIgnore.executeUpdate();
            
            fireCoreEvent(new MsgEvent(MsgEvent.UPDATE,bundleid,spid));
        } catch(SQLException sqe) { throw new GalleryException("setMsgIgnore failed",sqe);
        }
    }
    
    public ArrayList<Strand> getStrands(Long bundleid) {
        try {
            getStrandsPstmt.setLong(1,bundleid.longValue());
            ResultSet rs=getStrandsPstmt.executeQuery();
            
            ArrayList<Strand> atarray=new ArrayList<Strand>();
            
            while(rs.next()) {
                atarray.add(makeResultsToStrand(rs));
            }
            
            return atarray;
        } catch (SQLException e) { throw new GalleryException("getAugurThreads failed",e); }
        
    }
    
    public ArrayList<Strand> getStrandsWhichAreACommentTo(Long bundleid,Long commentto) {
        try {
            getCommentingStrandsPstmt.setLong(1,LongTolong(bundleid));
            getCommentingStrandsPstmt.setLong(2,LongTolong(commentto));
            ResultSet rs=getCommentingStrandsPstmt.executeQuery();
            
            ArrayList<Strand> atarray=new ArrayList<Strand>();
            
            while(rs.next()) {
                atarray.add(makeResultsToStrand(rs));
            }
            
            return atarray;
        } catch (SQLException e) { throw new GalleryException("getStrand failed",e); }
    }
    
    private Strand makeResultsToStrand(ResultSet rs) {
        try {
            Long bundleid=longToLong(rs.getLong("bundleid"));
            Long rootid=longToLong(rs.getLong("rootid"));
            int unread=rs.getInt("unread");
            int hot=rs.getInt("hot");
            int action=rs.getInt("action");
            int keep=rs.getInt("keep");
            int ignore=rs.getInt("ignore");
            int deleted=rs.getInt("deleted");
            int total=rs.getInt("total");
            Long commentto=longToLong(rs.getLong("commentto"));
            
            Strand at=new Strand(bundleid,rootid,unread,hot,action,keep,ignore,deleted,total,commentto);
            
            return at;
        } catch (SQLException e) { throw new GalleryException("Failed to read strand",e); }
    }
    
    
    public void deleteStrand(Long bundleid,Long rootid) {
        try {
            deleteStrand_pstmt.setLong(1,LongTolong(bundleid));
            deleteStrand_pstmt.setLong(2,LongTolong(rootid));
            deleteStrand_pstmt.executeUpdate();
        } catch (SQLException e) { throw new GalleryException("deleteStrand failed",e); }
    }
    
    public void setStrandMsgsUnread(Long bundleid,Long rootid,boolean b) {
        try {
            setStrandMsgsUnread_pstmt.setBoolean(1,b);
            setStrandMsgsUnread_pstmt.setLong(2,LongTolong(bundleid));
            setStrandMsgsUnread_pstmt.setLong(3,LongTolong(rootid));
            setStrandMsgsUnread_pstmt.setBoolean(4,!b);
            setStrandMsgsUnread_pstmt.executeUpdate();
        } catch (SQLException e) { throw new GalleryException("setStrandUnread failed",e); }
    }
    
    public void checkpoint() {
        try {
            stmt.execute("checkpoint;");
        } catch (SQLException e) { throw new GalleryException("checkpoint failed",e); }
    }
    
//	public GalleryTreeModel getGalleryTreeModel()
//	{
//		return galleryTreeModel;
//	}
    
    public boolean getBundleExists(String bundlename) {
        return bundlemanager.getBundleExists(bundlename);
    }
    
    public String getText(Long id) {
        try {
            getFulltextPstmt.setLong(1,id.longValue());
            ResultSet rs=getFulltextPstmt.executeQuery();
            
            if(rs.next()) {
                String s=rs.getString(1);
                return s;
            }
        } catch (SQLException e) { throw new GalleryException("Couldn't get full text",e); }
        
        return null;
    }
    
    public Long saveText(String text) {
        try {
            insertFulltextPstmt.setString(1,text);
            insertFulltextPstmt.execute();
            insertFulltextPstmt.clearParameters();
            
            
            ResultSet rs=stmt.executeQuery("CALL IDENTITY();");
            
            if(rs.next()) {
                return longToLong(rs.getLong(1));
            } else {
                throw new GalleryException("Did not set msgid",null);
            }
        } catch(SQLException e) { throw new GalleryException("Couldn't save full text",e); }
    }
    
    public ArrayList<Bundle> getBundles() {
        ArrayList<Bundle> al=bundlemanager.getBundles();
        
        Collections.sort(al,new Comparator<Bundle>() {
            
            public int compare(Bundle p1, Bundle p2) {
                return p1.getBundlename().compareTo(p2.getBundlename());
            }
        });
        return al;
    }
    
    public boolean newMsg(Msg new_msg,Long doorid) {
        Bundle bundle=null;
        
        if(new_msg.getBundleId()==null) {
            bundle=bundlemanager.getBundle(new_msg.getTextpath());
            if(bundle==null) {
                bundle=bundlemanager.createBundle(new_msg.getTextpath(),doorid,false);
            }
            
            new_msg.setBundleId(bundle.getBundleid());
        } else {
            bundle=bundlemanager.getBundle(new_msg.getBundleId());
        }
        
        return bundle.newMsg(new_msg);
    }
    
    public void persistMsg(Msg msg) {
        try {
            insertMsgPstmt.setLong(1,LongTolong(msg.getMsgId()));
            insertMsgPstmt.setLong(2,LongTolong(msg.getBundleId()));
            insertMsgPstmt.setLong(3,LongTolong(msg.getKnotId()));
            
            
            if(msg.getMsgDate()==null) {
                insertMsgPstmt.setTimestamp(4,new Timestamp((new java.util.Date().getTime())));
            } else {
                insertMsgPstmt.setTimestamp(4,msg.getMsgDate());
            }
            
            insertMsgPstmt.setString(5,msg.getSubject());
            insertMsgPstmt.setString(6,msg.getAuthor());
            insertMsgPstmt.setLong(7,LongTolong(msg.getCommentto()));
            insertMsgPstmt.setString(8,msg.getSourceid());
            insertMsgPstmt.setBoolean(9,msg.isUnread());
            insertMsgPstmt.setBoolean(10,msg.isIgnore());
            insertMsgPstmt.setBoolean(11,msg.isKeep());
            insertMsgPstmt.setBoolean(12,msg.isHot());
            insertMsgPstmt.setBoolean(13,msg.isTagged());
            insertMsgPstmt.setInt(14,msg.getBodystart());
            insertMsgPstmt.setLong(15,LongTolong(msg.getRootKnotId()));
            insertMsgPstmt.setBoolean(16,msg.isDeleted());
            insertMsgPstmt.setLong(17,LongTolong(msg.getFirstcomment()));
            
            
            insertMsgPstmt.executeUpdate();
            
            insertMsgPstmt.clearParameters();
        } catch (SQLException e) {
//			log.error("Failed to write "+msg,e);
            throw new GalleryException("Failed to write Msg",e); }
    }
    
    public void setFirstComment(Long bundleid,Long id,Long fcid) {
        try {
            setMsgFirstComment.setLong(1,LongTolong(fcid));
            setMsgFirstComment.setLong(2,LongTolong(bundleid));
            setMsgFirstComment.setLong(3,LongTolong(id));
            
            setMsgFirstComment.executeUpdate();
            
            fireCoreEvent(new MsgEvent(MsgEvent.UPDATE,bundleid,id));
        } catch(SQLException sqe) {
            throw new GalleryException("Failed to set first comment field",sqe);
        }
    }
    
    public boolean isUnread(Long bundleid,Long id) { return isAttrib(bundleid,id,"unread"); }
    public boolean isHot(Long bundleid,Long id) { return isAttrib(bundleid,id,"hot"); }
    public boolean isIgnore(Long bundleid,Long id) { return isAttrib(bundleid,id,"ignore"); }
    public boolean isActionable(Long bundleid,Long id) { return isAttrib(bundleid,id,"actionable"); }
    public boolean isKeep(Long bundleid,Long id) { return isAttrib(bundleid,id,"keep"); }
    
    
    boolean isAttrib(Long bundleid,Long id,String attribname) {
        try {
            getMsgAttributesPstmt.setLong(1,LongTolong(bundleid));
            getMsgAttributesPstmt.setLong(2,LongTolong(id));
            ResultSet rs=getMsgAttributesPstmt.executeQuery();
            
            if(!rs.next()) throw new GalleryException("Tried to get attributes on non existant message",null);
            
            return rs.getBoolean(attribname);
        } catch (SQLException e) { throw new GalleryException("Failed in isAttrib",e); }
        
        // unread,ignore,keep,hot,actionable
    }
    
    public Long getCommentTo(Long bundleid,Long id) {
        try {
            getMsgAttributesPstmt.setLong(1,LongTolong(bundleid));
            getMsgAttributesPstmt.setLong(2,LongTolong(id));
            ResultSet rs=getMsgAttributesPstmt.executeQuery();
            
            if(!rs.next()) return new Long(-1);
            
            return longToLong(rs.getLong("commentto"));
        } catch (SQLException e) { throw new GalleryException("Failed in getCommentTo",e); }
    }
    
    public ArrayList<Bundle> getSubPaths(Long bundleid) {
        Bundle bundle=bundlemanager.getBundle(bundleid);
        
        ArrayList<Bundle> results=new ArrayList<Bundle>(bundle.getChildren());
        
        Collections.sort(results,new Comparator<Bundle>() {
            public int compare(Bundle b1, Bundle b2) {
                return b1.getBundlename().compareTo(b2.getBundlename());
            }
        });
        
        return results;
    }
    
    public void deleteBundle(Long path) {
        ArrayList<Bundle> paths=getSubPaths(path);
        Iterator<Bundle> i=paths.iterator();
        while(i.hasNext()) {
            Bundle ps=i.next();
            sendGalleryEvent(new GalleryEvent("Deleting "+ps.getBundlename()));
            deleteBundle(ps.getBundleid());
        }
        
        try {
            Bundle ps=bundlemanager.getBundle(path);
            
            if(ps==null) { return; }
            
            deleteMsgsByBundleidPstmt.setLong(1,LongTolong(path));
            deleteMsgsByBundleidPstmt.executeUpdate();
            
            deleteBundlePstmt.setLong(1,LongTolong(path));
            deleteBundlePstmt.executeUpdate();
            
            fireCoreEvent(new BundleEvent(BundleEvent.DELETE,ps.getBundleid()));
        } catch (SQLException e) { e.printStackTrace(); }
        
        //	deletePath(path); // Tidy up :)
    }
    
    
    
    public BundleManager getBundleManager() {
        return bundlemanager;
    }
    
//	public void setReadMsgFlag(Long path,boolean b) throws GalleryException
//	{
//		Bundle p=bundlemanager.getBundle(path);
//
//
//		ArrayList<Bundle> a=getSubPaths(path);
//
//		Iterator<Bundle> i=a.iterator();
//
//		while(i.hasNext())
//		{
//			Bundle cp=i.next();
//			cp.setBundleUnread(b);
//		}
//
//		if(p!=null) p.setBundleUnread(b);
//
//		return;
//
////		p.setPathUnread(b);
//	}
    
    private ArrayList<Msg> makeResultsToMsgArrayList(ResultSet rs) throws SQLException {
        ArrayList<Msg> a=new ArrayList<Msg>();
        
        while(rs.next()) {
            Msg m = makeResultToMsg(rs);
            a.add(m);
        }
        
        return a;
    }
    
    private Msg makeResultToMsg(ResultSet rs) throws SQLException {
        Long bundleid=longToLong(rs.getLong("bundleid"));
        
        Msg m=new Msg(getDoorByBundleId(bundleid));
        
        m.setMsgId(longToLong(rs.getLong("msgid")));
        m.setBundleId(longToLong(rs.getLong("bundleid")));
        m.setKnotId(longToLong(rs.getLong("spid")));
        m.setMsgdate(rs.getTimestamp("msgdate"));
        m.setAuthor(rs.getString("author"));
        m.setSubject(rs.getString("subject"));
        m.setCommentto(longToLong(rs.getLong("commentto")));
        m.setSourceid(rs.getString("sourceid"));
        m.setUnread(rs.getBoolean("unread"));
        m.setKeep(rs.getBoolean("keep"));
        m.setIgnore(rs.getBoolean("ignore"));
        m.setHot(rs.getBoolean("hot"));
        m.setTagged(rs.getBoolean("actionable"));
        m.setBodystart(rs.getInt("bodystart"));
        m.setRootKnotId(longToLong(rs.getLong("rootid")));
        m.setDeleted(rs.getBoolean("deleted"));
        m.setFirstcomment(longToLong(rs.getLong("firstcomment")));
        
        return m;
        
    }
    
    
    
    public void writeBundle(Bundle bundle) {
//		log.info("Writing Bundle "+bundle);
        
        try {
//				bundlemanager.addBundle(bundle);
            
//				bundle.setBundleid(bundleidgen++);
            insertBundlePstmt.setLong(1,bundle.getDoorid().longValue());
            insertBundlePstmt.setLong(2,bundle.getBundleid().longValue());
            insertBundlePstmt.setString(3,bundle.getBundlename());
            insertBundlePstmt.setInt(4,bundle.getUnread());
            insertBundlePstmt.setInt(5,bundle.getTotal());
            insertBundlePstmt.setInt(6,bundle.getWarmunread());
            insertBundlePstmt.setBoolean(7,bundle.isContainer());
            insertBundlePstmt.setLong(8,bundle.getParentid().longValue());
            
            insertBundlePstmt.execute();
            insertBundlePstmt.clearParameters();
            
            //		putMap(bundle);
            
//				fireCoreEvent(new BundleEvent(BundleEvent.NEW,bundle.getBundleRef()));
        } catch(SQLException sqe) {
            throw new GalleryException("writeBundle failed",sqe);
        }
    }
    
    public void updateBundle(Bundle bundle) {
//		log.info("Updating Bundle"+bundle);
        
        try {
            
            updateBundlePstmt.setInt(1,bundle.getUnread());
            updateBundlePstmt.setInt(2,bundle.getTotal());
            updateBundlePstmt.setInt(3,bundle.getWarmunread());
            updateBundlePstmt.setLong(4,bundle.getParentid().longValue());
            
            updateBundlePstmt.setLong(5,bundle.getBundleid().longValue());
            
            updateBundlePstmt.execute();
        } catch (SQLException e) { throw new GalleryException("updateBundle failed",e); }
        
//			fireCoreEvent(new BundleEvent(BundleEvent.UPDATE,bundle.getBundleRef()));
        
    }
    
    public synchronized Msg getMsg(Long bundleid,Long spid) throws GalleryException {
        try {
            getMsgByBundleidSpidPstmt.setLong(1,LongTolong(bundleid));
            getMsgByBundleidSpidPstmt.setLong(2,LongTolong(spid));
            
            ResultSet rs=getMsgByBundleidSpidPstmt.executeQuery();
            
            while(rs.next()) {
                Msg m=makeResultToMsg(rs);
                
                return m;
            }
            
            return null;
        } catch(SQLException sqe) {
            throw new GalleryException("get message failed",sqe);
        }
    }
    
    public ArrayList<Msg> getMsgs(Long bundleid) {
        try {
            getMsgsPstmt.setLong(1,LongTolong(bundleid));
            ResultSet rs=getMsgsPstmt.executeQuery();
            
            return makeResultsToMsgArrayList(rs);
        } catch(SQLException sqe) {
            throw new GalleryException("GetMsgs",sqe);
        }
    }
    
    public ArrayList<Msg> getMsgs(long bundleid,long []ids) {
        if(ids.length==0) return new ArrayList<Msg>();
        
        StringBuffer sb=new StringBuffer();
        
        sb.append("select * from ");
        sb.append(T_MSGINDEX);
        sb.append(" where bundleid=");
        sb.append(bundleid);
        sb.append(" and spid in ( ");
        for(int i=0;i<ids.length;i++) {
            sb.append(Long.toString(ids[i]));
            if(i<ids.length-1) sb.append(",");
        }
        
        sb.append(")  order by spid asc;");
        
        try {
//			get_msgs_in_pstmt.setLong(1,bundleid);
//			get_msgs_in_pstmt.set(2,ids);
//			//sb.toString());
            
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sb.toString());
            
//			ResultSet rs=get_msgs_in_pstmt.executeQuery();
            
            return makeResultsToMsgArrayList(rs);
        } catch(SQLException sqe) {
            throw new GalleryException("GetMsgs",sqe);
        }
    }
    
    public ArrayList<Long> getChildIds(Long bundleid,Long spid) {
        try {
            getKnotIdsOfChildren.setLong(1,LongTolong(bundleid));
            getKnotIdsOfChildren.setLong(2,LongTolong(spid));
            ResultSet rs=getKnotIdsOfChildren.executeQuery();
            ArrayList<Long> al=new ArrayList<Long>();
            while(rs.next()) {
                al.add(new Long(rs.getLong(1)));
            }
            return al;
        } catch(SQLException sqe) {
            throw new GalleryException("GetChildMsgs",sqe);
        }
    }
    
    public ArrayList<Msg> getMsgsOnStrand(Long bundleid,Long rootid) {
        try {
            getMsgsInStrandPstmt.setLong(1,LongTolong(bundleid));
            getMsgsInStrandPstmt.setLong(2,LongTolong(rootid));
            ResultSet rs=getMsgsInStrandPstmt.executeQuery();
            
            return makeResultsToMsgArrayList(rs);
        } catch(SQLException sqe) {
            throw new GalleryException("GetMsgs",sqe);
        }
    }
    
    public ArrayList<Long> getStrandIds(Long bundleid,Long rootid) {
        try {
            getMsgsInStrandPstmt.setLong(1,LongTolong(bundleid));
            getMsgsInStrandPstmt.setLong(2,LongTolong(rootid));
            ResultSet rs=getMsgsInStrandPstmt.executeQuery();
            
            ArrayList<Long> al=new ArrayList<Long>();
            
            while(rs.next()) {
                al.add(rs.getLong("spid"));
            }
            return al;
        } catch(SQLException sqe) {
            throw new GalleryException("GetMsgs",sqe);
        }
    }
    public void addCommand(Command cmd) throws GalleryException {
        try {
            
            insertCommandPstmt.setString(1,cmd.getId());
            insertCommandPstmt.setLong(2,LongTolong(cmd.getDoorid()));
            insertCommandPstmt.setTimestamp(3,new Timestamp(cmd.getCreated().getTime()));
            insertCommandPstmt.setBoolean(4,cmd.isHold());
            insertCommandPstmt.setBoolean(5,cmd.isKeep());
            insertCommandPstmt.setBoolean(6,cmd.isDone());
            insertCommandPstmt.setTimestamp(7,cmd.getWhendone()==null?null:new Timestamp(cmd.getWhendone().getTime()));
            insertCommandPstmt.setObject(8,cmd);
            
            insertCommandPstmt.execute();
            insertCommandPstmt.clearParameters();
            
            fireCommandEvent(new CommandEvent(CommandEvent.ADDED,cmd));
        } catch(SQLException sqe) {
            throw new GalleryException("failed to store command",sqe);
        }
    }
    
    public void deleteCommand(Command cmd) throws GalleryException {
        try {
            deleteCommandPstmt.setString(1,cmd.getId());
            deleteCommandPstmt.execute();
            fireCommandEvent(new CommandEvent(cmd.getId()));
        } catch (SQLException sqe) {
            throw new GalleryException("failed to delete command",sqe);
        }
    }
    
    private ArrayList<Command> makeResultsToCommandArrayList(ResultSet rs) throws SQLException {
        ArrayList<Command> a=new ArrayList<Command>();
        
        while(rs.next()) {
            Command cmd=(Command)rs.getObject(1);
            a.add(cmd);
        }
        
        return a;
    }
    
    public ArrayList<Command> getCommands(boolean pendingOnly) {
        try {
            ResultSet rs;
            
            if(pendingOnly) {
                rs=getPendingCommandsPstmt.executeQuery();
            } else {
                rs=getCommandsPstmt.executeQuery();
            }
            
            return makeResultsToCommandArrayList(rs);
        } catch(SQLException sqe) {
            throw new GalleryException("GetCommands",sqe);
        }
    }
    
    public ArrayList<String> getCommandIds(Long doorid) {
        try {
            ResultSet rs;
            
            if(doorid==null) {
                rs=getCommandIdsPstmt.executeQuery();
            } else {
                getCommandIdsForDoorIdPstmt.setLong(1,LongTolong(doorid));
                rs=getCommandIdsForDoorIdPstmt.executeQuery();
            }
            
            ArrayList<String> al=new ArrayList<String>();
            
            while(rs.next()) {
                al.add(rs.getString(1));
            }
            
            return al;
        } catch(SQLException sqe) {
            throw new GalleryException("GetCommandIds",sqe);
        }
    }
    
    public Command getCommand(String id) {
        try {
            getCommandPstmt.setString(1,id);
            ResultSet rs=getCommandPstmt.executeQuery();
            
            if(!rs.next()) return null;
            
            return (Command)rs.getObject(1);
        } catch(SQLException sqe) {
            throw new GalleryException("GetCommand",sqe);
        }
    }
    
    public ArrayList<Command> getPendingCommands(Long doorid) {
        try {
            ResultSet rs=getPendingCommandsPstmt.executeQuery();
            return makeCommandResultsToArrayList(rs);
        } catch(SQLException sqe) {
            throw new GalleryException("GetPendingCommands",sqe);
        }
    }
    
    public int getPendingCommandsCount() {
        if (getPendingCommandCountPstmt == null)
        {
            return 0;
        }
        
        try {
            ResultSet rs=getPendingCommandCountPstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch(SQLException sqe) {
            throw new GalleryException("GetPendingCommandsCount",sqe);
        }
    }
    
    private ArrayList<Command> makeCommandResultsToArrayList(ResultSet rs) throws SQLException {
        ArrayList<Command> a=new ArrayList<Command>();
        
        while(rs.next()) {
            Command cmd=(Command)rs.getObject(1);
            a.add(cmd);
        }
        
        return a;
    }
    
    public void updateCommand(Command cmd) {
        try {
            updateCommandsPstmt.setLong(1,LongTolong(cmd.getDoorid()));
            updateCommandsPstmt.setTimestamp(2,new Timestamp(cmd.getCreated().getTime()));
            updateCommandsPstmt.setObject(3,cmd);
            updateCommandsPstmt.setInt(4,cmd.isHold()?1:0);
            updateCommandsPstmt.setInt(5,cmd.isKeep()?1:0);
            updateCommandsPstmt.setInt(6,cmd.isDone()?1:0);
            updateCommandsPstmt.setTimestamp(7,cmd.getWhendone()==null?null:new Timestamp(cmd.getWhendone().getTime()));
            updateCommandsPstmt.setString(8,cmd.getId());
            
            updateCommandsPstmt.execute();
            updateCommandsPstmt.clearParameters();
            
            fireCommandEvent(new CommandEvent(CommandEvent.UPDATED,cmd));
            
        } catch(SQLException sqe) {
            throw new GalleryException("Store failed",sqe);
        }
    }
    
    
    public void addPoolable(Poolable pi) {
        try {
            
            insertPoolablePstmt.setString(1,pi.getPool());
            insertPoolablePstmt.setString(2,pi.getKey());
            insertPoolablePstmt.setObject(3,pi);
            insertPoolablePstmt.execute();
            insertPoolablePstmt.clearParameters();
            
            firePoolEvent(new PoolEvent(PoolEvent.ADDED,pi));
        } catch(SQLException sqe) {
            throw new GalleryException("AddPoolable failed",sqe);
        }
    }
    
    public void updatePoolable(Poolable pi) {
        try {
            updatePoolablePstmt.setObject(1,pi);
            updatePoolablePstmt.setString(2,pi.getPool());
            updatePoolablePstmt.setString(3,pi.getKey());
            updatePoolablePstmt.execute();
            updatePoolablePstmt.clearParameters();
            
            firePoolEvent(new PoolEvent(PoolEvent.UPDATED,pi));
            
        } catch(SQLException sqe) {
            throw new GalleryException("UpdatePoolable failed",sqe);
        }
    }
    public Poolable getPoolable(String pool,String key) {
        try {
            getPoolablePstmt.setString(1,pool);
            getPoolablePstmt.setString(2,key);
            
            ResultSet rs=getPoolablePstmt.executeQuery();
            
            if(rs.next()) {
                try {
                    Poolable pi=(Poolable)rs.getObject("poolable");
                    return pi;
                } catch(SQLException sqe) {
                    deletePoolable(pool,key);
                    return null;
                }
            }
            return null;
        } catch(SQLException sqe) {
            throw new GalleryException("GetPool",sqe);
        }
    }
    
    public ArrayList<Poolable> getPoolables(String pool) {
        try {
            getPoolablePstmt.setString(1,pool);
            getPoolablePstmt.setString(2,"%");
            
            ResultSet rs=getPoolablePstmt.executeQuery();
            
            ArrayList<Poolable> a=makePoolableResultsToArrayList(rs);
            
            return a;
        } catch(SQLException sqe) {
            throw new GalleryException("GetPool",sqe);
        }
    }
    
    public ArrayList<String> getPoolKeys(String pool) {
        try {
            getPoolKeysPstmt.setString(1,pool);
            
            ResultSet rs=getPoolKeysPstmt.executeQuery();
            
            ArrayList<String> al=new ArrayList<String>();
            
            while(rs.next()) {
                al.add(rs.getString(1));
            }
            
            return al;
        } catch(SQLException sqe) {
            throw new GalleryException("GetPoolKeys",sqe);
        }
    }
    public void deletePoolable(Poolable pi) {
        deletePoolable(pi.getPool(),pi.getKey());
    }
    
    public void deletePoolable(String pool,String key) {
        try {
            deletePoolablePstmt.setString(1,pool);
            deletePoolablePstmt.setString(2,key);
            deletePoolablePstmt.execute();
            firePoolEvent(new PoolEvent(pool,key));
        } catch (SQLException sqe) {
            throw new GalleryException("deletePool",sqe);
        }
    }
    
    private ArrayList<Poolable> makePoolableResultsToArrayList(ResultSet rs) throws SQLException {
        ArrayList<Poolable> a=new ArrayList<Poolable>();
        
        while(rs.next()) {
            Poolable pi=(Poolable)rs.getObject("poolable");
            a.add(pi);
        }
        
        return a;
    }
    
    
    HashSet<GalleryEventListener> galleryEventListenerSet=new HashSet<GalleryEventListener>();
    
    public void addGalleryEventListener(GalleryEventListener gallerylistener) {
        galleryEventListenerSet.add(gallerylistener);
    }
    
    public void removeGalleryEventListener(GalleryEventListener gallerylistener) {
        galleryEventListenerSet.remove(gallerylistener);
    }
    
    
    public void sendGalleryEvent(GalleryEvent galleryevent) {
        Iterator<GalleryEventListener> i=galleryEventListenerSet.iterator();
        
        while(i.hasNext()) {
            GalleryEventListener gallerylistener=i.next();
            
            gallerylistener.galleryEventRecieved(galleryevent);
        }
    }
    
    public PathInfo getPathInfo(Long bundleid) {
        return (PathInfo)getPoolable(PathInfo.getPoolName(),bundleid.toString());
    }
    
    public void addPathInfo(PathInfo pi) {
        addPoolable(pi);
    }
    
    public void updatePathInfo(PathInfo pi) {
        updatePoolable(pi);
    }
    
    public void deletePathInfo(PathInfo pi) {
        deletePoolable(pi);
    }
    
    public AuthorInfo getAuthorInfo(String address) {
        return (AuthorInfo)getPoolable(AuthorInfo.getPoolName(),address);
    }
    
    
    public ArrayList<Poolable> getAuthorInfo() {
        return getPoolables(AuthorInfo.getPoolName());
    }
    
    public ArrayList<String> getAuthorInfoAddresses() {
        return getPoolKeys(AuthorInfo.getPoolName());
    }
    
    public void addAuthorInfo(AuthorInfo ui) {
        addPoolable(ui);
    }
    
    public void updateAuthorInfo(AuthorInfo ui) {
        updatePoolable(ui);
    }
    
    public void deleteAuthorInfo(AuthorInfo ui) {
        deletePoolable(ui);
    }
    
//	ArrayList slog=new ArrayList();
//
//	public void log(String s)
//	{
//		slog.add(s);
//	}
    
    
    Vector<CommandEventListener> celisteners=new Vector<CommandEventListener>();
    
    
    public void addCommandEventListener(CommandEventListener piel) {
        celisteners.add(piel);
    }
    
    public void removeCommandEventListener(CommandEventListener piel) {
        celisteners.remove(piel);
    }
    
    public void fireCommandEvent(CommandEvent ce) // Everyone wants a bit of pie
    {
        for(Iterator<CommandEventListener> i=celisteners.iterator();i.hasNext();) {
            CommandEventListener cel=i.next();
            cel.commandEventOccurred(ce);
        }
    }
    
    Vector<CoreEventListener> coreEventListeners=new Vector<CoreEventListener>();
    
    public void addCoreEventListener(CoreEventListener mel) {
        synchronized(coreEventListeners) {
            coreEventListeners.add(mel);
        }
    }
    
    public void removeCoreEventListener(CoreEventListener mel) {
        synchronized(coreEventListeners) {
            coreEventListeners.remove(mel);
        }
    }
    
    public void fireCoreEvent(CoreEvent ce) // Everyone wants a bit of me
    {
        synchronized(coreEventListeners) {
            
            for(Iterator<CoreEventListener> i=coreEventListeners.iterator();i.hasNext();) {
                CoreEventListener mel=i.next();
                try {
                    mel.coreEventOccurred(ce);
                } catch (Exception e) { /* log.error("Error caught in fireCore Event",e); */ System.out.println(e);e.printStackTrace(); }
            }
            
        }
    }
    
    
    Vector<PoolEventListener> poolEventListeners=new Vector<PoolEventListener>();
    
    public void addPoolEventListener(PoolEventListener piel) {
        poolEventListeners.add(piel);
    }
    
    public void removePoolEventListener(PoolEventListener piel) {
        poolEventListeners.remove(piel);
    }
    
    public void firePoolEvent(PoolEvent piel) // Everyone wants a bit of pie
    {
        for(Iterator<PoolEventListener> i=poolEventListeners.iterator();i.hasNext();) {
            PoolEventListener cel=i.next();
            cel.poolEventOccurred(piel);
        }
    }
    
    /**
     * Method syncAll
     *
     */
    
    
    private void loadDoors() {
        try {
            
            ResultSet rs=stmt.executeQuery("select doorid,doorname,doorclass from "+T_DOORS+";");
            
            while (rs.next()) {
                Long doorid=longToLong(rs.getLong(1));
                String doorname=rs.getString(2);
                String doorclass=rs.getString(3);
                
                try {
                    Door d=(Door)Class.forName(doorclass).newInstance();
                    d.initialiseDoor(doorid,doorname);
                    doormap.put(doorname,d);
                    doormapbyid.put(doorid,d);
                } catch (InstantiationException e) {} catch (IllegalAccessException e) {} catch (ClassNotFoundException e) {}
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
    }
    public int syncDoor(String doorname)
    {
        int newmsgs=0;
        Door[] doors=getDoors();
        for(int i=0;i<doors.length;i++) {
            if (doors[i].getDoorname().equalsIgnoreCase(doorname))
            {
                int result=doors[i].session();
                if(result==-1) {
                // Error
                    System.out.println("Session reported error");
                } else {
                 newmsgs=newmsgs+result;
                }
            }
        }
        return newmsgs;
    }
    public int syncAll() {
        int newmsgs=0;
        Door[] doors=getDoors();
        for(int i=0;i<doors.length;i++) {
            int result=doors[i].session();
            if(result==-1) {
                // Error
                System.out.println("Session reported error");
            } else {
                newmsgs=newmsgs+result;
            }
        }
        return newmsgs;
    }
    
    SortedMap<String, Door> doormap=new TreeMap<String, Door>();
    SortedMap<Long, Door> doormapbyid=new TreeMap<Long, Door>();
    
    public Door[] getDoors() {
        return (Door [])doormap.values().toArray(new Door[]{});
        
    }
    
    public Door getDoorForAddress(String address) {
        int i=address.lastIndexOf('@');
        if(i==-1) return null;
        String doorname=address.substring(i+1);
        return doormap.get(doorname);
    }
    
    public Door getDoorForPath(String path) {
        String door=getDoornameForBundleName(path);
        return getDoorByName(door);
    }
    
    public String getDoornameForBundleName(String bundlename) {
        
        int i=bundlename.indexOf('/',1);
        
        if(i==-1) {
            return bundlename.substring(1);
        } else {
            return bundlename.substring(1,i);
        }
    }
    
    public Door getDoorByName(String doorname) {
        return doormap.get(doorname);
    }
    
    public Door getDoorByDoorId(Long doorid) {
        return doormapbyid.get(doorid);
    }
    
    public Door getDoorByBundleId(Long bundleid) {
        Bundle bundle=bundlemanager.getBundle(bundleid);
        
        return getDoorByDoorId(bundle.getDoorid());
    }
    
    public Long getDoorIdByBundleId(Long bundleid) {
        Door door=getDoorByBundleId(bundleid);
        
        return door.getDoorid();
    }
    
    public Tag getTag(Long tagid) {
        try {
            getTag.setLong(1,longToLong(tagid));
            ResultSet rs=getTag.executeQuery();
            if(!rs.next()) return null;
            Tag t=new Tag(tagid,rs.getString(2),rs.getString(3));
            return t;
        } catch (SQLException e) { return null; }
    }
    
    public ArrayList<Tag> getTags() {
        try {
            ArrayList<Tag> tl=new ArrayList<Tag>();
            
            ResultSet rs=getTags.executeQuery();
            while(rs.next()) {
                Tag t=new Tag(rs.getLong(1),rs.getString(2),rs.getString(3));
                tl.add(t);
            }
            
            return tl;
        } catch(SQLException sqe) {}
        
        return null;
    }
    
    public void updateTag(Tag t) {
        try {
            updateTag.setString(1,t.getName());
            updateTag.setString(2,t.getDescription());
            updateTag.setLong(3,LongTolong(t.getTagid()));
            updateTag.executeUpdate();
        } catch(SQLException sqe) {}
    }
    
    public void insertTag(Tag t) {
        try {
            insertTag.setLong(1,LongTolong(t.getTagid()));
            insertTag.setString(2,t.getName());
            insertTag.setString(3,t.getDescription());
            insertTag.executeUpdate();
        } catch(SQLException sqe) {}
    }
    
    public void deleteTag(Long tagid) {
        try {
            deleteTag.setLong(1,LongTolong(tagid));
            deleteTag.executeUpdate();
        } catch(SQLException sqe) {}
    }
    
}

