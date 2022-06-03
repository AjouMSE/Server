package com.ajou.mse.magicaduel.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.ajou.mse.magicaduel.server.controller.dto.LeaderBoardDto;
import com.ajou.mse.magicaduel.server.controller.dto.RankingDto;
import com.ajou.mse.magicaduel.server.controller.dto.SessionUser;
import com.ajou.mse.magicaduel.server.controller.dto.UserResponseDto;
import com.ajou.mse.magicaduel.server.domain.user.User;
import com.ajou.mse.magicaduel.server.domain.user.UserRepository;
import com.ajou.mse.magicaduel.server.util.Consts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RankingService {

	private final StringRedisTemplate redisTemplate;

	private final UserRepository userRepository;

	private int rankingPerPage = 10;

	public int getRanking(Long userId) {
		// Ranking starts with 0
		Long ranking = redisTemplate.opsForZSet().reverseRank(Consts.RANKING_KEY, String.valueOf(userId));
		if (ranking == null) {
			return 0;
		}
		return ranking.intValue() + 1;
	}

	public void setScore(Long userId, int score) {
		redisTemplate.opsForZSet().add(Consts.RANKING_KEY, String.valueOf(userId), score);
	}

	public LeaderBoardDto getLeaderBoard(int page) {
		int start = (page - 1) * rankingPerPage;
		int end = page * rankingPerPage - 1;

		List<UserResponseDto> users = new ArrayList<>();
		Set<String> rankingList = redisTemplate.opsForZSet().reverseRange(Consts.RANKING_KEY, start, end);

		int ranking = start + 1;
		for (String id : rankingList) {
			User user = userRepository.findById(Long.parseLong(id))
					.orElseThrow(() -> new IllegalArgumentException("Not found user id = " + id));

			users.add(new UserResponseDto(user, ranking++));
		}
		
		long totalCount = redisTemplate.opsForZSet().size(Consts.RANKING_KEY);

		return new LeaderBoardDto(users, totalCount);
	}

	public RankingDto getPlayerRanking(SessionUser sessionUser) {
		return new RankingDto(getRanking(sessionUser.getId()));
	}
}
