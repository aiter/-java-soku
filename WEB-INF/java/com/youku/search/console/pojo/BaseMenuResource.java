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
 * ????��???����
 *
 * This class was autogenerated by Torque on:
 *
 * [Fri Jun 12 09:58:42 CST 2009]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to MenuResource
 */
public abstract class BaseMenuResource extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1244771922000L;

    /** The Peer class */
    private static final MenuResourcePeer peer =
        new MenuResourcePeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the menuId field */
    private int menuId;
      
    /** The value for the resourceId field */
    private int resourceId;
  
            
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
     * Get the MenuId
     *
     * @return int
     */
    public int getMenuId()
    {
        return menuId;
    }

                              
    /**
     * Set the value of MenuId
     *
     * @param v new value
     */
    public void setMenuId(int v) throws TorqueException
    {
    
                  if (this.menuId != v)
              {
            this.menuId = v;
            setModified(true);
        }
    
                                  
                if (aMenu != null && !(aMenu.getId() == v))
                {
            aMenu = null;
        }
      
              }
          
    /**
     * Get the ResourceId
     *
     * @return int
     */
    public int getResourceId()
    {
        return resourceId;
    }

                              
    /**
     * Set the value of ResourceId
     *
     * @param v new value
     */
    public void setResourceId(int v) throws TorqueException
    {
    
                  if (this.resourceId != v)
              {
            this.resourceId = v;
            setModified(true);
        }
    
                                  
                if (aResource != null && !(aResource.getId() == v))
                {
            aResource = null;
        }
      
              }
  
      
        
                  
    
        private Menu aMenu;

    /**
     * Declares an association between this object and a Menu object
     *
     * @param v Menu
     * @throws TorqueException
     */
    public void setMenu(Menu v) throws TorqueException
    {
            if (v == null)
        {
                          setMenuId( 0);
              }
        else
        {
            setMenuId(v.getId());
        }
            aMenu = v;
    }

                        
    /**
     * Returns the associated Menu object.
           * If it was not retrieved before, the object is retrieved from
     * the database
           *
     * @return the associated Menu object
           * @throws TorqueException
           */
    public Menu getMenu()
              throws TorqueException
          {
              if (aMenu == null && (this.menuId != 0))
        {
                              aMenu = MenuPeer.retrieveByPK(SimpleKey.keyFor(this.menuId));
                  
            /* The following can be used instead of the line above to
               guarantee the related object contains a reference
               to this object, but this level of coupling
               may be undesirable in many circumstances.
               As it can lead to a db query with many results that may
               never be used.
               Menu obj = MenuPeer.retrieveByPK(this.menuId);
               obj.add${pCollName}(this);
            */
        }
              return aMenu;
    }

    /**
     * Return the associated Menu object
     * If it was not retrieved before, the object is retrieved from
     * the database using the passed connection
     *
     * @param connection the connection used to retrieve the associated object
     *        from the database, if it was not retrieved before
     * @return the associated Menu object
     * @throws TorqueException
     */
    public Menu getMenu(Connection connection)
        throws TorqueException
    {
        if (aMenu == null && (this.menuId != 0))
        {
                          aMenu = MenuPeer.retrieveByPK(SimpleKey.keyFor(this.menuId), connection);
              
            /* The following can be used instead of the line above to
               guarantee the related object contains a reference
               to this object, but this level of coupling
               may be undesirable in many circumstances.
               As it can lead to a db query with many results that may
               never be used.
               Menu obj = MenuPeer.retrieveByPK(this.menuId, connection);
               obj.add${pCollName}(this);
            */
        }
        return aMenu;
    }

    /**
     * Provides convenient way to set a relationship based on a
     * ObjectKey, for example
     * <code>bar.setFooKey(foo.getPrimaryKey())</code>
     *
         */
    public void setMenuKey(ObjectKey key) throws TorqueException
    {
      
                        setMenuId(((NumberKey) key).intValue());
                  }
    
        
                  
    
        private Resource aResource;

    /**
     * Declares an association between this object and a Resource object
     *
     * @param v Resource
     * @throws TorqueException
     */
    public void setResource(Resource v) throws TorqueException
    {
            if (v == null)
        {
                          setResourceId( 0);
              }
        else
        {
            setResourceId(v.getId());
        }
            aResource = v;
    }

                        
    /**
     * Returns the associated Resource object.
           * If it was not retrieved before, the object is retrieved from
     * the database
           *
     * @return the associated Resource object
           * @throws TorqueException
           */
    public Resource getResource()
              throws TorqueException
          {
              if (aResource == null && (this.resourceId != 0))
        {
                              aResource = ResourcePeer.retrieveByPK(SimpleKey.keyFor(this.resourceId));
                  
            /* The following can be used instead of the line above to
               guarantee the related object contains a reference
               to this object, but this level of coupling
               may be undesirable in many circumstances.
               As it can lead to a db query with many results that may
               never be used.
               Resource obj = ResourcePeer.retrieveByPK(this.resourceId);
               obj.add${pCollName}(this);
            */
        }
              return aResource;
    }

    /**
     * Return the associated Resource object
     * If it was not retrieved before, the object is retrieved from
     * the database using the passed connection
     *
     * @param connection the connection used to retrieve the associated object
     *        from the database, if it was not retrieved before
     * @return the associated Resource object
     * @throws TorqueException
     */
    public Resource getResource(Connection connection)
        throws TorqueException
    {
        if (aResource == null && (this.resourceId != 0))
        {
                          aResource = ResourcePeer.retrieveByPK(SimpleKey.keyFor(this.resourceId), connection);
              
            /* The following can be used instead of the line above to
               guarantee the related object contains a reference
               to this object, but this level of coupling
               may be undesirable in many circumstances.
               As it can lead to a db query with many results that may
               never be used.
               Resource obj = ResourcePeer.retrieveByPK(this.resourceId, connection);
               obj.add${pCollName}(this);
            */
        }
        return aResource;
    }

    /**
     * Provides convenient way to set a relationship based on a
     * ObjectKey, for example
     * <code>bar.setFooKey(foo.getPrimaryKey())</code>
     *
         */
    public void setResourceKey(ObjectKey key) throws TorqueException
    {
      
                        setResourceId(((NumberKey) key).intValue());
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
              fieldNames.add("MenuId");
              fieldNames.add("ResourceId");
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
          if (name.equals("MenuId"))
        {
                return new Integer(getMenuId());
            }
          if (name.equals("ResourceId"))
        {
                return new Integer(getResourceId());
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
          if (name.equals("MenuId"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setMenuId(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("ResourceId"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setResourceId(((Integer) value).intValue());
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
          if (name.equals(MenuResourcePeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(MenuResourcePeer.MENU_ID))
        {
                return new Integer(getMenuId());
            }
          if (name.equals(MenuResourcePeer.RESOURCE_ID))
        {
                return new Integer(getResourceId());
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
        if (MenuResourcePeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (MenuResourcePeer.MENU_ID.equals(name))
        {
            return setByName("MenuId", value);
        }
        if (MenuResourcePeer.RESOURCE_ID.equals(name))
        {
            return setByName("ResourceId", value);
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
                return new Integer(getMenuId());
            }
              if (pos == 2)
        {
                return new Integer(getResourceId());
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
            return setByName("MenuId", value);
        }
          if (position == 2)
        {
            return setByName("ResourceId", value);
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
          save(MenuResourcePeer.DATABASE_NAME);
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
                    MenuResourcePeer.doInsert((MenuResource) this, con);
                    setNew(false);
                }
                else
                {
                    MenuResourcePeer.doUpdate((MenuResource) this, con);
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
      public MenuResource copy() throws TorqueException
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
    public MenuResource copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new MenuResource(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected MenuResource copyInto(MenuResource copyObj) throws TorqueException
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
      protected MenuResource copyInto(MenuResource copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setMenuId(menuId);
          copyObj.setResourceId(resourceId);
  
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
    public MenuResourcePeer getPeer()
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
        return MenuResourcePeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("MenuResource:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("MenuId = ")
               .append(getMenuId())
             .append("\n");
        str.append("ResourceId = ")
               .append(getResourceId())
             .append("\n");
        return(str.toString());
    }
}
