package me.gg.pinit.infra.events.dto;


public record MemberCreatedPayload(Long memberId, String nickname) {
}
