package com.youku.soku.manage.torque;


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
 * Item Table
 *
 * This class was autogenerated by Torque on:
 *
 * [Wed Mar 10 16:13:12 CST 2010]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to Item
 */
public abstract class BaseItem extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1268208792937L;

    /** The Peer class */
    private static final ItemPeer peer =
        new ItemPeer();


    /** The value for the itemId field */
    private int itemId;

    /** The value for the name field */
    private String name;

    /** The value for the url field */
    private String url;

    /** The value for the navigation field */
    private int navigation;

    /** The value for the indexType field */
    private int indexType;

    /** The value for the sort field */
    private int sort;


    /**
     * Get the ItemId
     *
     * @return int
     */
    public int getItemId()
    {
        return itemId;
    }


    /**
     * Set the value of ItemId
     *
     * @param v new value
     */
    public void setItemId(int v) 
    {

        if (this.itemId != v)
        {
            this.itemId = v;
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
     * Get the Navigation
     *
     * @return int
     */
    public int getNavigation()
    {
        return navigation;
    }


    /**
     * Set the value of Navigation
     *
     * @param v new value
     */
    public void setNavigation(int v) 
    {

        if (this.navigation != v)
        {
            this.navigation = v;
            setModified(true);
        }


    }

    /**
     * Get the IndexType
     *
     * @return int
     */
    public int getIndexType()
    {
        return indexType;
    }


    /**
     * Set the value of IndexType
     *
     * @param v new value
     */
    public void setIndexType(int v) 
    {

        if (this.indexType != v)
        {
            this.indexType = v;
            setModified(true);
        }


    }

    /**
     * Get the Sort
     *
     * @return int
     */
    public int getSort()
    {
        return sort;
    }


    /**
     * Set the value of Sort
     *
     * @param v new value
     */
    public void setSort(int v) 
    {

        if (this.sort != v)
        {
            this.sort = v;
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
            fieldNames.add("ItemId");
            fieldNames.add("Name");
            fieldNames.add("Url");
            fieldNames.add("Navigation");
            fieldNames.add("IndexType");
            fieldNames.add("Sort");
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
        if (name.equals("ItemId"))
        {
            return new Integer(getItemId());
        }
        if (name.equals("Name"))
        {
            return getName();
        }
        if (name.equals("Url"))
        {
            return getUrl();
        }
        if (name.equals("Navigation"))
        {
            return new Integer(getNavigation());
        }
        if (name.equals("IndexType"))
        {
            return new Integer(getIndexType());
        }
        if (name.equals("Sort"))
        {
            return new Integer(getSort());
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
        if (name.equals("ItemId"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setItemId(((Integer) value).intValue());
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
        if (name.equals("Navigation"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setNavigation(((Integer) value).intValue());
            return true;
        }
        if (name.equals("IndexType"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setIndexType(((Integer) value).intValue());
            return true;
        }
        if (name.equals("Sort"))
        {
            if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setSort(((Integer) value).intValue());
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
        if (name.equals(ItemPeer.ITEM_ID))
        {
            return new Integer(getItemId());
        }
        if (name.equals(ItemPeer.NAME))
        {
            return getName();
        }
        if (name.equals(ItemPeer.URL))
        {
            return getUrl();
        }
        if (name.equals(ItemPeer.NAVIGATION))
        {
            return new Integer(getNavigation());
        }
        if (name.equals(ItemPeer.INDEX_TYPE))
        {
            return new Integer(getIndexType());
        }
        if (name.equals(ItemPeer.SORT))
        {
            return new Integer(getSort());
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
      if (ItemPeer.ITEM_ID.equals(name))
        {
            return setByName("ItemId", value);
        }
      if (ItemPeer.NAME.equals(name))
        {
            return setByName("Name", value);
        }
      if (ItemPeer.URL.equals(name))
        {
            return setByName("Url", value);
        }
      if (ItemPeer.NAVIGATION.equals(name))
        {
            return setByName("Navigation", value);
        }
      if (ItemPeer.INDEX_TYPE.equals(name))
        {
            return setByName("IndexType", value);
        }
      if (ItemPeer.SORT.equals(name))
        {
            return setByName("Sort", value);
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
            return new Integer(getItemId());
        }
        if (pos == 1)
        {
            return getName();
        }
        if (pos == 2)
        {
            return getUrl();
        }
        if (pos == 3)
        {
            return new Integer(getNavigation());
        }
        if (pos == 4)
        {
            return new Integer(getIndexType());
        }
        if (pos == 5)
        {
            return new Integer(getSort());
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
            return setByName("ItemId", value);
        }
    if (position == 1)
        {
            return setByName("Name", value);
        }
    if (position == 2)
        {
            return setByName("Url", value);
        }
    if (position == 3)
        {
            return setByName("Navigation", value);
        }
    if (position == 4)
        {
            return setByName("IndexType", value);
        }
    if (position == 5)
        {
            return setByName("Sort", value);
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
        save(ItemPeer.DATABASE_NAME);
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
                    ItemPeer.doInsert((Item) this, con);
                    setNew(false);
                }
                else
                {
                    ItemPeer.doUpdate((Item) this, con);
                }
            }

            alreadyInSave = false;
        }
    }


    /**
     * Set the PrimaryKey using ObjectKey.
     *
     * @param key itemId ObjectKey
     */
    public void setPrimaryKey(ObjectKey key)
        
    {
        setItemId(((NumberKey) key).intValue());
    }

    /**
     * Set the PrimaryKey using a String.
     *
     * @param key
     */
    public void setPrimaryKey(String key) 
    {
        setItemId(Integer.parseInt(key));
    }


    /**
     * returns an id that differentiates this object from others
     * of its class.
     */
    public ObjectKey getPrimaryKey()
    {
        return SimpleKey.keyFor(getItemId());
    }
 

    /**
     * Makes a copy of this object.
     * It creates a new object filling in the simple attributes.
     * It then fills all the association collections and sets the
     * related objects to isNew=true.
     */
    public Item copy() throws TorqueException
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
    public Item copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new Item(), deepcopy);
    }

    /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected Item copyInto(Item copyObj) throws TorqueException
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
    protected Item copyInto(Item copyObj, boolean deepcopy) throws TorqueException
    {
        copyObj.setItemId(itemId);
        copyObj.setName(name);
        copyObj.setUrl(url);
        copyObj.setNavigation(navigation);
        copyObj.setIndexType(indexType);
        copyObj.setSort(sort);

        copyObj.setItemId( 0);

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
    public ItemPeer getPeer()
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
        return ItemPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("Item:\n");
        str.append("ItemId = ")
           .append(getItemId())
           .append("\n");
        str.append("Name = ")
           .append(getName())
           .append("\n");
        str.append("Url = ")
           .append(getUrl())
           .append("\n");
        str.append("Navigation = ")
           .append(getNavigation())
           .append("\n");
        str.append("IndexType = ")
           .append(getIndexType())
           .append("\n");
        str.append("Sort = ")
           .append(getSort())
           .append("\n");
        return(str.toString());
    }
}