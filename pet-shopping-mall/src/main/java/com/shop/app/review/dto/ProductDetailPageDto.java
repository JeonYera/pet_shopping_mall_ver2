package com.shop.app.review.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailPageDto {
	
	private String productName; // product
	private int productId; // product
	private long totalCount;
	private double reviewStarRate; // review
	private List<ProductDetailPageDto> reviews;
	private int reviewId;
	
	public long getTotalCount() {
		return totalCount;
	}

}
