package com.youku.soku.manage.questionnaire.orm.map;

import java.util.Date;
import java.math.BigDecimal;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.DB;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.InheritanceMap;

/**
 * 问题
 *
  *  This class was autogenerated by Torque on:
  *
  * [Tue Jul 05 22:39:54 CST 2011]
  *
  */
public class QuestionMapBuilder implements MapBuilder
{
    /**
     * The name of this class
     */
    public static final String CLASS_NAME =
        "com.youku.soku.manage.questionnaire.orm.map.QuestionMapBuilder";

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
        dbMap = Torque.getDatabaseMap("survey");

        dbMap.addTable("question");
        TableMap tMap = dbMap.getTable("question");
        tMap.setJavaName("Question");
        tMap.setOMClass( com.youku.soku.manage.questionnaire.orm.Question.class );
        tMap.setPeerClass( com.youku.soku.manage.questionnaire.orm.QuestionPeer.class );
        tMap.setDescription("问题");
        tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        DB dbAdapter = Torque.getDB("survey");
        if (dbAdapter.getIDMethodType().equals(DB.SEQUENCE))
        {
            tMap.setPrimaryKeyMethodInfo("question_SEQ");
        }
        else if (dbAdapter.getIDMethodType().equals(DB.AUTO_INCREMENT))
        {
            tMap.setPrimaryKeyMethodInfo("question");
        }

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
        cMap.setDescription("Id, 自增");
        cMap.setInheritance("false");
        cMap.setSize( 11 );
        cMap.setPosition(1);
        tMap.addColumn(cMap);
  // ------------- Column: content --------------------
        cMap = new ColumnMap( "content", tMap);
        cMap.setType( "" );
        cMap.setTorqueType( "VARCHAR" );
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName( "Content" );
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("问题描述");
        cMap.setInheritance("false");
        cMap.setSize( 500 );
        cMap.setPosition(2);
        tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
