package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 Created by: Mehrojbek
 DateTime: 22/02/25 19:30
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Music {

    private Integer id;

    private String title;

    private String artist;

    //on bytes
    private Long length;

    //on seconds
    private Integer duration;

    //file path C:/User/John/Downloads/
    private String path;

}
