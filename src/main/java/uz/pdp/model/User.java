package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 Created by: Mehrojbek
 DateTime: 22/02/25 21:18
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    private Long chatId;

    private String username;

    private String firstName;

    private String lastName;

    private List<Music> favoriteMusicList = new ArrayList<>();

    public User(org.telegram.telegrambots.meta.api.objects.User from) {
        this.chatId = from.getId();
        this.username = from.getUserName();
        this.firstName = from.getFirstName();
        this.lastName = from.getLastName();
    }
}
