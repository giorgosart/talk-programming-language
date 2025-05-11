package talk;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTest {
    @Test
    void testTokenizeSimpleAssignment() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList("variable x equal 10");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        // Should be 4 tokens: variable, x, equal, 10
        assertEquals(4, tokens.size());
        assertEquals("variable", tokens.get(0).value);
        assertEquals("x", tokens.get(1).value);
        assertEquals("equal", tokens.get(2).value);
        assertEquals("10", tokens.get(3).value);
    }

    @Test
    void testTokenizeIfOtherwise() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList("if x is greater than 10 then", "otherwise");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        // Should be 7 tokens: if, x, is, greater, than, 10, then, otherwise
        assertEquals("otherwise", tokens.get(7).value);
    }

    @Test
    void testTokenizeAskAttempt() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList("ask \"What is your name?\" and store in name", "attempt");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        // Should be: ask, "What, is, your, name?", and, store, in, name, attempt
        assertEquals("attempt", tokens.get(tokens.size() - 1).value);
    }

    @Test
    void testSkipComments() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList("# this is a comment", "variable x");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        assertEquals("variable", tokens.get(0).value);
        // The line number should be 2 (since the comment is skipped)
        assertEquals(2, tokens.get(0).lineNumber);
    }

    @Test
    void testIndentDedentTokens() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList(
            "if x is greater than 10 then",
            "    write \"big\" in log.txt",
            "    if x is greater than 100 then",
            "        write \"huge\" in log.txt",
            "otherwise",
            "    write \"small\" in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        // Should contain INDENT/DEDENT tokens at correct places
        assertTrue(tokens.stream().anyMatch(t -> t.value.equals("INDENT")));
        assertTrue(tokens.stream().anyMatch(t -> t.value.equals("DEDENT")));
        // Check that the first INDENT comes after 'then'
        int thenIdx = -1, indentIdx = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).value.equals("then")) thenIdx = i;
            if (tokens.get(i).value.equals("INDENT")) { indentIdx = i; break; }
        }
        assertTrue(indentIdx > thenIdx);
    }
}
