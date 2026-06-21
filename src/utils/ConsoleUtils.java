package utils;

import java.util.List;

public final class ConsoleUtils {

    private ConsoleUtils() {
    }

    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String GRAY = "\u001B[90m";
    public static final String WHITE = "\u001B[37m";
    public static final String BOLD = "\u001B[1m";

    private static final String H = "─";
    private static final String V = "│";
    private static final String TL = "┌";
    private static final String TR = "┐";
    private static final String BL = "└";
    private static final String BR = "┘";
    private static final String T_RIGHT = "├";
    private static final String T_LEFT = "┤";
    private static final String T_DOWN = "┬";
    private static final String T_UP = "┴";
    private static final String CROSS = "┼";

    public static String truncate(String text, int maxWidth) {
        if (text == null) {
            text = "";
        }
        if (maxWidth <= 3) {
            return text.length() <= maxWidth ? text : text.substring(0, maxWidth);
        }
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - 3) + "...";
    }

    public static String padCell(String text, int width) {
        return String.format("%-" + width + "s", truncate(text, width));
    }

    public static void printTableTop(int[] widths) {
        System.out.println(buildBorderLine(widths, TL, T_DOWN, TR));
    }

    public static void printTableMiddle(int[] widths) {
        System.out.println(buildBorderLine(widths, T_RIGHT, CROSS, T_LEFT));
    }

    public static void printTableBottom(int[] widths) {
        System.out.println(buildBorderLine(widths, BL, T_UP, BR));
    }

    public static void printTableHeader(String[] headers, int[] widths) {
        printTableRow(headers, widths, true);
    }

    public static void printTableRow(String[] cells, int[] widths, boolean header) {
        StringBuilder sb = new StringBuilder();
        sb.append(CYAN).append(V).append(RESET);
        String dataColor = header ? CYAN + BOLD : GRAY;
        for (int i = 0; i < widths.length; i++) {
            String cell = i < cells.length && cells[i] != null ? cells[i] : "";
            sb.append(" ")
                    .append(dataColor)
                    .append(padCell(cell, widths[i]))
                    .append(RESET)
                    .append(" ")
                    .append(CYAN)
                    .append(V)
                    .append(RESET);
        }
        System.out.println(sb);
    }

    public static void printTable(String[] headers, int[] widths, List<String[]> rows) {
        printTableTop(widths);
        printTableHeader(headers, widths);
        printTableMiddle(widths);
        for (String[] row : rows) {
            printTableRow(row, widths, false);
        }
        printTableBottom(widths);
    }

    public static void printMenuBox(String title, String[] options) {
        int width = title.length() + 4;
        for (String option : options) {
            width = Math.max(width, option.length() + 2);
        }
        width = Math.max(width, 43);

        System.out.println();
        System.out.println(CYAN + TL + repeat(H, width) + TR + RESET);
        System.out.println(CYAN + V + RESET + centerInBox(title, width) + CYAN + V + RESET);
        System.out.println(CYAN + T_RIGHT + repeat(H, width) + T_LEFT + RESET);
        for (String option : options) {
            System.out.println(CYAN + V + RESET + " " + padCell(option, width - 1) + CYAN + V + RESET);
        }
        System.out.println(CYAN + BL + repeat(H, width) + BR + RESET);
    }

    public static void printSubMenuTitle(String title) {
        int width = title.length() + 4;
        System.out.println();
        System.out.println(CYAN + TL + repeat(H, width) + TR + RESET);
        System.out.println(CYAN + V + RESET + "  " + BOLD + CYAN + title + RESET + "  " + CYAN + V + RESET);
        System.out.println(CYAN + BL + repeat(H, width) + BR + RESET);
    }

    public static void printPageHeader(String title) {
        int width = Math.max(title.length() + 4, 60);
        System.out.println();
        System.out.println(CYAN + TL + repeat(H, width) + TR + RESET);
        System.out.println(CYAN + V + RESET + " " + WHITE + title + RESET
                + repeat(" ", Math.max(0, width - title.length() - 1)) + CYAN + V + RESET);
        System.out.println(CYAN + BL + repeat(H, width) + BR + RESET);
    }

    public static void printDivider(int length) {
        System.out.println(GRAY + T_RIGHT + repeat(H, length) + T_LEFT + RESET);
    }

    public static void printPrompt(String message) {
        System.out.print(WHITE + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.print(YELLOW + message + RESET);
    }

    public static void printlnSuccess(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public static void printlnWarning(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    public static void printlnInfo(String message) {
        System.out.println(GRAY + message + RESET);
    }

    public static void printlnError(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    public static void printlnData(String message) {
        System.out.println(GRAY + message + RESET);
    }

    private static String buildBorderLine(int[] widths, String left, String mid, String right) {
        StringBuilder sb = new StringBuilder();
        sb.append(CYAN).append(left).append(RESET);
        for (int i = 0; i < widths.length; i++) {
            sb.append(CYAN).append(repeat(H, widths[i] + 2)).append(RESET);
            if (i < widths.length - 1) {
                sb.append(CYAN).append(mid).append(RESET);
            }
        }
        sb.append(CYAN).append(right).append(RESET);
        return sb.toString();
    }

    private static String centerInBox(String text, int width) {
        int pad = Math.max(0, width - text.length());
        int left = pad / 2;
        return repeat(" ", left) + BOLD + CYAN + text + RESET + repeat(" ", pad - left);
    }

    private static String repeat(String s, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
