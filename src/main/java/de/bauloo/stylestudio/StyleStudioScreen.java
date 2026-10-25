package de.bauloo.stylestudio;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;

import java.util.List;

public class StyleStudioScreen extends Screen {
    private static final int W = 620, H = 340, HEADER = 30, LEFT = 120, RIGHT = 130, FOOT = 26;
    private static final int TILE_W = 66, TILE_H = 58, GAP = 6;

    private static final int C_BG = 0xF00A121C, C_PANEL = 0xFF0F1B28, C_TILE = 0xFF152333;
    private static final int C_ACCENT = 0xFF4FC3F7, C_TEXT = 0xFFDDE6EE, C_DIM = 0xFF7C8A98;

    private final List<StyleCategory> categories = StyleCategory.defaults();
    private int category = 0;
    private int hovered = -1;

    public StyleStudioScreen() {
        super(Text.literal("Style Studio"));
    }

    private int x0() { return (this.width - W) / 2; }
    private int y0() { return (this.height - H) / 2; }

    @Override
    public void render(DrawContext g, int mx, int my, float delta) {
        int x0 = x0(), y0 = y0(), x1 = x0 + W, y1 = y0 + H;
        g.fill(0, 0, this.width, this.height, 0x99000000);
        g.fill(x0, y0, x1, y1, C_BG);

        // Header
        g.drawText(textRenderer, "STYLE STUDIO", x0 + 12, y0 + 7, C_TEXT, false);
        g.drawText(textRenderer, "STYLE ARCHIVE", x0 + 12, y0 + 18, C_DIM, false);

        // Linke Spalte: Kategorien
        int ly = y0 + HEADER + 4;
        g.drawText(textRenderer, "COLLECTIONS", x0 + 10, ly, C_ACCENT, false);
        ly += 14;
        for (int i = 0; i < categories.size(); i++) {
            int ry = ly + i * 13;
            boolean active = i == category;
            if (active) {
                g.fill(x0 + 6, ry - 2, x0 + LEFT - 4, ry + 10, C_PANEL);
                g.fill(x0 + 6, ry - 2, x0 + 8, ry + 10, C_ACCENT);
            }
            g.drawText(textRenderer, categories.get(i).name(), x0 + 14, ry, active ? C_TEXT : C_DIM, false);
            String n = String.valueOf(categories.get(i).styles().size());
            g.drawText(textRenderer, n, x0 + LEFT - 8 - textRenderer.getWidth(n), ry, C_DIM, false);
        }

        // Mitte: Raster
        StyleCategory cat = categories.get(category);
        int gx = x0 + LEFT + 8, gy = y0 + HEADER + 24;
        int gw = W - LEFT - RIGHT - 16;
        g.drawText(textRenderer, cat.name(), gx, y0 + HEADER + 4, C_TEXT, false);
        g.drawText(textRenderer, cat.styles().size() + " styles", gx, y0 + HEADER + 14, C_DIM, false);

        int cols = Math.max(1, (gw + GAP) / (TILE_W + GAP));
        String current = StyleConfig.get(cat.name());
        hovered = -1;
        for (int i = 0; i < cat.styles().size(); i++) {
            int tx = gx + (i % cols) * (TILE_W + GAP);
            int ty = gy + (i / cols) * (TILE_H + 14 + GAP);
            if (ty + TILE_H + 14 > y1 - FOOT) break;
            String style = cat.styles().get(i);
            boolean over = mx >= tx && mx < tx + TILE_W && my >= ty && my < ty + TILE_H;
            if (over) hovered = i;

            int color = style.equals("Vanilla") ? C_TILE : 0xFF000000 | (style.hashCode() & 0x6F6F6F) + 0x202020;
            g.fill(tx, ty, tx + TILE_W, ty + TILE_H, color);
            if (style.equals(current)) {
                border(g, tx, ty, TILE_W, TILE_H, C_ACCENT);
            } else if (over) {
                border(g, tx, ty, TILE_W, TILE_H, C_DIM);
            }
            String label = textRenderer.trimToWidth(style, TILE_W);
            g.drawText(textRenderer, label, tx + (TILE_W - textRenderer.getWidth(label)) / 2,
                    ty + TILE_H + 3, style.equals(current) ? C_ACCENT : C_TEXT, false);
        }

        // Rechte Spalte: Inspector
        int rx = x1 - RIGHT;
        g.fill(rx, y0 + HEADER, x1, y1 - FOOT, C_PANEL);
        g.drawText(textRenderer, "INSPECTOR", rx + 8, y0 + HEADER + 6, C_ACCENT, false);
        g.fill(rx + 8, y0 + HEADER + 20, x1 - 8, y0 + HEADER + 20 + (RIGHT - 16), C_TILE);
        border(g, rx + 8, y0 + HEADER + 20, RIGHT - 16, RIGHT - 16, C_ACCENT);
        String shown = hovered >= 0 ? cat.styles().get(hovered) : current;
        int iy = y0 + HEADER + 20 + RIGHT - 8;
        g.drawText(textRenderer, shown, rx + 8, iy, C_TEXT, false);
        g.drawText(textRenderer, shown.equals(current) ? "ACTIVE" : "Left-click to apply", rx + 8, iy + 12,
                shown.equals(current) ? C_ACCENT : C_DIM, false);

        // Fussleiste
        int fy = y1 - FOOT + 8;
        g.drawText(textRenderer, "RESET", x0 + 12, fy, C_DIM, false);
        String hint = "OPEN: Right Shift";
        g.drawText(textRenderer, hint, x1 - 10 - textRenderer.getWidth(hint), fy, C_DIM, false);

        super.render(g, mx, my, delta);
    }

    private static void border(DrawContext g, int x, int y, int w, int h, int c) {
        g.fill(x, y, x + w, y + 1, c);
        g.fill(x, y + h - 1, x + w, y + h, c);
        g.fill(x, y, x + 1, y + h, c);
        g.fill(x + w - 1, y, x + w, y + h, c);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int x0 = x0(), y0 = y0();

        // Kategorie waehlen
        int ly = y0 + HEADER + 18;
        for (int i = 0; i < categories.size(); i++) {
            int ry = ly + i * 13;
            if (mx >= x0 + 6 && mx < x0 + LEFT - 4 && my >= ry - 2 && my < ry + 10) {
                category = i;
                return true;
            }
        }
        // Style waehlen
        if (hovered >= 0) {
            StyleConfig.set(categories.get(category).name(), categories.get(category).styles().get(hovered));
            return true;
        }
        // Reset
        int fy = y0 + H - FOOT + 8;
        if (mx >= x0 + 12 && mx < x0 + 50 && my >= fy - 2 && my < fy + 10) {
            StyleConfig.reset();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
