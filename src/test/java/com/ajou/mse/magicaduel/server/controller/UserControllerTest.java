package com.ajou.mse.magicaduel.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ajou.mse.magicaduel.server.controller.dto.UserDto;
import com.ajou.mse.magicaduel.server.controller.dto.UserSignInDto;
import com.ajou.mse.magicaduel.server.controller.dto.UserSignUpDto;
import com.ajou.mse.magicaduel.server.domain.battleinfo.BattleInfo;
import com.ajou.mse.magicaduel.server.domain.user.User;
import com.ajou.mse.magicaduel.server.service.RankingService;
import com.ajou.mse.magicaduel.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private RankingService rankingService;

	String email = "email";
	String password = "password";
	String nickname = "nickname";
	int score = 10;
	int win = 1;
	int lose = 2;
	int draw = 3;
	int ranking = 4;
	User saveUser;
	BattleInfo saveBattleInfo;
	UserDto saveUserDto;

	@BeforeEach
	public void setup() {
		saveUser = User.builder()
				.email(email)
				.password(password)
				.nickname(nickname)
				.build();

		saveBattleInfo = BattleInfo.builder()
				.score(score)
				.win(win)
				.lose(lose)
				.draw(draw)
				.build();

		saveUser.updateBattleInfo(saveBattleInfo);

		saveUserDto = UserDto.builder()
				.user(saveUser)
				.build();
	}

	@Test
	void 회원가입() throws Exception {
		// given
		UserSignUpDto requestDto = UserSignUpDto.builder()
				.email(email)
				.password(password)
				.nickname(nickname)
				.build();

		// when
		mockMvc.perform(
				post("/user/sign-up")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(requestDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.result").value(true))
				.andDo(print());
	}

	@Test
	void 로그인_성공() throws Exception {
		// given
		UserSignInDto requestDto = UserSignInDto.builder()
				.email(email)
				.password(password)
				.build();

		// when
		when(userService.signIn(any(UserSignInDto.class))).thenReturn(saveUserDto);
		when(rankingService.getRanking(any())).thenReturn(ranking);

		mockMvc.perform(
				post("/user/sign-in")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(requestDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nickname").value(nickname))
				.andExpect(jsonPath("$.score").value(score))
				.andExpect(jsonPath("$.win").value(win))
				.andExpect(jsonPath("$.lose").value(lose))
				.andExpect(jsonPath("$.draw").value(draw))
				.andExpect(jsonPath("$.ranking").value(ranking))
				.andDo(print());
	}
}
