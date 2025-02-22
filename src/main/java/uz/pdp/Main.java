package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.service.BotService;

public class Main {
    public static void main(String[] args) {
        try {
            BotService botService = BotService.getInstance();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(botService);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}