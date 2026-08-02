package unlucky.greentext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GreenTextConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("unlucky-greentext.json");

	/** 4chan's greentext olive, the shade everyone recognises. */
	private static final TextColor DEFAULT_GREENTEXT_COLOR = TextColor.fromRgb(0x789922);
	private static final TextColor DEFAULT_ORANGETEXT_COLOR = TextColor.fromRgb(0xFF7700);

	/** Written into the generated config file so the reference is always at hand. */
	private static final String HEADER = String.join("\n",
			"  // ─── Unlucky GreenText ─────────────────────────────────────────────",
			"  // Chat messages that start with a prefix get recoloured, 2b2t style:",
			"  //",
			"  //   > be me                     →  the whole line turns green",
			"  //",
			"  // Only the colour changes — the text itself is left exactly as typed,",
			"  // so vanilla clients never mark the message as modified.",
			"  //",
			"  // enabled                    master switch; false leaves chat alone",
			"  // greentext_prefix           what starts a greentext line",
			"  // greentext_color            &#rrggbb hex, an &-code like &a, or a",
			"  //                            vanilla name like green / dark_green",
			"  // orangetext_enabled         second colour on its own prefix, off by default",
			"  // orangetext_prefix          what starts an orangetext line",
			"  // orangetext_color           same formats as greentext_color",
			"  // allow_leading_whitespace   also match \"  > like this\"",
			"  //",
			"  // Apply changes in game with /greentext reload.",
			"  // ───────────────────────────────────────────────────────────────────",
			"");

	public boolean enabled = true;
	public String greentext_prefix = ">";
	public String greentext_color = "&#789922";
	public boolean orangetext_enabled = false;
	public String orangetext_prefix = "<";
	public String orangetext_color = "&#ff7700";
	public boolean allow_leading_whitespace = true;

	// Parsed once per load so chat decoration never re-parses a colour string,
	// and so a typo is logged once instead of on every message. transient keeps
	// them out of the written file.
	private transient TextColor greentextColor;
	private transient TextColor orangetextColor;

	public static Path path() {
		return PATH;
	}

	public static GreenTextConfig load() {
		try {
			if (Files.exists(PATH)) {
				try (var reader = Files.newBufferedReader(PATH)) {
					GreenTextConfig config = GSON.fromJson(reader, GreenTextConfig.class);
					if (config != null) {
						config.resolveColors();
						return config;
					}
				}
			}
		} catch (IOException | JsonParseException e) {
			UnluckyGreenText.LOGGER.error("Failed to read {}, keeping defaults (file left untouched)", PATH, e);
			return defaults();
		}

		GreenTextConfig config = defaults();
		config.save();
		return config;
	}

	public void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, withHeader(GSON.toJson(this)));
		} catch (IOException e) {
			UnluckyGreenText.LOGGER.error("Failed to write {}", PATH, e);
		}
	}

	public TextColor greentextColor() {
		return greentextColor;
	}

	public TextColor orangetextColor() {
		return orangetextColor;
	}

	private static GreenTextConfig defaults() {
		GreenTextConfig config = new GreenTextConfig();
		config.resolveColors();
		return config;
	}

	private void resolveColors() {
		greentextColor = parseColor(greentext_color, DEFAULT_GREENTEXT_COLOR);
		orangetextColor = parseColor(orangetext_color, DEFAULT_ORANGETEXT_COLOR);
	}

	/** Slots the reference block in after the opening brace. Gson reads leniently, so the comments survive a reload. */
	private static String withHeader(String json) {
		int firstBreak = json.indexOf('\n');
		return firstBreak < 0 ? json : json.substring(0, firstBreak + 1) + HEADER + json.substring(firstBreak + 1);
	}

	/** Accepts {@code &#789922}, {@code #789922}, an &-code like {@code &a}, or a vanilla name like {@code green}. */
	private static TextColor parseColor(String value, TextColor fallback) {
		if (value == null || value.isBlank()) {
			return fallback;
		}

		String cleaned = value.trim();
		if (cleaned.startsWith("&")) {
			cleaned = cleaned.substring(1);
		}

		if (cleaned.length() == 1) {
			ChatFormatting code = ChatFormatting.getByCode(cleaned.charAt(0));
			TextColor color = code == null ? null : TextColor.fromLegacyFormat(code);

			if (color != null) {
				return color;
			}
		} else {
			var parsed = TextColor.parseColor(cleaned).result();

			if (parsed.isPresent()) {
				return parsed.get();
			}
		}

		UnluckyGreenText.LOGGER.warn("Unknown colour \"{}\" in {}, falling back to {}", value, PATH, fallback.serialize());
		return fallback;
	}
}
