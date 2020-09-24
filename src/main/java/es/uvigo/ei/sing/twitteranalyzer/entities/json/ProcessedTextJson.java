package es.uvigo.ei.sing.twitteranalyzer.entities.json;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProcessedTextJson {
    private String processedText = "";
    // Token and POS
    private List<TokenJson> tokens = new ArrayList<>();
    // From bi-grams to n-grams
    private Map<Integer, List<String>> lemmatizedNGrams = new HashMap<>();
}
