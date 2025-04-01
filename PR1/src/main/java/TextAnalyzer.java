import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * Class for analyzing text content.
 * Provides methods to count total words and occurrences of a specific word.
 */
@AllArgsConstructor
class TextAnalyzer {
    private final String content;

    /**
     * Counts the total number of words in the content.
     * @return The number of words.
     */
    public int countWords() {
        return getWords().length;
    }

    /**
     * Counts occurrences of a specific word in the content.
     * @param searchWord The word to search for.
     * @return The number of occurrences.
     */
    public int countOccurrences(String searchWord) {
        return (int) Arrays.stream(getWords())
                .filter(word -> word.equalsIgnoreCase(searchWord))
                .count();
    }

    /**
     * Splits the content into words, removing punctuation.
     * @return Array of words in the content.
     */
    private String[] getWords() {
        return content.split("\\s+|[.,!?;:(){}]\\s*");
    }
}
