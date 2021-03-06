package com.youku.soku.suggest.orm;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
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
 * trie�����
 *
 * This class was autogenerated by Torque on:
 *
 * [Thu Dec 09 16:56:03 CST 2010]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to TreeWords
 */
public abstract class BaseTreeWords extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1291884963589L;

    /** The Peer class */
    private static final TreeWordsPeer peer =
        new TreeWordsPeer();


    /** The value for the id field */
    private int id;

    /** The value for the keyword field */
    private String keyword;

    /** The value for the queryCount field */
    private int queryCount;

    /** The value for the score field */
    private int score;

    /** The value for the type field */
    private byte type;


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
     * Get the Score
     *
     * @return int
     */
    public int getScore()
    {
        return score;
    }


    /**
     * Set the value of Score
     *
     * @param v new value
     */
    public void setScore(int v) 
    {

        if (this.score != v)
        {
            this.score = v;
            setModified(true);
        }


    }

    /**
     * Get the Type
     *
     * @return byte
     */
    public byte getType()
    {
        return type;
    }


    /**
     * Set the value of Type
     *
     * @param v new value
     */
    public void setType(byte v) 
    {

        if (this.type != v)
        {
            this.type = v;
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
            fieldNames.add("Keyword");
            fieldNames.add("QueryCount");
            fieldNames.add("Score");
            fieldNames.add("Type");
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
        if (name.equals("QueryCount"))
        {
            return new Integer(getQueryCount());
        }
        if (name.equals("Score"))
        {
            return new Integer(getScore());
        }
        if (name.equals("Type"))
        {
            return new Byte(getType());
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
        if (name.equals("QueryCount"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setQueryCount(((Integer) value).intValue());
            return true;
        }
        if (name.equals("Score"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setScore(((Integer) value).intValue());
            return true;
        }
        if (name.equals("Type"))
        {
            if (value == null || ! (Byte.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not a Byte object.");
            }
            setType(((Byte) value).byteValue());
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
        if (name.equals(TreeWordsPeer.ID))
        {
            return new Integer(getId());
        }
        if (name.equals(TreeWordsPeer.KEYWORD))
        {
            return getKeyword();
        }
        if (name.equals(TreeWordsPeer.QUERY_COUNT))
        {
            return new Integer(getQueryCount());
        }
        if (name.equals(TreeWordsPeer.SCORE))
        {
            return new Integer(getScore());
        }
        if (name.equals(TreeWordsPeer.TYPE))
        {
            return new Byte(getType());
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
      if (TreeWordsPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
      if (TreeWordsPeer.KEYWORD.equals(name))
        {
            return setByName("Keyword", value);
        }
      if (TreeWordsPeer.QUERY_COUNT.equals(name))
        {
            return setByName("QueryCount", value);
        }
      if (TreeWordsPeer.SCORE.equals(name))
        {
            return setByName("Score", value);
        }
      if (TreeWordsPeer.TYPE.equals(name))
        {
            return setByName("Type", value);
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
            return new Integer(getQueryCount());
        }
        if (pos == 3)
        {
            return new Integer(getScore());
        }
        if (pos == 4)
        {
            return new Byte(getType());
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
            return setByName("QueryCount", value);
        }
    if (position == 3)
        {
            return setByName("Score", value);
        }
    if (position == 4)
        {
            return setByName("Type", value);
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
        save(TreeWordsPeer.DATABASE_NAME);
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
                    TreeWordsPeer.doInsert((TreeWords) this, con);
                    setNew(false);
                }
                else
                {
                    TreeWordsPeer.doUpdate((TreeWords) this, con);
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
    public TreeWords copy() throws TorqueException
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
    public TreeWords copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new TreeWords(), deepcopy);
    }

    /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected TreeWords copyInto(TreeWords copyObj) throws TorqueException
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
    protected TreeWords copyInto(TreeWords copyObj, boolean deepcopy) throws TorqueException
    {
        copyObj.setId(id);
        copyObj.setKeyword(keyword);
        copyObj.setQueryCount(queryCount);
        copyObj.setScore(score);
        copyObj.setType(type);

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
    public TreeWordsPeer getPeer()
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
        return TreeWordsPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("TreeWords:\n");
        str.append("Id = ")
           .append(getId())
           .append("\n");
        str.append("Keyword = ")
           .append(getKeyword())
           .append("\n");
        str.append("QueryCount = ")
           .append(getQueryCount())
           .append("\n");
        str.append("Score = ")
           .append(getScore())
           .append("\n");
        str.append("Type = ")
           .append(getType())
           .append("\n");
        return(str.toString());
    }
}
