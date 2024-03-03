package org.churk.telegrambot.handler.game;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.fact.FactHandler;
import org.churk.telegrambot.handler.game.stats.StatsHandler;
import org.churk.telegrambot.handler.game.stats.StatsService;
import org.churk.telegrambot.handler.sticker.StickerHandler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RandomResponseHandler extends Handler {
    private final FactHandler factHandler;
    private final StickerHandler stickerHandler;
    private final StatsHandler statsHandler;
    private final StatsService statsService;
    private final Random random = new Random();

    private boolean shouldTriggerRandomResponse() {
        return random.nextInt(100) < botProperties.getRandomResponseChance() * 10;
    }

    private CommandHandler selectRandomHandler() {
        List<CommandHandler> handlersToChooseFrom = List.of(
                factHandler,
                stickerHandler,
                statsHandler
        );
        int index = random.nextInt(handlersToChooseFrom.size());
        return handlersToChooseFrom.get(index);
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        if (!shouldTriggerRandomResponse()) {
            return List.of();
        }
        CommandHandler randomHandler = selectRandomHandler();
        if (randomHandler instanceof StatsHandler) {
            Long chatId = context.getUpdate().getMessage().getChatId();
            String randomFirstName = statsService.getRandomFirstName(chatId, LocalDateTime.now().getYear());
            context.setArgs(List.of("user", randomFirstName));
        }
        context.setReply(ThreadLocalRandom.current().nextBoolean());

        return randomHandler.handle(context);
    }

    @Override
    public Command getSupportedCommand() {
        return null;
    }
}
