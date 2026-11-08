package br.com.alertasp.soa.service;

import br.com.alertasp.soa.enums.OccurrenceStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OccurrenceStatusTest {

    @Test
    void reportedCanBecomeConfirmedOrResolved() {
        assertTrue(OccurrenceStatus.REPORTED.canTransitionTo(OccurrenceStatus.CONFIRMED));
        assertTrue(OccurrenceStatus.REPORTED.canTransitionTo(OccurrenceStatus.RESOLVED));
    }

    @Test
    void reportedCannotSkipDirectlyToInProgress() {
        assertFalse(OccurrenceStatus.REPORTED.canTransitionTo(OccurrenceStatus.IN_PROGRESS));
    }

    @Test
    void resolvedIsFinal() {
        assertFalse(OccurrenceStatus.RESOLVED.canTransitionTo(OccurrenceStatus.REPORTED));
        assertFalse(OccurrenceStatus.RESOLVED.canTransitionTo(OccurrenceStatus.CONFIRMED));
        assertFalse(OccurrenceStatus.RESOLVED.canTransitionTo(OccurrenceStatus.IN_PROGRESS));
    }
}
