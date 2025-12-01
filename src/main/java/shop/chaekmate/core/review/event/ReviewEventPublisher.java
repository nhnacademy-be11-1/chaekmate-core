package shop.chaekmate.core.review.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.event.MemberCreatedEvent;
import shop.chaekmate.core.review.dto.response.CreateReviewResponse;
import shop.chaekmate.core.review.dto.response.ReviewImageResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishTextReviewCreate(CreateReviewResponse response) {
        log.info("[TEXT 이벤트] 텍스트 리뷰 이벤트 발행 - 회원ID: {}, 로그인ID: {}", response.id(), response.memberId());
        publisher.publishEvent(new TextReviewCreatedEvent(response));
        log.info("[회원 이벤트] 회원가입 이벤트 발행 완료 - 회원ID: {}", response.id());
    }

    public void publishImageReviewCreate(ReviewImageResponse response) {
        log.info("[IMAGE 이벤트] 이미지 리뷰 이벤트 발행 - ReviewID: {}", response.reviewImageId());
        publisher.publishEvent(new ImageReviewCreatedEvent(response));
        log.info("[IMAGE 이벤트] 이미지 리뷰 이벤트 발행 완료 - ReviewID: {}", response.reviewImageId());
    }
}
