package uz.pdp.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.model.Music;
import uz.pdp.utils.BotConstants;

import java.util.ArrayList;
import java.util.List;

/**
 Created by: Mehrojbek
 DateTime: 22/02/25 20:30
 **/
public class ButtonService {

    public static ButtonService instance = new ButtonService();

    public static ButtonService getInstance() {
        return instance;
    }

    public InlineKeyboardMarkup buildInlineBtn(List<Music> musicList) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        inlineKeyboardMarkup.setKeyboard(rows);

        int counter = 1;

        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Music music : musicList) {

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(counter));
            button.setCallbackData(BotConstants.MUSIC_PREFIX + music.getId());//MUSIC:12

            row.add(button);

            if (counter % 5 == 0) {

                rows.add(row);
                row = new ArrayList<>();

            }

            counter++;
        }

        if (!rows.contains(row)){
            rows.add(row);
        }

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup buildOneMusicBtn(Music music) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        inlineKeyboardMarkup.setKeyboard(rows);

        List<InlineKeyboardButton> row = new ArrayList<>();

        rows.add(row);

        InlineKeyboardButton favoriteAndNonFavoriteButton = new InlineKeyboardButton();
        favoriteAndNonFavoriteButton.setText("❤\uFE0F/\uD83D\uDC94");
        favoriteAndNonFavoriteButton.setCallbackData(BotConstants.FAVORITE_AND_NOT_FAVORITE + music.getId());

        row.add(favoriteAndNonFavoriteButton);

        InlineKeyboardButton deleteBtn = new InlineKeyboardButton();
        deleteBtn.setText("❌");
        deleteBtn.setCallbackData(BotConstants.DELETE_MUSIC);

        row.add(deleteBtn);

        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard home() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton searchButton = new InlineKeyboardButton();
        searchButton.setText("Tezkor qidiruv");
        searchButton.setSwitchInlineQueryCurrentChat("");

        row.add(searchButton);

        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }
}
