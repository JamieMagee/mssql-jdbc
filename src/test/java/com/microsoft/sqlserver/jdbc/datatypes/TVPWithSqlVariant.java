/*
 * Microsoft JDBC Driver for SQL Server
 * 
 * Copyright(c) Microsoft Corporation All rights reserved.
 * 
 * This program is made available under the terms of the MIT License. See the LICENSE file in the project root for more information.
 */
package com.microsoft.sqlserver.jdbc.datatypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.testframework.AbstractTest;
import com.microsoft.sqlserver.testframework.sqlType.SqlDate;
import com.microsoft.sqlserver.testframework.Utils;

@RunWith(JUnitPlatform.class)
public class TVPWithSqlVariant extends AbstractTest {

    private static SQLServerConnection conn = null;
    static SQLServerStatement stmt = null;
    static SQLServerResultSet rs = null;
    static SQLServerDataTable tvp = null;
    static String expectecValue1 = "hello";
    static String expectecValue2 = "world";
    static String expectecValue3 = "again";
    private static String tvpName = "numericTVP";
    private static String destTable = "destTvpSqlVariantTable";
    private static String procedureName = "procedureThatCallsTVP";

    /**
     * Test a previous failure regarding to numeric precision. Issue #211
     * 
     * @throws SQLServerException
     */
    @Test
    public void testInt() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        tvp.addRow(12);
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }

        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getInt(1), 12);
            assertEquals(rs.getString(1), "" + 12);
            assertEquals(rs.getObject(1), 12);
        }
    }

    /**
     * 
     * @throws SQLServerException
     */
    @Test
    public void testDate() throws SQLServerException {
        SqlDate sqlDate = new SqlDate();
        Date date = (Date) sqlDate.createdata();
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        tvp.addRow(date);
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getString(1), "" + date); // TODO: GetDate has issues
        }
    }

    @Test
    public void testMoney() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        String[] numeric = createNumericValues();
        tvp.addRow(new BigDecimal(numeric[14]));
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getMoney(1), new BigDecimal(numeric[14]));
        }
    }

    @Test
    public void testsmallInt() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        String[] numeric = createNumericValues();
        tvp.addRow(Short.valueOf(numeric[2]));
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();

        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals("" + rs.getInt(1), numeric[2]);
            // System.out.println(rs.getShort(1)); //does not work says cannot cast integer to short cause it is written as int
        }
    }

    @Test
    public void testBigInt() throws SQLServerException {
        Random r = new Random();
        Date date = new Date(r.nextLong());
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        String[] numeric = createNumericValues();
        tvp.addRow(Long.parseLong(numeric[4]));

        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getLong(1), Long.parseLong(numeric[4]));
        }
    }

    @Test
    public void testBoolean() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        String[] numeric = createNumericValues();
        tvp.addRow(Boolean.parseBoolean(numeric[0]));
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getBoolean(1), Boolean.parseBoolean(numeric[0]));
        }
    }

    @Test
    public void testFloat() throws SQLServerException {
        Random r = new Random();
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        String[] numeric = createNumericValues();
        tvp.addRow(Float.parseFloat(numeric[1]));
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getFloat(1), Float.parseFloat(numeric[1]));
        }
    }

    @Test
    public void testNvarchar() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        String colValue = "س";
        tvp.addRow(colValue);
        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getString(1), colValue);
        }
    }

    @Test
    public void testVarchar8000() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 8000; i++) {
            buffer.append("a");
        }
        String value = buffer.toString();
        tvp.addRow(value);

        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getString(1), value);
        }
    }

    /**
     * Check that we throw proper error message when inserting more than 8000 
     * 
     * @throws SQLServerException
     */
    @Test
    public void testLongVarChar() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 8001; i++) {
            buffer.append("a");
        }
        String value = buffer.toString();
        tvp.addRow(value);

        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        try {
            pstmt.execute();
        }
        catch (SQLServerException e) {
            assertTrue(e.getMessage().contains("sql_variant does not support string values more than 8000"));
        }
        catch (Exception e) {
            // Otherwise fail the test
            fail("Test should have failed! mistakenly inserted string value of more than 8000 in sql-variant");
        }
        finally {
            if (null != pstmt) {
                pstmt.close();
            }
        }
    }

    /**
     * 
     * @throws SQLServerException
     */
    @Test
    public void testDateTime() throws SQLServerException {
        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0");
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        tvp.addRow(timestamp);

        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            assertEquals(rs.getString(1), "" + timestamp);
            // System.out.println(rs.getDateTime(1));// TODO does not work
        }
    }

    /**
     * 
     * @throws SQLServerException
     */
     @Test //TODO We need to check this later. Right now sending null with TVP is not supported 
    public void testNull() throws SQLServerException {
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        try{
        tvp.addRow((Date) null);
        }catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Sending null value with column"));
        }

        SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement) connection
                .prepareStatement("INSERT INTO " + destTable + " select * from ? ;");
        pstmt.setStructured(1, tvpName, tvp);
        pstmt.execute();
        if (null != pstmt) {
            pstmt.close();
        }
        rs = (SQLServerResultSet) stmt.executeQuery("SELECT * FROM " + destTable);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    /**
     * 
     * @throws SQLServerException
     */
    @Test
    public void testIntStoredProcedure() throws SQLServerException {
        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0");
        final String sql = "{call " + procedureName + "(?)}";
        tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("c1", microsoft.sql.Types.SQL_VARIANT);
        tvp.addRow(timestamp);
        SQLServerCallableStatement Cstatement = (SQLServerCallableStatement) connection.prepareCall(sql);
        Cstatement.setStructured(1, tvpName, tvp);
        Cstatement.execute();
        rs = (SQLServerResultSet) stmt.executeQuery("select * from " + destTable);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        if (null != Cstatement) {
            Cstatement.close();
        }
    }

    private static String[] createNumericValues() {
        Boolean C1_BIT;
        Short C2_TINYINT;
        Short C3_SMALLINT;
        Integer C4_INT;
        Long C5_BIGINT;
        Double C6_FLOAT;
        Double C7_FLOAT;
        Float C8_REAL;
        BigDecimal C9_DECIMAL;
        BigDecimal C10_DECIMAL;
        BigDecimal C11_NUMERIC;

        boolean nullable = false;
        RandomData.returnNull = nullable;
        C1_BIT = RandomData.generateBoolean(nullable);
        C2_TINYINT = RandomData.generateTinyint(nullable);
        C3_SMALLINT = RandomData.generateSmallint(nullable);
        C4_INT = RandomData.generateInt(nullable);
        C5_BIGINT = RandomData.generateLong(nullable);
        C6_FLOAT = RandomData.generateFloat(24, nullable);
        C7_FLOAT = RandomData.generateFloat(53, nullable);
        C8_REAL = RandomData.generateReal(nullable);
        C9_DECIMAL = RandomData.generateDecimalNumeric(18, 0, nullable);
        C10_DECIMAL = RandomData.generateDecimalNumeric(10, 5, nullable);
        C11_NUMERIC = RandomData.generateDecimalNumeric(18, 0, nullable);
        BigDecimal C12_NUMERIC = RandomData.generateDecimalNumeric(8, 2, nullable);
        BigDecimal C13_smallMoney = RandomData.generateSmallMoney(nullable);
        BigDecimal C14_money = RandomData.generateMoney(nullable);
        BigDecimal C15_decimal = RandomData.generateDecimalNumeric(28, 4, nullable);
        BigDecimal C16_numeric = RandomData.generateDecimalNumeric(28, 4, nullable);

        String[] numericValues = {"" + C1_BIT, "" + C2_TINYINT, "" + C3_SMALLINT, "" + C4_INT, "" + C5_BIGINT, "" + C6_FLOAT, "" + C7_FLOAT,
                "" + C8_REAL, "" + C9_DECIMAL, "" + C10_DECIMAL, "" + C11_NUMERIC, "" + C12_NUMERIC, "" + C13_smallMoney, "" + C14_money,
                "" + C15_decimal, "" + C16_numeric};

        if (RandomData.returnZero && !RandomData.returnNull) {
            C10_DECIMAL = new BigDecimal(0);
            C12_NUMERIC = new BigDecimal(0);
            C13_smallMoney = new BigDecimal(0);
            C14_money = new BigDecimal(0);
            C15_decimal = new BigDecimal(0);
            C16_numeric = new BigDecimal(0);
        }
        return numericValues;
    }

    @BeforeEach
    private void testSetup() throws SQLException {
        conn = (SQLServerConnection) DriverManager.getConnection(connectionString + ";sendStringParametersAsUnicode=true;");
        stmt = (SQLServerStatement) conn.createStatement();

        Utils.dropProcedureIfExists(procedureName, stmt);
        Utils.dropTableIfExists(destTable, stmt);
        dropTVPS();

        createTVPS();
        createTables();
        createPreocedure();
    }

    private static void dropTVPS() throws SQLException {
        stmt.executeUpdate("IF EXISTS (SELECT * FROM sys.types WHERE is_table_type = 1 AND name = '" + tvpName + "') " + " drop type " + tvpName);
    }

    private static void createPreocedure() throws SQLException {
        String sql = "CREATE PROCEDURE " + procedureName + " @InputData " + tvpName + " READONLY " + " AS " + " BEGIN " + " INSERT INTO " + destTable
                + " SELECT * FROM @InputData" + " END";

        stmt.execute(sql);
    }

    private void createTables() throws SQLException {
        String sql = "create table " + destTable + " (c1 sql_variant null);";
        stmt.execute(sql);
    }

    private void createTVPS() throws SQLException {
        String TVPCreateCmd = "CREATE TYPE " + tvpName + " as table (c1 sql_variant null)";
        stmt.executeUpdate(TVPCreateCmd);
    }

    @AfterEach
    private void terminateVariation() throws SQLException {
        if (null != conn) {
            conn.close();
        }
        if (null != stmt) {
            stmt.close();
        }
        if (null != rs) {
            rs.close();
        }
        if (null != tvp) {
            tvp.clear();
        }
    }

}