package shop.chaekmate.core.member.event;

import shop.chaekmate.core.member.dto.response.MemberResponse;

public record MemberCreatedEvent (MemberResponse memberResponse){}
