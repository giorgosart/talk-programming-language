package talk;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TokenDebug {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("app/examples/input_fallback.talk"));
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        for (Tokenizer.Token token : tokens) {
            System.out.println(token.value + " (line " + token.lineNumber + ")");
        }
    }
}
