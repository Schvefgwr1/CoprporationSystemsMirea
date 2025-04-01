import lombok.AllArgsConstructor;

/**
 * Class for analyzing text content.
 */
@AllArgsConstructor
class TextAnalyzer {
    private final String content;

    public int countWords() {
        return getWords().length;
    }

    public int countCharacters() {
        return content.length();
    }

    private String[] getWords() {
        return content.split("\\s+|[.,!?;:(){}]\\s*");
    }
}