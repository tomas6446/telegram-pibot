package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Data
@AllArgsConstructor
public final class HandlerContext {
    private Update update;
    private List<String> args;
    private boolean isReply;
}
