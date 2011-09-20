package com.youku.soku.manage.torque;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.map.TableMap;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.util.Transaction;





/**
 * 屏蔽系统邮件设置
 *
 * This class was autogenerated by Torque on:
 *
 * [Tue Sep 21 16:44:53 CST 2010]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to ShieldMailSetting
 */
public abstract class BaseShieldMailSetting extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1285058693550L;

    /** The Peer class */
    private static final ShieldMailSettingPeer peer =
        new ShieldMailSettingPeer();


    /** The value for the id field */
    private int id;

    /** The value for the email field */
    private String email;

    /** The value for the periods field */
    private int periods;

    /** The value for the periodsPast field */
    private int periodsPast;

    /** The value for the status field */
    private int status;

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
     * Get the Email
     *
     * @return String
     */
    public String getEmail()
    {
        return email;
    }


    /**
     * Set the value of Email
     *
     * @param v new value
     */
    public void setEmail(String v) 
    {

        if (!ObjectUtils.equals(this.email, v))
        {
            this.email = v;
            setModified(true);
        }


    }

    /**
     * Get the Periods
     *
     * @return int
     */
    public int getPeriods()
    {
        return periods;
    }


    /**
     * Set the value of Periods
     *
     * @param v new value
     */
    public void setPeriods(int v) 
    {

        if (this.periods != v)
        {
            this.periods = v;
            setModified(true);
        }


    }

    /**
     * Get the PeriodsPast
     *
     * @return int
     */
    public int getPeriodsPast()
    {
        return periodsPast;
    }


    /**
     * Set the value of PeriodsPast
     *
     * @param v new value
     */
    public void setPeriodsPast(int v) 
    {

        if (this.periodsPast != v)
        {
            this.periodsPast = v;
            setModified(true);
        }


    }

    /**
     * Get the Status
     *
     * @return int
     */
    public int getStatus()
    {
        return status;
    }


    /**
     * Set the value of Status
     *
     * @param v new value
     */
    public void setStatus(int v) 
    {

        if (this.status != v)
        {
            this.status = v;
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
            fieldNames.add("Email");
            fieldNames.add("Periods");
            fieldNames.add("PeriodsPast");
            fieldNames.add("Status");
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
        if (name.equals("Email"))
        {
            return getEmail();
        }
        if (name.equals("Periods"))
        {
            return new Integer(getPeriods());
        }
        if (name.equals("PeriodsPast"))
        {
            return new Integer(getPeriodsPast());
        }
        if (name.equals("Status"))
        {
            return new Integer(getStatus());
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
     * @throws TorqueException If a problem occures with the set[Field] method.
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
        if (name.equals("Email"))
        {
            // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setEmail((String) value);
            return true;
        }
        if (name.equals("Periods"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setPeriods(((Integer) value).intValue());
            return true;
        }
        if (name.equals("PeriodsPast"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setPeriodsPast(((Integer) value).intValue());
            return true;
        }
        if (name.equals("Status"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setStatus(((Integer) value).intValue());
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
        if (name.equals(ShieldMailSettingPeer.ID))
        {
            return new Integer(getId());
        }
        if (name.equals(ShieldMailSettingPeer.EMAIL))
        {
            return getEmail();
        }
        if (name.equals(ShieldMailSettingPeer.PERIODS))
        {
            return new Integer(getPeriods());
        }
        if (name.equals(ShieldMailSettingPeer.PERIODS_PAST))
        {
            return new Integer(getPeriodsPast());
        }
        if (name.equals(ShieldMailSettingPeer.STATUS))
        {
            return new Integer(getStatus());
        }
        if (name.equals(ShieldMailSettingPeer.UPDATE_TIME))
        {
            return getUpdateTime();
        }
        if (name.equals(ShieldMailSettingPeer.CREATE_TIME))
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
     * @throws TorqueException If a problem occures with the set[Field] method.
     */
    public boolean setByPeerName(String name, Object value)
        throws TorqueException, IllegalArgumentException
    {
      if (ShieldMailSettingPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
      if (ShieldMailSettingPeer.EMAIL.equals(name))
        {
            return setByName("Email", value);
        }
      if (ShieldMailSettingPeer.PERIODS.equals(name))
        {
            return setByName("Periods", value);
        }
      if (ShieldMailSettingPeer.PERIODS_PAST.equals(name))
        {
            return setByName("PeriodsPast", value);
        }
      if (ShieldMailSettingPeer.STATUS.equals(name))
        {
            return setByName("Status", value);
        }
      if (ShieldMailSettingPeer.UPDATE_TIME.equals(name))
        {
            return setByName("UpdateTime", value);
        }
      if (ShieldMailSettingPeer.CREATE_TIME.equals(name))
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
            return getEmail();
        }
        if (pos == 2)
        {
            return new Integer(getPeriods());
        }
        if (pos == 3)
        {
            return new Integer(getPeriodsPast());
        }
        if (pos == 4)
        {
            return new Integer(getStatus());
        }
        if (pos == 5)
        {
            return getUpdateTime();
        }
        if (pos == 6)
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
     * @throws TorqueException If a problem occures with the set[Field] method.
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
            return setByName("Email", value);
        }
    if (position == 2)
        {
            return setByName("Periods", value);
        }
    if (position == 3)
        {
            return setByName("PeriodsPast", value);
        }
    if (position == 4)
        {
            return setByName("Status", value);
        }
    if (position == 5)
        {
            return setByName("UpdateTime", value);
        }
    if (position == 6)
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
        save(ShieldMailSettingPeer.DATABASE_NAME);
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
                    ShieldMailSettingPeer.doInsert((ShieldMailSetting) this, con);
                    setNew(false);
                }
                else
                {
                    ShieldMailSettingPeer.doUpdate((ShieldMailSetting) this, con);
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
    public ShieldMailSetting copy() throws TorqueException
    {
        return copy(true);
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
    public ShieldMailSetting copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new ShieldMailSetting(), deepcopy);
    }

    /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected ShieldMailSetting copyInto(ShieldMailSetting copyObj) throws TorqueException
    {
        return copyInto(copyObj, true);
    }

    /**
     * Fills the copyObj with the contents of this object.
     * If deepcopy is true, The associated objects are also copied
     * and treated as new objects.
     * @param copyObj the object to fill.
     * @param deepcopy whether the associated objects should be copied.
     */
    protected ShieldMailSetting copyInto(ShieldMailSetting copyObj, boolean deepcopy) throws TorqueException
    {
        copyObj.setId(id);
        copyObj.setEmail(email);
        copyObj.setPeriods(periods);
        copyObj.setPeriodsPast(periodsPast);
        copyObj.setStatus(status);
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
    public ShieldMailSettingPeer getPeer()
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
        return ShieldMailSettingPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("ShieldMailSetting:\n");
        str.append("Id = ")
           .append(getId())
           .append("\n");
        str.append("Email = ")
           .append(getEmail())
           .append("\n");
        str.append("Periods = ")
           .append(getPeriods())
           .append("\n");
        str.append("PeriodsPast = ")
           .append(getPeriodsPast())
           .append("\n");
        str.append("Status = ")
           .append(getStatus())
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
