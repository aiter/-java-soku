package com.youku.soku.library.load;


import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.map.TableMap;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.ComboKey;
import org.apache.torque.om.DateKey;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.om.StringKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.Transaction;





/**
 * MUSIC-站点表
 *
 * This class was autogenerated by Torque on:
 *
 * [Sat Apr 23 11:09:17 CST 2011]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to MusicSite
 */
public abstract class BaseMusicSite extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1303528157948L;

    /** The Peer class */
    private static final MusicSitePeer peer =
        new MusicSitePeer();


    /** The value for the id field */
    private int id;

    /** The value for the fkMusicId field */
    private int fkMusicId;

    /** The value for the sourceSite field */
    private int sourceSite;

    /** The value for the logo field */
    private String logo;

    /** The value for the hd field */
    private int hd = 0;

    /** The value for the blocked field */
    private int blocked = 0;

    /** The value for the viewUrl field */
    private String viewUrl;

    /** The value for the detailUrl field */
    private String detailUrl;

    /** The value for the source field */
    private int source = 0;

    /** The value for the orderId field */
    private int orderId;

    /** The value for the showtotalVv field */
    private int showtotalVv;

    /** The value for the updateTime field */
    private Date updateTime;

    /** The value for the createTime field */
    private Date createTime;


    /**
     * Get the Id
     *
     * @return int
     */
    public int getId()
    {
        return id;
    }


    /**
     * Set the value of Id
     *
     * @param v new value
     */
    public void setId(int v) 
    {

        if (this.id != v)
        {
            this.id = v;
            setModified(true);
        }


    }

    /**
     * Get the FkMusicId
     *
     * @return int
     */
    public int getFkMusicId()
    {
        return fkMusicId;
    }


    /**
     * Set the value of FkMusicId
     *
     * @param v new value
     */
    public void setFkMusicId(int v) 
    {

        if (this.fkMusicId != v)
        {
            this.fkMusicId = v;
            setModified(true);
        }


    }

    /**
     * Get the SourceSite
     *
     * @return int
     */
    public int getSourceSite()
    {
        return sourceSite;
    }


    /**
     * Set the value of SourceSite
     *
     * @param v new value
     */
    public void setSourceSite(int v) 
    {

        if (this.sourceSite != v)
        {
            this.sourceSite = v;
            setModified(true);
        }


    }

    /**
     * Get the Logo
     *
     * @return String
     */
    public String getLogo()
    {
        return logo;
    }


    /**
     * Set the value of Logo
     *
     * @param v new value
     */
    public void setLogo(String v) 
    {

        if (!ObjectUtils.equals(this.logo, v))
        {
            this.logo = v;
            setModified(true);
        }


    }

    /**
     * Get the Hd
     *
     * @return int
     */
    public int getHd()
    {
        return hd;
    }


    /**
     * Set the value of Hd
     *
     * @param v new value
     */
    public void setHd(int v) 
    {

        if (this.hd != v)
        {
            this.hd = v;
            setModified(true);
        }


    }

    /**
     * Get the Blocked
     *
     * @return int
     */
    public int getBlocked()
    {
        return blocked;
    }


    /**
     * Set the value of Blocked
     *
     * @param v new value
     */
    public void setBlocked(int v) 
    {

        if (this.blocked != v)
        {
            this.blocked = v;
            setModified(true);
        }


    }

    /**
     * Get the ViewUrl
     *
     * @return String
     */
    public String getViewUrl()
    {
        return viewUrl;
    }


    /**
     * Set the value of ViewUrl
     *
     * @param v new value
     */
    public void setViewUrl(String v) 
    {

        if (!ObjectUtils.equals(this.viewUrl, v))
        {
            this.viewUrl = v;
            setModified(true);
        }


    }

    /**
     * Get the DetailUrl
     *
     * @return String
     */
    public String getDetailUrl()
    {
        return detailUrl;
    }


    /**
     * Set the value of DetailUrl
     *
     * @param v new value
     */
    public void setDetailUrl(String v) 
    {

        if (!ObjectUtils.equals(this.detailUrl, v))
        {
            this.detailUrl = v;
            setModified(true);
        }


    }

    /**
     * Get the Source
     *
     * @return int
     */
    public int getSource()
    {
        return source;
    }


    /**
     * Set the value of Source
     *
     * @param v new value
     */
    public void setSource(int v) 
    {

        if (this.source != v)
        {
            this.source = v;
            setModified(true);
        }


    }

    /**
     * Get the OrderId
     *
     * @return int
     */
    public int getOrderId()
    {
        return orderId;
    }


    /**
     * Set the value of OrderId
     *
     * @param v new value
     */
    public void setOrderId(int v) 
    {

        if (this.orderId != v)
        {
            this.orderId = v;
            setModified(true);
        }


    }

    /**
     * Get the ShowtotalVv
     *
     * @return int
     */
    public int getShowtotalVv()
    {
        return showtotalVv;
    }


    /**
     * Set the value of ShowtotalVv
     *
     * @param v new value
     */
    public void setShowtotalVv(int v) 
    {

        if (this.showtotalVv != v)
        {
            this.showtotalVv = v;
            setModified(true);
        }


    }

    /**
     * Get the UpdateTime
     *
     * @return Date
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }


    /**
     * Set the value of UpdateTime
     *
     * @param v new value
     */
    public void setUpdateTime(Date v) 
    {

        if (!ObjectUtils.equals(this.updateTime, v))
        {
            this.updateTime = v;
            setModified(true);
        }


    }

    /**
     * Get the CreateTime
     *
     * @return Date
     */
    public Date getCreateTime()
    {
        return createTime;
    }


    /**
     * Set the value of CreateTime
     *
     * @param v new value
     */
    public void setCreateTime(Date v) 
    {

        if (!ObjectUtils.equals(this.createTime, v))
        {
            this.createTime = v;
            setModified(true);
        }


    }

       
        
    private static List fieldNames = null;

    /**
     * Generate a list of field names.
     *
     * @return a list of field names
     */
    public static synchronized List getFieldNames()
    {
        if (fieldNames == null)
        {
            fieldNames = new ArrayList();
            fieldNames.add("Id");
            fieldNames.add("FkMusicId");
            fieldNames.add("SourceSite");
            fieldNames.add("Logo");
            fieldNames.add("Hd");
            fieldNames.add("Blocked");
            fieldNames.add("ViewUrl");
            fieldNames.add("DetailUrl");
            fieldNames.add("Source");
            fieldNames.add("OrderId");
            fieldNames.add("ShowtotalVv");
            fieldNames.add("UpdateTime");
            fieldNames.add("CreateTime");
            fieldNames = Collections.unmodifiableList(fieldNames);
        }
        return fieldNames;
    }

    /**
     * Retrieves a field from the object by field (Java) name passed in as a String.
     *
     * @param name field name
     * @return value
     */
    public Object getByName(String name)
    {
        if (name.equals("Id"))
        {
            return new Integer(getId());
        }
        if (name.equals("FkMusicId"))
        {
            return new Integer(getFkMusicId());
        }
        if (name.equals("SourceSite"))
        {
            return new Integer(getSourceSite());
        }
        if (name.equals("Logo"))
        {
            return getLogo();
        }
        if (name.equals("Hd"))
        {
            return new Integer(getHd());
        }
        if (name.equals("Blocked"))
        {
            return new Integer(getBlocked());
        }
        if (name.equals("ViewUrl"))
        {
            return getViewUrl();
        }
        if (name.equals("DetailUrl"))
        {
            return getDetailUrl();
        }
        if (name.equals("Source"))
        {
            return new Integer(getSource());
        }
        if (name.equals("OrderId"))
        {
            return new Integer(getOrderId());
        }
        if (name.equals("ShowtotalVv"))
        {
            return new Integer(getShowtotalVv());
        }
        if (name.equals("UpdateTime"))
        {
            return getUpdateTime();
        }
        if (name.equals("CreateTime"))
        {
            return getCreateTime();
        }
        return null;
    }

    /**
     * Set a field in the object by field (Java) name.
     *
     * @param name field name
     * @param value field value
     * @return True if value was set, false if not (invalid name / protected field).
     * @throws IllegalArgumentException if object type of value does not match field object type.
     * @throws TorqueException If a problem occurs with the set[Field] method.
     */
    public boolean setByName(String name, Object value )
        throws TorqueException, IllegalArgumentException
    {
        if (name.equals("Id"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setId(((Integer) value).intValue());
            return true;
        }
        if (name.equals("FkMusicId"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setFkMusicId(((Integer) value).intValue());
            return true;
        }
        if (name.equals("SourceSite"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setSourceSite(((Integer) value).intValue());
            return true;
        }
        if (name.equals("Logo"))
        {
            // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setLogo((String) value);
            return true;
        }
        if (name.equals("Hd"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setHd(((Integer) value).intValue());
            return true;
        }
        if (name.equals("Blocked"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setBlocked(((Integer) value).intValue());
            return true;
        }
        if (name.equals("ViewUrl"))
        {
            // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setViewUrl((String) value);
            return true;
        }
        if (name.equals("DetailUrl"))
        {
            // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setDetailUrl((String) value);
            return true;
        }
        if (name.equals("Source"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setSource(((Integer) value).intValue());
            return true;
        }
        if (name.equals("OrderId"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setOrderId(((Integer) value).intValue());
            return true;
        }
        if (name.equals("ShowtotalVv"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setShowtotalVv(((Integer) value).intValue());
            return true;
        }
        if (name.equals("UpdateTime"))
        {
            // Object fields can be null
            if (value != null && ! Date.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setUpdateTime((Date) value);
            return true;
        }
        if (name.equals("CreateTime"))
        {
            // Object fields can be null
            if (value != null && ! Date.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setCreateTime((Date) value);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.  The String must be one of the static
     * Strings defined in this Class' Peer.
     *
     * @param name peer name
     * @return value
     */
    public Object getByPeerName(String name)
    {
        if (name.equals(MusicSitePeer.ID))
        {
            return new Integer(getId());
        }
        if (name.equals(MusicSitePeer.FK_MUSIC_ID))
        {
            return new Integer(getFkMusicId());
        }
        if (name.equals(MusicSitePeer.SOURCE_SITE))
        {
            return new Integer(getSourceSite());
        }
        if (name.equals(MusicSitePeer.LOGO))
        {
            return getLogo();
        }
        if (name.equals(MusicSitePeer.HD))
        {
            return new Integer(getHd());
        }
        if (name.equals(MusicSitePeer.BLOCKED))
        {
            return new Integer(getBlocked());
        }
        if (name.equals(MusicSitePeer.VIEW_URL))
        {
            return getViewUrl();
        }
        if (name.equals(MusicSitePeer.DETAIL_URL))
        {
            return getDetailUrl();
        }
        if (name.equals(MusicSitePeer.SOURCE))
        {
            return new Integer(getSource());
        }
        if (name.equals(MusicSitePeer.ORDER_ID))
        {
            return new Integer(getOrderId());
        }
        if (name.equals(MusicSitePeer.SHOWTOTAL_VV))
        {
            return new Integer(getShowtotalVv());
        }
        if (name.equals(MusicSitePeer.UPDATE_TIME))
        {
            return getUpdateTime();
        }
        if (name.equals(MusicSitePeer.CREATE_TIME))
        {
            return getCreateTime();
        }
        return null;
    }

    /**
     * Set field values by Peer Field Name
     *
     * @param name field name
     * @param value field value
     * @return True if value was set, false if not (invalid name / protected field).
     * @throws IllegalArgumentException if object type of value does not match field object type.
     * @throws TorqueException If a problem occurs with the set[Field] method.
     */
    public boolean setByPeerName(String name, Object value)
        throws TorqueException, IllegalArgumentException
    {
      if (MusicSitePeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
      if (MusicSitePeer.FK_MUSIC_ID.equals(name))
        {
            return setByName("FkMusicId", value);
        }
      if (MusicSitePeer.SOURCE_SITE.equals(name))
        {
            return setByName("SourceSite", value);
        }
      if (MusicSitePeer.LOGO.equals(name))
        {
            return setByName("Logo", value);
        }
      if (MusicSitePeer.HD.equals(name))
        {
            return setByName("Hd", value);
        }
      if (MusicSitePeer.BLOCKED.equals(name))
        {
            return setByName("Blocked", value);
        }
      if (MusicSitePeer.VIEW_URL.equals(name))
        {
            return setByName("ViewUrl", value);
        }
      if (MusicSitePeer.DETAIL_URL.equals(name))
        {
            return setByName("DetailUrl", value);
        }
      if (MusicSitePeer.SOURCE.equals(name))
        {
            return setByName("Source", value);
        }
      if (MusicSitePeer.ORDER_ID.equals(name))
        {
            return setByName("OrderId", value);
        }
      if (MusicSitePeer.SHOWTOTAL_VV.equals(name))
        {
            return setByName("ShowtotalVv", value);
        }
      if (MusicSitePeer.UPDATE_TIME.equals(name))
        {
            return setByName("UpdateTime", value);
        }
      if (MusicSitePeer.CREATE_TIME.equals(name))
        {
            return setByName("CreateTime", value);
        }
        return false;
    }

    /**
     * Retrieves a field from the object by Position as specified
     * in the xml schema.  Zero-based.
     *
     * @param pos position in xml schema
     * @return value
     */
    public Object getByPosition(int pos)
    {
        if (pos == 0)
        {
            return new Integer(getId());
        }
        if (pos == 1)
        {
            return new Integer(getFkMusicId());
        }
        if (pos == 2)
        {
            return new Integer(getSourceSite());
        }
        if (pos == 3)
        {
            return getLogo();
        }
        if (pos == 4)
        {
            return new Integer(getHd());
        }
        if (pos == 5)
        {
            return new Integer(getBlocked());
        }
        if (pos == 6)
        {
            return getViewUrl();
        }
        if (pos == 7)
        {
            return getDetailUrl();
        }
        if (pos == 8)
        {
            return new Integer(getSource());
        }
        if (pos == 9)
        {
            return new Integer(getOrderId());
        }
        if (pos == 10)
        {
            return new Integer(getShowtotalVv());
        }
        if (pos == 11)
        {
            return getUpdateTime();
        }
        if (pos == 12)
        {
            return getCreateTime();
        }
        return null;
    }

    /**
     * Set field values by its position (zero based) in the XML schema.
     *
     * @param position The field position
     * @param value field value
     * @return True if value was set, false if not (invalid position / protected field).
     * @throws IllegalArgumentException if object type of value does not match field object type.
     * @throws TorqueException If a problem occurs with the set[Field] method.
     */
    public boolean setByPosition(int position, Object value)
        throws TorqueException, IllegalArgumentException
    {
    if (position == 0)
        {
            return setByName("Id", value);
        }
    if (position == 1)
        {
            return setByName("FkMusicId", value);
        }
    if (position == 2)
        {
            return setByName("SourceSite", value);
        }
    if (position == 3)
        {
            return setByName("Logo", value);
        }
    if (position == 4)
        {
            return setByName("Hd", value);
        }
    if (position == 5)
        {
            return setByName("Blocked", value);
        }
    if (position == 6)
        {
            return setByName("ViewUrl", value);
        }
    if (position == 7)
        {
            return setByName("DetailUrl", value);
        }
    if (position == 8)
        {
            return setByName("Source", value);
        }
    if (position == 9)
        {
            return setByName("OrderId", value);
        }
    if (position == 10)
        {
            return setByName("ShowtotalVv", value);
        }
    if (position == 11)
        {
            return setByName("UpdateTime", value);
        }
    if (position == 12)
        {
            return setByName("CreateTime", value);
        }
        return false;
    }
     
    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     *
     * @throws Exception
     */
    public void save() throws Exception
    {
        save(MusicSitePeer.DATABASE_NAME);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     * Note: this code is here because the method body is
     * auto-generated conditionally and therefore needs to be
     * in this file instead of in the super class, BaseObject.
     *
     * @param dbName
     * @throws TorqueException
     */
    public void save(String dbName) throws TorqueException
    {
        Connection con = null;
        try
        {
            con = Transaction.begin(dbName);
            save(con);
            Transaction.commit(con);
        }
        catch(TorqueException e)
        {
            Transaction.safeRollback(con);
            throw e;
        }
    }

    /** flag to prevent endless save loop, if this object is referenced
        by another object which falls in this transaction. */
    private boolean alreadyInSave = false;
    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     *
     * @param con
     * @throws TorqueException
     */
    public void save(Connection con) throws TorqueException
    {
        if (!alreadyInSave)
        {
            alreadyInSave = true;



            // If this object has been modified, then save it to the database.
            if (isModified())
            {
                if (isNew())
                {
                    MusicSitePeer.doInsert((MusicSite) this, con);
                    setNew(false);
                }
                else
                {
                    MusicSitePeer.doUpdate((MusicSite) this, con);
                }
            }

            alreadyInSave = false;
        }
    }


    /**
     * Set the PrimaryKey using ObjectKey.
     *
     * @param key id ObjectKey
     */
    public void setPrimaryKey(ObjectKey key)
        
    {
        setId(((NumberKey) key).intValue());
    }

    /**
     * Set the PrimaryKey using a String.
     *
     * @param key
     */
    public void setPrimaryKey(String key) 
    {
        setId(Integer.parseInt(key));
    }


    /**
     * returns an id that differentiates this object from others
     * of its class.
     */
    public ObjectKey getPrimaryKey()
    {
        return SimpleKey.keyFor(getId());
    }
 

    /**
     * Makes a copy of this object.
     * It creates a new object filling in the simple attributes.
     * It then fills all the association collections and sets the
     * related objects to isNew=true.
     */
    public MusicSite copy() throws TorqueException
    {
        return copy(true);
    }

    /**
     * Makes a copy of this object using connection.
     * It creates a new object filling in the simple attributes.
     * It then fills all the association collections and sets the
     * related objects to isNew=true.
     *
     * @param con the database connection to read associated objects.
     */
    public MusicSite copy(Connection con) throws TorqueException
    {
        return copy(true, con);
    }

    /**
     * Makes a copy of this object.
     * It creates a new object filling in the simple attributes.
     * If the parameter deepcopy is true, it then fills all the
     * association collections and sets the related objects to
     * isNew=true.
     *
     * @param deepcopy whether to copy the associated objects.
     */
    public MusicSite copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new MusicSite(), deepcopy);
    }

    /**
     * Makes a copy of this object using connection.
     * It creates a new object filling in the simple attributes.
     * If the parameter deepcopy is true, it then fills all the
     * association collections and sets the related objects to
     * isNew=true.
     *
     * @param deepcopy whether to copy the associated objects.
     * @param con the database connection to read associated objects.
     */
    public MusicSite copy(boolean deepcopy, Connection con) throws TorqueException
    {
        return copyInto(new MusicSite(), deepcopy, con);
    }
  
    /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     *
     * @param copyObj the object to fill.
     */
    protected MusicSite copyInto(MusicSite copyObj) throws TorqueException
    {
        return copyInto(copyObj, true);
    }

  
    /**
     * Fills the copyObj with the contents of this object using connection.
     * The associated objects are also copied and treated as new objects.
     *
     * @param copyObj the object to fill.
     * @param con the database connection to read associated objects.
     */
    protected MusicSite copyInto(MusicSite copyObj, Connection con) throws TorqueException
    {
        return copyInto(copyObj, true, con);
    }
  
    /**
     * Fills the copyObj with the contents of this object.
     * If deepcopy is true, The associated objects are also copied
     * and treated as new objects.
     *
     * @param copyObj the object to fill.
     * @param deepcopy whether the associated objects should be copied.
     */
    protected MusicSite copyInto(MusicSite copyObj, boolean deepcopy) throws TorqueException
    {
        copyObj.setId(id);
        copyObj.setFkMusicId(fkMusicId);
        copyObj.setSourceSite(sourceSite);
        copyObj.setLogo(logo);
        copyObj.setHd(hd);
        copyObj.setBlocked(blocked);
        copyObj.setViewUrl(viewUrl);
        copyObj.setDetailUrl(detailUrl);
        copyObj.setSource(source);
        copyObj.setOrderId(orderId);
        copyObj.setShowtotalVv(showtotalVv);
        copyObj.setUpdateTime(updateTime);
        copyObj.setCreateTime(createTime);

        copyObj.setId( 0);

        if (deepcopy)
        {
        }
        return copyObj;
    }
        
    
    /**
     * Fills the copyObj with the contents of this object using connection.
     * If deepcopy is true, The associated objects are also copied
     * and treated as new objects.
     *
     * @param copyObj the object to fill.
     * @param deepcopy whether the associated objects should be copied.
     * @param con the database connection to read associated objects.
     */
    protected MusicSite copyInto(MusicSite copyObj, boolean deepcopy, Connection con) throws TorqueException
    {
        copyObj.setId(id);
        copyObj.setFkMusicId(fkMusicId);
        copyObj.setSourceSite(sourceSite);
        copyObj.setLogo(logo);
        copyObj.setHd(hd);
        copyObj.setBlocked(blocked);
        copyObj.setViewUrl(viewUrl);
        copyObj.setDetailUrl(detailUrl);
        copyObj.setSource(source);
        copyObj.setOrderId(orderId);
        copyObj.setShowtotalVv(showtotalVv);
        copyObj.setUpdateTime(updateTime);
        copyObj.setCreateTime(createTime);

        copyObj.setId( 0);

        if (deepcopy)
        {
        }
        return copyObj;
    }
    
    

    /**
     * returns a peer instance associated with this om.  Since Peer classes
     * are not to have any instance attributes, this method returns the
     * same instance for all member of this class. The method could therefore
     * be static, but this would prevent one from overriding the behavior.
     */
    public MusicSitePeer getPeer()
    {
        return peer;
    }

    /**
     * Retrieves the TableMap object related to this Table data without
     * compiler warnings of using getPeer().getTableMap().
     *
     * @return The associated TableMap object.
     */
    public TableMap getTableMap() throws TorqueException
    {
        return MusicSitePeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("MusicSite:\n");
        str.append("Id = ")
           .append(getId())
           .append("\n");
        str.append("FkMusicId = ")
           .append(getFkMusicId())
           .append("\n");
        str.append("SourceSite = ")
           .append(getSourceSite())
           .append("\n");
        str.append("Logo = ")
           .append(getLogo())
           .append("\n");
        str.append("Hd = ")
           .append(getHd())
           .append("\n");
        str.append("Blocked = ")
           .append(getBlocked())
           .append("\n");
        str.append("ViewUrl = ")
           .append(getViewUrl())
           .append("\n");
        str.append("DetailUrl = ")
           .append(getDetailUrl())
           .append("\n");
        str.append("Source = ")
           .append(getSource())
           .append("\n");
        str.append("OrderId = ")
           .append(getOrderId())
           .append("\n");
        str.append("ShowtotalVv = ")
           .append(getShowtotalVv())
           .append("\n");
        str.append("UpdateTime = ")
           .append(getUpdateTime())
           .append("\n");
        str.append("CreateTime = ")
           .append(getCreateTime())
           .append("\n");
        return(str.toString());
    }
}
