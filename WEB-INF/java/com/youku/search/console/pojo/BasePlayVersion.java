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
 * ??????��?��?
 *
 * This class was autogenerated by Torque on:
 *
 * [Wed Mar 24 10:32:59 CST 2010]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to PlayVersion
 */
public abstract class BasePlayVersion extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1269397979171L;

    /** The Peer class */
    private static final PlayVersionPeer peer =
        new PlayVersionPeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the fkTeleplayId field */
    private int fkTeleplayId;
      
    /** The value for the cate field */
    private int cate;
      
    /** The value for the subcate field */
    private int subcate;
      
    /** The value for the name field */
    private String name;
      
    /** The value for the viewName field */
    private String viewName;
      
    /** The value for the alias field */
    private String alias;
      
    /** The value for the orderId field */
    private int orderId;
      
    /** The value for the reverse field */
    private int reverse;
      
    /** The value for the episodeCount field */
    private int episodeCount;
      
    /** The value for the totalCount field */
    private int totalCount;
                                          
    /** The value for the fixed field */
    private int fixed = 0;
      
    /** The value for the firstlogo field */
    private String firstlogo;
  
            
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
     * Get the FkTeleplayId
     *
     * @return int
     */
    public int getFkTeleplayId()
    {
        return fkTeleplayId;
    }

                        
    /**
     * Set the value of FkTeleplayId
     *
     * @param v new value
     */
    public void setFkTeleplayId(int v) 
    {
    
                  if (this.fkTeleplayId != v)
              {
            this.fkTeleplayId = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Cate
     *
     * @return int
     */
    public int getCate()
    {
        return cate;
    }

                        
    /**
     * Set the value of Cate
     *
     * @param v new value
     */
    public void setCate(int v) 
    {
    
                  if (this.cate != v)
              {
            this.cate = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Subcate
     *
     * @return int
     */
    public int getSubcate()
    {
        return subcate;
    }

                        
    /**
     * Set the value of Subcate
     *
     * @param v new value
     */
    public void setSubcate(int v) 
    {
    
                  if (this.subcate != v)
              {
            this.subcate = v;
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
     * Get the ViewName
     *
     * @return String
     */
    public String getViewName()
    {
        return viewName;
    }

                        
    /**
     * Set the value of ViewName
     *
     * @param v new value
     */
    public void setViewName(String v) 
    {
    
                  if (!ObjectUtils.equals(this.viewName, v))
              {
            this.viewName = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Alias
     *
     * @return String
     */
    public String getAlias()
    {
        return alias;
    }

                        
    /**
     * Set the value of Alias
     *
     * @param v new value
     */
    public void setAlias(String v) 
    {
    
                  if (!ObjectUtils.equals(this.alias, v))
              {
            this.alias = v;
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
     * Get the Reverse
     *
     * @return int
     */
    public int getReverse()
    {
        return reverse;
    }

                        
    /**
     * Set the value of Reverse
     *
     * @param v new value
     */
    public void setReverse(int v) 
    {
    
                  if (this.reverse != v)
              {
            this.reverse = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the EpisodeCount
     *
     * @return int
     */
    public int getEpisodeCount()
    {
        return episodeCount;
    }

                        
    /**
     * Set the value of EpisodeCount
     *
     * @param v new value
     */
    public void setEpisodeCount(int v) 
    {
    
                  if (this.episodeCount != v)
              {
            this.episodeCount = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the TotalCount
     *
     * @return int
     */
    public int getTotalCount()
    {
        return totalCount;
    }

                        
    /**
     * Set the value of TotalCount
     *
     * @param v new value
     */
    public void setTotalCount(int v) 
    {
    
                  if (this.totalCount != v)
              {
            this.totalCount = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Fixed
     *
     * @return int
     */
    public int getFixed()
    {
        return fixed;
    }

                        
    /**
     * Set the value of Fixed
     *
     * @param v new value
     */
    public void setFixed(int v) 
    {
    
                  if (this.fixed != v)
              {
            this.fixed = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Firstlogo
     *
     * @return String
     */
    public String getFirstlogo()
    {
        return firstlogo;
    }

                        
    /**
     * Set the value of Firstlogo
     *
     * @param v new value
     */
    public void setFirstlogo(String v) 
    {
    
                  if (!ObjectUtils.equals(this.firstlogo, v))
              {
            this.firstlogo = v;
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
              fieldNames.add("FkTeleplayId");
              fieldNames.add("Cate");
              fieldNames.add("Subcate");
              fieldNames.add("Name");
              fieldNames.add("ViewName");
              fieldNames.add("Alias");
              fieldNames.add("OrderId");
              fieldNames.add("Reverse");
              fieldNames.add("EpisodeCount");
              fieldNames.add("TotalCount");
              fieldNames.add("Fixed");
              fieldNames.add("Firstlogo");
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
          if (name.equals("FkTeleplayId"))
        {
                return new Integer(getFkTeleplayId());
            }
          if (name.equals("Cate"))
        {
                return new Integer(getCate());
            }
          if (name.equals("Subcate"))
        {
                return new Integer(getSubcate());
            }
          if (name.equals("Name"))
        {
                return getName();
            }
          if (name.equals("ViewName"))
        {
                return getViewName();
            }
          if (name.equals("Alias"))
        {
                return getAlias();
            }
          if (name.equals("OrderId"))
        {
                return new Integer(getOrderId());
            }
          if (name.equals("Reverse"))
        {
                return new Integer(getReverse());
            }
          if (name.equals("EpisodeCount"))
        {
                return new Integer(getEpisodeCount());
            }
          if (name.equals("TotalCount"))
        {
                return new Integer(getTotalCount());
            }
          if (name.equals("Fixed"))
        {
                return new Integer(getFixed());
            }
          if (name.equals("Firstlogo"))
        {
                return getFirstlogo();
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
          if (name.equals("FkTeleplayId"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setFkTeleplayId(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Cate"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setCate(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Subcate"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setSubcate(((Integer) value).intValue());
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
          if (name.equals("ViewName"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setViewName((String) value);
                      return true;
        }
          if (name.equals("Alias"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setAlias((String) value);
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
          if (name.equals("Reverse"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setReverse(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("EpisodeCount"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setEpisodeCount(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("TotalCount"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setTotalCount(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Fixed"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setFixed(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Firstlogo"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setFirstlogo((String) value);
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
          if (name.equals(PlayVersionPeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(PlayVersionPeer.FK_TELEPLAY_ID))
        {
                return new Integer(getFkTeleplayId());
            }
          if (name.equals(PlayVersionPeer.CATE))
        {
                return new Integer(getCate());
            }
          if (name.equals(PlayVersionPeer.SUBCATE))
        {
                return new Integer(getSubcate());
            }
          if (name.equals(PlayVersionPeer.NAME))
        {
                return getName();
            }
          if (name.equals(PlayVersionPeer.VIEW_NAME))
        {
                return getViewName();
            }
          if (name.equals(PlayVersionPeer.ALIAS))
        {
                return getAlias();
            }
          if (name.equals(PlayVersionPeer.ORDER_ID))
        {
                return new Integer(getOrderId());
            }
          if (name.equals(PlayVersionPeer.REVERSE))
        {
                return new Integer(getReverse());
            }
          if (name.equals(PlayVersionPeer.EPISODE_COUNT))
        {
                return new Integer(getEpisodeCount());
            }
          if (name.equals(PlayVersionPeer.TOTAL_COUNT))
        {
                return new Integer(getTotalCount());
            }
          if (name.equals(PlayVersionPeer.FIXED))
        {
                return new Integer(getFixed());
            }
          if (name.equals(PlayVersionPeer.FIRSTLOGO))
        {
                return getFirstlogo();
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
        if (PlayVersionPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (PlayVersionPeer.FK_TELEPLAY_ID.equals(name))
        {
            return setByName("FkTeleplayId", value);
        }
        if (PlayVersionPeer.CATE.equals(name))
        {
            return setByName("Cate", value);
        }
        if (PlayVersionPeer.SUBCATE.equals(name))
        {
            return setByName("Subcate", value);
        }
        if (PlayVersionPeer.NAME.equals(name))
        {
            return setByName("Name", value);
        }
        if (PlayVersionPeer.VIEW_NAME.equals(name))
        {
            return setByName("ViewName", value);
        }
        if (PlayVersionPeer.ALIAS.equals(name))
        {
            return setByName("Alias", value);
        }
        if (PlayVersionPeer.ORDER_ID.equals(name))
        {
            return setByName("OrderId", value);
        }
        if (PlayVersionPeer.REVERSE.equals(name))
        {
            return setByName("Reverse", value);
        }
        if (PlayVersionPeer.EPISODE_COUNT.equals(name))
        {
            return setByName("EpisodeCount", value);
        }
        if (PlayVersionPeer.TOTAL_COUNT.equals(name))
        {
            return setByName("TotalCount", value);
        }
        if (PlayVersionPeer.FIXED.equals(name))
        {
            return setByName("Fixed", value);
        }
        if (PlayVersionPeer.FIRSTLOGO.equals(name))
        {
            return setByName("Firstlogo", value);
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
                return new Integer(getFkTeleplayId());
            }
              if (pos == 2)
        {
                return new Integer(getCate());
            }
              if (pos == 3)
        {
                return new Integer(getSubcate());
            }
              if (pos == 4)
        {
                return getName();
            }
              if (pos == 5)
        {
                return getViewName();
            }
              if (pos == 6)
        {
                return getAlias();
            }
              if (pos == 7)
        {
                return new Integer(getOrderId());
            }
              if (pos == 8)
        {
                return new Integer(getReverse());
            }
              if (pos == 9)
        {
                return new Integer(getEpisodeCount());
            }
              if (pos == 10)
        {
                return new Integer(getTotalCount());
            }
              if (pos == 11)
        {
                return new Integer(getFixed());
            }
              if (pos == 12)
        {
                return getFirstlogo();
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
            return setByName("FkTeleplayId", value);
        }
          if (position == 2)
        {
            return setByName("Cate", value);
        }
          if (position == 3)
        {
            return setByName("Subcate", value);
        }
          if (position == 4)
        {
            return setByName("Name", value);
        }
          if (position == 5)
        {
            return setByName("ViewName", value);
        }
          if (position == 6)
        {
            return setByName("Alias", value);
        }
          if (position == 7)
        {
            return setByName("OrderId", value);
        }
          if (position == 8)
        {
            return setByName("Reverse", value);
        }
          if (position == 9)
        {
            return setByName("EpisodeCount", value);
        }
          if (position == 10)
        {
            return setByName("TotalCount", value);
        }
          if (position == 11)
        {
            return setByName("Fixed", value);
        }
          if (position == 12)
        {
            return setByName("Firstlogo", value);
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
          save(PlayVersionPeer.DATABASE_NAME);
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
                    PlayVersionPeer.doInsert((PlayVersion) this, con);
                    setNew(false);
                }
                else
                {
                    PlayVersionPeer.doUpdate((PlayVersion) this, con);
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
      public PlayVersion copy() throws TorqueException
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
    public PlayVersion copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new PlayVersion(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected PlayVersion copyInto(PlayVersion copyObj) throws TorqueException
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
      protected PlayVersion copyInto(PlayVersion copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setFkTeleplayId(fkTeleplayId);
          copyObj.setCate(cate);
          copyObj.setSubcate(subcate);
          copyObj.setName(name);
          copyObj.setViewName(viewName);
          copyObj.setAlias(alias);
          copyObj.setOrderId(orderId);
          copyObj.setReverse(reverse);
          copyObj.setEpisodeCount(episodeCount);
          copyObj.setTotalCount(totalCount);
          copyObj.setFixed(fixed);
          copyObj.setFirstlogo(firstlogo);
  
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
    public PlayVersionPeer getPeer()
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
        return PlayVersionPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("PlayVersion:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("FkTeleplayId = ")
               .append(getFkTeleplayId())
             .append("\n");
        str.append("Cate = ")
               .append(getCate())
             .append("\n");
        str.append("Subcate = ")
               .append(getSubcate())
             .append("\n");
        str.append("Name = ")
               .append(getName())
             .append("\n");
        str.append("ViewName = ")
               .append(getViewName())
             .append("\n");
        str.append("Alias = ")
               .append(getAlias())
             .append("\n");
        str.append("OrderId = ")
               .append(getOrderId())
             .append("\n");
        str.append("Reverse = ")
               .append(getReverse())
             .append("\n");
        str.append("EpisodeCount = ")
               .append(getEpisodeCount())
             .append("\n");
        str.append("TotalCount = ")
               .append(getTotalCount())
             .append("\n");
        str.append("Fixed = ")
               .append(getFixed())
             .append("\n");
        str.append("Firstlogo = ")
               .append(getFirstlogo())
             .append("\n");
        return(str.toString());
    }
}
