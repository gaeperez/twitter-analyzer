package es.uvigo.ei.sing.twitteranalyzer.entities.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenJson {
    private String token = "";
    private String lemma = "";
    private String pos = "";
    private Integer init = -1;
    private Integer end = -1;
}
