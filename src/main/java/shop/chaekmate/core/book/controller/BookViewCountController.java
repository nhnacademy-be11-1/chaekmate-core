package shop.chaekmate.core.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.controller.docs.BookViewCountControllerDocs;
import shop.chaekmate.core.book.service.BookViewCountService;

@RestController
@RequiredArgsConstructor
public class BookViewCountController implements BookViewCountControllerDocs {

    private final BookViewCountService bookViewCountService;

    @PostMapping("/books/{bookId}/views")
    public ResponseEntity<Void> increaseView(@PathVariable Long bookId){

        bookViewCountService.increase(bookId);

        return ResponseEntity.ok().build();
    }
}
