package decompiled.cybersource.flex.android.jwt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

public final class Tools {

    static final SecureRandom PRNG;
    static final Charset ASCII = StandardCharsets.US_ASCII;
    static final Charset UTF8 = StandardCharsets.UTF_8;
    static final String DOT = ".";
    static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final Set<Character> MUST_ESCAPE; // characters that must be escaped
    private static final Set<Character> WANT_ESCAPE; // additional characters we want to escape

    private Tools() {
        throw new IllegalStateException("Utility class");
    }

    static {
            PRNG = new SecureRandom();
    }

    static {
        Set<Character> mandatory = new HashSet<>();
        mandatory.add('"');
        mandatory.add('\\');
        MUST_ESCAPE = Collections.unmodifiableSet(mandatory);
    }

    static {
        Set<Character> optional = new HashSet<>();
        optional.add('<');
        optional.add('>');
        optional.add('&');
        optional.add('=');
        optional.add('\'');
        //optional.add('/');
        WANT_ESCAPE = Collections.unmodifiableSet(optional);
    }

    static int countDots(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '.') {
                count++;
            }
        }
        return count;
    }

    private static final char[] ALPHABET = "1234567890AaBbCcDdEeFfGg".toCharArray();

    static String randomId(int lenght) {
        final StringBuilder id = new StringBuilder();
        while (lenght-- > 0) {
            id.append(ALPHABET[PRNG.nextInt(ALPHABET.length)]);
        }
        return id.toString();
    }

    /**
     * Converts a Map into a Base64 encoded JSON
     *
     * @param map to be converted into JSON
     * @return base64 URL friendly encoded object
     */
    static String base64(final Map<String, Object> map) {
        return ENCODER.encodeToString(bytesUTF8(map));
    }

    static byte[] bytesASCII(String s) {
        return s.getBytes(ASCII);
    }

    static byte[] bytesUTF8(final Map<String, Object> map) {
        StringBuilder out = new StringBuilder();
        write(out, map);
        return out.toString().getBytes(UTF8);
    }

    /**
     * Serializes a Map object (can be nested) into JSON structure
     *
     * @param out StringBuilder to write to
     * @param map to be converted into JSON object
     */
    static void write(final StringBuilder out, final Map<String, Object> map) {
        out.append('{');
        for(Map.Entry<String, Object> e : map.entrySet()) {
            out.append('\"');
            write(out, e.getKey());
            final Object v = e.getValue();
            out.append("\":");
            if (v instanceof String) {
                out.append('\"');
                write(out, v.toString());
                out.append('\"');
            } else if (v instanceof Long) {
                out.append(v);
            } else if (v instanceof Map) {
                write(out, (Map) v);
            } else if (v == null) {
                out.append("null");
            }
            out.append(',');
        }
        if (out.charAt(out.length() - 1) == ',') out.deleteCharAt(out.length() - 1);
        out.append('}');
    }

//    static String escape(String plainText) {
//        StringBuilder out = new StringBuilder();
//        write(out, plainText);
//        return out.toString();
//    }

    /**
     * Escapes the String in JSON and HTML friendly manner
     *
     * @param out
     * @param text to be escaped
     */
    static void write(StringBuilder out, String text) {
        for (int i = 0; i < text.length(); i++) {
            int codePoint = Character.codePointAt(text, i);
            if (!ctrlChar(codePoint) && !escape(codePoint)) {
                out.append(text.charAt(i));
                continue;
            }
            switch (codePoint) {
                case '\b':
                    out.append("\\b");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\f':
                    out.append("\\f");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                case '/':
                    out.append("\\/");
                    break;
                case '"':
                    out.append("\\\"");
                    break;
                default:
                    appendHex(codePoint, out);
                    break;
            }
        }
    }

    /**
     * JSON spec defines these code points as control characters, so they must be escaped
     *
     * @param codePoint
     * @return true if control character
     */
    private static boolean ctrlChar(int codePoint) {
        return codePoint < 0x20
                || codePoint == 0x2028  // Line separator
                || codePoint == 0x2029  // Paragraph separator
                || (codePoint >= 0x7f && codePoint <= 0x9f);
    }

    private static boolean escape(int codepoint) {
        final char c = (char) codepoint;
        return MUST_ESCAPE.contains(c) || WANT_ESCAPE.contains(c) || Character.isSupplementaryCodePoint(codepoint);
    }

    private static void appendHex(int codePoint, StringBuilder out) {
        if (Character.isSupplementaryCodePoint(codePoint)) {
            // Handle supplementary unicode values which are not representable in
            // javascript.  We deal with these by escaping them as two 4B sequences
            // so that they will round-trip properly when sent from java to javascript
            // and back.
            char[] surrogates = Character.toChars(codePoint);
            appendHex(surrogates[0], out);
            appendHex(surrogates[1], out);
            return;
        }
        out.append("\\u")
                .append(HEX_DIGITS[(codePoint >>> 12) & 0xf])
                .append(HEX_DIGITS[(codePoint >>> 8) & 0xf])
                .append(HEX_DIGITS[(codePoint >>> 4) & 0xf])
                .append(HEX_DIGITS[codePoint & 0xf]);
    }

    /**
     * Splits JWT into parts and does Base64 decoding of parts.
     *
     * @param token string with two (JWS) or four (JWE) dots
     * @return an Object array (mix of Strings and byte arrays)
     */
    static Object[] splitToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("The token must not be null.");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3 && parts.length != 5) {
            throw new IllegalArgumentException(String.format("The token was expected to have 3 parts (JWS) or 5 parts (JWE), but got %s.", parts.length));
        }

        Object[] retVal = new Object[parts.length];
        if (parts.length == 3) { // decode JWS
            retVal[0] = new String(DECODER.decode(parts[0]), UTF8);
            retVal[1] = new String(DECODER.decode(parts[1]), UTF8);
            retVal[2] = DECODER.decode(parts[2]);
        } else { // decode JWE
            retVal[0] = new String(DECODER.decode(parts[0]), UTF8);
            retVal[1] = DECODER.decode(parts[1]);
            retVal[2] = DECODER.decode(parts[2]);
            retVal[3] = DECODER.decode(parts[3]);
            retVal[4] = DECODER.decode(parts[4]);
        }

        return retVal;
    }

    static class JsonReader {
        private static final Object OBJECT_END = "}".intern();
        private static final Object ARRAY_END = "]".intern();
        private static final Object OBJECT_START = "{".intern();
        private static final Object ARRAY_START = "[".intern();
        private static final Object COLON = ":".intern();
        private static final Object COMMA = ",".intern();
        private static final HashSet<Object> PUNCTUATION = new HashSet<>(Arrays.asList(OBJECT_END, OBJECT_START, ARRAY_END, ARRAY_START, COLON, COMMA));
        private static final int FIRST = 0;
        private static final int CURRENT = 1;
        private static final int NEXT = 2;

        private static Map<Character, Character> escapes = new HashMap<>();

        static {
            escapes.put(Character.valueOf('"'), Character.valueOf('"'));
            escapes.put(Character.valueOf('\\'), Character.valueOf('\\'));
            escapes.put(Character.valueOf('/'), Character.valueOf('/'));
            escapes.put(Character.valueOf('b'), Character.valueOf('\b'));
            escapes.put(Character.valueOf('f'), Character.valueOf('\f'));
            escapes.put(Character.valueOf('n'), Character.valueOf('\n'));
            escapes.put(Character.valueOf('r'), Character.valueOf('\r'));
            escapes.put(Character.valueOf('t'), Character.valueOf('\t'));
        }

        private CharacterIterator it;
        private char c;
        private Object token;
        private StringBuffer buf = new StringBuffer();

        private char next() {
            if (it.getIndex() == it.getEndIndex())
                throw new IllegalArgumentException("Reached end of input at the " +
                        it.getIndex() + "th character.");
            c = it.next();
            return c;
        }

        private char previous() {
            c = it.previous();
            return c;
        }

        private void skipWhiteSpace() {
            do {
                if (Character.isWhitespace(c))
                    ;
                else if (c == '/') {
                    next();
                    if (c == '*') {
                        // skip multiline comments
                        while (c != CharacterIterator.DONE)
                            if (next() == '*' && next() == '/')
                                break;
                        if (c == CharacterIterator.DONE)
                            throw new IllegalArgumentException("Unterminated comment while parsing JSON string.");
                    } else if (c == '/')
                        while (c != '\n' && c != CharacterIterator.DONE)
                            next();
                    else {
                        previous();
                        break;
                    }
                } else
                    break;
            } while (next() != CharacterIterator.DONE);
        }

        public Object read(CharacterIterator ci, int start) {
            it = ci;
            switch (start) {
                case FIRST:
                    c = it.first();
                    break;
                case CURRENT:
                    c = it.current();
                    break;
                case NEXT:
                    c = it.next();
                    break;
            }
            return read();
        }

        public Object read(CharacterIterator it) {
            return read(it, NEXT);
        }

        public Object read(String string) {
            return read(new StringCharacterIterator(string), FIRST);
        }

        private void expected(Object expectedToken, Object actual) {
            if (expectedToken != actual)
                throw new IllegalArgumentException("Expected " + expectedToken + ", but got " + actual + " instead");
        }

        private Object read() {
            skipWhiteSpace();
            char ch = c;
            next();
            switch (ch) {
                case '"':
                    token = readString();
                    break;
                case '[':
                    token = readArray();
                    break;
                case ']':
                    token = ARRAY_END;
                    break;
                case ',':
                    token = COMMA;
                    break;
                case '{':
                    token = readObject();
                    break;
                case '}':
                    token = OBJECT_END;
                    break;
                case ':':
                    token = COLON;
                    break;
                case 't':
                    if (c != 'r' || next() != 'u' || next() != 'e')
                        throw new IllegalArgumentException("Invalid JSON token: expected 'true' keyword.");
                    next();
                    token = Boolean.TRUE;
                    break;
                case 'f':
                    if (c != 'a' || next() != 'l' || next() != 's' || next() != 'e')
                        throw new IllegalArgumentException("Invalid JSON token: expected 'false' keyword.");
                    next();
                    token = Boolean.FALSE;
                    break;
                case 'n':
                    if (c != 'u' || next() != 'l' || next() != 'l')
                        throw new IllegalArgumentException("Invalid JSON token: expected 'null' keyword.");
                    next();
                    token = null;
                    break;
                default:
                    c = it.previous();
                    if (Character.isDigit(c) || c == '-') {
                        token = readNumber();
                    } else throw new IllegalArgumentException("Invalid JSON near position: " + it.getIndex());
            }
            return token;
        }

        private String readObjectKey() {
            Object key = read();
            if (key == null)
                throw new IllegalArgumentException("Missing object key (don't forget to put quotes!).");
            else if (key == OBJECT_END)
                return null;
            else if (PUNCTUATION.contains(key))
                throw new IllegalArgumentException("Missing object key, found: " + key);
            else
                return (String) key;
        }

        private Map<String, Object> readObject() {
            Map<String, Object> ret = new HashMap<>();
            String key = readObjectKey();
            while (token != OBJECT_END) {
                expected(COLON, read()); // should be a colon
                if (token != OBJECT_END) {
                    Object value = read();
                    ret.put(key, value);
                    if (read() == COMMA) {
                        key = readObjectKey();
                        if (key == null || PUNCTUATION.contains(key))
                            throw new IllegalArgumentException("Expected a property name, but found: " + key);
                    } else
                        expected(OBJECT_END, token);
                }
            }
            return ret;
        }

        private Object[] readArray() {
            List<Object> ret = new ArrayList<>();
            Object value = read();
            while (token != ARRAY_END) {
                if (PUNCTUATION.contains(value))
                    throw new IllegalArgumentException("Expected array element, but found: " + value);
                ret.add(value);
                if (read() == COMMA) {
                    value = read();
                    if (value == ARRAY_END)
                        throw new IllegalArgumentException("Expected array element, but found end of array after command.");
                } else
                    expected(ARRAY_END, token);
            }
            return ret.toArray();
        }

        private Number readNumber() {
            int length = 0;
            boolean isFloatingPoint = false;
            buf.setLength(0);

            if (c == '-') {
                add();
            }
            length += addDigits();
            if (c == '.') {
                add();
                length += addDigits();
                isFloatingPoint = true;
            }
            if (c == 'e' || c == 'E') {
                add();
                if (c == '+' || c == '-') {
                    add();
                }
                addDigits();
                isFloatingPoint = true;
            }

            String s = buf.toString();
            Number n = isFloatingPoint
                    ? (length < 17) ? Double.valueOf(s) : new BigDecimal(s)
                    : (length < 20) ? Long.valueOf(s) : new BigInteger(s);
//            return factory().number(n);
            return n;
        }

        private int addDigits() {
            int ret;
            for (ret = 0; Character.isDigit(c); ++ret) {
                add();
            }
            return ret;
        }

        private String readString() {
            buf.setLength(0);
            while (c != '"') {
                if (c == '\\') {
                    next();
                    if (c == 'u') {
                        add(unicode());
                    } else {
                        Object value = escapes.get(Character.valueOf(c));
                        if (value != null) {
                            add(((Character) value).charValue());
                        }
                    }
                } else {
                    add();
                }
            }
            next();
            return buf.toString();
        }

        private void add(char cc) {
            buf.append(cc);
            next();
        }

        private void add() {
            add(c);
        }

        private char unicode() {
            int value = 0;
            for (int i = 0; i < 4; ++i) {
                switch (next()) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        value = (value << 4) + c - '0';
                        break;
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                        value = (value << 4) + (c - 'a') + 10;
                        break;
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                        value = (value << 4) + (c - 'A') + 10;
                        break;
                }
            }
            return (char) value;
        }
    }

}
