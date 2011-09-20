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
 * ???�����
 *
 * This class was autogenerated by Torque on:
 *
 * [Fri Jun 12 09:58:42 CST 2009]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to User
 */
public abstract class BaseUser extends BaseObject
{
    /** Serial version */
    private static final long serialVersionUID = 1244771922000L;

    /** The Peer class */
    private static final UserPeer peer =
        new UserPeer();

        
    /** The value for the id field */
    private int id;
      
    /** The value for the name field */
    private String name;
      
    /** The value for the password field */
    private String password;
      
    /** The value for the trueName field */
    private String trueName;
      
    /** The value for the team field */
    private String team;
                                          
    /** The value for the sex field */
    private int sex = 1;
      
    /** The value for the birth field */
    private String birth;
      
    /** The value for the lastlogindate field */
    private Date lastlogindate;
  
            
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
    public void setId(int v) throws TorqueException
    {
    
                  if (this.id != v)
              {
            this.id = v;
            setModified(true);
        }
    
          
                                  
                  // update associated UserRole
        if (collUserRoles != null)
        {
            for (int i = 0; i < collUserRoles.size(); i++)
            {
                ((UserRole) collUserRoles.get(i))
                        .setUserId(v);
            }
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
     * Get the Password
     *
     * @return String
     */
    public String getPassword()
    {
        return password;
    }

                        
    /**
     * Set the value of Password
     *
     * @param v new value
     */
    public void setPassword(String v) 
    {
    
                  if (!ObjectUtils.equals(this.password, v))
              {
            this.password = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the TrueName
     *
     * @return String
     */
    public String getTrueName()
    {
        return trueName;
    }

                        
    /**
     * Set the value of TrueName
     *
     * @param v new value
     */
    public void setTrueName(String v) 
    {
    
                  if (!ObjectUtils.equals(this.trueName, v))
              {
            this.trueName = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Team
     *
     * @return String
     */
    public String getTeam()
    {
        return team;
    }

                        
    /**
     * Set the value of Team
     *
     * @param v new value
     */
    public void setTeam(String v) 
    {
    
                  if (!ObjectUtils.equals(this.team, v))
              {
            this.team = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Sex
     *
     * @return int
     */
    public int getSex()
    {
        return sex;
    }

                        
    /**
     * Set the value of Sex
     *
     * @param v new value
     */
    public void setSex(int v) 
    {
    
                  if (this.sex != v)
              {
            this.sex = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Birth
     *
     * @return String
     */
    public String getBirth()
    {
        return birth;
    }

                        
    /**
     * Set the value of Birth
     *
     * @param v new value
     */
    public void setBirth(String v) 
    {
    
                  if (!ObjectUtils.equals(this.birth, v))
              {
            this.birth = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Lastlogindate
     *
     * @return Date
     */
    public Date getLastlogindate()
    {
        return lastlogindate;
    }

                        
    /**
     * Set the value of Lastlogindate
     *
     * @param v new value
     */
    public void setLastlogindate(Date v) 
    {
    
                  if (!ObjectUtils.equals(this.lastlogindate, v))
              {
            this.lastlogindate = v;
            setModified(true);
        }
    
          
              }
  
         
                                
            
          /**
     * Collection to store aggregation of collUserRoles
     */
    protected List<UserRole> collUserRoles;

    /**
     * Temporary storage of collUserRoles to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
    protected void initUserRoles()
    {
        if (collUserRoles == null)
        {
            collUserRoles = new ArrayList<UserRole>();
        }
    }

        
    /**
     * Method called to associate a UserRole object to this object
     * through the UserRole foreign key attribute
     *
     * @param l UserRole
     * @throws TorqueException
     */
    public void addUserRole(UserRole l) throws TorqueException
    {
        getUserRoles().add(l);
        l.setUser((User) this);
    }

    /**
     * The criteria used to select the current contents of collUserRoles
     */
    private Criteria lastUserRolesCriteria = null;
      
    /**
                   * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of
     * getUserRoles(new Criteria())
                   *
     * @return the collection of associated objects
           * @throws TorqueException
           */
    public List<UserRole> getUserRoles()
              throws TorqueException
          {
                      if (collUserRoles == null)
        {
            collUserRoles = getUserRoles(new Criteria(10));
        }
                return collUserRoles;
          }

    /**
           * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this User has previously
           * been saved, it will retrieve related UserRoles from storage.
     * If this User is new, it will return
     * an empty collection or the current collection, the criteria
     * is ignored on a new object.
     *
     * @throws TorqueException
     */
    public List<UserRole> getUserRoles(Criteria criteria) throws TorqueException
    {
              if (collUserRoles == null)
        {
            if (isNew())
            {
               collUserRoles = new ArrayList<UserRole>();
            }
            else
            {
                        criteria.add(UserRolePeer.USER_ID, getId() );
                        collUserRoles = UserRolePeer.doSelect(criteria);
            }
        }
        else
        {
            // criteria has no effect for a new object
            if (!isNew())
            {
                // the following code is to determine if a new query is
                // called for.  If the criteria is the same as the last
                // one, just return the collection.
                            criteria.add(UserRolePeer.USER_ID, getId());
                            if (!lastUserRolesCriteria.equals(criteria))
                {
                    collUserRoles = UserRolePeer.doSelect(criteria);
                }
            }
        }
        lastUserRolesCriteria = criteria;

        return collUserRoles;
          }

    /**
           * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of
     * getUserRoles(new Criteria(),Connection)
           * This method takes in the Connection also as input so that
     * referenced objects can also be obtained using a Connection
     * that is taken as input
     */
    public List<UserRole> getUserRoles(Connection con) throws TorqueException
    {
              if (collUserRoles == null)
        {
            collUserRoles = getUserRoles(new Criteria(10), con);
        }
        return collUserRoles;
          }

    /**
           * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this User has previously
           * been saved, it will retrieve related UserRoles from storage.
     * If this User is new, it will return
     * an empty collection or the current collection, the criteria
     * is ignored on a new object.
     * This method takes in the Connection also as input so that
     * referenced objects can also be obtained using a Connection
     * that is taken as input
     */
    public List<UserRole> getUserRoles(Criteria criteria, Connection con)
            throws TorqueException
    {
              if (collUserRoles == null)
        {
            if (isNew())
            {
               collUserRoles = new ArrayList<UserRole>();
            }
            else
            {
                         criteria.add(UserRolePeer.USER_ID, getId());
                         collUserRoles = UserRolePeer.doSelect(criteria, con);
             }
         }
         else
         {
             // criteria has no effect for a new object
             if (!isNew())
             {
                 // the following code is to determine if a new query is
                 // called for.  If the criteria is the same as the last
                 // one, just return the collection.
                             criteria.add(UserRolePeer.USER_ID, getId());
                             if (!lastUserRolesCriteria.equals(criteria))
                 {
                     collUserRoles = UserRolePeer.doSelect(criteria, con);
                 }
             }
         }
         lastUserRolesCriteria = criteria;

         return collUserRoles;
           }

                        
              
                    
                              
                                
                                                              
                                        
                    
                    
          
    /**
                 * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this User is new, it will return
                 * an empty collection; or if this User has previously
     * been saved, it will retrieve related UserRoles from storage.
     *
     * This method is protected by default in order to keep the public
     * api reasonable.  You can provide public methods for those you
     * actually need in User.
     */
    protected List<UserRole> getUserRolesJoinUser(Criteria criteria)
        throws TorqueException
    {
                    if (collUserRoles == null)
        {
            if (isNew())
            {
               collUserRoles = new ArrayList<UserRole>();
            }
            else
            {
                              criteria.add(UserRolePeer.USER_ID, getId());
                              collUserRoles = UserRolePeer.doSelectJoinUser(criteria);
            }
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
                                    criteria.add(UserRolePeer.USER_ID, getId());
                                    if (!lastUserRolesCriteria.equals(criteria))
            {
                collUserRoles = UserRolePeer.doSelectJoinUser(criteria);
            }
        }
        lastUserRolesCriteria = criteria;

        return collUserRoles;
                }
                  
                    
                    
                                
                                                              
                                        
                    
                    
          
    /**
                 * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this User is new, it will return
                 * an empty collection; or if this User has previously
     * been saved, it will retrieve related UserRoles from storage.
     *
     * This method is protected by default in order to keep the public
     * api reasonable.  You can provide public methods for those you
     * actually need in User.
     */
    protected List<UserRole> getUserRolesJoinRole(Criteria criteria)
        throws TorqueException
    {
                    if (collUserRoles == null)
        {
            if (isNew())
            {
               collUserRoles = new ArrayList<UserRole>();
            }
            else
            {
                              criteria.add(UserRolePeer.USER_ID, getId());
                              collUserRoles = UserRolePeer.doSelectJoinRole(criteria);
            }
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
                                    criteria.add(UserRolePeer.USER_ID, getId());
                                    if (!lastUserRolesCriteria.equals(criteria))
            {
                collUserRoles = UserRolePeer.doSelectJoinRole(criteria);
            }
        }
        lastUserRolesCriteria = criteria;

        return collUserRoles;
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
              fieldNames.add("Name");
              fieldNames.add("Password");
              fieldNames.add("TrueName");
              fieldNames.add("Team");
              fieldNames.add("Sex");
              fieldNames.add("Birth");
              fieldNames.add("Lastlogindate");
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
          if (name.equals("Name"))
        {
                return getName();
            }
          if (name.equals("Password"))
        {
                return getPassword();
            }
          if (name.equals("TrueName"))
        {
                return getTrueName();
            }
          if (name.equals("Team"))
        {
                return getTeam();
            }
          if (name.equals("Sex"))
        {
                return new Integer(getSex());
            }
          if (name.equals("Birth"))
        {
                return getBirth();
            }
          if (name.equals("Lastlogindate"))
        {
                return getLastlogindate();
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
          if (name.equals("Password"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setPassword((String) value);
                      return true;
        }
          if (name.equals("TrueName"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setTrueName((String) value);
                      return true;
        }
          if (name.equals("Team"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setTeam((String) value);
                      return true;
        }
          if (name.equals("Sex"))
        {
                      if (value == null || ! (Integer.class.isInstance(value)))
            {
                throw new IllegalArgumentException("setByName: value parameter was null or not an Integer object.");
            }
            setSex(((Integer) value).intValue());
                      return true;
        }
          if (name.equals("Birth"))
        {
                      // Object fields can be null
            if (value != null && ! String.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setBirth((String) value);
                      return true;
        }
          if (name.equals("Lastlogindate"))
        {
                      // Object fields can be null
            if (value != null && ! Date.class.isInstance(value))
            {
                throw new IllegalArgumentException("Invalid type of object specified for value in setByName");
            }
            setLastlogindate((Date) value);
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
          if (name.equals(UserPeer.ID))
        {
                return new Integer(getId());
            }
          if (name.equals(UserPeer.NAME))
        {
                return getName();
            }
          if (name.equals(UserPeer.PASSWORD))
        {
                return getPassword();
            }
          if (name.equals(UserPeer.TRUE_NAME))
        {
                return getTrueName();
            }
          if (name.equals(UserPeer.TEAM))
        {
                return getTeam();
            }
          if (name.equals(UserPeer.SEX))
        {
                return new Integer(getSex());
            }
          if (name.equals(UserPeer.BIRTH))
        {
                return getBirth();
            }
          if (name.equals(UserPeer.LASTLOGINDATE))
        {
                return getLastlogindate();
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
        if (UserPeer.ID.equals(name))
        {
            return setByName("Id", value);
        }
        if (UserPeer.NAME.equals(name))
        {
            return setByName("Name", value);
        }
        if (UserPeer.PASSWORD.equals(name))
        {
            return setByName("Password", value);
        }
        if (UserPeer.TRUE_NAME.equals(name))
        {
            return setByName("TrueName", value);
        }
        if (UserPeer.TEAM.equals(name))
        {
            return setByName("Team", value);
        }
        if (UserPeer.SEX.equals(name))
        {
            return setByName("Sex", value);
        }
        if (UserPeer.BIRTH.equals(name))
        {
            return setByName("Birth", value);
        }
        if (UserPeer.LASTLOGINDATE.equals(name))
        {
            return setByName("Lastlogindate", value);
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
                return getName();
            }
              if (pos == 2)
        {
                return getPassword();
            }
              if (pos == 3)
        {
                return getTrueName();
            }
              if (pos == 4)
        {
                return getTeam();
            }
              if (pos == 5)
        {
                return new Integer(getSex());
            }
              if (pos == 6)
        {
                return getBirth();
            }
              if (pos == 7)
        {
                return getLastlogindate();
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
            return setByName("Name", value);
        }
          if (position == 2)
        {
            return setByName("Password", value);
        }
          if (position == 3)
        {
            return setByName("TrueName", value);
        }
          if (position == 4)
        {
            return setByName("Team", value);
        }
          if (position == 5)
        {
            return setByName("Sex", value);
        }
          if (position == 6)
        {
            return setByName("Birth", value);
        }
          if (position == 7)
        {
            return setByName("Lastlogindate", value);
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
          save(UserPeer.DATABASE_NAME);
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
                    UserPeer.doInsert((User) this, con);
                    setNew(false);
                }
                else
                {
                    UserPeer.doUpdate((User) this, con);
                }
                }

                                      
                                    if (collUserRoles != null)
            {
                for (int i = 0; i < collUserRoles.size(); i++)
                {
                    ((UserRole) collUserRoles.get(i)).save(con);
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
        throws TorqueException
    {
            setId(((NumberKey) key).intValue());
        }

    /**
     * Set the PrimaryKey using a String.
     *
     * @param key
     */
    public void setPrimaryKey(String key) throws TorqueException
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
      public User copy() throws TorqueException
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
    public User copy(boolean deepcopy) throws TorqueException
    {
        return copyInto(new User(), deepcopy);
    }
      
      /**
     * Fills the copyObj with the contents of this object.
     * The associated objects are also copied and treated as new objects.
     * @param copyObj the object to fill.
     */
    protected User copyInto(User copyObj) throws TorqueException
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
      protected User copyInto(User copyObj, boolean deepcopy) throws TorqueException
      {
          copyObj.setId(id);
          copyObj.setName(name);
          copyObj.setPassword(password);
          copyObj.setTrueName(trueName);
          copyObj.setTeam(team);
          copyObj.setSex(sex);
          copyObj.setBirth(birth);
          copyObj.setLastlogindate(lastlogindate);
  
                            copyObj.setId( 0);
                                                      
          if (deepcopy) 
        {
                                    
                            
        List<UserRole> vUserRoles = getUserRoles();
                            if (vUserRoles != null)
        {
            for (int i = 0; i < vUserRoles.size(); i++)
            {
                UserRole obj =  vUserRoles.get(i);
                copyObj.addUserRole(obj.copy());
            }
        }
        else
        {
            copyObj.collUserRoles = null;
        }
                          }
          return copyObj;
    }

    /**
     * returns a peer instance associated with this om.  Since Peer classes
     * are not to have any instance attributes, this method returns the
     * same instance for all member of this class. The method could therefore
     * be static, but this would prevent one from overriding the behavior.
     */
    public UserPeer getPeer()
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
        return UserPeer.getTableMap();
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("User:\n");
        str.append("Id = ")
               .append(getId())
             .append("\n");
        str.append("Name = ")
               .append(getName())
             .append("\n");
        str.append("Password = ")
               .append(getPassword())
             .append("\n");
        str.append("TrueName = ")
               .append(getTrueName())
             .append("\n");
        str.append("Team = ")
               .append(getTeam())
             .append("\n");
        str.append("Sex = ")
               .append(getSex())
             .append("\n");
        str.append("Birth = ")
               .append(getBirth())
             .append("\n");
        str.append("Lastlogindate = ")
               .append(getLastlogindate())
             .append("\n");
        return(str.toString());
    }
}
