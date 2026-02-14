/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2027] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.ws.client.v1.stats;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A utility class that provides methods to collect, manage, and log performance statistics
 * using a caching mechanism with automatic eviction of entries after a specified duration.
 */
@Log
public class PerfStats {
    public static final int KEEP_FOR_MINUTES = 5;

    private static Cache<Label, AtomicLong> stats = CacheBuilder.newBuilder()
            .expireAfterWrite(KEEP_FOR_MINUTES, TimeUnit.MINUTES)
            .build();
    private static Cache<Label, AtomicLong> statsCallCounter = CacheBuilder.newBuilder()
            .expireAfterWrite(KEEP_FOR_MINUTES, TimeUnit.MINUTES)
            .build();
    private static Cache<String, Long> startStopTime = CacheBuilder.newBuilder()
            .expireAfterWrite(KEEP_FOR_MINUTES, TimeUnit.MINUTES)
            .build();

    public static boolean ENABLED = true;

    public enum Label {
        Asn1DerParsing,
        Asn1DerWebSocketReqRspRaw,
        Asn1DerWebSocketReqRspSerialized,
        // ASN1DER
        Asn1DerCreateAttachment,
        Asn1DerCreateFluidItem,
        Asn1DerCreateFormContainer,
        Asn1DerGetFluidItemByForm,
        // REST
        RestCreateFormContainer,
        RestCreateFluidItem
    }

    public static void reset() {
        stats.invalidateAll();
        for (Label label : Label.values()) {
            stats.put(label, new AtomicLong(0));
            statsCallCounter.put(label, new AtomicLong(0));
        }
    }

    public static void printOutcomes() {
        int maxLabelLength = 0;
        for (Map.Entry<Label, AtomicLong> entry : stats.asMap().entrySet()) {
            String labelText = String.valueOf(entry.getKey());
            if (labelText.length() > maxLabelLength) {
                maxLabelLength = labelText.length();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n===> PERFORMANCE STATS <===\n\n");
        for (Map.Entry<Label, AtomicLong> entry : stats.asMap().entrySet()) {
            String labelText = String.valueOf(entry.getKey());
            long val = entry.getValue().get();
            if (val == 0) continue;

            long timesCalled = statsCallCounter.getIfPresent(entry.getKey()).get();

            sb.append(String.format(
                    "%-" + maxLabelLength + "s : %dms avg. after being called '%s' times. Total %dms.%n",
                    labelText,
                    (val / timesCalled),
                    timesCalled,
                    val
            ));
        }
        sb.append("\n===> END <===\n\n");

        log.info(sb.toString());
    }

    public static void increment(Label label, long addition) {
        if (!ENABLED) return;
        increment(stats.getIfPresent(label), addition);
        increment(statsCallCounter.getIfPresent(label), 1);
    }

    public static Long totalFor(Label label) {
        return stats.getIfPresent(label).get();
    }

    public static String timedStart(String uuid) {
        if (!ENABLED) return null;
        startStopTime.put(uuid, System.currentTimeMillis());
        return uuid;
    }

    public static String timedStart() {
        if (!ENABLED) return null;
        String uuid = java.util.UUID.randomUUID().toString();
        return timedStart(uuid);
    }

    public static void timedStop(Label label, String ref) {
        if (!ENABLED) return;

        Long startTime = startStopTime.getIfPresent(ref);
        if (startTime == null) return;
        increment(label, System.currentTimeMillis() - startTime);
        startStopTime.invalidate(ref);
    }

    private static void increment(AtomicLong counter, long addition) {
        if (counter == null) return;
        long existing = counter.get();
        counter.set(existing + addition);
    }
}
