package uz.pdp.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultAudio;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.model.Music;
import uz.pdp.model.User;
import uz.pdp.utils.BotConstants;
import uz.pdp.utils.TimeFormatter;

import java.io.File;
import java.util.*;

/**
 Created by: Mehrojbek
 DateTime: 22/02/25 19:19
 **/
public class BotService extends TelegramLongPollingBot {

    private Map<Long, User> userMap = new HashMap<>();

    public static BotService botService;
    private static final String token = "1875454739:AAFL1VQadzbEm9WFIHWOGVFwvdMlyebvy6U";

    public static BotService getInstance() {
        if (botService == null) {
            synchronized (BotService.class) {
                if (botService == null) {
                    botService = new BotService(token);
                }
            }
        }
        return botService;
    }

    public BotService(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {

        //message kelsa
        if (update.hasMessage()) {

            processMessage(update.getMessage());

            //call back
        } else if (update.hasCallbackQuery()) {

            processCallBack(update.getCallbackQuery());

        } else if (update.hasInlineQuery()) {

            processInlineQuery(update.getInlineQuery());

        } else if (update.hasChannelPost()) {

            processChannelPost(update.getChannelPost());

        }

    }

    private void processChannelPost(Message channelPost) {

        if (!channelPost.hasAudio()) {
            return;
        }

        Audio audio = channelPost.getAudio();
        String title = audio.getTitle();
        String artist = audio.getPerformer();
        Integer duration = audio.getDuration();
        String fileName = audio.getFileName();

        Integer messageId = channelPost.getMessageId();

        String userName = channelPost.getChat().getUserName();

        //https://t.me/{channelUsername}/{messageId}

        String href = "https://t.me/%s/%s".formatted(userName, messageId);

        System.out.println(href);

    }

    private void processInlineQuery(InlineQuery inlineQuery) {

        System.out.println(inlineQuery.toString());

        MusicService musicService = MusicService.getInstance();

        List<Music> musicList = musicService.search(inlineQuery.getQuery());

        if (musicList.isEmpty())
            return;

        // ""-> 106 [0-49] :50 [50-99] : 100 [100-106]
        int start = 0;

        if (!inlineQuery.getOffset().isEmpty()) {
            start = Integer.parseInt(inlineQuery.getOffset());
        }

        int end = Math.min(musicList.size(), start + 50);

        List<InlineQueryResult> audioList = new ArrayList<>();

        for (int i = start; i < end; i++) {

            Music music = musicList.get(i);

            InlineQueryResult inlineQueryResultAudio = convertToAudio(i + 1, music);

            audioList.add(inlineQueryResultAudio);
        }

        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(
                inlineQuery.getId(),
                audioList
        );

        if (end != musicList.size()) {
            answerInlineQuery.setNextOffset(String.valueOf(end));
        }

        try {
            execute(answerInlineQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private InlineQueryResult convertToAudio(int number, Music music) {
        InlineQueryResultAudio inlineQueryResultAudio = new InlineQueryResultAudio();
        inlineQueryResultAudio.setId(music.getId().toString());
        inlineQueryResultAudio.setTitle(number + ". " + music.getTitle());
        inlineQueryResultAudio.setPerformer(music.getArtist());
        inlineQueryResultAudio.setAudioDuration(music.getDuration());
        inlineQueryResultAudio.setCaption("Bu yaxshi audio");
        inlineQueryResultAudio.setAudioUrl("https://t.me/gshggbftbtby/192");
        return inlineQueryResultAudio;
    }

    private void processCallBack(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();

        Message message = callbackQuery.getMessage();

        userMap.putIfAbsent(message.getChatId(), new User(message.getFrom()));

        //
        if (data.startsWith(BotConstants.MUSIC_PREFIX)) {

            downloadMusic(callbackQuery);

        } else if (data.startsWith(BotConstants.DELETE_MUSIC)) {

            deleteMsg(callbackQuery);

        } else if (data.startsWith(BotConstants.FAVORITE_AND_NOT_FAVORITE)) {

            favoriteNotFavorite(callbackQuery);

        }

        //-> <-

    }

    private void favoriteNotFavorite(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getMessage().getChatId();

        //FAV_OR_NOT_FAVORITE:12 -> 12 -> int
        int musicId = Integer.parseInt(callbackQuery.getData().replace(BotConstants.FAVORITE_AND_NOT_FAVORITE, ""));

        Optional<Music> optionalMusic = MusicService.getInstance().getMusicById(musicId);

        if (optionalMusic.isEmpty()) {

            sendMsg(chatId, "Musiqa topilmadi");

        } else {

            Music music = optionalMusic.get();

            User user = userMap.get(chatId);

            List<Music> favoriteMusicList = user.getFavoriteMusicList();

            //avval bo'lgan hozir o'chirmoqchi
            if (favoriteMusicList.contains(music)) {

                favoriteMusicList.remove(music);

                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
//                answerCallbackQuery.setShowAlert(true);
                answerCallbackQuery.setText("❌ Musiqa playlistdan o'chirildi");
                answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());

                try {
                    execute(answerCallbackQuery);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }


                //qo'shmoqchi
            } else {

                favoriteMusicList.add(music);

                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
//                answerCallbackQuery.setShowAlert(true);
                answerCallbackQuery.setText("✅ Musiqa playlistga qo'shildi");
                answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());

                try {
                    execute(answerCallbackQuery);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private void deleteMsg(CallbackQuery callbackQuery) {
        Integer messageId = callbackQuery.getMessage().getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage(
                callbackQuery.getMessage().getChatId().toString(),
                messageId
        );
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void downloadMusic(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getMessage().getChatId();

        //MUSIC:25 -> 25 -> Integer
        int musicId = Integer.parseInt(callbackQuery.getData().replace(BotConstants.MUSIC_PREFIX, ""));

        Optional<Music> optionalMusic = MusicService.getInstance().getMusicById(musicId);

        if (optionalMusic.isEmpty()) {

            sendMsg(chatId, "Musiqa topilmadi");
        } else {

            Music music = optionalMusic.get();

            //C:/Downloads/gulbadan.mp3
            String path = music.getPath();

            SendAudio sendAudio = new SendAudio(
                    chatId.toString(),
                    new InputFile(new File(path))
            );
            InlineKeyboardMarkup keyboardMarkup = ButtonService.getInstance().buildOneMusicBtn(music);
            sendAudio.setReplyMarkup(keyboardMarkup);
            try {
                execute(sendAudio);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void processMessage(Message message) {

        userMap.putIfAbsent(message.getChatId(), new User(message.getFrom()));

        if (message.hasText()) {

            processMessageTxt(message);

        }

    }

    private void processMessageTxt(Message message) {

        String text = message.getText();

        ButtonService buttonService = ButtonService.getInstance();

        if (text.equals("/start")) {

            Long chatId = message.getChatId();
            String sendText = "Xush kelibsiz";

//            sendMsg(chatId, sendText, new ReplyKeyboardRemove(true));
            sendMsg(chatId, sendText, buttonService.home());

        } else if (text.equals("/search")) {

            String sendText = "Qidirayotgan narsangizni kiriting";
            sendMsg(message.getChatId(), sendText);

        } else {

            MusicService musicService = MusicService.getInstance();
            List<Music> musicList = musicService.search(text);

            if (musicList.isEmpty()) {
                sendMsg(message.getChatId(), "Birorta musiqa topilmadi");
            } else {

                InlineKeyboardMarkup keyboardMarkup = buttonService.buildInlineBtn(musicList);

                String sendText = makeMusicText(musicList);

                sendMsg(message.getChatId(), sendText, keyboardMarkup);
            }

        }

    }

    private String makeMusicText(List<Music> musicList) {

        StringBuilder stringBuilder = new StringBuilder();

        int counter = 1;

        for (Music music : musicList) {

            String durationText = TimeFormatter.formatSecondsToMinutes(music.getDuration());

            stringBuilder
                    .append(counter++).append(". ")
                    .append(music.getArtist())
                    .append(" - ")
                    .append(music.getTitle())
                    .append(" - ")
                    .append(durationText)
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.disableWebPagePreview();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "ussd_operator_bot";
    }
}
