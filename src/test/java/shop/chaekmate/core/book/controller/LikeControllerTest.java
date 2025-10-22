package shop.chaekmate.core.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.chaekmate.core.book.dto.CreateLikeRequest;
import shop.chaekmate.core.book.dto.DeleteLikeRequest;
import shop.chaekmate.core.book.dto.LikeResponse;
import shop.chaekmate.core.book.service.LikeService;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("좋아요 생성 요청 성공")
    void createLike_success() throws Exception {
        Long bookId = 1L;
        Long memberId = 1L;
        CreateLikeRequest request = new CreateLikeRequest(memberId);
        LikeResponse response = new LikeResponse(1L, bookId, memberId);

        when(likeService.createLike(anyLong(), any(CreateLikeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/books/{bookId}/likes", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("ID로 좋아요 조회 요청 성공")
    void readLike_success() throws Exception {
        Long likeId = 1L;
        LikeResponse response = new LikeResponse(likeId, 1L, 1L);

        when(likeService.readLikeById(likeId)).thenReturn(response);

        mockMvc.perform(get("/likes/{likeId}", likeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeId));
    }

    @Test
    @DisplayName("책 ID로 좋아요 목록 조회 요청 성공")
    void getBookLikes_success() throws Exception {
        Long bookId = 1L;
        LikeResponse response = new LikeResponse(1L, bookId, 1L);

        when(likeService.getBookLikes(bookId)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/books/{bookId}/likes", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(bookId));
    }

    @Test
    @DisplayName("회원 ID로 좋아요 목록 조회 요청 성공")
    void getMemberLikes_success() throws Exception {
        Long memberId = 1L;
        LikeResponse response = new LikeResponse(1L, 1L, memberId);

        when(likeService.getMemberLikes(memberId)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/members/{memberId}/likes", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memeberId").value(memberId));
    }

    @Test
    @DisplayName("ID로 좋아요 삭제 요청 성공")
    void deleteLikeById_success() throws Exception {
        Long likeId = 1L;
        doNothing().when(likeService).deleteLikeById(likeId);

        mockMvc.perform(delete("/likes/{likeId}", likeId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("책 및 회원 ID로 좋아요 삭제 요청 성공")
    void deleteLikeByBookIdAndMemberId_success() throws Exception {
        Long bookId = 1L;
        Long memberId = 1L;
        DeleteLikeRequest request = new DeleteLikeRequest(memberId);

        doNothing().when(likeService).deleteLikeByBookIdAndMemberId(anyLong(), any(DeleteLikeRequest.class));

        mockMvc.perform(delete("/books/{bookId}/likes", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
