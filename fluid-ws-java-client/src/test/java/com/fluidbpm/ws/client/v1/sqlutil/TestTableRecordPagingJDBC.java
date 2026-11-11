package com.fluidbpm.ws.client.v1.sqlutil;

import com.fluidbpm.program.api.util.sql.impl.SQLFormUtil;
import com.fluidbpm.program.api.util.sql.impl.SQLFormDefinitionUtil;
import com.fluidbpm.program.api.vo.form.FormListing;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Verifies JDBC binding, bounded hydration and resource closure without a database. */
public class TestTableRecordPagingJDBC {
    @SuppressWarnings("unchecked") private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T)Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }
    static class JDBC implements InvocationHandler {
        final List<Long> hydrated = new ArrayList<>();
        boolean pageClosed, connectionClosed;
        int opened, closed;
        final boolean empty;
        JDBC(boolean empty) { this.empty=empty; }
        Connection connection() { return proxy(Connection.class,this); }
        @Override public Object invoke(Object p, Method m, Object[] a) {
            switch(m.getName()) {
                case "getMetaData": return proxy(DatabaseMetaData.class,(p2,m2,a2) -> "PostgreSQL");
                case "isClosed": return connectionClosed;
                case "close": connectionClosed=true; return null;
                case "prepareStatement": return statement((String)a[0]);
                default: throw new AssertionError("Unexpected connection call: "+m);
            }
        }
        PreparedStatement statement(String sql) {
            opened++;
            Map<Integer,Number> bindings=new HashMap<>();
            return proxy(PreparedStatement.class,(p,m,a) -> {
                if (m.getName().equals("setLong") || m.getName().equals("setInt")) { bindings.put((Integer)a[0],(Number)a[1]); return null; }
                if (m.getName().equals("close")) { closed++; return null; }
                if (!m.getName().equals("executeQuery")) throw new AssertionError(m);
                Object[][] rows;
                if (sql.contains("Fluid_GetTableFormsCount")) {
                    assertEquals(99L,bindings.get(1).longValue()); assertEquals(7L,bindings.get(2).longValue()); rows=new Object[][]{{10003L}};
                } else if (sql.contains("Fluid_GetTableFormsPage")) {
                    assertEquals(99L,bindings.get(1).longValue()); assertEquals(7L,bindings.get(2).longValue());
                    assertEquals(2,bindings.get(3).intValue()); assertEquals(5000,bindings.get(4).intValue());
                    rows=empty ? new Object[0][] : new Object[][]{{5001L,7L,"first",null,null,0L},{5002L,7L,"second",null,null,0L}};
                } else if (sql.contains("Fluid_GetFormDefinitions")) rows=new Object[][]{{7L,"Adjustment"}};
                else if (sql.contains("Fluid_GetFormDefinitionForFormContainer")) {
                    assertTrue("Close the page before loading fields",pageClosed);
                    hydrated.add(bindings.get(1).longValue()); rows=new Object[][]{{7L}};
                } else if (sql.contains("Fluid_GetFormFieldsForFormContainer")) rows=new Object[0][];
                else throw new AssertionError("Unexpected/unpaged SQL: "+sql);
                int[] index={-1};
                return proxy(ResultSet.class,(r,method,args) -> {
                    switch(method.getName()) {
                        case "next": return ++index[0] < rows.length;
                        case "close": if(sql.contains("Fluid_GetTableFormsPage")) pageClosed=true; return null;
                        case "getLong": return ((Number)rows[index[0]][(Integer)args[0]-1]).longValue();
                        case "getString": return rows[index[0]][(Integer)args[0]-1];
                        case "getDate": return null;
                        default: throw new AssertionError(method);
                    }
                });
            });
        }
    }
    @Test public void onlyHydratesReturnedPageAndClosesResources() {
        SQLFormDefinitionUtil.clearStaticMapping(); JDBC jdbc=new JDBC(false);
        try(SQLFormUtil util=new SQLFormUtil(jdbc.connection())) {
            FormListing page=util.getFormTableFormsPage(99L,true,7,2,5000);
            assertEquals(Integer.valueOf(10003),page.getListingCount());
            assertEquals(Integer.valueOf(5000),page.getListingIndex());
            assertEquals(Integer.valueOf(2500),page.getListingPage());
            assertEquals(2,page.getListing().size());
            assertEquals(Arrays.asList(5001L,5002L),jdbc.hydrated);
        } finally { SQLFormDefinitionUtil.clearStaticMapping(); }
        assertTrue(jdbc.connectionClosed); assertEquals(jdbc.opened,jdbc.closed);
    }
    @Test public void emptyPagePreservesTotalWithoutHydrating() {
        SQLFormDefinitionUtil.clearStaticMapping(); JDBC jdbc=new JDBC(true);
        try(SQLFormUtil util=new SQLFormUtil(jdbc.connection())) {
            FormListing page=util.getFormTableFormsPage(99L,true,7,2,5000);
            assertEquals(Integer.valueOf(10003),page.getListingCount());
            assertTrue(page.getListing().isEmpty()); assertTrue(jdbc.hydrated.isEmpty());
        } finally { SQLFormDefinitionUtil.clearStaticMapping(); }
        assertEquals(jdbc.opened,jdbc.closed);
    }
}
