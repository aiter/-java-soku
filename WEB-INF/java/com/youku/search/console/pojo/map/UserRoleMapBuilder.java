package com.youku.search.console.pojo.map;

import java.util.Date;
import java.math.BigDecimal;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.InheritanceMap;

/**
 * ???��????����
 *
  *  This class was autogenerated by Torque on:
  *
  * [Fri Jun 12 09:58:42 CST 2009]
  *
  */
public class UserRoleMapBuilder implements MapBuilder
{
    /**
     * The name of this class
     */
    public static final String CLASS_NAME =
        "com.youku.search.console.pojo.map.UserRoleMapBuilder";

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
        dbMap = Torque.getDatabaseMap("searchconsole");

        dbMap.addTable("user_role");
        TableMap tMap = dbMap.getTable("user_role");
        tMap.setJavaName("UserRole");
        tMap.setOMClass( com.youku.search.console.pojo.UserRole.class );
        tMap.setPeerClass( com.youku.search.console.pojo.UserRolePeer.class );
        tMap.setDescription("???��????����");
	    tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        tMap.setPrimaryKeyMethodInfo("user_role");

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
      		cMap.setDescription("?��?��id??��???");
        		cMap.setInheritance("false");
                cMap.setSize( 11 );
   	              cMap.setPosition(1);
          tMap.addColumn(cMap);
    // ------------- Column: user_id --------------------
        cMap = new ColumnMap( "user_id", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(true);
        cMap.setJavaName( "UserId" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("???��id");
        		cMap.setInheritance("false");
                cMap.setSize( 11 );
   	      	    cMap.setForeignKey("user", "id"); 
            cMap.setPosition(2);
          tMap.addColumn(cMap);
    // ------------- Column: role_id --------------------
        cMap = new ColumnMap( "role_id", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(true);
        cMap.setJavaName( "RoleId" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("????id");
        		cMap.setInheritance("false");
                cMap.setSize( 11 );
   	      	    cMap.setForeignKey("role", "id"); 
            cMap.setPosition(3);
          tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
