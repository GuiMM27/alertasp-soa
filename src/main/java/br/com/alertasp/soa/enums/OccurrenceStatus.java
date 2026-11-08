package br.com.alertasp.soa.enums;

import java.util.EnumSet;
import java.util.Set;

public enum OccurrenceStatus {
    REPORTED,
    CONFIRMED,
    IN_PROGRESS,
    RESOLVED;

    public boolean canTransitionTo(OccurrenceStatus nextStatus) {
        if (nextStatus == null || this == nextStatus) {
            return false;
        }

        Set<OccurrenceStatus> allowedTransitions = switch (this) {
            case REPORTED -> EnumSet.of(CONFIRMED, RESOLVED);
            case CONFIRMED -> EnumSet.of(IN_PROGRESS, RESOLVED);
            case IN_PROGRESS -> EnumSet.of(RESOLVED);
            case RESOLVED -> EnumSet.noneOf(OccurrenceStatus.class);
        };

        return allowedTransitions.contains(nextStatus);
    }
}
