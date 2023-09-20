package com.shop.app.review.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.shop.app.pet.entity.Pet;
import com.shop.app.pet.entity.PetGender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailPageDto {
	
	private String productName;
	private int productId;
	
	private long totalCount;
	private double reviewStarRate;
	private String reviewTitle;
	private List<ProductDetailPageDto> reviews;
	private int reviewId;
	private String reviewContent;
	private String reviewMemberId;
	private LocalDateTime reviewCreateAt;

	private List<Pet> pets;
	private int petId;
	private String memberId;
    private String petName;    
    private int petAge;
    private String petKind;
    private String petBreed;
    private String petWeight;
    private PetGender petGender;
    
	public long getTotalCount() {
		return totalCount;
	}

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

}
