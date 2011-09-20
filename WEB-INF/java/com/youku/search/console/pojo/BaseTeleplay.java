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
 * ???????�¡���
 *
 * This class was autogenerated by Torque on:
 *
 * [Thu Feb 19 10:34:56 CST 2009]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to Teleplay
 */
public abstract class BaseTeleplay extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1235010896562L;

    /** The Peer class */
    private static final TeleplayPeer peer =
        new TeleplayPeer();

        
    /** The value for the id field */
    private int id;
                                          
    /** The value for the versionCount field */
    private int versionCount = 1;
                                          
    /** The value for the isValid field */
    private int isValid = 1;
      
    /** The value for the cate field */
    private int cate;
      
    /** The value for the subcate field */
    private int subcate;
  
            
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
     * Get the VersionCount
     *
     * @return int
     */
    public int getVersionCount()
    {
        return versionCount;
    }

                        
    /**
     * Set the value of VersionCount
     *
     * @param v new value
     */
    public void setVersionCount(int v) 
    {
    
                  if (this.versionCount != v)
              {
            this.versionCount = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the IsValid
     *
     * @return int
     */
    public int getIsValid()
    {
        return isValid;
    }

                        
    /**
     * Set the value of IsValid
     *
     * @param v new value
     */
    public void setIsValid(int v) 
    {
    
                  if (this.isValid != v)
              {
            this.isValid = v;
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
              fieldNames.add("VersionCount");
              fieldNames.add("IsValid");
              fieldNames.add("Cate");
              fieldNames.add("Subcate");
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
          if (name.equals("VersionCount"))
        {
                return new Integer(getVersionCount());
            }
          if (name.equals("IsValid"))
        {
                return new Integer(getIsValid());
            }
          if (name.equals("Cate"))
        {
                return new Integer(getCate());
            }
          if (name.equals("Subcate"))
        {
                return new Integer(getSubcate());
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
          if (name.equals("VersionCount"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setVersionCount(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("IsValid"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setIsValid(((Integer) value).intValue());
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
          if (name.equals(TeleplayPeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(TeleplayPeer.VERSION_COUNT))
        {
                return new Integer(getVersionCount());
            }
          if (name.equals(TeleplayPeer.IS_VALID))
        {
                return new Integer(getIsValid());
            }
          if (name.equals(TeleplayPeer.CATE))
        {
                return new Integer(getCate());
            }
          if (name.equals(TeleplayPeer.SUBCATE))
        {
                return new Integer(getSubcate());
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
        if (TeleplayPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (TeleplayPeer.VERSION_COUNT.equals(name))
        {
            return setByName("VersionCount", value);
        }
        if (TeleplayPeer.IS_VALID.equals(name))
        {
            return setByName("IsValid", value);
        }
        if (TeleplayPeer.CATE.equals(name))
        {
            return setByName("Cate", value);
        }
        if (TeleplayPeer.SUBCATE.equals(name))
        {
            return setByName("Subcate", value);
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
                return new Integer(getVersionCount());
            }
              if (pos == 2)
        {
                return new Integer(getIsValid());
            }
              if (pos == 3)
        {
                return new Integer(getCate());
            }
              if (pos == 4)
        {
                return new Integer(getSubcate());
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
            return setByName("VersionCount", value);
        }
          if (position == 2)
        {
            return setByName("IsValid", value);
        }
          if (position == 3)
        {
            return setByName("Cate", value);
        }
          if (position == 4)
        {
            return setByName("Subcate", value);
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
          save(TeleplayPeer.DATABASE_NAME);
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
                    TeleplayPeer.doInsert((Teleplay) this, con);
                    setNew(false);
                }
                else
                {
                    TeleplayPeer.doUpdate((Teleplay) this, con);
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
      public Teleplay copy() throws TorqueException
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
    public Teleplay copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new Teleplay(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected Teleplay copyInto(Teleplay copyObj) throws TorqueException
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
      protected Teleplay copyInto(Teleplay copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setVersionCount(versionCount);
          copyObj.setIsValid(isValid);
          copyObj.setCate(cate);
          copyObj.setSubcate(subcate);
  
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
    public TeleplayPeer getPeer()
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
        return TeleplayPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("Teleplay:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("VersionCount = ")
               .append(getVersionCount())
             .append("\n");
        str.append("IsValid = ")
               .append(getIsValid())
             .append("\n");
        str.append("Cate = ")
               .append(getCate())
             .append("\n");
        str.append("Subcate = ")
               .append(getSubcate())
             .append("\n");
        return(str.toString());
    }
}
