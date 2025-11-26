package shop.chaekmate.core.order.dto.response;

import shop.chaekmate.core.order.entity.OrderedBook;

public record OrderedBookHistoryResponse(
        Long orderedBookId,
        Long bookId,
        String bookTitle,
        Integer quantity,
        Integer finalUnitPrice,
        Long orderId,
        Integer originalPrice,
        Integer salesPrice,
        Integer discountPrice,
        Long wrapperId,
        Integer wrapperPrice,
        Long issuedCouponId,
        Integer couponDiscount,
        Integer pointUsed,
        String unitStatus,
        Long totalPrice
) {
    public static OrderedBookHistoryResponse from(OrderedBook ob) {

        Long wrapperId = null;
        Integer wrapperPrice = null;
        if (ob.getWrapper() != null) {
            wrapperId = ob.getWrapper().getId();
            wrapperPrice = ob.getWrapper().getPrice();
        }

        Long issuedCouponId = null;
        Integer couponDiscount = null;
        if (ob.getIssuedCouponId() != null) {
            issuedCouponId = ob.getIssuedCouponId();
            couponDiscount = ob.getCouponDiscount();
        }

        Integer pointUsed = ob.getPointUsed() != null ? ob.getPointUsed() : 0;

        return new OrderedBookHistoryResponse(
                ob.getId(),
                ob.getBook().getId(),
                ob.getBook().getTitle(),
                ob.getQuantity(),
                ob.getFinalUnitPrice(),
                ob.getOrder().getId(),
                ob.getOriginalPrice(),
                ob.getSalesPrice(),
                ob.getDiscountPrice(),
                wrapperId,
                wrapperPrice,
                issuedCouponId,
                couponDiscount,
                pointUsed,
                ob.getUnitStatus().name(),
                ob.getTotalPrice()
        );
    }
}
