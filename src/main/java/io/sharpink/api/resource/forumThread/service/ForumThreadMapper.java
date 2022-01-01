package io.sharpink.api.resource.forumThread.service;

import io.sharpink.api.resource.forumThread.dto.ForumThreadRequest;
import io.sharpink.api.resource.forumThread.dto.ForumThreadResponse;
import io.sharpink.api.resource.forumThread.persistence.ForumMessage;
import io.sharpink.api.resource.forumThread.persistence.ForumThread;
import io.sharpink.api.resource.forumThread.persistence.MessagesLoadingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component
public class ForumThreadMapper {

    private final ForumMessageMapper forumMessageMapper;

    @Autowired
    public ForumThreadMapper(ForumMessageMapper forumMessageMapper) {
        this.forumMessageMapper = forumMessageMapper;
    }

    public ForumThreadResponse toThreadResponse(ForumThread source, MessagesLoadingStrategy messagesLoadingStrategy) {
        ForumThreadResponse target = ForumThreadResponse.builder()
            .id(source.getId())
            .title(source.getTitle())
            .authorId(source.getAuthor().getId())
            .authorNickname(source.getAuthor().getNickname())
            .creationDate(source.getCreationDate())
            .messagesCount(source.getMessages().size())
            .build();

        List<ForumMessage> messages = source.getMessages();

        if (isNotEmpty(messages)) {
            Collections.sort(messages);
            ForumMessage lastMessage = messages.get(messages.size() - 1);
            target.setLastMessage(forumMessageMapper.toMessageResponse(lastMessage));
        }

        if (messagesLoadingStrategy == MessagesLoadingStrategy.ENABLED) {
            target.setMessages(forumMessageMapper.toMessageResponseList(messages));
        }

        return target;
    }

    public List<ForumThreadResponse> toThreadResponseList(List<ForumThread> source) {
        return source.stream().map(thread -> this.toThreadResponse(thread, MessagesLoadingStrategy.DISABLED)).collect(Collectors.toList());
    }

    public ForumThread toThread(ForumThreadRequest forumThreadRequest) {
        return ForumThread.builder().title(forumThreadRequest.getTitle()).build();
    }
}
