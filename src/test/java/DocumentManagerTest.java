import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentManagerTest {

    @Test
    public void testSaveAndFindById() {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument, foundDocument.get());
    }

    @Test
    public void testSearchByTitlePrefix() {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id("doc1")
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id("doc2")
                .title("AnotherTitle")
                .content("Content2")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Arrays.asList("Title"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);
        assertEquals(1, result.size());
        assertEquals("doc1", result.get(0).getId());
    }

    @Test
    public void testSearchByContent() {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id("doc1")
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id("doc2")
                .title("Title2")
                .content("DifferentContent")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(Arrays.asList("Content1"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);
        assertEquals(1, result.size());
        assertEquals("doc1", result.get(0).getId());
    }

    @Test
    public void testSearchByAuthor() {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author2")
                .name("Jane Smith")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id("doc1")
                .title("Title1")
                .content("Content1")
                .author(author1)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id("doc2")
                .title("Title2")
                .content("Content2")
                .author(author2)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(Arrays.asList("author1"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);
        assertEquals(1, result.size());
        assertEquals("doc1", result.get(0).getId());
    }

    @Test
    public void testSearchByCreatedDateRange() {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);
        Instant future = now.plusSeconds(3600);

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id("doc1")
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(past)
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id("doc2")
                .title("Title2")
                .content("Content2")
                .author(author)
                .created(future)
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(past)
                .createdTo(now)
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);
        assertEquals(1, result.size());
        assertEquals("doc1", result.get(0).getId());
    }
}
