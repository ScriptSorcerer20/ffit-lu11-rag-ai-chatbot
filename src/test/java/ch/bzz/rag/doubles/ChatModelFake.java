package ch.bzz.rag.doubles;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

@RequiredArgsConstructor
public class ChatModelFake implements ChatModel {

    private final String answer;

    @Override
    public ChatResponse call(Prompt prompt) {
        AssistantMessage assistantMessage = new AssistantMessage(answer);
        Generation generation = new Generation(assistantMessage);
        return new ChatResponse(List.of(generation));
    }
}
