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
 * ???��????????
 *
  *  This class was autogenerated by Torque on:
  *
  * [Wed Nov 24 17:59:18 CST 2010]
  *
  */
public class SearchNumMapBuilder implements MapBuilder
{
    /**
     * The name of this class
     */
    public static final String CLASS_NAME =
        "com.youku.search.console.pojo.map.SearchNumMapBuilder";

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
        dbMap = Torque.getDatabaseMap("searchteleplay");

        dbMap.addTable("search_num");
        TableMap tMap = dbMap.getTable("search_num");
        tMap.setJavaName("SearchNum");
        tMap.setOMClass( com.youku.search.console.pojo.SearchNum.class );
        tMap.setPeerClass( com.youku.search.console.pojo.SearchNumPeer.class );
        tMap.setDescription("???��????????");
	    tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        tMap.setPrimaryKeyMethodInfo("search_num");

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
    // ------------- Column: words --------------------
        cMap = new ColumnMap( "words", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(true);
        cMap.setJavaName( "Words" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("???��??");
        		cMap.setInheritance("false");
                cMap.setSize( 255 );
   	              cMap.setPosition(2);
          tMap.addColumn(cMap);
    // ------------- Column: num --------------------
        cMap = new ColumnMap( "num", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(true);
        cMap.setJavaName( "Num" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("????");
      		cMap.setDefault("1");
      		cMap.setInheritance("false");
                cMap.setSize( 4 );
   	              cMap.setPosition(3);
          tMap.addColumn(cMap);
    // ------------- Column: status --------------------
        cMap = new ColumnMap( "status", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(false);
        cMap.setJavaName( "Status" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
        		cMap.setDefault("0");
      		cMap.setInheritance("false");
                cMap.setSize( 1 );
   	              cMap.setPosition(4);
          tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
