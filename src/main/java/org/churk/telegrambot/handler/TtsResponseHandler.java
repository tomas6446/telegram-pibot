package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.TtsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TtsResponseHandler extends ResponseHandler {
    private final TtsService ttsService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            context.setMarkdown(true);
            return createReplyMessage(context,
                    "Please provide a text %s <text>"
                            .formatted(Command.TTS.getPatternCleaned()));
        }

        String text;
        Command command = Command.getTextCommand(args.getFirst(), botProperties.getWinnerName());
        if (command != Command.NONE) {
            HandlerFactory handlerFactory = context.getHandlerFactory();
            CommandHandler handler = handlerFactory.getHandler(command);
            List<Validable> result = handler.handle(UpdateContext.builder()
                    .handlerFactory(handlerFactory)
                    .update(context.getUpdate())
                    .args(args.subList(1, args.size()))
                    .build());
            text = getAudioMessage(result).formatted(botProperties.getWinnerName());
        } else {
            text = args.stream()
                    .map(String::trim)
                    .collect(Collectors.joining(" "))
                    .replace("\n", " ")
                    .replace("\r", " ");
        }
        if (text.isBlank()) {
            context.setMarkdown(true);
            return createReplyMessage(context,
                    "Please provide a text %s <text>"
                            .formatted(Command.TTS.getPatternCleaned()));
        }
        Optional<File> speechFile;
        try {
            context.setMarkdown(true);
            speechFile = ttsService.getSpeech(text);
            return speechFile.isEmpty() ?
                    createReplyMessage(context, "Error generating audio") :
                    createAudioMessage(context, text, speechFile.get());
        } catch (Exception e) {
            return createLogMessage(context, "Error generating audio", e.getMessage());
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.TTS;
    }
}
