package com.shop.app.member.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shop.app.coupon.entity.Coupon;
import com.shop.app.coupon.entity.MemberCoupon;
import com.shop.app.coupon.repository.CouponRepository;
import com.shop.app.member.dto.MemberCreateDto;
import com.shop.app.member.dto.MypageDto;
import com.shop.app.member.entity.Member;
import com.shop.app.member.entity.SubMember;
import com.shop.app.member.repository.MemberRepository;
import com.shop.app.notification.service.NotificationService;
import com.shop.app.notification.service.NotificationServiceImpl;
import com.shop.app.order.dto.OrderHistoryDto;
import com.shop.app.order.entity.Order;
import com.shop.app.order.repository.OrderRepository;
import com.shop.app.point.entity.Point;
import com.shop.app.point.repository.PointRepository;
import com.shop.app.terms.entity.Accept;
import com.shop.app.terms.entity.Terms;
import com.shop.app.terms.repository.TermsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PointRepository pointRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	NotificationService notificationService; // 알림 서비스

	@Autowired
	private TermsRepository termsRepository;

	@Override
	public int insertMember(MemberCreateDto member) {
		return memberRepository.insertMember(member);
	}

	@Override
	public Member findMemberById(String memberId) {
		return memberRepository.findMemberById(memberId);
	}

	@Override
	public int updateMember(Member member) {
		return memberRepository.updateMember(member);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails memberDetails = memberRepository.loadUserByUsername(username);
		if (memberDetails == null)
			throw new UsernameNotFoundException(username);
		return memberDetails;
	}

	@Override
	public int deleteMember(String memberId) {
		return memberRepository.deleteMember(memberId);

	}

	@Override
	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	@Override
	public MypageDto getMyPage(String memberId,  Map<String, Object> params) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		MypageDto myPage = memberRepository.getMyPage(memberId);
		List<Order> histories = orderRepository.getOrderListByPeriod(memberId, 1, rowBounds);

		SubMember subMember = null;

		if((myPage.getSubscribe()).equals("Y")) {
			subMember = memberRepository.findSubMemberByMemberId(memberId);
		}

		myPage.setSubMember(subMember);
		myPage.setOrderHistory(histories);
		return myPage;
	}

	@Override
	public int memberSubscribe(String memberId) {
		return memberRepository.memberSubscribe(memberId);
	}

	@Override
	public int subscribeCancel(String memberId) {
		return memberRepository.subscribeCancel(memberId);
	}


	@Override
	public SubMember findSubMemberByMemberId(String memberId) {
		return memberRepository.findSubMemberByMemberId(memberId);
	}

	@Override
	public int cancelSubscribe(String memberId) {
		return memberRepository.cancelSubscribe(memberId);
	}

	@Override
	public int updateCancelSubscribers() {
		List<SubMember> subMembers = memberRepository.updateCancelSubscribers();
		int result = 0;
		for(SubMember s : subMembers) {
			result = memberRepository.subscribeCancel(s.getMemberId());
		}
		return result; 
	}

	@Override
	public MypageDto getMyPage(String memberId) {
		MypageDto myPage = memberRepository.getMyPage(memberId);

		SubMember subMember = null;

		if((myPage.getSubscribe()).equals("Y")) {
			subMember = memberRepository.findSubMemberByMemberId(memberId);
		}

		myPage.setSubMember(subMember);
		return myPage;
	}

	/**
	 * @author 전예라
	 * 포인트, 쿠폰, 이용약관 리팩토링(리팩토링)
	 * 
	 * @author 김대원
     * 회원가입 쿠폰이 발급되면 회원에게 알림 발송(리팩토링)
	 */
	@Override
	public int createMember(MemberCreateDto member) {
	    int memberId = memberRepository.insertMember(member);

	    handlePointCreate(member.getMemberId());
	    handleCouponCreate(member.getMemberId());
	    handleUserAgreements(member);

	    return memberId;
	}

	private void handlePointCreate(String memberId) {
	    Point point = new Point();
	    point.setPointMemberId(memberId);
	    point.setPointCurrent(3000);
	    point.setPointType("회원가입");
	    point.setPointAmount(3000);
	    pointRepository.insertPoint(point);
	}

	private void handleCouponCreate(String memberId) {
	    List<Coupon> coupons = couponRepository.findCoupon();
	    for (Coupon coupon : coupons) {
	        MemberCoupon memberCoupon = new MemberCoupon();
	        memberCoupon.setCouponId(coupon.getCouponId());
	        memberCoupon.setMemberId(memberId);
	        LocalDateTime issuanceDate = LocalDateTime.now();
	        LocalDateTime endDate = issuanceDate.plusMonths(1);
	        memberCoupon.setCreateDate(issuanceDate);
	        memberCoupon.setEndDate(endDate);
	        memberCoupon.setUseStatus(0);
	        couponRepository.insertDeliveryCoupon(memberCoupon);

	        int memberCreateNotification = notificationService.memberCreateNotification(memberCoupon);
	    }
	}

	private void handleUserAgreements(MemberCreateDto member) {
	    HashMap<Integer, Accept> userAgreements = member.getUserAgreements();
	    for (Map.Entry<Integer, Accept> entry : userAgreements.entrySet()) {
	        Terms terms = new Terms();
	        terms.setMemberId(member.getMemberId());
	        terms.setHistoryId(entry.getKey());
	        terms.setTermsId(entry.getKey());
	        terms.setAccept(entry.getValue());
	        termsRepository.insertTerms(terms);
	    }
	}
}
