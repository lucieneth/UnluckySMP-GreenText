package unlucky.greentext;

import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;

public final class GreenTextDecorator implements ChatDecorator {
	/** {@code sender} is null when the message comes from the console rather than a player. */
	@Override
	public Component decorate(ServerPlayer sender, Component message) {
		GreenTextConfig config = UnluckyGreenText.CONFIG;

		if (!config.enabled) {
			return message;
		}

		String body = message.getString();

		if (config.allow_leading_whitespace) {
			body = body.stripLeading();
		}

		TextColor color = colorFor(config, body);

		if (color == null) {
			return message;
		}

		// Styling only: the plain text still matches what the player signed, so the
		// client renders it normally instead of flagging it as a modified message.
		return message.copy().withStyle(style -> style.withColor(color));
	}

	private static TextColor colorFor(GreenTextConfig config, String body) {
		if (startsWith(body, config.greentext_prefix)) {
			return config.greentextColor();
		}

		if (config.orangetext_enabled && startsWith(body, config.orangetext_prefix)) {
			return config.orangetextColor();
		}

		return null;
	}

	private static boolean startsWith(String body, String prefix) {
		// An empty prefix would colour every message ever sent, so treat it as "off".
		return prefix != null && !prefix.isEmpty() && body.startsWith(prefix);
	}
}
