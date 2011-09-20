package com.youku.soku.manage.torque.map;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.TableMap;

/**
 * Permission Table
 *
  *  This class was autogenerated by Torque on:
  *
  * [Wed Mar 10 16:13:12 CST 2010]
  *
  */
public class AuthPermissionMapBuilder implements MapBuilder
{
    /**
     * The name of this class
     */
    public static final String CLASS_NAME =
        "com.youku.soku.manage.torque.map.AuthPermissionMapBuilder";

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

        dbMap.addTable("auth_permission");
        TableMap tMap = dbMap.getTable("auth_permission");
        tMap.setJavaName("AuthPermission");
        tMap.setOMClass( com.youku.soku.manage.torque.AuthPermission.class );
        tMap.setPeerClass( com.youku.soku.manage.torque.AuthPermissionPeer.class );
        tMap.setDescription("Permission Table");
        tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        tMap.setPrimaryKeyMethodInfo("auth_permission");

        ColumnMap cMap = null;


  // ------------- Column: id --------------------
        cMap = new ColumnMap( "id", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(true);
        cMap.setNotNull(true);
        cMap.setJavaName( "Id" );
        cMap.setAutoIncrement(true);
        cMap.setProtected(false);
        cMap.setDescription("Permission Id");
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
        cMap.setDescription("Permission Name");
        cMap.setInheritance("false");
        cMap.setSize( 200 );
        cMap.setPosition(2);
        tMap.addColumn(cMap);
  // ------------- Column: code_name --------------------
        cMap = new ColumnMap( "code_name", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName( "CodeName" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("Permission Code");
        cMap.setInheritance("false");
        cMap.setSize( 50 );
        cMap.setPosition(3);
        tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
