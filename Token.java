import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Token {
    TAGIDENT ("<+\\w+"),
    NUMBER ("\\d+((\\.\\d+)?+[Ee]?+(\\-{0,1}(?!0)\\d+)?)"),
    IDENT ("\\w+"),
    ENDTAGHEAD("</"),
    PLUS ("\\+"),
    MINUS ("\\-"),
    MULT ("\\*"),
    DIVIDE ("/"),
    MODULO ("%"),
    EXP ("\\*\\*"),
    LPAREN ("\\("),
    RPAREN ("\\)"),
    EQUALS ("="),
    LTHAN (">"),
    GTHAN ("<"),
    COLON (":"),
    COMMA (","),
    SCOLON (";"),
    PERIOD ("\\."),
    QUOTE ("'"),
    DQUOTE ("\""),
    EOF ("\\u001a")
    ;

    private final Pattern pattern;

    Token(String regex) {
        pattern = Pattern.compile("^" + regex);
    }

    int endOfMatch(String s) {
        Matcher m = pattern.matcher(s);

        if (m.find()) {
            return m.end();
        }

        return -1;
    }
}