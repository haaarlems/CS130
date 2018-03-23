import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Lexer {
    private StringBuilder input = new StringBuilder();
    private Token token;
    private String lexema;
    private boolean exhausted = false;
    private String errorMessage = "";
    private Set<Character> blankChars = new HashSet<Character>();

    public Lexer(String filePath) {
        try (Stream<String> st = Files.lines(Paths.get(filePath))) {
            st.forEach(input::append);
        } catch (IOException ex) {
            exhausted = true;
            errorMessage = "Could not read file: " + filePath;
            return;
        }

        blankChars.add('\r');
        blankChars.add('\n');
        blankChars.add((char) 8);
        blankChars.add((char) 9);
        blankChars.add((char) 11);
        blankChars.add((char) 12);
        blankChars.add((char) 32);

        System.out.println(input);

        Pattern comments = Pattern.compile("<!--.*?-->");
        Matcher commentsMatcher = comments.matcher(input);
        input.replace(0, input.length(), commentsMatcher.replaceAll(""));

        System.out.println(input);
        moveAhead();
    }

    public void moveAhead() {
        if (exhausted) {
            return;
        }

        if (input.length() == 0) {
            exhausted = true;
            return;
        }

        ignoreWhiteSpaces();

        if (getToken()) {
            return;
        }

        exhausted = true;

        if (input.length() > 0) {
            errorMessage = "Unexpected symbol: '" + input.charAt(0) + "'";
        }
    }

    private void ignoreWhiteSpaces() {
        int charsToDelete = 0;

        while (blankChars.contains(input.charAt(charsToDelete))) {
            charsToDelete++;
        }

        if (charsToDelete > 0) {
            input.delete(0, charsToDelete);
        }
    }

    private boolean getToken() {
        for (Token t : Token.values()) {
            int end = t.endOfMatch(input.toString());

            if (end != -1) {
                token = t;
                lexema = input.substring(0, end);
                input.delete(0, end);
                return true;
            }
        }

        return false;
    }

    public Token currentToken() {
        return token;
    }

    public String currentLexema() {
        return lexema;
    }

    public boolean isSuccessful() {
        return errorMessage.isEmpty();
    }

    public String errorMessage() {
        return errorMessage;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public static void main(String args[]) throws IOException {

        File file = new File(args[0]);

        String filename = file.getName().split("\\.")[0];
        PrintWriter pw = new PrintWriter(new FileWriter(filename + "out.txt", false));
        pw.flush();

        Lexer lexer = new Lexer(file.getPath());

        while (!lexer.isExhausted()) {
            System.out.printf("%-18s %s\n", lexer.currentToken(), lexer.currentLexema());
            String a = String.format("%-11s %s\n", lexer.currentToken(), lexer.currentLexema());
            pw.print(a);
            lexer.moveAhead();
        }

        if (lexer.isSuccessful()) {
            //System.out.println("Ok! :D");
        } else {
            System.out.println(lexer.errorMessage());
        }
        pw.close();
    }

}