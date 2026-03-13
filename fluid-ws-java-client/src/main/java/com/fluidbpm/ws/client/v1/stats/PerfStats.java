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
import lombok.Getter;
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

    public enum Group {
        RestBusinessWS,
        ASN1Der_Parsing,
        ASN1Der_NetworkAndLatency,
        ASN1Der_ServerSide,
        ASN1Der_Business
    }

    @Getter
    public enum Label {
        // ASN1DER - Parsing
        Asn1DerMapper_InitSeq(Group.ASN1Der_Parsing),
        Asn1DerMapper_DoesHandlerQualify(Group.ASN1Der_Parsing),
        Asn1DerMapper_HandleMessage(Group.ASN1Der_Parsing),
        Asn1DerMapper_BTEncode(Group.ASN1Der_Parsing),
        Asn1DerMapper_BTDecode(Group.ASN1Der_Parsing),
        // ASN1DER - Network and Latency:
        Asn1Der_RoundRobin(Group.ASN1Der_NetworkAndLatency),
        Asn1Der_Latency(Group.ASN1Der_NetworkAndLatency),
        Asn1Der_BytesSent(Group.ASN1Der_NetworkAndLatency),
        Asn1Der_BytesReceive(Group.ASN1Der_NetworkAndLatency),
        // ASN1DER - Server Side:
        Asn1Der_ServerAppProcessDuration(Group.ASN1Der_ServerSide),
        Asn1Der_ServerAppEncodeDuration(Group.ASN1Der_ServerSide),
        Asn1Der_ServerAppDecodeDuration(Group.ASN1Der_ServerSide),
        // ASN1DER - Business Methods
        Asn1Der_DeleteAttachment(Group.ASN1Der_Business),
        Asn1Der_ListAttachment(Group.ASN1Der_Business),
        Asn1Der_CreateAttachment(Group.ASN1Der_Business),
        Asn1Der_CreateFluidItem(Group.ASN1Der_Business),
        Asn1Der_CreateFormContainer(Group.ASN1Der_Business),
        Asn1Der_GetFluidItemByForm(Group.ASN1Der_Business),
        Asn1Der_SendOn(Group.ASN1Der_Business),
        Asn1Der_ClearPI(Group.ASN1Der_Business),
        Asn1Der_LockForm(Group.ASN1Der_Business),
        Asn1Der_UpdateForm(Group.ASN1Der_Business),
        Asn1Der_FormHistory(Group.ASN1Der_Business),
        Asn1Der_GetTableForms(Group.ASN1Der_Business),
        Asn1Der_CreateTableRecord(Group.ASN1Der_Business),
        Asn1Der_ExecCustomWebAction(Group.ASN1Der_Business),
        // REST
        Rest_CreateFormContainer(Group.RestBusinessWS),
        Rest_CreateFluidItem(Group.RestBusinessWS),
        Rest_GetFluidItemByForm(Group.RestBusinessWS);

        private final Group group;
        private Label(Group group) {
            this.group = group;
        }
    }

    public static void reset() {
        stats.invalidateAll();
        for (Label label : Label.values()) {
            stats.put(label, new AtomicLong(0));
            statsCallCounter.put(label, new AtomicLong(0));
        }
    }

    public static void printOutcomes() {
        class Outcome {
            private final Label label;
            private final long total;
            private final long calls;

            Outcome(Label label, long total, long calls) {
                this.label = label;
                this.total = total;
                this.calls = calls;
            }
        }

        java.util.Map<Group, java.util.List<Outcome>> outcomesByGroup = new java.util.EnumMap<>(Group.class);
        java.util.Map<Group, Long> groupCallTotals = new java.util.EnumMap<>(Group.class);
        java.util.Map<Group, Long> groupTimeTotals = new java.util.EnumMap<>(Group.class);
        for (Group group : Group.values()) {
            outcomesByGroup.put(group, new java.util.ArrayList<>());
            groupCallTotals.put(group, 0L);
            groupTimeTotals.put(group, 0L);
        }
        int maxLabelLength = 0;
        for (Map.Entry<Label, AtomicLong> entry : stats.asMap().entrySet()) {
            long total = entry.getValue().get();
            if (total == 0) continue;

            Label label = entry.getKey();
            AtomicLong callsCounter = statsCallCounter.getIfPresent(label);
            long calls = callsCounter == null ? 0 : callsCounter.get();

            String labelText = String.valueOf(label);
            if (labelText.length() > maxLabelLength) maxLabelLength = labelText.length();

            java.util.List<Outcome> groupOutcomes = outcomesByGroup.get(label.getGroup());
            if (groupOutcomes != null) {
                groupOutcomes.add(new Outcome(label, total, calls));
                groupCallTotals.put(label.getGroup(), groupCallTotals.get(label.getGroup()) + calls);
                groupTimeTotals.put(label.getGroup(), groupTimeTotals.get(label.getGroup()) + total);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n===> PERFORMANCE STATS <===\n\n");
        java.util.List<Group> orderedGroups = new java.util.ArrayList<>(outcomesByGroup.keySet());
        orderedGroups.removeIf(group -> outcomesByGroup.get(group).isEmpty());
        orderedGroups.sort((left, right) -> {
            long leftAvg = groupCallTotals.get(left) == 0 ? 0 : groupTimeTotals.get(left) / groupCallTotals.get(left);
            long rightAvg = groupCallTotals.get(right) == 0 ? 0 : groupTimeTotals.get(right) / groupCallTotals.get(right);
            int compare = Long.compare(rightAvg, leftAvg);
            if (compare != 0) return compare;
            compare = Long.compare(groupTimeTotals.get(right), groupTimeTotals.get(left));
            if (compare != 0) return compare;
            return Integer.compare(left.ordinal(), right.ordinal());
        });

        for (Group group : orderedGroups) {
            java.util.List<Outcome> outcomes = outcomesByGroup.get(group);
            if (outcomes.isEmpty()) continue;

            sb.append(group).append(":\n");
            sb.append("========================\n");
            outcomes.sort((left, right) -> Long.compare(right.total, left.total));
            for (Outcome outcome : outcomes) {
                String labelText = String.valueOf(outcome.label);
                long total = outcome.total;
                long timesCalled = outcome.calls;
                long avg = timesCalled == 0 ? 0 : (total / timesCalled);

                switch (outcome.label) {
                    case Asn1Der_BytesSent:
                    case Asn1Der_BytesReceive:
                        sb.append(String.format(
                                "%-" + maxLabelLength + "s : %d bytes avg. after being called '%s' times. Total %d KB's.%n",
                                labelText,
                                avg,
                                timesCalled,
                                total
                        ));
                        break;
                    default:
                        sb.append(String.format(
                                "%-" + maxLabelLength + "s : %dms avg. after being called '%s' times. Total %dms.%n",
                                labelText,
                                avg,
                                timesCalled,
                                total % 1024//Kilobytes.
                        ));
                }
            }
            sb.append('\n');
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
