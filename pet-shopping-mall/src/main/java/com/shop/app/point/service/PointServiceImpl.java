package com.shop.app.point.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.app.notification.repository.NotificationRepository;
import com.shop.app.notification.service.NotificationService;
import com.shop.app.notification.service.NotificationServiceImpl;
import com.shop.app.point.entity.Point;
import com.shop.app.point.repository.PointRepository;
import com.shop.app.review.dto.ReviewCreateDto;
import com.shop.app.review.entity.ReviewDetails;

@Service
public class PointServiceImpl implements PointService {

	@Autowired
	NotificationService notificationService;
	
	@Autowired
	private PointRepository pointRepository;
	
	@Override
	public List<Point> findPointAll(Map<String, Object> params, String pointMemberId) {
		int limit = (int) params.get("limit");
		int page = (int) params.get("page");
		int offset = (page - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return pointRepository.findPointAll(rowBounds, pointMemberId);
	}

	@Override
	public int insertPoint(Point point) {
		return pointRepository.insertPoint(point);
	}

	@Override
	public Point findCurrentPointById(String memberId) {
		return pointRepository.findCurrentPointById(memberId);
	}

	@Override
	public Point findReviewPointCurrentById(Point point) {
		return pointRepository.findReviewPointCurrentById(point);
	}

	@Override
	public Point findPointCurrentById(Point points) {
		return pointRepository.findPointCurrentById(points);
	}

	@Override
	public int insertUsedPoint(Point usedPoint) {
		return pointRepository.insertUsedPoint(usedPoint);
	}

	@Override
	public int insertRollbackPoint(Point rollbackPoint) {
		return pointRepository.insertRollbackPoint(rollbackPoint);
	}

	@Override
	public Point getPointByReviewId(int reviewId) {
		return pointRepository.getPointByReviewId(reviewId);
	}

	@Override
	public List<Point> findRollbackPointCurrentById(Point rollbackPoint) {
		return pointRepository.findRollbackPointCurrentById(rollbackPoint);
	}

	@Override
	public int findTotalPointCount(String pointMemberId) {
		return pointRepository.findTotalPointCount(pointMemberId);
	}
	
	
    /**
     * @author 전예라
     * 리뷰 작성 시 텍스트 500원, 사진 1,000원 적립 (리팩토링)
     * 
     * @author 김대원
     * 리뷰 작성 시 실시간 알림 (리팩토링)
     */
    @Override
    public void handleReviewPoints(ReviewCreateDto _review, boolean hasImage) {
        Point point = new Point();
        point.setPointMemberId(_review.getReviewMemberId());

        Point currentPoints = findReviewPointCurrentById(point); 

        int pointAmount = 500;
        if (hasImage) {
            pointAmount += 500;
        }

        int updatedPointAmount = currentPoints.getPointCurrent() + pointAmount;

        Point newPoint = new Point();
        newPoint.setPointCurrent(updatedPointAmount);
        newPoint.setPointAmount(pointAmount);
        newPoint.setPointType("리뷰적립");
        newPoint.setPointMemberId(_review.getReviewMemberId());

        int newPointResult = insertPoint(newPoint);
        
		// 리팩토링 김대원(리뷰 적립금 알림)
        int reviewCreateNotification = notificationService.reviewCreateNotification(newPoint);
    }
}
