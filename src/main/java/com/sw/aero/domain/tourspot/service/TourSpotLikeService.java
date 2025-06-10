package com.sw.aero.domain.tourspot.service;

import com.sw.aero.domain.tourspot.dto.TourSpotResponse;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import com.sw.aero.domain.tourspot.entity.TourSpotLike;
import com.sw.aero.domain.tourspot.repository.TourSpotLikeRepository;
import com.sw.aero.domain.tourspot.repository.TourSpotRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourSpotLikeService {

    private final TourSpotLikeRepository likeRepository;
    private final TourSpotRepository tourSpotRepository;
    private final UserRepository userRepository;

    public void likeSpot(Long userId, Long contentId) {
        TourSpot spot = tourSpotRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 관광지 ID: " + contentId));

        if (likeRepository.existsByUserIdAndTourSpotId(userId, spot.getId())) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 사용자 ID: " + userId));

        TourSpotLike like = TourSpotLike.builder()
                .user(user)
                .tourSpot(spot)
                .build();

        likeRepository.save(like);
    }

    public void unlikeSpot(Long userId, Long contentId) {
        TourSpot spot = tourSpotRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 관광지 ID: " + contentId));

        TourSpotLike like = likeRepository.findByUserIdAndTourSpotId(userId, spot.getId())
                .orElseThrow(() -> new IllegalStateException("좋아요 기록이 없습니다."));

        likeRepository.delete(like);
    }

    public boolean hasLiked(Long userId, Long contentId) {
        TourSpot spot = tourSpotRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 관광지 ID: " + contentId));
        return likeRepository.existsByUserIdAndTourSpotId(userId, spot.getId());
    }

    public long countLikes(Long contentId) {
        TourSpot spot = tourSpotRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 관광지 ID: " + contentId));
        return likeRepository.countByTourSpotId(spot.getId());
    }

    // 유저가 좋아요한 관광지 목록 조회
    public List<TourSpotResponse> getLikedTourSpots(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 사용자 ID: " + userId));

        List<TourSpotLike> likes = likeRepository.findAllByUser(user);

        return likes.stream()
                .map(like -> {
                    TourSpot spot = like.getTourSpot();
                    long likeCount = likeRepository.countByTourSpotId(spot.getId());
                    return TourSpotResponse.from(spot, likeCount);
                })
                .toList();
    }

}
