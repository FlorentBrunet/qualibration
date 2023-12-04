package eu.brnt.qualibration.util;

import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static javafx.scene.paint.Color.*;

public final class ColorUtil {

    private ColorUtil() {
        throw new RuntimeException(ColorUtil.class.getName() + " must not be instantiated");
    }

    // Named inside a nested class to initialize them only when they are needed
    private static final class NamedColors {
        private static final Map<String, Color> NAMED_COLORS = Map.ofEntries(
                Map.entry("aliceblue", ALICEBLUE),
                Map.entry("antiquewhite", ANTIQUEWHITE),
                Map.entry("aqua", AQUA),
                Map.entry("aquamarine", AQUAMARINE),
                Map.entry("azure", AZURE),
                Map.entry("beige", BEIGE),
                Map.entry("bisque", BISQUE),
                Map.entry("black", BLACK),
                Map.entry("blanchedalmond", BLANCHEDALMOND),
                Map.entry("blue", BLUE),
                Map.entry("blueviolet", BLUEVIOLET),
                Map.entry("brown", BROWN),
                Map.entry("burlywood", BURLYWOOD),
                Map.entry("cadetblue", CADETBLUE),
                Map.entry("chartreuse", CHARTREUSE),
                Map.entry("chocolate", CHOCOLATE),
                Map.entry("coral", CORAL),
                Map.entry("cornflowerblue", CORNFLOWERBLUE),
                Map.entry("cornsilk", CORNSILK),
                Map.entry("crimson", CRIMSON),
                Map.entry("cyan", CYAN),
                Map.entry("darkblue", DARKBLUE),
                Map.entry("darkcyan", DARKCYAN),
                Map.entry("darkgoldenrod", DARKGOLDENROD),
                Map.entry("darkgray", DARKGRAY),
                Map.entry("darkgreen", DARKGREEN),
                Map.entry("darkgrey", DARKGREY),
                Map.entry("darkkhaki", DARKKHAKI),
                Map.entry("darkmagenta", DARKMAGENTA),
                Map.entry("darkolivegreen", DARKOLIVEGREEN),
                Map.entry("darkorange", DARKORANGE),
                Map.entry("darkorchid", DARKORCHID),
                Map.entry("darkred", DARKRED),
                Map.entry("darksalmon", DARKSALMON),
                Map.entry("darkseagreen", DARKSEAGREEN),
                Map.entry("darkslateblue", DARKSLATEBLUE),
                Map.entry("darkslategray", DARKSLATEGRAY),
                Map.entry("darkslategrey", DARKSLATEGREY),
                Map.entry("darkturquoise", DARKTURQUOISE),
                Map.entry("darkviolet", DARKVIOLET),
                Map.entry("deeppink", DEEPPINK),
                Map.entry("deepskyblue", DEEPSKYBLUE),
                Map.entry("dimgray", DIMGRAY),
                Map.entry("dimgrey", DIMGREY),
                Map.entry("dodgerblue", DODGERBLUE),
                Map.entry("firebrick", FIREBRICK),
                Map.entry("floralwhite", FLORALWHITE),
                Map.entry("forestgreen", FORESTGREEN),
                Map.entry("fuchsia", FUCHSIA),
                Map.entry("gainsboro", GAINSBORO),
                Map.entry("ghostwhite", GHOSTWHITE),
                Map.entry("gold", GOLD),
                Map.entry("goldenrod", GOLDENROD),
                Map.entry("gray", GRAY),
                Map.entry("green", GREEN),
                Map.entry("greenyellow", GREENYELLOW),
                Map.entry("grey", GREY),
                Map.entry("honeydew", HONEYDEW),
                Map.entry("hotpink", HOTPINK),
                Map.entry("indianred", INDIANRED),
                Map.entry("indigo", INDIGO),
                Map.entry("ivory", IVORY),
                Map.entry("khaki", KHAKI),
                Map.entry("lavender", LAVENDER),
                Map.entry("lavenderblush", LAVENDERBLUSH),
                Map.entry("lawngreen", LAWNGREEN),
                Map.entry("lemonchiffon", LEMONCHIFFON),
                Map.entry("lightblue", LIGHTBLUE),
                Map.entry("lightcoral", LIGHTCORAL),
                Map.entry("lightcyan", LIGHTCYAN),
                Map.entry("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW),
                Map.entry("lightgray", LIGHTGRAY),
                Map.entry("lightgreen", LIGHTGREEN),
                Map.entry("lightgrey", LIGHTGREY),
                Map.entry("lightpink", LIGHTPINK),
                Map.entry("lightsalmon", LIGHTSALMON),
                Map.entry("lightseagreen", LIGHTSEAGREEN),
                Map.entry("lightskyblue", LIGHTSKYBLUE),
                Map.entry("lightslategray", LIGHTSLATEGRAY),
                Map.entry("lightslategrey", LIGHTSLATEGREY),
                Map.entry("lightsteelblue", LIGHTSTEELBLUE),
                Map.entry("lightyellow", LIGHTYELLOW),
                Map.entry("lime", LIME),
                Map.entry("limegreen", LIMEGREEN),
                Map.entry("linen", LINEN),
                Map.entry("magenta", MAGENTA),
                Map.entry("maroon", MAROON),
                Map.entry("mediumaquamarine", MEDIUMAQUAMARINE),
                Map.entry("mediumblue", MEDIUMBLUE),
                Map.entry("mediumorchid", MEDIUMORCHID),
                Map.entry("mediumpurple", MEDIUMPURPLE),
                Map.entry("mediumseagreen", MEDIUMSEAGREEN),
                Map.entry("mediumslateblue", MEDIUMSLATEBLUE),
                Map.entry("mediumspringgreen", MEDIUMSPRINGGREEN),
                Map.entry("mediumturquoise", MEDIUMTURQUOISE),
                Map.entry("mediumvioletred", MEDIUMVIOLETRED),
                Map.entry("midnightblue", MIDNIGHTBLUE),
                Map.entry("mintcream", MINTCREAM),
                Map.entry("mistyrose", MISTYROSE),
                Map.entry("moccasin", MOCCASIN),
                Map.entry("navajowhite", NAVAJOWHITE),
                Map.entry("navy", NAVY),
                Map.entry("oldlace", OLDLACE),
                Map.entry("olive", OLIVE),
                Map.entry("olivedrab", OLIVEDRAB),
                Map.entry("orange", ORANGE),
                Map.entry("orangered", ORANGERED),
                Map.entry("orchid", ORCHID),
                Map.entry("palegoldenrod", PALEGOLDENROD),
                Map.entry("palegreen", PALEGREEN),
                Map.entry("paleturquoise", PALETURQUOISE),
                Map.entry("palevioletred", PALEVIOLETRED),
                Map.entry("papayawhip", PAPAYAWHIP),
                Map.entry("peachpuff", PEACHPUFF),
                Map.entry("peru", PERU),
                Map.entry("pink", PINK),
                Map.entry("plum", PLUM),
                Map.entry("powderblue", POWDERBLUE),
                Map.entry("purple", PURPLE),
                Map.entry("red", RED),
                Map.entry("rosybrown", ROSYBROWN),
                Map.entry("royalblue", ROYALBLUE),
                Map.entry("saddlebrown", SADDLEBROWN),
                Map.entry("salmon", SALMON),
                Map.entry("sandybrown", SANDYBROWN),
                Map.entry("seagreen", SEAGREEN),
                Map.entry("seashell", SEASHELL),
                Map.entry("sienna", SIENNA),
                Map.entry("silver", SILVER),
                Map.entry("skyblue", SKYBLUE),
                Map.entry("slateblue", SLATEBLUE),
                Map.entry("slategray", SLATEGRAY),
                Map.entry("slategrey", SLATEGREY),
                Map.entry("snow", SNOW),
                Map.entry("springgreen", SPRINGGREEN),
                Map.entry("steelblue", STEELBLUE),
                Map.entry("tan", TAN),
                Map.entry("teal", TEAL),
                Map.entry("thistle", THISTLE),
                Map.entry("tomato", TOMATO),
                Map.entry("transparent", TRANSPARENT),
                Map.entry("turquoise", TURQUOISE),
                Map.entry("violet", VIOLET),
                Map.entry("wheat", WHEAT),
                Map.entry("white", WHITE),
                Map.entry("whitesmoke", WHITESMOKE),
                Map.entry("yellow", YELLOW),
                Map.entry("yellowgreen", YELLOWGREEN)
        );
    }

    private static Color[] paletteForDarkBackground = null;

    public static Color[] getPaletteForLightBackground() {
        if (paletteForDarkBackground == null) {
            List<Color> colors = new LinkedList<>();
            for (Color col : NamedColors.NAMED_COLORS.values()) {
                if (col.getBrightness() <= 0.8) {
                    colors.add(col);
                }
            }
            paletteForDarkBackground = colors.toArray(new Color[0]);
        }
        return paletteForDarkBackground;
    }

    private static Color[] matlabPalette = null;

    public static Color[] getMatlabPalette() {
        if (matlabPalette == null) {
            matlabPalette = new Color[]{
                    new Color(0, 0.4470, 0.7410, 1),
                    new Color(0.8500, 0.3250, 0.0980, 1),
                    new Color(0.9290, 0.6940, 0.1250, 1),
                    new Color(0.4940, 0.1840, 0.5560, 1),
                    new Color(0.4660, 0.6740, 0.1880, 1),
                    new Color(0.3010, 0.7450, 0.9330, 1),
                    new Color(0.6350, 0.0780, 0.1840, 1)
            };
        }
        return matlabPalette;
    }
}
