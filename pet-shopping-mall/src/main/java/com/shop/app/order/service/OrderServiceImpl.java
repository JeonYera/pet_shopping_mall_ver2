package com.shop.app.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Build;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.app.coupon.entity.MemberCoupon;
import com.shop.app.coupon.repository.CouponRepository;
import com.shop.app.coupon.service.CouponService;
import com.shop.app.member.entity.Member;
import com.shop.app.member.entity.Subscribe;
import com.shop.app.member.repository.MemberRepository;
import com.shop.app.order.dto.OrderAdminListDto;
import com.shop.app.order.dto.OrderAdminProductStatisticsDto;
import com.shop.app.order.dto.OrderAdminStatisticsByDateDto;
import com.shop.app.order.dto.OrderCancelInfoDto;
import com.shop.app.order.dto.OrderHistoryDto;
import com.shop.app.order.dto.OrderReviewListDto;
import com.shop.app.order.entity.CancelOrder;
import com.shop.app.order.entity.Order;
import com.shop.app.order.entity.OrderDetail;
import com.shop.app.order.repository.OrderRepository;
import com.shop.app.payment.entity.Payment;
import com.shop.app.payment.repository.PaymentRepository;
import com.shop.app.point.entity.Point;
import com.shop.app.point.repository.PointRepository;
import com.shop.app.point.service.PointService;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private PointService pointService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private MemberRepository memberRepository;

	/**
	 * @author 김담희
	 * 주문 테이블과 주문 상세 테이블에 내역 저장
	 * 
	 * @author 전예라
	 * 포인트/쿠폰 사용과 포인트 적립 - 일반 1% / 구독자 3%, 포인트 사용하면 적립 안됨 (리팩토링)
	 */
	@Override
	@Transactional
	public int insertOrder(Order order, List<OrderDetail> orderDetails, String memberId, int pointsUsed, Integer couponId) {
		int result = 0;
		result = orderRepository.insertOrder(order);
		int orderId = order.getOrderId();
		
		for(OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderId(orderId);
			result = orderRepository.insertOrderDetail(orderDetail);
		}
		
		// 포인트 사용 처리
	    if (pointsUsed > 0) {
	        Point usedPoint = new Point();
	        usedPoint.setPointMemberId(memberId);
	        usedPoint.setPointType("구매사용");
	        usedPoint.setPointAmount(-pointsUsed);
	        
	    	Point points = new Point();
			points.setPointMemberId(memberId);
	        Point currentPoints = pointService.findPointCurrentById(points);
	        usedPoint.setPointCurrent(currentPoints.getPointCurrent() - pointsUsed);
	        
	        pointService.insertUsedPoint(usedPoint);
	    }

	    // 쿠폰 사용 처리
	    if (couponId != null) {
	        MemberCoupon coupon = new MemberCoupon();
	        coupon.setMemberId(memberId);
	        coupon.setCouponId(couponId);
	        coupon.setUseStatus(1);
	        
	        couponService.updateCouponStatus(coupon);
	    }

	    // 포인트 적립 처리 (포인트를 사용한 경우에는 적립하지 않음)
	    if (pointsUsed <= 0) {
	        Member subscribeMember = memberRepository.findMemberById(memberId);
	        boolean subscriber = (subscribeMember.getSubscribe() == Subscribe.Y);

	        int amount = order.getAmount();
	        double pointRate = subscriber ? 0.03 : 0.01;
	        int pointAmount = (int) (amount * pointRate);

	        Point point = new Point();
	        point.setPointMemberId(memberId);
	        point.setPointType("구매적립");
	        point.setPointAmount(pointAmount);

	        Point currentPoints = pointService.findReviewPointCurrentById(point);
	        point.setPointCurrent(currentPoints.getPointCurrent() + pointAmount);

	        pointService.insertPoint(point);
	    }

	    return result;
	}

	
	// 관리자페이지 주문조회 (대원)
	@Override
	public List<OrderAdminListDto> adminOrderList() {
		return orderRepository.adminOrderList();
	}
	
	
	// 관리자페이지 주문검색 조회 (대원)
	@Override
	public List<OrderAdminListDto> adminOrderSearch(String searchKeyword, String startDate, String endDate,
													List<String> paymentMethod, List<String> orderStatus) {
		return orderRepository.adminOrderSearch(searchKeyword, startDate, endDate, paymentMethod, orderStatus);
	}
	
	
	// 관리자페이지 상품매출통계 조회 - 판매수량 (대원)
	@Override
	public List<OrderAdminProductStatisticsDto> adminStatisticsProduct() {
		return orderRepository.adminStatisticsProduct();
	}
	
	
	// 관리자페이지 상품매출통계 조회 - 매출액(대원)
	@Override
	public List<OrderAdminProductStatisticsDto> adminStatisticsPrice() {
		return orderRepository.adminStatisticsPrice();
	}
	
	
	// 관리자페이지 날짜별 상품매출통계 조회 - 일별 (대원)
	@Override
	public List<OrderAdminStatisticsByDateDto> adminStatisticsByDaily() {
		return orderRepository.adminStatisticsByDaily();
	}
	
	
	// 관리자페이지 날짜별 상품매출통계 조회 -월별 (대원)
	@Override
	public List<OrderAdminStatisticsByDateDto> adminStatisticsByMonthly() {
		return orderRepository.adminStatisticsByMonthly();
	}
	
	@Override
	public Order findByOrder(Order order) {
		return orderRepository.findByOrder(order);
	}
	

	@Override
	public List<Order> getOrderList(String memberId, Map<String, Object> params) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		return orderRepository.getOrderList(memberId, rowBounds);
	}


	/**
	 * @author 김담희
	 * 주문 취소 시 주문 취소 테이블에 insert
	 * 주문 테이블에서 주문 상태를 주문 취소 상태로 변경
	 */
	@Override
	public int insertCancelOrder(String orderNo, String isRefund) {
		int result = 0;
		Order order = orderRepository.findOrderByOrderNo(orderNo);
		int orderId = order.getOrderId();
		CancelOrder cancel = CancelOrder.builder()
				.orderId(orderId)
				.build();
		result = orderRepository.insertCancelOrder(cancel, 1, orderId);
		result = orderRepository.updateOrderStatus(orderNo, 5);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
		return result;
	}

	
	
	@Override
	public List<Order> getOrderListByPeriod(String memberId, int period, Map<String, Object> params) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		return orderRepository.getOrderListByPeriod(memberId, period, rowBounds);
	}
	
	

	@Override
	public OrderCancelInfoDto getCancelInfo(String orderNo) {
		return orderRepository.getCancelInfo(orderNo);
	}

	
	@Override
	public List<OrderCancelInfoDto> getCancelInfoAll(String memberId, Map<String, Object> params) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		return orderRepository.getCancelInfoAll(memberId, rowBounds);
	}
	

	
	@Override
	public List<OrderCancelInfoDto> getCancelInfoByPeriod(String memberId, int period, Map<String, Object> params) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		return orderRepository.getCancelInfoByPeriod(memberId, period, rowBounds);
	}
	

	/**
	 * @author 전예라
	 * 결제창이 넘어가기 전에 취소하면 사용했던 포인트, 쿠폰 롤백 (리팩토링)
	 */
	@Override
	public int deleteOrder(String orderNo, String memberId, Integer pointsUsed, Integer couponId) {
		int result = orderRepository.deleteOrder(orderNo);
		
		// 포인트 롤백
        if (pointsUsed != null) {
            Point rollbackPoint = new Point();
            rollbackPoint.setPointMemberId(memberId);
            rollbackPoint.setPointType("구매취소");
            rollbackPoint.setPointAmount(pointsUsed);

            Point currentPoints = pointService.findPointCurrentById(rollbackPoint);
            int currentPoint = currentPoints.getPointCurrent();
            int earnedPoint = currentPoints.getPointAmount();
            int netPoint = currentPoint - earnedPoint;
            rollbackPoint.setPointCurrent(netPoint);
            
            pointService.insertRollbackPoint(rollbackPoint);
        }

        // 쿠폰 롤백
        if (couponId != null) {
            MemberCoupon coupon = new MemberCoupon();
            coupon.setCouponId(couponId);
            coupon.setMemberId(memberId);
            coupon.setUseStatus(0);
            coupon.setUseDate(null);

            couponService.updateCoupon(coupon);
        }

        return result;
	}
	

	/**
	 * @author 김담희
	 * 주문 상세 내역 조회
	 * 주문 내역과 그에 매칭되는 결제 정보를 담아 반환
	 */
	@Override
	public List<Map<OrderHistoryDto, Payment>> getOrderDetail(String orderNo) {
		List<Map<OrderHistoryDto, Payment>> orderList = new ArrayList<>();
		
		Map<OrderHistoryDto, Payment> orderDetailMap = new HashMap<>();
		
		List<OrderHistoryDto> history = orderRepository.getOrderDetail(orderNo);
		Payment payment = paymentRepository.getPaymentInfo(history.get(0).getOrderId());
		
		for(OrderHistoryDto o : history) {
			orderDetailMap.put(o, payment);
		}
		
		orderList.add(orderDetailMap);
		
		return orderList;
	}

	@Override
	public Order findOrderByOrderNo(String orderNo) {
		return orderRepository.findOrderByOrderNo(orderNo);
	}

	@Override
	public boolean reviewWrite(String memberId, int orderId, int productDetailId, int productId) {
		return orderRepository.reviewWrite(memberId, orderId, productDetailId, productId);
	}

	
	// 상품별 주문확정 주문 수 조회 (수경)
	@Override
	public int findOrderCntByProductId(int productDetailId) {
		return orderRepository.findOrderCntByProductId(productDetailId);
	}

	// 리뷰 리스트 - 주문자 연결 (혜령)
	@Override
	public List<OrderReviewListDto> findOrdersByReviewId(String reviewMemberId) {
		return orderRepository.findOrdersByReviewId(reviewMemberId);
	}
	
	
	
	/**
	 * @author 김담희
	 * 결제하고 7일이 지났으며 주문 상태가 "배송완료"일 경우, 주문 상태를
	 * 주문 확정으로 변경
	 */
	@Override
	public int updateOrderStatusIfExpired() {
		int result = 0;
        List<Order> orders = orderRepository.findOrdersWithExpiredStatus();
        for (Order order : orders) {
            result = orderRepository.updateOrderStatus(order.getOrderNo(), 6);
        }
        return result;
	}
	

	// 상품상세 - 리뷰 - 상품
	@Override
	public List<OrderReviewListDto> findProductByReviewId(int reviewId, int productId) {
		return orderRepository.findProductByReviewId(reviewId, productId);
	}
	

	@Override
	public int findTotalOrderCount(String memberId) {
		return orderRepository.findTotalOrderCount(memberId);
	}


	@Override
	public int findTotalCancelOrderCount(String memberId) {
		return orderRepository.findTotalCacncelOrderCount(memberId);
	}

}
