package com.shop.app.pet.service;

import java.util.List;
import java.util.Map;

import com.shop.app.pet.dto.PetCreateDto;
import com.shop.app.pet.dto.PetUpdateDto;
import com.shop.app.pet.entity.Pet;
import com.shop.app.review.dto.ProductDetailPageDto;
import com.shop.app.review.dto.ReviewDetailDto;
import com.shop.app.review.entity.Review;

public interface PetService {
    int petCreate(PetCreateDto petCreateDto);
    
    int petUpdate(PetUpdateDto petUpdateDto);

    List<Pet> findPetsByMemberId(String memberId);

	Pet findPetById(int petId);

	int petDelete(int petId);

	// 리뷰-펫 정보 가져오기 (혜령)
	List<Pet> findPetId(Pet pet, String memberId);

	// 상품 상세페이지 - 리뷰 - 펫 정보 가져오기 (혜령) 
	List<Pet> findProductRevicePet(String memberId);

//	List<Pet> findReviewPetByMemberId(String reviewMemberId);

	Map<Integer, List<Pet>> findPetsMapByReviews(ProductDetailPageDto reviewPageInfo);


	

}