package com.youku.soku.manage.torque.map;

import java.util.Date;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.TableMap;

/**
 * User Table
 *
  *  This class was autogenerated by Torque on:
  *
  * [Fri Aug 27 18:41:14 CST 2010]
  *
  */
public class UserMapBuilder implements MapBuilder
{
    /**
     * The name of this class
     */
    public static final String CLASS_NAME =
        "com.youku.soku.manage.torque.map.UserMapBuilder";

    /**
     * The database map.
     */
    private DatabaseMap dbMap = null;

    /**
     * Tells us if this DatabaseMapBuilder is built so that we
     * don't have to re-build it every time.
     *
     * @return true if this DatabaseMapBuilder is built
     */
    public boolean isBuilt()
    {
        return (dbMap != null);
    }

    /**
     * Gets the databasemap this map builder built.
     *
     * @return the databasemap
     */
    public DatabaseMap getDatabaseMap()
    {
        return this.dbMap;
    }

    /**
     * The doBuild() method builds the DatabaseMap
     *
     * @throws TorqueException
     */
    public synchronized void doBuild() throws TorqueException
    {
        if ( isBuilt() ) {
            return;
        }
        dbMap = Torque.getDatabaseMap("soku");

        dbMap.addTable("user");
        TableMap tMap = dbMap.getTable("user");
        tMap.setJavaName("User");
        tMap.setOMClass( com.youku.soku.manage.torque.User.class );
        tMap.setPeerClass( com.youku.soku.manage.torque.UserPeer.class );
        tMap.setDescription("User Table");
        tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        tMap.setPrimaryKeyMethodInfo("user");

        ColumnMap cMap = null;


  // ------------- Column: user_id --------------------
        cMap = new ColumnMap( "user_id", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(true);
        cMap.setNotNull(true);
        cMap.setJavaName( "UserId" );
        cMap.setAutoIncrement(true);
        cMap.setProtected(false);
        cMap.setDescription("User Id");
        cMap.setInheritance("false");
        cMap.setPosition(1);
        tMap.addColumn(cMap);
  // ------------- Column: name --------------------
        cMap = new ColumnMap( "name", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName( "Name" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("User Name");
        cMap.setInheritance("false");
        cMap.setSize( 128 );
        cMap.setPosition(2);
        tMap.addColumn(cMap);
  // ------------- Column: password --------------------
        cMap = new ColumnMap( "password", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName( "Password" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("User Password");
        cMap.setInheritance("false");
        cMap.setSize( 80 );
        cMap.setPosition(3);
        tMap.addColumn(cMap);
  // ------------- Column: actual_name --------------------
        cMap = new ColumnMap( "actual_name", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "ActualName" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("User's actual Name");
        cMap.setInheritance("false");
        cMap.setSize( 20 );
        cMap.setPosition(4);
        tMap.addColumn(cMap);
  // ------------- Column: email --------------------
        cMap = new ColumnMap( "email", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "Email" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("User's email");
        cMap.setInheritance("false");
        cMap.setSize( 128 );
        cMap.setPosition(5);
        tMap.addColumn(cMap);
  // ------------- Column: is_active --------------------
        cMap = new ColumnMap( "is_active", tMap);
        cMap.setType( new Byte((byte)0) );
        cMap.setTorqueType( "TINYINT" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "IsActive" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("Flag to idetify the user state");
        cMap.setInheritance("false");
        cMap.setPosition(6);
        tMap.addColumn(cMap);
  // ------------- Column: is_shield_system_user --------------------
        cMap = new ColumnMap( "is_shield_system_user", tMap);
        cMap.setType( new Byte((byte)0) );
        cMap.setTorqueType( "TINYINT" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "IsShieldSystemUser" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("Flag to idetify the user of shield system");
        cMap.setInheritance("false");
        cMap.setPosition(7);
        tMap.addColumn(cMap);
  // ------------- Column: is_super_user --------------------
        cMap = new ColumnMap( "is_super_user", tMap);
        cMap.setType( new Byte((byte)0) );
        cMap.setTorqueType( "TINYINT" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "IsSuperUser" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("Flag to identify the user's permission");
        cMap.setInheritance("false");
        cMap.setPosition(8);
        tMap.addColumn(cMap);
  // ------------- Column: date_joined --------------------
        cMap = new ColumnMap( "date_joined", tMap);
        cMap.setType( new Date() );
        cMap.setTorqueType( "DATE" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName( "DateJoined" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("Record create date");
        cMap.setInheritance("false");
        cMap.setPosition(9);
        tMap.addColumn(cMap);
  // ------------- Column: last_login --------------------
        cMap = new ColumnMap( "last_login", tMap);
        cMap.setType( new Date() );
        cMap.setTorqueType( "DATE" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "LastLogin" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("User last login date");
        cMap.setInheritance("false");
        cMap.setPosition(10);
        tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}