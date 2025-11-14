package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.book.controller.docs.BookImageControllerDocs;
import shop.chaekmate.core.book.dto.request.BookImageAddRequest;
import shop.chaekmate.core.book.dto.request.ThumbnailUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookImageResponse;
import shop.chaekmate.core.book.service.BookImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookImageController implements BookImageControllerDocs {

    private final BookImageService bookImageService;

    @Override
    @PostMapping("/books/{bookId}/images")
    public ResponseEntity<BookImageResponse> addImage(
            @PathVariable Long bookId,
            @Valid @RequestBody BookImageAddRequest request) {
        BookImageResponse response = bookImageService.addImage(bookId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/books/{bookId}/images/thumbnail")
    public ResponseEntity<BookImageResponse> getThumbnail(
            @PathVariable Long bookId) {
        BookImageResponse response = bookImageService.findThumbnail(bookId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/books/{bookId}/images/details")
    public ResponseEntity<List<BookImageResponse>> getDetailImages(
            @PathVariable Long bookId) {
        List<BookImageResponse> response = bookImageService.findDetailImages(bookId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/books/{bookId}/images")
    public ResponseEntity<List<BookImageResponse>> getAllImages(
            @PathVariable Long bookId) {
        List<BookImageResponse> response = bookImageService.findAllImages(bookId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/books/{bookId}/images/thumbnail")
    public ResponseEntity<Void> updateThumbnail(
            @PathVariable Long bookId,
            @Valid @RequestBody ThumbnailUpdateRequest request) {
        bookImageService.updateThumbnail(bookId, request);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/books/{bookId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long bookId,
            @PathVariable Long imageId) {
        bookImageService.deleteImage(bookId, imageId);
        return ResponseEntity.ok().build();
    }
}
