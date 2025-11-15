package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.dto.request.BookImageAddRequest;
import shop.chaekmate.core.book.dto.request.ThumbnailUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookImageResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.book.event.BookThumbnailEvent;
import shop.chaekmate.core.book.exception.BookImageNotFoundException;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.repository.BookImageQueryRepository;
import shop.chaekmate.core.book.repository.BookImageRepository;
import shop.chaekmate.core.book.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BookImageService {

    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final BookImageQueryRepository bookImageQueryRepository;

    // 트랜잭션 끝 난 뒤 서비스 호출 이벤트 발행
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public BookImageResponse addImage(Long bookId, BookImageAddRequest request) {
        Book book = findBookById(bookId);
        BookImage newBookImage = new BookImage(book, request.getImageUrl());
        newBookImage = bookImageRepository.save(newBookImage);

        List<BookImage> images = bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId);
        boolean isThumbnail = images.size() == 1 && images.getFirst().getId().equals(newBookImage.getId());

        if(isThumbnail){
            // 섬네일 변경시 검색 서버에 반영
            eventPublisher.publishEvent(new BookThumbnailEvent(bookId,newBookImage.getImageUrl()));
        }

        return BookImageResponse.builder()
                .bookImageId(newBookImage.getId())
                .imageUrl(newBookImage.getImageUrl())
                .isThumbnail(isThumbnail)
                .build();
    }

    public BookImageResponse findThumbnail(Long bookId) {
        findBookById(bookId);
        return bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)
                .stream()
                .findFirst()
                .map(image -> BookImageResponse.builder()
                        .bookImageId(image.getId())
                        .imageUrl(image.getImageUrl())
                        .isThumbnail(true)
                        .build())
                .orElseThrow(BookImageNotFoundException::new);
    }

    public List<BookImageResponse> findDetailImages(Long bookId) {
        findBookById(bookId);
        List<BookImage> images = bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId);

        if (images.size() <= 1) {
            return Collections.emptyList();
        }

        return images.stream()
                .skip(1)
                .map(image -> BookImageResponse.builder()
                        .bookImageId(image.getId())
                        .imageUrl(image.getImageUrl())
                        .isThumbnail(false)
                        .build())
                .toList();
    }

    public List<BookImageResponse> findAllImages(Long bookId) {
        findBookById(bookId);
        List<BookImage> images = bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId);

        return IntStream.range(0, images.size())
                .mapToObj(i -> {
                    BookImage image = images.get(i);
                    return BookImageResponse.builder()
                            .bookImageId(image.getId())
                            .imageUrl(image.getImageUrl())
                            .isThumbnail(i == 0)
                            .build();
                })
                .toList();
    }


    // 섬네일 생성 , 수정
    @Transactional
    public void updateThumbnail(Long bookId, ThumbnailUpdateRequest request) {
        Book book = findBookById(bookId); // Check if book exists
        BookImage thumbnail = bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)
                .stream()
                .findFirst()
                .orElse(null);

        // 섬네일이 없으면 추가
        if(thumbnail == null){
            BookImage newBookImage = new BookImage(book, request.getNewImageUrl());
            bookImageRepository.save(newBookImage);
        } else {
            thumbnail.updateUrl(request.getNewImageUrl());
        }

        // 섬네일 이벤트 발행
        eventPublisher.publishEvent(new BookThumbnailEvent(bookId, request.getNewImageUrl()));

    }

    @Transactional
    public void deleteImage(Long bookId, Long imageId) {
        findBookById(bookId);
        BookImage bookImage = findBookImageById(imageId);

        if (!bookImage.getBook().getId().equals(bookId)) {
            throw new IllegalArgumentException("해당 이미지는 지정된 도서에 속하지 않습니다.");
        }

        bookImageRepository.delete(bookImage);
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);
    }

    private BookImage findBookImageById(Long imageId) {
        return bookImageRepository.findById(imageId)
                .orElseThrow(BookImageNotFoundException::new);
    }
}
