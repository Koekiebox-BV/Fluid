package com.fluidbpm.ws.client.v1.config;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.config.StoredDataSource;
import com.fluidbpm.program.api.vo.config.StoredDataSourceListing;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class TestStoredDataSource {
    @Test
    public void roundTripConnectionAndPoolSettings() {
        StoredDataSource source = new StoredDataSource(42L);
        source.setAlias("reporting");
        source.setDriverClass("org.postgresql.Driver");
        source.setConnectURI("jdbc:postgresql://localhost/reporting");
        source.setUsername("reports");
        source.setPassword("secret");
        source.setMaxTotal(30);
        source.setDefaultMaxWaitMillis(4500);
        source.setDefaultMaxIdle(5);
        StoredDataSourceListing listing = new StoredDataSourceListing();
        listing.setListing(Collections.singletonList(source));

        StoredDataSource restored = new StoredDataSourceListing(listing.toJsonObject()).getListing().get(0);
        assertEquals(source.getId(), restored.getId());
        assertEquals(source.getAlias(), restored.getAlias());
        assertEquals(source.getDriverClass(), restored.getDriverClass());
        assertEquals(source.getConnectURI(), restored.getConnectURI());
        assertEquals(source.getUsername(), restored.getUsername());
        assertEquals(source.getPassword(), restored.getPassword());
        assertEquals(30, restored.getMaxTotal());
        assertEquals(4500, restored.getDefaultMaxWaitMillis());
        assertEquals(5, restored.getDefaultMaxIdle());
    }

    @Test
    public void omittedPoolSettingsKeepServerDefaults() {
        StoredDataSource source = new StoredDataSource(new JsonObject());
        assertEquals(20, source.getMaxTotal());
        assertEquals(9000, source.getDefaultMaxWaitMillis());
        assertEquals(10, source.getDefaultMaxIdle());
        assertNull(source.getPassword());
    }

    @Test
    public void clientSendsAuthenticatedBatchWithPutAndReadsStoredIds() {
        ConfigurationClient client = new ConfigurationClient("http://localhost", "ticket") {
            @Override
            protected JsonObject putJson(ABaseFluidGSONObject body, String path) {
                assertEquals("v1/configuration/upsert_data_sources", path);
                assertEquals("ticket", body.getServiceTicket());
                StoredDataSourceListing request = new StoredDataSourceListing(body.toJsonObject());
                assertEquals(1, request.getListing().size());
                assertEquals("reporting", request.getListing().get(0).getAlias());
                assertEquals("secret", request.getListing().get(0).getPassword());
                request.getListing().get(0).setId(123L);
                request.getListing().get(0).setPassword(null);
                return request.toJsonObject();
            }
        };
        StoredDataSource source = new StoredDataSource();
        source.setAlias("reporting");
        source.setPassword("secret");
        StoredDataSource saved = client.upsertDataSources(Collections.singletonList(source)).getListing().get(0);
        assertEquals(Long.valueOf(123L), saved.getId());
        assertNull(saved.getPassword());
        assertEquals("secret", source.getPassword());
    }
}
