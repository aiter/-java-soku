package com.youku.soku.top.mapping.map;

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
 * 关键词类型
 *
  *  This class was autogenerated by Torque on:
  *
  * [Mon May 02 14:40:09 CST 2011]
  *
  */
public class TypeWordsMapBuilder implements MapBuilder
{
    /**
     * The name of this class
     */
    public static final String CLASS_NAME =
        "com.youku.soku.top.mapping.map.TypeWordsMapBuilder";

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
        dbMap = Torque.getDatabaseMap("new_soku_top");

        dbMap.addTable("type_words");
        TableMap tMap = dbMap.getTable("type_words");
        tMap.setJavaName("TypeWords");
        tMap.setOMClass( com.youku.soku.top.mapping.TypeWords.class );
        tMap.setPeerClass( com.youku.soku.top.mapping.TypeWordsPeer.class );
        tMap.setDescription("关键词类型");
	    tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        tMap.setPrimaryKeyMethodInfo("type_words");

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
      		cMap.setDescription("Id，自增");
        		cMap.setInheritance("false");
                cMap.setSize( 11 );
   	              cMap.setPosition(1);
          tMap.addColumn(cMap);
    // ------------- Column: programme_id --------------------
        cMap = new ColumnMap( "programme_id", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(false);
        cMap.setJavaName( "ProgrammeId" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("节目ID");
        		cMap.setInheritance("false");
                cMap.setSize( 11 );
   	              cMap.setPosition(2);
          tMap.addColumn(cMap);
    // ------------- Column: keyword --------------------
        cMap = new ColumnMap( "keyword", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(true);
        cMap.setJavaName( "Keyword" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("关键词");
        		cMap.setInheritance("false");
                cMap.setSize( 255 );
   	              cMap.setPosition(3);
          tMap.addColumn(cMap);
    // ------------- Column: cate --------------------
        cMap = new ColumnMap( "cate", tMap);
        cMap.setType( new Integer(0) );
        cMap.setTorqueType( "INTEGER" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(true);
        cMap.setJavaName( "Cate" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("分类");
        		cMap.setInheritance("false");
                cMap.setSize( 11 );
   	              cMap.setPosition(4);
          tMap.addColumn(cMap);
    // ------------- Column: pic --------------------
        cMap = new ColumnMap( "pic", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(false);
        cMap.setJavaName( "Pic" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("图片");
        		cMap.setInheritance("false");
                cMap.setSize( 255 );
   	              cMap.setPosition(5);
          tMap.addColumn(cMap);
    // ------------- Column: state --------------------
        cMap = new ColumnMap( "state", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(false);
        cMap.setJavaName( "State" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("状态");
      		cMap.setDefault("uncheck");
      		cMap.setInheritance("false");
                cMap.setSize( 50 );
   	              cMap.setPosition(6);
          tMap.addColumn(cMap);
    // ------------- Column: checker --------------------
        cMap = new ColumnMap( "checker", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(false);
        cMap.setJavaName( "Checker" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("最后审核人");
        		cMap.setInheritance("false");
                cMap.setSize( 255 );
   	              cMap.setPosition(7);
          tMap.addColumn(cMap);
    // ------------- Column: create_date --------------------
        cMap = new ColumnMap( "create_date", tMap);
        cMap.setType( new Date() );
        cMap.setTorqueType( "TIMESTAMP" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
  		cMap.setNotNull(false);
        cMap.setJavaName( "CreateDate" );
        cMap.setAutoIncrement(false);
  		cMap.setProtected(false);
      		cMap.setDescription("创建时间");
        		cMap.setInheritance("false");
                    cMap.setPosition(8);
          tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
