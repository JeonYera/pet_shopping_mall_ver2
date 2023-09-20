 package com.shop.app.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.app.admin.repository.AdminRepository;
import com.shop.app.member.entity.Member;
import com.shop.app.member.entity.MemberDetails;
import com.shop.app.member.entity.Subscribe;
import com.shop.app.servicecenter.inquiry.entity.Question;

@Transactional(rollbackFor = Exception.class) // 어떤예외가 발생하면 자동으로 롤백
@Service
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private AdminRepository adminRepository;

	/**
	 * 리팩토링(김대원)
	 */
	@Override
	public List<MemberDetails> adminMemberList() {
		List<MemberDetails> members = adminRepository.adminMemberList();
		// EnumTypeHandler 사용하여 enum 값 매핑
	    for (MemberDetails member : members) {
	        
	        String subscribeString = member.getSubscribe().toString(); 
	        Subscribe subscribe = Subscribe.valueOf(subscribeString); 
	        
	        member.setSubscribe(subscribe);
	    }
		return members;
	}
	
	/**
	 * 리팩토링(김대원)
	 */
	@Override
	public List<MemberDetails> adminSubscribeList() {
		List<MemberDetails> subscribedMembers = adminRepository.adminSubscribeList();
		
		// EnumTypeHandler 사용하여 enum 값 매핑
	    for (MemberDetails subscribedMember : subscribedMembers) {
	        
	        String subscribeString = subscribedMember.getSubscribe().toString(); 
	        Subscribe subscribe = Subscribe.valueOf(subscribeString); 
	        
	        subscribedMember.setSubscribe(subscribe);
	    }
		
		return subscribedMembers;
	}
	
	/**
	 * 리팩토링(김대원)
	 */
	@Override
	public List<MemberDetails> adminMemberSearchByNameOrId(String searchKeyword) {
		List<MemberDetails> members = adminRepository.adminMemberSearchByNameOrId(searchKeyword);
		for (MemberDetails member : members) {
	        String subscribeString = member.getSubscribe().toString(); 
	        Subscribe subscribe = Subscribe.valueOf(subscribeString);
	        
	        member.setSubscribe(subscribe);
	    }
	    return members;
	}	
	
	/**
	 * 리팩토링(김대원)
	 */
	@Override
	public List<MemberDetails> adminSubscribeSearchByNameOrId(String searchKeyword) {
		List<MemberDetails> members = adminRepository.adminSubscribeSearchByNameOrId(searchKeyword);
		List<MemberDetails> subscribedMembers = new ArrayList<>();
        for (MemberDetails member : members) {
	        
	        String subscribeString = member.getSubscribe().toString(); 
	        Subscribe subscribe = Subscribe.valueOf(subscribeString);
	        
	        member.setSubscribe(subscribe);
        
	        if (member.getSubscribe() == Subscribe.Y) {
                subscribedMembers.add(member);
	        }
        }
		return subscribedMembers;
	}

	@Override
	public List<Question> findQuestionAll(Map<String, Object> params) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		return adminRepository.findQuestionAll(rowBounds);
	}

	@Override
	public List<Question> questionSearch(String searchKeyword) {
		return adminRepository.questionSearch(searchKeyword);
	}
	
	@Override
	public int findTotalQuestionCount() {
		return adminRepository.findTotalQuestionCount();
	}

	@Override
	public int findTotalAdminCount() {
		return adminRepository.findTotalAdminCount();
	}
	
	@Override
	public int findTotalubscribeCount() {
		return adminRepository.findTotalubscribeCount();
	}
	
	@Override
	public int updateMemberPoints(String memberId, int pointChange) {
		return adminRepository.updateMemberPoints(memberId, pointChange);
	}

}
