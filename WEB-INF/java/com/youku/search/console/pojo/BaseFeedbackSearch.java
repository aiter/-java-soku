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
 * ???�¡�???����
 *
 * This class was autogenerated by Torque on:
 *
 * [Fri Jun 12 09:58:42 CST 2009]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to FeedbackSearch
 */
public abstract class BaseFeedbackSearch extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1244771922000L;

    /** The Peer class */
    private static final FeedbackSearchPeer peer =
        new FeedbackSearchPeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the errorType field */
    private int errorType;
      
    /** The value for the description field */
    private String description;
      
    /** The value for the keyword field */
    private String keyword;
                                          
    /** The value for the page field */
    private int page = 1;
                                          
    /** The value for the creator field */
    private int creator = -1;
      
    /** The value for the createtime field */
    private Date createtime;
      
    /** The value for the url field */
    private String url;
  
            
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
     * Get the ErrorType
     *
     * @return int
     */
    public int getErrorType()
    {
        return errorType;
    }

                        
    /**
     * Set the value of ErrorType
     *
     * @param v new value
     */
    public void setErrorType(int v) 
    {
    
                  if (this.errorType != v)
              {
            this.errorType = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Description
     *
     * @return String
     */
    public String getDescription()
    {
        return description;
    }

                        
    /**
     * Set the value of Description
     *
     * @param v new value
     */
    public void setDescription(String v) 
    {
    
                  if (!ObjectUtils.equals(this.description, v))
              {
            this.description = v;
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
     * Get the Page
     *
     * @return int
     */
    public int getPage()
    {
        return page;
    }

                        
    /**
     * Set the value of Page
     *
     * @param v new value
     */
    public void setPage(int v) 
    {
    
                  if (this.page != v)
              {
            this.page = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Creator
     *
     * @return int
     */
    public int getCreator()
    {
        return creator;
    }

                        
    /**
     * Set the value of Creator
     *
     * @param v new value
     */
    public void setCreator(int v) 
    {
    
                  if (this.creator != v)
              {
            this.creator = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Createtime
     *
     * @return Date
     */
    public Date getCreatetime()
    {
        return createtime;
    }

                        
    /**
     * Set the value of Createtime
     *
     * @param v new value
     */
    public void setCreatetime(Date v) 
    {
    
                  if (!ObjectUtils.equals(this.createtime, v))
              {
            this.createtime = v;
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
              fieldNames.add("ErrorType");
              fieldNames.add("Description");
              fieldNames.add("Keyword");
              fieldNames.add("Page");
              fieldNames.add("Creator");
              fieldNames.add("Createtime");
              fieldNames.add("Url");
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
          if (name.equals("ErrorType"))
        {
                return new Integer(getErrorType());
            }
          if (name.equals("Description"))
        {
                return getDescription();
            }
          if (name.equals("Keyword"))
        {
                return getKeyword();
            }
          if (name.equals("Page"))
        {
                return new Integer(getPage());
            }
          if (name.equals("Creator"))
        {
                return new Integer(getCreator());
            }
          if (name.equals("Createtime"))
        {
                return getCreatetime();
            }
          if (name.equals("Url"))
        {
                return getUrl();
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
          if (name.equals("ErrorType"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setErrorType(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Description"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setDescription((String) value);
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
          if (name.equals("Page"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setPage(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Creator"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setCreator(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Createtime"))
        {
                      // Object fields can be null
            if (value != null && ! Date.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setCreatetime((Date) value);
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
          if (name.equals(FeedbackSearchPeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(FeedbackSearchPeer.ERROR_TYPE))
        {
                return new Integer(getErrorType());
            }
          if (name.equals(FeedbackSearchPeer.DESCRIPTION))
        {
                return getDescription();
            }
          if (name.equals(FeedbackSearchPeer.KEYWORD))
        {
                return getKeyword();
            }
          if (name.equals(FeedbackSearchPeer.PAGE))
        {
                return new Integer(getPage());
            }
          if (name.equals(FeedbackSearchPeer.CREATOR))
        {
                return new Integer(getCreator());
            }
          if (name.equals(FeedbackSearchPeer.CREATETIME))
        {
                return getCreatetime();
            }
          if (name.equals(FeedbackSearchPeer.URL))
        {
                return getUrl();
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
        if (FeedbackSearchPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (FeedbackSearchPeer.ERROR_TYPE.equals(name))
        {
            return setByName("ErrorType", value);
        }
        if (FeedbackSearchPeer.DESCRIPTION.equals(name))
        {
            return setByName("Description", value);
        }
        if (FeedbackSearchPeer.KEYWORD.equals(name))
        {
            return setByName("Keyword", value);
        }
        if (FeedbackSearchPeer.PAGE.equals(name))
        {
            return setByName("Page", value);
        }
        if (FeedbackSearchPeer.CREATOR.equals(name))
        {
            return setByName("Creator", value);
        }
        if (FeedbackSearchPeer.CREATETIME.equals(name))
        {
            return setByName("Createtime", value);
        }
        if (FeedbackSearchPeer.URL.equals(name))
        {
            return setByName("Url", value);
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
                return new Integer(getErrorType());
            }
              if (pos == 2)
        {
                return getDescription();
            }
              if (pos == 3)
        {
                return getKeyword();
            }
              if (pos == 4)
        {
                return new Integer(getPage());
            }
              if (pos == 5)
        {
                return new Integer(getCreator());
            }
              if (pos == 6)
        {
                return getCreatetime();
            }
              if (pos == 7)
        {
                return getUrl();
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
            return setByName("ErrorType", value);
        }
          if (position == 2)
        {
            return setByName("Description", value);
        }
          if (position == 3)
        {
            return setByName("Keyword", value);
        }
          if (position == 4)
        {
            return setByName("Page", value);
        }
          if (position == 5)
        {
            return setByName("Creator", value);
        }
          if (position == 6)
        {
            return setByName("Createtime", value);
        }
          if (position == 7)
        {
            return setByName("Url", value);
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
          save(FeedbackSearchPeer.DATABASE_NAME);
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
                    FeedbackSearchPeer.doInsert((FeedbackSearch) this, con);
                    setNew(false);
                }
                else
                {
                    FeedbackSearchPeer.doUpdate((FeedbackSearch) this, con);
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
      public FeedbackSearch copy() throws TorqueException
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
    public FeedbackSearch copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new FeedbackSearch(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected FeedbackSearch copyInto(FeedbackSearch copyObj) throws TorqueException
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
      protected FeedbackSearch copyInto(FeedbackSearch copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setErrorType(errorType);
          copyObj.setDescription(description);
          copyObj.setKeyword(keyword);
          copyObj.setPage(page);
          copyObj.setCreator(creator);
          copyObj.setCreatetime(createtime);
          copyObj.setUrl(url);
  
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
    public FeedbackSearchPeer getPeer()
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
        return FeedbackSearchPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("FeedbackSearch:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("ErrorType = ")
               .append(getErrorType())
             .append("\n");
        str.append("Description = ")
               .append(getDescription())
             .append("\n");
        str.append("Keyword = ")
               .append(getKeyword())
             .append("\n");
        str.append("Page = ")
               .append(getPage())
             .append("\n");
        str.append("Creator = ")
               .append(getCreator())
             .append("\n");
        str.append("Createtime = ")
               .append(getCreatetime())
             .append("\n");
        str.append("Url = ")
               .append(getUrl())
             .append("\n");
        return(str.toString());
    }
}
