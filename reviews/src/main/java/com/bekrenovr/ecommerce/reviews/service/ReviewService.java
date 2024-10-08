package com.bekrenovr.ecommerce.reviews.service;

import com.bekrenovr.ecommerce.common.exception.EcommerceApplicationException;
import com.bekrenovr.ecommerce.common.security.AuthenticatedUser;
import com.bekrenovr.ecommerce.common.security.AuthenticationUtil;
import com.bekrenovr.ecommerce.common.security.Role;
import com.bekrenovr.ecommerce.common.security.SecurityConstants;
import com.bekrenovr.ecommerce.common.util.PageUtil;
import com.bekrenovr.ecommerce.common.util.RequestUtil;
import com.bekrenovr.ecommerce.reviews.dto.ReviewMapper;
import com.bekrenovr.ecommerce.reviews.dto.ReviewRequest;
import com.bekrenovr.ecommerce.reviews.dto.ReviewResponse;
import com.bekrenovr.ecommerce.reviews.model.Review;
import com.bekrenovr.ecommerce.reviews.proxy.OrdersServiceProxy;
import com.bekrenovr.ecommerce.reviews.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.bekrenovr.ecommerce.reviews.exception.ReviewsApplicationExceptionReason.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final String ORDER_STATUS_COMPLETED = "COMPLETED";

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OrdersServiceProxy ordersProxy;

    public Page<ReviewResponse> getReviewsForItem(UUID itemId, int pageNumber, int pageSize) {
        List<Review> reviews = reviewRepository.findAllByItemId(itemId);
        return PageUtil.paginateList(reviews, pageNumber, pageSize)
                .map(reviewMapper::documentToResponse);
    }

    public void createReview(ReviewRequest request) {
        String customerEmail = AuthenticationUtil.getAuthenticatedUser().getUsername();
        validateReviewDoesNotExist(request.itemId());
        validateCustomerHasCompletedOrderForItem(request.itemId());
        Review review = reviewMapper.requestToDocument(request);
        review.setCustomerEmail(customerEmail);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public ReviewResponse updateReview(String id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EcommerceApplicationException(REVIEW_NOT_FOUND, id));
        requireReviewOwnershipByCustomer(review);
        review.setTitle(request.title());
        review.setContent(request.content());
        review.setRating(request.rating());
        return reviewMapper.documentToResponse(reviewRepository.save(review));
    }

    public void deleteReview(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EcommerceApplicationException(REVIEW_NOT_FOUND, id));
        if(AuthenticationUtil.getAuthenticatedUser().hasRole(Role.CUSTOMER)) {
            requireReviewOwnershipByCustomer(review);
        }
        reviewRepository.delete(review);
    }

    @SuppressWarnings("unchecked")
    private void validateCustomerHasCompletedOrderForItem(UUID itemId) {
        String authenticatedUserHeader = RequestUtil.getCurrentRequest().getHeader(SecurityConstants.AUTHENTICATED_USER_HEADER);
        int completedOrdersCount = (int) ordersProxy
                .getAllOrdersForCustomer(authenticatedUserHeader, ORDER_STATUS_COMPLETED, 0, 10)
                .getBody()
                .getTotalElements();
        if(completedOrdersCount == 0) {
            throw new EcommerceApplicationException(CANNOT_CREATE_REVIEW);
        }
        boolean hasCompletedOrderForItem = ordersProxy
                .getAllOrdersForCustomer(authenticatedUserHeader, ORDER_STATUS_COMPLETED, 0, completedOrdersCount)
                .getBody()
                .stream()
                .flatMap(order -> ((List<Map<?, ?>>) order.get("itemEntries")).stream())
                .anyMatch(itemEntry -> itemEntry.get("itemId").equals(itemId.toString()));
        if(!hasCompletedOrderForItem) {
            throw new EcommerceApplicationException(CANNOT_CREATE_REVIEW);
        }
    }

    private void validateReviewDoesNotExist(UUID itemId) {
        String customerEmail = AuthenticationUtil.getAuthenticatedUser().getUsername();
        if(reviewRepository.existsByItemIdAndCustomerEmail(itemId, customerEmail)) {
            throw new EcommerceApplicationException(REVIEW_ALREADY_EXISTS);
        }
    }

    private void requireReviewOwnershipByCustomer(Review review) {
        AuthenticatedUser currentUser = AuthenticationUtil.getAuthenticatedUser();
        if(!review.getCustomerEmail().equals(currentUser.getUsername())) {
            throw new EcommerceApplicationException(NOT_REVIEW_OWNER);
        }
    }
}
