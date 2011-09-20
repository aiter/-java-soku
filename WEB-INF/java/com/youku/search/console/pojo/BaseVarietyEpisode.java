package com.youku.search.console.pojo;


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
 * ��???????����
 *
 * This class was autogenerated by Torque on:
 *
 * [Tue Feb 10 15:25:22 CST 2009]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to VarietyEpisode
 */
public abstract class BaseVarietyEpisode extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1234250722796L;

    /** The Peer class */
    private static final VarietyEpisodePeer peer =
        new VarietyEpisodePeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the fkVarietyId field */
    private int fkVarietyId;
      
    /** The value for the name field */
    private String name;
      
    /** The value for the orderId field */
    private String orderId;
      
    /** The value for the sourceName field */
    private String sourceName;
      
    /** The value for the videoId field */
    private String videoId;
      
    /** The value for the encodeVideoId field */
    private String encodeVideoId;
      
    /** The value for the logo field */
    private String logo;
      
    /** The value for the seconds field */
    private double seconds;
  
            
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
     * Get the FkVarietyId
     *
     * @return int
     */
    public int getFkVarietyId()
    {
        return fkVarietyId;
    }

                        
    /**
     * Set the value of FkVarietyId
     *
     * @param v new value
     */
    public void setFkVarietyId(int v) 
    {
    
                  if (this.fkVarietyId != v)
              {
            this.fkVarietyId = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Name
     *
     * @return String
     */
    public String getName()
    {
        return name;
    }

                        
    /**
     * Set the value of Name
     *
     * @param v new value
     */
    public void setName(String v) 
    {
    
                  if (!ObjectUtils.equals(this.name, v))
              {
            this.name = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the OrderId
     *
     * @return String
     */
    public String getOrderId()
    {
        return orderId;
    }

                        
    /**
     * Set the value of OrderId
     *
     * @param v new value
     */
    public void setOrderId(String v) 
    {
    
                  if (!ObjectUtils.equals(this.orderId, v))
              {
            this.orderId = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the SourceName
     *
     * @return String
     */
    public String getSourceName()
    {
        return sourceName;
    }

                        
    /**
     * Set the value of SourceName
     *
     * @param v new value
     */
    public void setSourceName(String v) 
    {
    
                  if (!ObjectUtils.equals(this.sourceName, v))
              {
            this.sourceName = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the VideoId
     *
     * @return String
     */
    public String getVideoId()
    {
        return videoId;
    }

                        
    /**
     * Set the value of VideoId
     *
     * @param v new value
     */
    public void setVideoId(String v) 
    {
    
                  if (!ObjectUtils.equals(this.videoId, v))
              {
            this.videoId = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the EncodeVideoId
     *
     * @return String
     */
    public String getEncodeVideoId()
    {
        return encodeVideoId;
    }

                        
    /**
     * Set the value of EncodeVideoId
     *
     * @param v new value
     */
    public void setEncodeVideoId(String v) 
    {
    
                  if (!ObjectUtils.equals(this.encodeVideoId, v))
              {
            this.encodeVideoId = v;
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
     * Get the Seconds
     *
     * @return double
     */
    public double getSeconds()
    {
        return seconds;
    }

                        
    /**
     * Set the value of Seconds
     *
     * @param v new value
     */
    public void setSeconds(double v) 
    {
    
                  if (this.seconds != v)
              {
            this.seconds = v;
            setModified(true);
        }
    
          
              }
  
         
                
    private static List<String> fieldNames = null;

    /**
     * Generate a list of field names.
     *
     * @return a list of field names
     */
    public static synchronized List<String> getFieldNames()
    {
        if (fieldNames == null)
        {
            fieldNames = new ArrayList<String>();
              fieldNames.add("Id");
              fieldNames.add("FkVarietyId");
              fieldNames.add("Name");
              fieldNames.add("OrderId");
              fieldNames.add("SourceName");
              fieldNames.add("VideoId");
              fieldNames.add("EncodeVideoId");
              fieldNames.add("Logo");
              fieldNames.add("Seconds");
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
          if (name.equals("FkVarietyId"))
        {
                return new Integer(getFkVarietyId());
            }
          if (name.equals("Name"))
        {
                return getName();
            }
          if (name.equals("OrderId"))
        {
                return getOrderId();
            }
          if (name.equals("SourceName"))
        {
                return getSourceName();
            }
          if (name.equals("VideoId"))
        {
                return getVideoId();
            }
          if (name.equals("EncodeVideoId"))
        {
                return getEncodeVideoId();
            }
          if (name.equals("Logo"))
        {
                return getLogo();
            }
          if (name.equals("Seconds"))
        {
                return new Double(getSeconds());
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
          if (name.equals("FkVarietyId"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setFkVarietyId(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Name"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setName((String) value);
                      return true;
        }
          if (name.equals("OrderId"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setOrderId((String) value);
                      return true;
        }
          if (name.equals("SourceName"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setSourceName((String) value);
                      return true;
        }
          if (name.equals("VideoId"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setVideoId((String) value);
                      return true;
        }
          if (name.equals("EncodeVideoId"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setEncodeVideoId((String) value);
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
          if (name.equals("Seconds"))
        {
                      if (value == null || ! (Double.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not a Double object.");
            }
            setSeconds(((Double) value).doubleValue());
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
          if (name.equals(VarietyEpisodePeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(VarietyEpisodePeer.FK_VARIETY_ID))
        {
                return new Integer(getFkVarietyId());
            }
          if (name.equals(VarietyEpisodePeer.NAME))
        {
                return getName();
            }
          if (name.equals(VarietyEpisodePeer.ORDER_ID))
        {
                return getOrderId();
            }
          if (name.equals(VarietyEpisodePeer.SOURCE_NAME))
        {
                return getSourceName();
            }
          if (name.equals(VarietyEpisodePeer.VIDEO_ID))
        {
                return getVideoId();
            }
          if (name.equals(VarietyEpisodePeer.ENCODE_VIDEO_ID))
        {
                return getEncodeVideoId();
            }
          if (name.equals(VarietyEpisodePeer.LOGO))
        {
                return getLogo();
            }
          if (name.equals(VarietyEpisodePeer.SECONDS))
        {
                return new Double(getSeconds());
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
        if (VarietyEpisodePeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (VarietyEpisodePeer.FK_VARIETY_ID.equals(name))
        {
            return setByName("FkVarietyId", value);
        }
        if (VarietyEpisodePeer.NAME.equals(name))
        {
            return setByName("Name", value);
        }
        if (VarietyEpisodePeer.ORDER_ID.equals(name))
        {
            return setByName("OrderId", value);
        }
        if (VarietyEpisodePeer.SOURCE_NAME.equals(name))
        {
            return setByName("SourceName", value);
        }
        if (VarietyEpisodePeer.VIDEO_ID.equals(name))
        {
            return setByName("VideoId", value);
        }
        if (VarietyEpisodePeer.ENCODE_VIDEO_ID.equals(name))
        {
            return setByName("EncodeVideoId", value);
        }
        if (VarietyEpisodePeer.LOGO.equals(name))
        {
            return setByName("Logo", value);
        }
        if (VarietyEpisodePeer.SECONDS.equals(name))
        {
            return setByName("Seconds", value);
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
                return new Integer(getFkVarietyId());
            }
              if (pos == 2)
        {
                return getName();
            }
              if (pos == 3)
        {
                return getOrderId();
            }
              if (pos == 4)
        {
                return getSourceName();
            }
              if (pos == 5)
        {
                return getVideoId();
            }
              if (pos == 6)
        {
                return getEncodeVideoId();
            }
              if (pos == 7)
        {
                return getLogo();
            }
              if (pos == 8)
        {
                return new Double(getSeconds());
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
            return setByName("FkVarietyId", value);
        }
          if (position == 2)
        {
            return setByName("Name", value);
        }
          if (position == 3)
        {
            return setByName("OrderId", value);
        }
          if (position == 4)
        {
            return setByName("SourceName", value);
        }
          if (position == 5)
        {
            return setByName("VideoId", value);
        }
          if (position == 6)
        {
            return setByName("EncodeVideoId", value);
        }
          if (position == 7)
        {
            return setByName("Logo", value);
        }
          if (position == 8)
        {
            return setByName("Seconds", value);
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
          save(VarietyEpisodePeer.DATABASE_NAME);
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
                    VarietyEpisodePeer.doInsert((VarietyEpisode) this, con);
                    setNew(false);
                }
                else
                {
                    VarietyEpisodePeer.doUpdate((VarietyEpisode) this, con);
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
      public VarietyEpisode copy() throws TorqueException
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
    public VarietyEpisode copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new VarietyEpisode(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected VarietyEpisode copyInto(VarietyEpisode copyObj) throws TorqueException
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
      protected VarietyEpisode copyInto(VarietyEpisode copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setFkVarietyId(fkVarietyId);
          copyObj.setName(name);
          copyObj.setOrderId(orderId);
          copyObj.setSourceName(sourceName);
          copyObj.setVideoId(videoId);
          copyObj.setEncodeVideoId(encodeVideoId);
          copyObj.setLogo(logo);
          copyObj.setSeconds(seconds);
  
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
    public VarietyEpisodePeer getPeer()
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
        return VarietyEpisodePeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("VarietyEpisode:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("FkVarietyId = ")
               .append(getFkVarietyId())
             .append("\n");
        str.append("Name = ")
               .append(getName())
             .append("\n");
        str.append("OrderId = ")
               .append(getOrderId())
             .append("\n");
        str.append("SourceName = ")
               .append(getSourceName())
             .append("\n");
        str.append("VideoId = ")
               .append(getVideoId())
             .append("\n");
        str.append("EncodeVideoId = ")
               .append(getEncodeVideoId())
             .append("\n");
        str.append("Logo = ")
               .append(getLogo())
             .append("\n");
        str.append("Seconds = ")
               .append(getSeconds())
             .append("\n");
        return(str.toString());
    }
}
