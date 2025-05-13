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
    void testTokenizeAskWithAndStore() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of("ask \"Enter a number:\" and store in num");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        assertEquals("ask", tokens.get(0).value);
        assertEquals("Enter a number:", tokens.get(1).value);
        assertEquals("and", tokens.get(2).value);
        assertEquals("store", tokens.get(3).value);
        assertEquals("in", tokens.get(4).value);
        assertEquals("num", tokens.get(5).value);
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

    @Test
    void testTokenizeLogicalOperators() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList(
            "if x is greater than 10 and y is less than 5 or not z then"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        boolean foundAnd = tokens.stream().anyMatch(t -> t.value.equals("AND"));
        boolean foundOr = tokens.stream().anyMatch(t -> t.value.equals("OR"));
        boolean foundNot = tokens.stream().anyMatch(t -> t.value.equals("NOT"));
        assertTrue(foundAnd, "Should recognize 'and' as AND token");
        assertTrue(foundOr, "Should recognize 'or' as OR token");
        assertTrue(foundNot, "Should recognize 'not' as NOT token");
        // Check order
        int andIdx = -1, orIdx = -1, notIdx = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).value.equals("AND")) andIdx = i;
            if (tokens.get(i).value.equals("OR")) orIdx = i;
            if (tokens.get(i).value.equals("NOT")) notIdx = i;
        }
        assertTrue(andIdx > 0 && orIdx > andIdx && notIdx > orIdx);
    }

    @Test
    void testTokenizeDefineAndCall() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList("define myFunc", "call myFunc");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        assertEquals("DEFINE", tokens.get(0).value);
        assertEquals("myFunc", tokens.get(1).value);
        assertEquals("CALL", tokens.get(2).value);
        assertEquals("myFunc", tokens.get(3).value);
    }

    @Test
    void testTokenizeListAssignment() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList(
            "variable items equals apple, banana and cherry",
            "variable fruits equals \"green apple\", orange and \"red cherry\""
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        // First line: should contain LIST_START, apple, banana, cherry, LIST_END
        int listStartIdx = -1, listEndIdx = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).value.equals("LIST_START")) listStartIdx = i;
            if (tokens.get(i).value.equals("LIST_END")) { listEndIdx = i; break; }
        }
        assertTrue(listStartIdx > 0 && listEndIdx > listStartIdx);
        assertEquals("apple", tokens.get(listStartIdx + 1).value);
        assertEquals("banana", tokens.get(listStartIdx + 2).value);
        assertEquals("cherry", tokens.get(listStartIdx + 3).value);
        // Second line: quoted multi-word values
        int listStart2 = -1, listEnd2 = -1;
        for (int i = listEndIdx + 1; i < tokens.size(); i++) {
            if (tokens.get(i).value.equals("LIST_START")) listStart2 = i;
            if (tokens.get(i).value.equals("LIST_END")) { listEnd2 = i; break; }
        }
        assertTrue(listStart2 > 0 && listEnd2 > listStart2);
        assertEquals("green apple", tokens.get(listStart2 + 1).value);
        assertEquals("orange", tokens.get(listStart2 + 2).value);
        assertEquals("red cherry", tokens.get(listStart2 + 3).value);
    }

    @Test
    void testTokenizeReadFileInto() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of("read file data.txt into content");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        assertEquals(5, tokens.size());
        assertEquals("read", tokens.get(0).value);
        assertEquals("file", tokens.get(1).value);
        assertEquals("data.txt", tokens.get(2).value);
        assertEquals("into", tokens.get(3).value);
        assertEquals("content", tokens.get(4).value);
    }

    @Test
    void testTokenizeAppendToFile() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of("append 'hello world' to log.txt");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        assertEquals(4, tokens.size());
        assertEquals("append", tokens.get(0).value);
        assertEquals("hello world", tokens.get(1).value); // Now expects unquoted value
        assertEquals("to", tokens.get(2).value);
        assertEquals("log.txt", tokens.get(3).value);
    }

    @Test
    void testTokenizeDeleteFile() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of("delete file temp.txt");
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        assertEquals(3, tokens.size());
        assertEquals("delete", tokens.get(0).value);
        assertEquals("file", tokens.get(1).value);
        assertEquals("temp.txt", tokens.get(2).value);
    }
}
