import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */

interface DocumentManagerRepository {

    DocumentManager.Document save(DocumentManager.Document document);

    List<DocumentManager.Document> search(DocumentManager.SearchRequest request);

    Optional<DocumentManager.Document> findById(String id);
}

public class DocumentManager  implements DocumentManagerRepository{

    private final Map<String, Document> documentStorage = new HashMap<>();

    @Override
    public Document save(Document document) {
        if (document.getId() == null) {
            String id = UUID.randomUUID().toString();
            document.setId(id);
        }
        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */

    @Override
    public List<Document> search(SearchRequest request) {

        Predicate<Document> predicate = doc ->
                (request.getTitlePrefixes() == null || request.getTitlePrefixes().isEmpty() ||
                        request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
                        &&
                        (request.getContainsContents() == null || request.getContainsContents().isEmpty() ||
                                request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content)))
                        &&
                        (request.getAuthorIds() == null || request.getAuthorIds().isEmpty() ||
                                request.getAuthorIds().contains(doc.getAuthor().getId()))
                        &&
                        (request.getCreatedFrom() == null || doc.getCreated().isAfter(request.getCreatedFrom().minusSeconds(1)))
                        &&
                        (request.getCreatedTo() == null || doc.getCreated().isBefore(request.getCreatedTo().plusSeconds(1)));

        return documentStorage.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */

    @Override
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(documentStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}