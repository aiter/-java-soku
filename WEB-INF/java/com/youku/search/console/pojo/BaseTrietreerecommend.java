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
 * ???��????????
 *
 * This class was autogenerated by Torque on:
 *
 * [Tue Jun 30 15:16:12 CST 2009]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to Trietreerecommend
 */
public abstract class BaseTrietreerecommend extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1246346172843L;

    /** The Peer class */
    private static final TrietreerecommendPeer peer =
        new TrietreerecommendPeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the keyword field */
    private String keyword;
      
    /** The value for the keywordPy field */
    private String keywordPy;
      
    /** The value for the recomendType field */
    private String recomendType;
      
    /** The value for the queryCount field */
    private int queryCount;
                                          
    /** The value for the result field */
    private int result = 1;
      
    /** The value for the starttime field */
    private Date starttime;
      
    /** The value for the endtime field */
    private Date endtime;
      
    /** The value for the creator field */
    private String creator;
      
    /** The value for the createtime field */
    private Date createtime;
  
            
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
     * Get the KeywordPy
     *
     * @return String
     */
    public String getKeywordPy()
    {
        return keywordPy;
    }

                        
    /**
     * Set the value of KeywordPy
     *
     * @param v new value
     */
    public void setKeywordPy(String v) 
    {
    
                  if (!ObjectUtils.equals(this.keywordPy, v))
              {
            this.keywordPy = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the RecomendType
     *
     * @return String
     */
    public String getRecomendType()
    {
        return recomendType;
    }

                        
    /**
     * Set the value of RecomendType
     *
     * @param v new value
     */
    public void setRecomendType(String v) 
    {
    
                  if (!ObjectUtils.equals(this.recomendType, v))
              {
            this.recomendType = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the QueryCount
     *
     * @return int
     */
    public int getQueryCount()
    {
        return queryCount;
    }

                        
    /**
     * Set the value of QueryCount
     *
     * @param v new value
     */
    public void setQueryCount(int v) 
    {
    
                  if (this.queryCount != v)
              {
            this.queryCount = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Result
     *
     * @return int
     */
    public int getResult()
    {
        return result;
    }

                        
    /**
     * Set the value of Result
     *
     * @param v new value
     */
    public void setResult(int v) 
    {
    
                  if (this.result != v)
              {
            this.result = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Starttime
     *
     * @return Date
     */
    public Date getStarttime()
    {
        return starttime;
    }

                        
    /**
     * Set the value of Starttime
     *
     * @param v new value
     */
    public void setStarttime(Date v) 
    {
    
                  if (!ObjectUtils.equals(this.starttime, v))
              {
            this.starttime = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Endtime
     *
     * @return Date
     */
    public Date getEndtime()
    {
        return endtime;
    }

                        
    /**
     * Set the value of Endtime
     *
     * @param v new value
     */
    public void setEndtime(Date v) 
    {
    
                  if (!ObjectUtils.equals(this.endtime, v))
              {
            this.endtime = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Creator
     *
     * @return String
     */
    public String getCreator()
    {
        return creator;
    }

                        
    /**
     * Set the value of Creator
     *
     * @param v new value
     */
    public void setCreator(String v) 
    {
    
                  if (!ObjectUtils.equals(this.creator, v))
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
              fieldNames.add("KeywordPy");
              fieldNames.add("RecomendType");
              fieldNames.add("QueryCount");
              fieldNames.add("Result");
              fieldNames.add("Starttime");
              fieldNames.add("Endtime");
              fieldNames.add("Creator");
              fieldNames.add("Createtime");
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
          if (name.equals("KeywordPy"))
        {
                return getKeywordPy();
            }
          if (name.equals("RecomendType"))
        {
                return getRecomendType();
            }
          if (name.equals("QueryCount"))
        {
                return new Integer(getQueryCount());
            }
          if (name.equals("Result"))
        {
                return new Integer(getResult());
            }
          if (name.equals("Starttime"))
        {
                return getStarttime();
            }
          if (name.equals("Endtime"))
        {
                return getEndtime();
            }
          if (name.equals("Creator"))
        {
                return getCreator();
            }
          if (name.equals("Createtime"))
        {
                return getCreatetime();
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
          if (name.equals("KeywordPy"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setKeywordPy((String) value);
                      return true;
        }
          if (name.equals("RecomendType"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setRecomendType((String) value);
                      return true;
        }
          if (name.equals("QueryCount"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setQueryCount(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Result"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setResult(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Starttime"))
        {
                      // Object fields can be null
            if (value != null && ! Date.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setStarttime((Date) value);
                      return true;
        }
          if (name.equals("Endtime"))
        {
                      // Object fields can be null
            if (value != null && ! Date.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setEndtime((Date) value);
                      return true;
        }
          if (name.equals("Creator"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setCreator((String) value);
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
          if (name.equals(TrietreerecommendPeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(TrietreerecommendPeer.KEYWORD))
        {
                return getKeyword();
            }
          if (name.equals(TrietreerecommendPeer.KEYWORD_PY))
        {
                return getKeywordPy();
            }
          if (name.equals(TrietreerecommendPeer.RECOMEND_TYPE))
        {
                return getRecomendType();
            }
          if (name.equals(TrietreerecommendPeer.QUERY_COUNT))
        {
                return new Integer(getQueryCount());
            }
          if (name.equals(TrietreerecommendPeer.RESULT))
        {
                return new Integer(getResult());
            }
          if (name.equals(TrietreerecommendPeer.STARTTIME))
        {
                return getStarttime();
            }
          if (name.equals(TrietreerecommendPeer.ENDTIME))
        {
                return getEndtime();
            }
          if (name.equals(TrietreerecommendPeer.CREATOR))
        {
                return getCreator();
            }
          if (name.equals(TrietreerecommendPeer.CREATETIME))
        {
                return getCreatetime();
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
        if (TrietreerecommendPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (TrietreerecommendPeer.KEYWORD.equals(name))
        {
            return setByName("Keyword", value);
        }
        if (TrietreerecommendPeer.KEYWORD_PY.equals(name))
        {
            return setByName("KeywordPy", value);
        }
        if (TrietreerecommendPeer.RECOMEND_TYPE.equals(name))
        {
            return setByName("RecomendType", value);
        }
        if (TrietreerecommendPeer.QUERY_COUNT.equals(name))
        {
            return setByName("QueryCount", value);
        }
        if (TrietreerecommendPeer.RESULT.equals(name))
        {
            return setByName("Result", value);
        }
        if (TrietreerecommendPeer.STARTTIME.equals(name))
        {
            return setByName("Starttime", value);
        }
        if (TrietreerecommendPeer.ENDTIME.equals(name))
        {
            return setByName("Endtime", value);
        }
        if (TrietreerecommendPeer.CREATOR.equals(name))
        {
            return setByName("Creator", value);
        }
        if (TrietreerecommendPeer.CREATETIME.equals(name))
        {
            return setByName("Createtime", value);
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
                return getKeywordPy();
            }
              if (pos == 3)
        {
                return getRecomendType();
            }
              if (pos == 4)
        {
                return new Integer(getQueryCount());
            }
              if (pos == 5)
        {
                return new Integer(getResult());
            }
              if (pos == 6)
        {
                return getStarttime();
            }
              if (pos == 7)
        {
                return getEndtime();
            }
              if (pos == 8)
        {
                return getCreator();
            }
              if (pos == 9)
        {
                return getCreatetime();
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
            return setByName("KeywordPy", value);
        }
          if (position == 3)
        {
            return setByName("RecomendType", value);
        }
          if (position == 4)
        {
            return setByName("QueryCount", value);
        }
          if (position == 5)
        {
            return setByName("Result", value);
        }
          if (position == 6)
        {
            return setByName("Starttime", value);
        }
          if (position == 7)
        {
            return setByName("Endtime", value);
        }
          if (position == 8)
        {
            return setByName("Creator", value);
        }
          if (position == 9)
        {
            return setByName("Createtime", value);
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
          save(TrietreerecommendPeer.DATABASE_NAME);
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
                    TrietreerecommendPeer.doInsert((Trietreerecommend) this, con);
                    setNew(false);
                }
                else
                {
                    TrietreerecommendPeer.doUpdate((Trietreerecommend) this, con);
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
      public Trietreerecommend copy() throws TorqueException
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
    public Trietreerecommend copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new Trietreerecommend(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected Trietreerecommend copyInto(Trietreerecommend copyObj) throws TorqueException
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
      protected Trietreerecommend copyInto(Trietreerecommend copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setKeyword(keyword);
          copyObj.setKeywordPy(keywordPy);
          copyObj.setRecomendType(recomendType);
          copyObj.setQueryCount(queryCount);
          copyObj.setResult(result);
          copyObj.setStarttime(starttime);
          copyObj.setEndtime(endtime);
          copyObj.setCreator(creator);
          copyObj.setCreatetime(createtime);
  
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
    public TrietreerecommendPeer getPeer()
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
        return TrietreerecommendPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("Trietreerecommend:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("Keyword = ")
               .append(getKeyword())
             .append("\n");
        str.append("KeywordPy = ")
               .append(getKeywordPy())
             .append("\n");
        str.append("RecomendType = ")
               .append(getRecomendType())
             .append("\n");
        str.append("QueryCount = ")
               .append(getQueryCount())
             .append("\n");
        str.append("Result = ")
               .append(getResult())
             .append("\n");
        str.append("Starttime = ")
               .append(getStarttime())
             .append("\n");
        str.append("Endtime = ")
               .append(getEndtime())
             .append("\n");
        str.append("Creator = ")
               .append(getCreator())
             .append("\n");
        str.append("Createtime = ")
               .append(getCreatetime())
             .append("\n");
        return(str.toString());
    }
}