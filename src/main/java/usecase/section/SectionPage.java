package usecase.section;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe DTO relativa alla sezione.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class SectionPage {

    private Integer id;
    private String name;
    private String description;
    private String picture;
    private String banner;
    private Integer nFollowers;
    private boolean isFollowed;
}
