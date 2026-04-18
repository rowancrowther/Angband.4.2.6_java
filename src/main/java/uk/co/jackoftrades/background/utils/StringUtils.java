package uk.co.jackoftrades.background.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private final static Logger logger = LogManager.getLogger();
    public static String programmeName;

    /**
     * This is a class solely used for string utilities, and as such should never be instantiated.
     */
    private StringUtils() {}

    /**
     * concatenate a string with a formated string
     * @param string The string to put at the front of the concatenated string
     * @param format The string to format and put at the end of the concatenated string
     * @param objects The objects to use to create the formatted string from format
     * @return A string consisting of string followed by format which has been formatted
     *         with the objects passed in.
     */
    @Contract(pure = true)
    public static @NonNull String strnfcat(String string, String format, Object... objects) {
        return string + String.format(format, objects);
    }

    /**
     * Create a formatted string which is max characters long
     * @param max the length of the resultant string
     * @param format the string to format
     * @param objects the objects to use to format the string
     * @return A formatted string of length max
     */
    public static @NonNull String vstrnfmt(int max, String format, Object... objects) {
        return String.format(format, objects).substring(0, max - 1);
    }

    /**
     * Create a standard formatted string and return it with no size limit
     * @param stringToFormat The string to format
     * @param objects The objects to format the string with
     * @return A formatted string
     */
    @Contract(pure = true)
    public static @NonNull String vformat(String stringToFormat, Object... objects) {
        return String.format(stringToFormat, objects);
    }

    /**
     * Return a substring of a formatted string from 0 to max - 1
     * @param stringToFormat The string to format
     * @param max The length of the return value
     * @param objects The objects to use in formatting the string
     * @return A formatted string of size max
     */
    public static @NonNull String strnfmt(String stringToFormat, int max, Object... objects){
        return String.format(stringToFormat, objects).substring(0, max - 1);
    }

    /**
     * Format a string with a set of objects
     * @param format The string to format
     * @param objects The objects used to format that string
     * @return A formatted string
     */
    @Contract(pure = true)
    public static @NonNull String format(@NotNull String format, Object... objects) {
        return String.format(format, objects);
    }

    /**
     * Pluralise a verb based on the number of them
     * @param number The number of items we are pluralising
     * @return nothing in number is one, the string "s" if it is more than that
     */
    @Contract(pure = true)
    public static @NonNull String plural(int number) {
        return (number == 1) ? "" : "s";
    }

    /**
     * Get the singular or plural of a word based on the number of items
     * we are dealing with
     * @param number The number of items we are dealing with
     * @param singular The result if it is a single item we are dealing with
     * @param plural The result if it is more than one item we are dealing with
     * @return singular if there is only one item (number == 1), plural otherwise
     */
    public static String verbAgreement(int number, @NotNull String singular, @NotNull String plural) {
        return (number == 1) ? singular : plural;
    }

    /**
     * Perform a substring of the first number characters of string
     * @param string the string to substring
     * @param number the number of characters to return
     * @return THe first number characters of string
     */
    @Contract(pure = true)
    public static @NonNull String clipTo(@NonNull String string, @NotNull int number) {
        return string.substring(0, number);
    }

    /**
     * Determine whether two strings are equal
     * @param s one of the strings to determine if it is equal to the other (t)
     * @param t one of the strings to determine if it is equal to the other (s)
     * @return true if s.equals(t), false otherwise
     */
    @Contract(pure = true)
    public static boolean streq(@NonNull String s, @NotNull String t) {
        return s.equals(t);
    }

    /**
     * Determines if a character is printable or not return a character
     * @param c THe character we are testing
     * @return the character if it is printable, '\0' if it is not
     */
    public static boolean isPrint(@NotNull char c) {
        String s = String.valueOf(c);
        String stripped = s.replaceAll("[^\\x20-\\x7E]", "");
        return (!stripped.isEmpty());
    }

    /**
     * Returns whether a particular string contains a given character
     * @param string The string to search
     * @param ch The character to look for
     * @return true if it is found in the string, false otherwise
     */
    @Contract(pure = true)
    public static boolean textSchr(@NonNull String string, @NotNull char ch) {
        return string.indexOf(ch) >= 0;
    }

    /**
     * Util to return the length of a string
     * TODO: Change all of these to string.length() as we find them.
     * @param string The string we are looking at
     * @return The number of characters in this string
     */
    @Contract(pure = true)
    public static int stringLength(@NonNull String string) {
        return string.length();
    }

    /**
     * Compare two strings case insensitively
     * @param string1 The first string to compare
     * @param string2 The second string to compare
     * @return -1 if string1 is before string2 alphabetically ignoring case<br>
     *          1 if string1 is after string2 alphabetically ignoring case<br>
     *          0 if they are identical ignoring case
     */
    @Contract(pure = true)
    public static int strICmp(@NonNull String string1, @NotNull String string2) {
        return string1.compareToIgnoreCase(string2);
    }

    /**
     * Compare the first n characters case insensitively of two different strings
     * @param string1 The first string
     * @param string2 The second string
     * @param n The number of characters to compare
     * @return -1 if string1's first n characters are before string2's first n characters alphabetically ignoring case<br>
     *          1 if string1's first n characters are after string2's first n characters alphabetically ignoring case<br>
     *          0 if the first n characters of both strings are identical ignoring case
     */
    public static int strNICmp(@NonNull String string1, @NonNull String string2, int n) {
        int maxLength = string1.length();
        if (string2.length() < maxLength) maxLength = string2.length();
        if (n < maxLength) maxLength = n;
        String a = string1.substring(0, maxLength);
        String b = string2.substring(0, maxLength);

        return strICmp(a, b);
    }

    /**
     * Search a given string for a substring, and return the substring and all subsequent characters from the search
     * string. This comparison is case-insensitive.
     * @param toSearch The string to search through
     * @param toFind The string to search for
     * @return If the string toFind is found in the string toSearch, then the return the substring of toSearch starting
     * at the first point the toFind string is found, otherwise return the toFind string.
     */
    public static @NonNull String strIStr(@NonNull String toSearch, @NotNull String toFind) {
        int foundAt = toSearch.toLowerCase().indexOf(toFind.toLowerCase());
        return foundAt == -1 ? toFind : toSearch.substring(foundAt);
    }

    /**
     * Copy a source string of size length to a target string, and then return that target string
     * @param source The source string to copy
     * @param length The number of characters to copy
     * @return A string of length length consisting of the first length characters of the source string.
     */
    @Contract(pure = true)
    public static @NonNull String strCpy(@NonNull String source, @NotNull int length) {
        if (length > source.length()) length = source.length();
        if (length < 0) length = 0;
        return source.substring(0, length);
    }

    /**
     * Concatenates two strings together and returns the resultant concatenated string of a specified size.
     * @param toConcatenateTo The first string to concatenate
     * @param source The second string to concatenate
     * @param size The length of the returned string
     * @return A substring of length size of the concatenation of the two strings
     */
    @Contract(pure = true)
    public static @NonNull String strCat(@NotNull String toConcatenateTo, @NotNull String source, @NotNull int size) {
        String result = toConcatenateTo + source;
        if (size < 0) size = 0;
        if (size > result.length()) size = result.length();
        return result.substring(0, size);
    }

    /**
     * Return a string with a capitalised first character
     * @param toCapitalise The string to capitalise
     * @return The incoming string with a capital first letter
     */
    public static @NonNull String strCap(@NonNull String toCapitalise) {
        return toCapitalise.isEmpty() ? "" : Character.toUpperCase(toCapitalise.charAt(0)) + toCapitalise.substring(1);
    }

    /**
     * Determines whether a string has a particular suffix.
     * @param string The string to check
     * @param suffix The suffix to check the string for
     * @return true if the suffix is the end of the string, false otherwise. Note, if the length of the string is less
     * than the length of the suffix or the string is empty then false will be returned; if the suffix is empty then
     * true will be returned.
     */
    public static boolean suffix(@NonNull String string, @NonNull String suffix) {
        if (string.length() < suffix.length()) return false;
        if (string.isEmpty()) return false;
        if (suffix.isEmpty()) return true;
        return (suffix.equals(string.substring(string.length() - suffix.length())));
    }

    /**
     * Determines whether a string has a particular suffix case-insensitive.
     * @param string The string to check
     * @param suffix The suffix to check the string for
     * @return true if the suffix is the end of the string case-insensitive, false otherwise. Note, if the length of the
     * string is less than the length of the suffix or the string is empty then false will be returned; if the suffix is
     * empty then true will be returned.
     */
    public static boolean suffixI(@NonNull String string, @NonNull String suffix) {
        if (suffix.isEmpty()) return true;
        return suffix(string.toUpperCase(), suffix.toUpperCase());
    }

    /**
     * Determines whether a string is a prefix of another string
     * @param string The string to see if prefix is a prefix of
     * @param prefix The potential prefix
     * @return True if prefix is a prefix of string, false otherwise
     */
    public static boolean prefix(@NonNull String string, @NonNull String prefix) {
        return (prefix.equals(string.substring(0, prefix.length())));
    }

    /**
     * Determines whether a string is a prefix of another string case insensitive
     * @param string The string to see if prefix is a prefix of
     * @param prefix The potential prefix
     * @return True if prefix is a prefix of string, false otherwise
     */
    public static boolean prefixI(@NotNull String string, @NotNull String prefix) {
        return prefix(string.toUpperCase(), prefix.toUpperCase());
    }

  /**
   * Remove all the characters c in a string, except where they are preceded by escapeChar
   * @param start The string to replace
   * @param removeChar The character to remove
     * @param escapeChar The character to mark as not to remove
     * @return A string where all the characters removeChar NOT preceeded by a character escapeChar have been removed.
     */
      public static @NonNull String strSkip(@NonNull String start, char removeChar, char escapeChar) {
          Pattern pattern = Pattern.compile("(^|[^" + escapeChar + "])" + removeChar +"+");
          Matcher matcher = pattern.matcher(start);
          if (!matcher.find()) return start;
          return matcher.replaceAll("$1");
      }

    /**
     * Remove all the characters of a particular type in a string, leaving one where two existed
     * @param string The string to strip of the character
     * @param escapeChar The character to strip from the string
     * @return A string with all entries of the character removed, except for the case where there were two adjacent
     * characters, in which case one remains.
     */
    public static String strEscape(String string, char escapeChar) {
        // TODO: Write this
        String regex = "(^|[^" + escapeChar + "])[[e][e]]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("$1");
    }

    /**
     * Returns a decimal integer value of a single hexidecimal character
     * @param ch The character to convert to a hex value, i.e. 0-9, A-F
     * @return the integer between 0 and 15 if the conversion worked, or -1 if there was an error.
     */
    public static int hexCharToInt(char ch) {
        if (ch >= '0' && ch <= '9') return ch - '0';
        if (ch >= 'A' && ch <= 'F') return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f') return ch - 'a' + 10;
        return -1;
    }

    /**
     * Return a decimal value of a hexidecimal string
     * @param string the string to convert from hex characters to an int
     * @return the integer calculated from the string, or -1 if an error occurred
     */
    public static int hexStrToInt(@NonNull String string) {
        int result = 0;
        for (int index = 0; index < string.length(); index++) {
            int current = hexCharToInt(string.charAt(index));
            if (current == -1) return -1;
            result = result * 16 + current;
        }

        return result;
    }

    /**
     * Returns true if the string only contains white space
     * @param string The string to check
     * @return true if string is blank, false otherwise
     */
    public static boolean containsOnlySpaces(@NonNull String string) { return string.isBlank(); }

    /**
     * Check to see if a character is a vowel
     * @param ch The character to check
     * @return true if ch is one of (AEIOUaeiou), false otherwise
     */
    public static boolean isVowel(char ch) {
        ch = Character.toLowerCase(ch);

        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }
}