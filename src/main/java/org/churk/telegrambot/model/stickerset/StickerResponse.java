package org.churk.telegrambot.model.stickerset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StickerResponse {
    private boolean ok;
    private StickerSet result;
}
