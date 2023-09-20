package com.shop.app.notification.service;

import java.util.List;

import com.shop.app.coupon.entity.MemberCoupon;
import com.shop.app.notification.entity.Notification;
import com.shop.app.payment.dto.PaymentCompleteNotificationDto;
import com.shop.app.point.entity.Point;
import com.shop.app.servicecenter.inquiry.entity.Question;

public interface NotificationService {

	List<Notification> findAllNotification(String memberId);

	int updateOrderStatusNotification();

	int deleteNotification(int id);

	int reviewCreateNotification(Point newPoint);

	int memberCreateNotification(MemberCoupon memberCoupon);

	int adminAnswerCreateNotification(Question questions);



}
