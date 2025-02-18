package org.churk.telegrambot.service;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Chat;
import org.churk.telegrambot.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void addChat(Update update) {
        chatRepository.save(new Chat(update.getMessage().getChatId(), update));
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public boolean isChatExists(Long chatId) {
        return chatRepository.existsById(chatId);
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }
}
