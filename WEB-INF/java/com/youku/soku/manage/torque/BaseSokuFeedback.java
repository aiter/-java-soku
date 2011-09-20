package com.youku.soku.manage.torque;


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
 * ����
 *
 * This class was autogenerated by Torque on:
 *
 * [Mon Jun 13 16:26:37 CST 2011]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to SokuFeedback
 */
public abstract class BaseSokuFeedback extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1307953597808L;

    /** The Peer class */
    private static final SokuFeedbackPeer peer =
        new SokuFeedbackPeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the keyword field */
    private String keyword;
      
    /** The value for the url field */
    private String url;
      
    /** The value for the state field */
    private int state;
      
    /** The value for the source field */
    private int source;
      
    /** The value for the score field */
    private double score;
      
    /** The value for the message field */
    private String message;
      
    /** The value for the ipHost field */
    private String ipHost;
      
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
     * Get the Keyword
     *
     * @return String
     */
    public String getKeyword()
    {
        return keyword;
    }

                        
    /**
     * Set the value of Keyword
     *
     * @param v new value
     */
    public void setKeyword(String v) 
    {
    
                  if (!ObjectUtils.equals(this.keyword, v))
              {
            this.keyword = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Url
     *
     * @return String
     */
    public String getUrl()
    {
        return url;
    }

                        
    /**
     * Set the value of Url
     *
     * @param v new value
     */
    public void setUrl(String v) 
    {
    
                  if (!ObjectUtils.equals(this.url, v))
              {
            this.url = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the State
     *
     * @return int
     */
    public int getState()
    {
        return state;
    }

                        
    /**
     * Set the value of State
     *
     * @param v new value
     */
    public void setState(int v) 
    {
    
                  if (this.state != v)
              {
            this.state = v;
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
     * Get the Score
     *
     * @return double
     */
    public double getScore()
    {
        return score;
    }

                        
    /**
     * Set the value of Score
     *
     * @param v new value
     */
    public void setScore(double v) 
    {
    
                  if (this.score != v)
              {
            this.score = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Message
     *
     * @return String
     */
    public String getMessage()
    {
        return message;
    }

                        
    /**
     * Set the value of Message
     *
     * @param v new value
     */
    public void setMessage(String v) 
    {
    
                  if (!ObjectUtils.equals(this.message, v))
              {
            this.message = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the IpHost
     *
     * @return String
     */
    public String getIpHost()
    {
        return ipHost;
    }

                        
    /**
     * Set the value of IpHost
     *
     * @param v new value
     */
    public void setIpHost(String v) 
    {
    
                  if (!ObjectUtils.equals(this.ipHost, v))
              {
            this.ipHost = v;
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
              fieldNames.add("Keyword");
              fieldNames.add("Url");
              fieldNames.add("State");
              fieldNames.add("Source");
              fieldNames.add("Score");
              fieldNames.add("Message");
              fieldNames.add("IpHost");
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
          if (name.equals("Keyword"))
        {
                return getKeyword();
            }
          if (name.equals("Url"))
        {
                return getUrl();
            }
          if (name.equals("State"))
        {
                return new Integer(getState());
            }
          if (name.equals("Source"))
        {
                return new Integer(getSource());
            }
          if (name.equals("Score"))
        {
                return new Double(getScore());
            }
          if (name.equals("Message"))
        {
                return getMessage();
            }
          if (name.equals("IpHost"))
        {
                return getIpHost();
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
          if (name.equals("Keyword"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setKeyword((String) value);
                      return true;
        }
          if (name.equals("Url"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setUrl((String) value);
                      return true;
        }
          if (name.equals("State"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setState(((Integer) value).intValue());
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
          if (name.equals("Score"))
        {
                      if (value == null || ! (Double.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not a Double object.");
            }
            setScore(((Double) value).doubleValue());
                      return true;
        }
          if (name.equals("Message"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setMessage((String) value);
                      return true;
        }
          if (name.equals("IpHost"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setIpHost((String) value);
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
          if (name.equals(SokuFeedbackPeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(SokuFeedbackPeer.KEYWORD))
        {
                return getKeyword();
            }
          if (name.equals(SokuFeedbackPeer.URL))
        {
                return getUrl();
            }
          if (name.equals(SokuFeedbackPeer.STATE))
        {
                return new Integer(getState());
            }
          if (name.equals(SokuFeedbackPeer.SOURCE))
        {
                return new Integer(getSource());
            }
          if (name.equals(SokuFeedbackPeer.SCORE))
        {
                return new Double(getScore());
            }
          if (name.equals(SokuFeedbackPeer.MESSAGE))
        {
                return getMessage();
            }
          if (name.equals(SokuFeedbackPeer.IP_HOST))
        {
                return getIpHost();
            }
          if (name.equals(SokuFeedbackPeer.UPDATE_TIME))
        {
                return getUpdateTime();
            }
          if (name.equals(SokuFeedbackPeer.CREATE_TIME))
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
        if (SokuFeedbackPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (SokuFeedbackPeer.KEYWORD.equals(name))
        {
            return setByName("Keyword", value);
        }
        if (SokuFeedbackPeer.URL.equals(name))
        {
            return setByName("Url", value);
        }
        if (SokuFeedbackPeer.STATE.equals(name))
        {
            return setByName("State", value);
        }
        if (SokuFeedbackPeer.SOURCE.equals(name))
        {
            return setByName("Source", value);
        }
        if (SokuFeedbackPeer.SCORE.equals(name))
        {
            return setByName("Score", value);
        }
        if (SokuFeedbackPeer.MESSAGE.equals(name))
        {
            return setByName("Message", value);
        }
        if (SokuFeedbackPeer.IP_HOST.equals(name))
        {
            return setByName("IpHost", value);
        }
        if (SokuFeedbackPeer.UPDATE_TIME.equals(name))
        {
            return setByName("UpdateTime", value);
        }
        if (SokuFeedbackPeer.CREATE_TIME.equals(name))
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
                return getKeyword();
            }
              if (pos == 2)
        {
                return getUrl();
            }
              if (pos == 3)
        {
                return new Integer(getState());
            }
              if (pos == 4)
        {
                return new Integer(getSource());
            }
              if (pos == 5)
        {
                return new Double(getScore());
            }
              if (pos == 6)
        {
                return getMessage();
            }
              if (pos == 7)
        {
                return getIpHost();
            }
              if (pos == 8)
        {
                return getUpdateTime();
            }
              if (pos == 9)
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
            return setByName("Keyword", value);
        }
          if (position == 2)
        {
            return setByName("Url", value);
        }
          if (position == 3)
        {
            return setByName("State", value);
        }
          if (position == 4)
        {
            return setByName("Source", value);
        }
          if (position == 5)
        {
            return setByName("Score", value);
        }
          if (position == 6)
        {
            return setByName("Message", value);
        }
          if (position == 7)
        {
            return setByName("IpHost", value);
        }
          if (position == 8)
        {
            return setByName("UpdateTime", value);
        }
          if (position == 9)
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
          save(SokuFeedbackPeer.DATABASE_NAME);
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
                    SokuFeedbackPeer.doInsert((SokuFeedback) this, con);
                    setNew(false);
                }
                else
                {
                    SokuFeedbackPeer.doUpdate((SokuFeedback) this, con);
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
      public SokuFeedback copy() throws TorqueException
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
    public SokuFeedback copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new SokuFeedback(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected SokuFeedback copyInto(SokuFeedback copyObj) throws TorqueException
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
      protected SokuFeedback copyInto(SokuFeedback copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setKeyword(keyword);
          copyObj.setUrl(url);
          copyObj.setState(state);
          copyObj.setSource(source);
          copyObj.setScore(score);
          copyObj.setMessage(message);
          copyObj.setIpHost(ipHost);
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
    public SokuFeedbackPeer getPeer()
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
        return SokuFeedbackPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("SokuFeedback:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("Keyword = ")
               .append(getKeyword())
             .append("\n");
        str.append("Url = ")
               .append(getUrl())
             .append("\n");
        str.append("State = ")
               .append(getState())
             .append("\n");
        str.append("Source = ")
               .append(getSource())
             .append("\n");
        str.append("Score = ")
               .append(getScore())
             .append("\n");
        str.append("Message = ")
               .append(getMessage())
             .append("\n");
        str.append("IpHost = ")
               .append(getIpHost())
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
