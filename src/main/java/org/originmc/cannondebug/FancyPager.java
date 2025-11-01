/*
 * This file is part of CannonProfiler, licensed under the MIT License (MIT).
 *
 * Copyright (c) Origin <http://www.originmc.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.originmc.cannondebug;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


public final class FancyPager {
    public static final FancyPager DEFAULT = new FancyPager("Default Pager");

    private static final int maxLines = 10;

    private final Text[][] pages;

    private final int pageCount;

    private final int totalLines;

    public Text[][] getPages() {
        return this.pages;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public int getTotalLines() {
        return this.totalLines;
    }

    public FancyPager(String header, Text... lines) {
        if (lines.length == 0)
            lines = new Text[]{Text.literal("Sorry, no results were found.").formatted(Formatting.YELLOW)};
        int totalLines = lines.length + lines.length / 8 * 2;
        if (totalLines % 10 == 0)
            totalLines -= 2;
        this.totalLines = totalLines;
        this.pageCount = totalLines / 10 + 1;
        this.pages = new Text[this.pageCount][10];
        int page = 0;
        for (int i = 0; i <= totalLines; i++) {
            if (i != 0 && i % 10 == 0)
                page++;
            int line = i % 10;
            switch (line) {
                case 0:
                    this.pages[page][line] = Text.literal("_____.[ ").formatted(Formatting.GOLD)
                        .append(Text.literal(header).formatted(Formatting.DARK_GREEN))
                        .append(Text.literal(" - ").formatted(Formatting.GOLD))
                        .append(Text.literal((page + 1) + "/" + this.pageCount).formatted(Formatting.AQUA))
                        .append(Text.literal(" ]._____").formatted(Formatting.GOLD));
                    break;
                case 9:
                    this.pages[page][line] = getFooter(page);
                    break;
                default:
                    this.pages[page][line] = lines[i - 2 * page - 1];
                    break;
            }
        }
        if (this.pages[this.pageCount - 1][9] == null)
            this.pages[this.pageCount - 1][9] = getFooter(this.pageCount - 1);
    }

    private Text getFooter(int page) {
        // Tooltip texts (hover tooltips)
        Text prevTooltip = Text.literal("Previous Page: ")
            .formatted(Formatting.YELLOW)
            .append(Text.literal(String.valueOf(page)).formatted(Formatting.LIGHT_PURPLE));

        Text nextTooltip = Text.literal("Next Page: ")
            .formatted(Formatting.YELLOW)
            .append(Text.literal(String.valueOf(page + 2)).formatted(Formatting.LIGHT_PURPLE));

        // Helper to build clickable parts
        Text prevButton = Text.empty()
            .append(Text.literal("<<< ")
                .formatted(Formatting.DARK_GRAY)
                .styled(s -> s
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cannondebug p " + page))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, prevTooltip))
                ))
            .append(Text.literal("PREV")
                .formatted(Formatting.RED, Formatting.BOLD)
                .styled(s -> s
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cannondebug p " + page))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, prevTooltip))
                )
            );

        Text nextButton = Text.empty()
            .append(
                Text.literal("NEXT")
                    .formatted(Formatting.GREEN, Formatting.BOLD)
                    .styled(s -> s
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cannondebug p " + (page + 2)))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, nextTooltip))
                    ))
            .append(Text.literal(" >>>")
                .formatted(Formatting.DARK_GRAY)
                .styled(s -> s
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cannondebug p " + (page + 2)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, nextTooltip))
                )
            );

        // Now decide what footer to show depending on page
        if (page == 0) {
            if (this.pageCount == 1) {
                return null; // single-page help
            }
            return nextButton;
        } else if (page == this.pageCount - 1) {
            return prevButton;
        } else {
            // Both PREV and NEXT with spacing in between
            return Text.empty()
                .append(prevButton)
                .append(Text.literal("    ")) // spacer
                .append(nextButton);
        }
    }

    public Text[] getPage(int page) {
        return this.pages[page];
    }
}
