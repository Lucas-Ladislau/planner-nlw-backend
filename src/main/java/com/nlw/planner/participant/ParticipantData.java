package com.nlw.planner.participant;

import java.util.UUID;

public record ParticipantData(UUID participantId, String name, String email, Boolean isConfirmedd) {
}
