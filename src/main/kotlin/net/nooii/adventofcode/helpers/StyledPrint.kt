package net.nooii.adventofcode.helpers

/**
 * Represents various text styling options for console output.
 *
 * This enum provides a set of ANSI escape codes for different text colors,
 * styles (bold, underlined), and background colors. These can be used to
 * format text output in the console.
 *
 * @property code The ANSI escape code string for the specific style.
 */
enum class PrintStyle(val code: String) {

    BLACK("\u001B[0;30m"),
    RED("\u001B[0;31m"),
    GREEN("\u001B[0;32m"),
    YELLOW("\u001B[0;33m"),
    BLUE("\u001B[0;34m"),
    PURPLE("\u001B[0;35m"),
    CYAN("\u001B[0;36m"),
    WHITE("\u001B[0;37m"),

    BLACK_BOLD("\u001B[1;30m"),
    RED_BOLD("\u001B[1;31m"),
    GREEN_BOLD("\u001B[1;32m"),
    YELLOW_BOLD("\u001B[1;33m"),
    BLUE_BOLD("\u001B[1;34m"),
    PURPLE_BOLD("\u001B[1;35m"),
    CYAN_BOLD("\u001B[1;36m"),
    WHITE_BOLD("\u001B[1;37m"),

    BLACK_UNDERLINED("\u001B[4;30m"),
    RED_UNDERLINED("\u001B[4;31m"),
    GREEN_UNDERLINED("\u001B[4;32m"),
    YELLOW_UNDERLINED("\u001B[4;33m"),
    BLUE_UNDERLINED("\u001B[4;34m"),
    PURPLE_UNDERLINED("\u001B[4;35m"),
    CYAN_UNDERLINED("\u001B[4;36m"),
    WHITE_UNDERLINED("\u001B[4;37m"),

    BLACK_BACKGROUND("\u001B[40m"),
    RED_BACKGROUND("\u001B[41m"),
    GREEN_BACKGROUND("\u001B[42m"),
    YELLOW_BACKGROUND("\u001B[43m"),
    BLUE_BACKGROUND("\u001B[44m"),
    PURPLE_BACKGROUND("\u001B[45m"),
    CYAN_BACKGROUND("\u001B[46m"),
    WHITE_BACKGROUND("\u001B[47m"),

    BLACK_BRIGHT("\u001B[0;90m"),
    RED_BRIGHT("\u001B[0;91m"),
    GREEN_BRIGHT("\u001B[0;92m"),
    YELLOW_BRIGHT("\u001B[0;93m"),
    BLUE_BRIGHT("\u001B[0;94m"),
    PURPLE_BRIGHT("\u001B[0;95m"),
    CYAN_BRIGHT("\u001B[0;96m"),
    WHITE_BRIGHT("\u001B[0;97m"),

    BLACK_BOLD_BRIGHT("\u001B[1;90m"),
    RED_BOLD_BRIGHT("\u001B[1;91m"),
    GREEN_BOLD_BRIGHT("\u001B[1;92m"),
    YELLOW_BOLD_BRIGHT("\u001B[1;93m"),
    BLUE_BOLD_BRIGHT("\u001B[1;94m"),
    PURPLE_BOLD_BRIGHT("\u001B[1;95m"),
    CYAN_BOLD_BRIGHT("\u001B[1;96m"),
    WHITE_BOLD_BRIGHT("\u001B[1;97m"),

    BLACK_BACKGROUND_BRIGHT("\u001B[0;100m"),
    RED_BACKGROUND_BRIGHT("\u001B[0;101m"),
    GREEN_BACKGROUND_BRIGHT("\u001B[0;102m"),
    YELLOW_BACKGROUND_BRIGHT("\u001B[0;103m"),
    BLUE_BACKGROUND_BRIGHT("\u001B[0;104m"),
    PURPLE_BACKGROUND_BRIGHT("\u001B[0;105m"),
    CYAN_BACKGROUND_BRIGHT("\u001B[0;106m"),
    WHITE_BACKGROUND_BRIGHT("\u001B[0;107m");
}

private const val RESET = "\u001B[0m"

/**
 * Prints the given text to the console with optional color styling.
 *
 * This function allows printing text with a specified color style. If no color
 * is provided, it prints the text as-is. When a color is specified, it wraps
 * the text with the appropriate ANSI color codes.
 *
 * @param text The text to be printed. Can be any object, which will be converted
 *             to a string representation.
 * @param color The [PrintStyle] to be applied to the text. If null, no styling
 *              is applied.
 */
fun styledPrint(text: Any?, color: PrintStyle?) {
    if (color == null) {
        print(text)
    } else {
        print("${color.code}$text$RESET")
    }
}

/**
 * [styledPrint] variant, followed by a line break.
 */
fun styledPrintln(text: Any?, color: PrintStyle?) {
    if (color == null) {
        println(text)
    } else {
        println("${color.code}$text$RESET")
    }
}