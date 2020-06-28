package ru.itmo.mit.cli.execution.domain;

import java.util.Objects;

/**
 * CommandWord class
 *
 * This class stores a single command word, i.e.
 * a sequence of characters that was enclosed in
 * spaces or quotation marks in the original typed command.
 *
 * Allows to restore command word by word as it was typed by a user, with all the
 * quotation marks and character escaping symbols.
 */
public class CommandWord {

    private final String rawValue;
    private final String escapedAndStrippedValue;
    public static final CommandWord EMPTY_WORD = new CommandWord("");

    public CommandWord(String rawValue) {
        this.rawValue = rawValue;
        escapedAndStrippedValue = escapeAndStrip(rawValue);
    }

    /**
     * Strips a pair of quotation mark at the ends of a rawValue String, if present
     *
     * Deletes backslash characters representing character escaping
     *
     * @param rawValue
     * @return
     */
    private static String escapeAndStrip(String rawValue) {
        if (rawValue.equals("")) {
            return rawValue;
        }
        // Stripping from quotation marks:
        int lastCharIdx = rawValue.length() - 1;
        boolean singleQuoted = (rawValue.charAt(0) == '\'' && rawValue.charAt(lastCharIdx) == '\'');
        boolean doubleQuoted = (rawValue.charAt(0) == '"' && rawValue.charAt(lastCharIdx) == '"');
        if (singleQuoted) {
            return rawValue.substring(1, rawValue.length() - 1);
        }

        int loopStart = 0;
        int loopEnd = rawValue.length();
        if (doubleQuoted) {
            loopStart++;
            loopEnd--;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean escaped = false;
        for (int i = loopStart; i < loopEnd; i++) {
            char currChar = rawValue.charAt(i);
            if (escaped) {
                stringBuilder.append(currChar);
                escaped = false;
                continue;
            }

            if (currChar == '\\') {
                // In double quotes only double quote symbol
                // can be escaped. In all the other cases
                // backslash should be treated as a simple character
                if (doubleQuoted) {
                    if (i != loopEnd - 1 && rawValue.charAt(i + 1) == '"') {
                        escaped = true;
                    } else {
                        stringBuilder.append(currChar);
                    }
                } else {
                    escaped = true;
                }
            } else {
                stringBuilder.append(currChar);
            }
        }
        return stringBuilder.toString();
    }

    public String getEscapedAndStrippedValue() {
        return escapedAndStrippedValue;
    }

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public String toString() {
        return escapedAndStrippedValue;
    }

    /**
     * Equals compares escaped and stripped value for the sake of testing
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CommandWord) {
            return rawValue.equals(((CommandWord) obj).getEscapedAndStrippedValue());
        }
        return false;
    }

    /**
     * appropriate hash code method
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(escapedAndStrippedValue);
    }
}
