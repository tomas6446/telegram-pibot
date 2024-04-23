package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.model.Shitpost;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.service.ShitpostingService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ShitpostingHandler extends Handler {
    private final ShitpostingService shitpostingService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        if (context.getArgs().isEmpty()) {
            Shitpost shitpost = shitpostingService.getShitpost();
            return shitpost.isError() ?
                    getReplyMessage(context.getUpdate().getMessage().getChatId(), context.getUpdate().getMessage().getMessageId(), "Not found") :
                    handlePost(context, shitpost.getUrl(), "Random Shitpost");
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toLowerCase());
        return switch (subCommand) {
            case QUOTE -> handleQuote(context);
            default -> {
                Shitpost shitpost = shitpostingService.getShitpostByName(context.getArgs().getFirst());
                yield shitpost.isError() ?
                        getReplyMessage(context.getUpdate().getMessage().getChatId(), context.getUpdate().getMessage().getMessageId(), "Not found") :
                        handlePost(context, shitpost.getUrl(), "Shitpost: " + context.getArgs().getFirst());
            }
        };
    }

    private List<Validable> handleQuote(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        Quote quote = shitpostingService.getQuote();
        String message = quote.getQuote() + "\n\n" + quote.getQuotee();
        return getReplyMessage(chatId, messageId, message);
    }

    private List<Validable> handlePost(HandlerContext context, String url, String caption) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        Optional<File> file = shitpostingService.getFile(url).join();
        if (file.isPresent()) {
            file.get().deleteOnExit();
            return getVideo(chatId, file.get(), caption);
        } else {
            return getReplyMessage(chatId, messageId, "Error downloading file");
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.SHITPOST;
    }
}
