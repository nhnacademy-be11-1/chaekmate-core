package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;
import shop.chaekmate.core.book.repository.BookImageRepository;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.book.repository.AdminBookRepositoryImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBookService {

    private final AdminBookRepositoryImpl adminBookRepository;
    private final BookImageRepository bookImageRepository;

    public List<AdminBookResponse> findRecentBooks(int limit) {
        return adminBookRepository.findRecentBooks(limit).stream()
                .map(book -> {
                    String imageUrl = bookImageRepository.findByBookId(book.getId())
                            .map(BookImage::getImageUrl)
                            .orElse(null);
                    return AdminBookResponse.of(book, imageUrl);
                })
                .toList();
    }
}
