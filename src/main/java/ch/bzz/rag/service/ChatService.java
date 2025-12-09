package ch.bzz.rag.service;

import ch.bzz.rag.chat.ChatMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Pattern DOC_ID_PATTERN = Pattern.compile("DOC_\\d+");
    private final ChatModel chatModel;
    private final StoreService storeService;

    public ChatAnswer chat(ChatMode mode, String question) {
        List<Document> docs = storeService.search(question, mode.getNumberOfDocuments());
        for (Document doc : docs) {
            log.debug("Using doc with id '{}' and metadata '{}'", doc.getId(), doc.getMetadata());
        }
        StringBuilder context = new StringBuilder();
        Map<String, String> idMap = buildContext(docs, context);
        ChatOptions options = ChatOptions.builder()
                .temperature(mode.getTemperature())
                .maxTokens(mode.getMaxTokens())
                .build();
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(mode.getSystemPrompt()),
                new UserMessage("""
            <context>
            Kontext:
            %s

            Frage:
            %s
            </context>
            """.formatted(context, question))
        ), options);

        ChatResponse result = chatModel.call(prompt);
        String answer = result.getResult().getOutput().getText();
        List<SourceReference> sources = resolveSources(answer, idMap);
        return new ChatAnswer(answer, sources);
    }

    private Map<String, String> buildContext(List<Document> documents, StringBuilder contextBuilder) {
        Map<String, String> idToSource = new LinkedHashMap<>();

        int index = 1;
        for (Document doc : documents) {
            String docId = "DOC_" + index;
            String source = (String) doc.getMetadata().get("source");

            contextBuilder
                    .append("[")
                    .append(docId)
                    .append("]\n")
                    .append(doc.getText())
                    .append("\n\n");

            idToSource.put(docId, source);
            index++;
        }
        return idToSource;
    }

    private List<SourceReference> resolveSources(String answer, Map<String, String> idToSource) {
        Matcher matcher = DOC_ID_PATTERN.matcher(answer);

        return matcher.results()
                .map(MatchResult::group)
                .distinct()
                .map(id -> new SourceReference(id, idToSource.get(id)))
                .filter(ref -> ref.source() != null)
                .toList();
    }
    public record SourceReference(String id, String source) {}
    public record ChatAnswer(String text, List<SourceReference> sources) {}
}
