package com.shop.app.review.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.shop.app.common.entity.ImageAttachment;
import com.shop.app.common.entity.ImageAttachmentMapping;
import com.shop.app.pet.entity.Pet;
import com.shop.app.pet.entity.PetGender;
import com.shop.app.product.entity.Product;
import com.shop.app.review.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailPageDto {
	
	private int productId;
	private String productName;
	
	public Product toProduct() {
		return Product.builder()
				.productId(productId)
				.productName(productName)
				.build();
	}
	
	private int productDetailId;
	private String optionName;
	private String optionValue;
	private int additionalPrice;
	
	
	private List<ProductDetailPageDto> reviews;
	private int reviewId;
	private String reviewMemberId;
	private int reviewStarRate;
	private String reviewTitle;
	private String reviewContent;
	private LocalDateTime reviewCreatedAt;
	private double avgStarRate;

	private long totalCount;
	
	public long getTotalCount() {
		return totalCount;
	}
	
	public Review toReview() {
		return Review.builder()
				.reviewId(reviewId)
				.reviewMemberId(reviewMemberId)
				.reviewTitle(reviewTitle)
				.reviewContent(reviewContent)
				.reviewStarRate(reviewStarRate)
				.reviewCreatedAt(reviewCreatedAt)
				.build();
	}
	
	private int petId;
	private String memberId;
	private String petName;
	private int petAge;
	private String petBreed;
	private String petWeight;
	private PetGender petGender;
	private String petKind;
    
	
	public Pet toPet() {
		return Pet.builder()
				.petId(petId)
				.petName(petName)
				.memberId(memberId)
				.petAge(petAge)
				.petBreed(petBreed)
				.petWeight(petWeight)
				.petKind(petKind)
				.petGender(petGender)
				.build();
	}

	public Pet getPet() {
	    return this.toPet();
	}

	private List<ImageAttachment> attachments;
	private List<ImageAttachmentMapping> attachmentMapping;
}
