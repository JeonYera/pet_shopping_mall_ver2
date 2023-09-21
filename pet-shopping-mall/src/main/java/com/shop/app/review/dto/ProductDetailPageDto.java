package com.shop.app.review.dto;

import java.sql.Timestamp;
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
	private int categoryId; // 1: 사료, 2: 간식, 3: 패션용품, 4: 산책용품, 5: 위생용품, 6: 고양이, 7: 기타용품
	private int productPrice;
	private int imageId;
	private Timestamp createDate;
	private int likeCnt;
	
	public Product toProduct() {
		return Product.builder()
				.productId(productId)
				.productName(productName)
				.categoryId(categoryId)
				.createDate(createDate)
				.likeCnt(likeCnt)
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
