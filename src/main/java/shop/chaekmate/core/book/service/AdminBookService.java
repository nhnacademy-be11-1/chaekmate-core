package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.request.AdminBookPagedRequest;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;
import shop.chaekmate.core.book.exception.BookImageNotFoundException;
import shop.chaekmate.core.book.repository.AdminBookRepositoryImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBookService {

    private final AdminBookRepositoryImpl adminBookRepository;
    private final BookImageService bookImageService; // BookImageService 주입

    public List<AdminBookResponse> findRecentBooks(int limit) {
        return adminBookRepository.findRecentBooks(limit).stream()
                .map(book -> {
                    String imageUrl = null;
                    try {
                        imageUrl = bookImageService.findThumbnail(book.getId()).imageUrl();
                    } catch (BookImageNotFoundException e) {
                        // 썸네일 이미지가 없는 경우 null 처리
                    }
                    return AdminBookResponse.of(book, imageUrl,null);
                })
                .toList();
    }

    public Page<AdminBookResponse> findBooks(AdminBookPagedRequest request) {

        return adminBookRepository.findBooks(request);
    }
}
