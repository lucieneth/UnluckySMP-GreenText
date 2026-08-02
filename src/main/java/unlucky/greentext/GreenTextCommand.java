package unlucky.greentext;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public final class GreenTextCommand {
	private GreenTextCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("greentext")
				.executes(ctx -> {
					ctx.getSource().sendSuccess(GreenTextCommand::info, false);
					return 1;
				})
				.then(Commands.literal("preview")
						.then(Commands.argument("message", StringArgumentType.greedyString())
								.executes(ctx -> {
									Component line = Component.literal(StringArgumentType.getString(ctx, "message"));
									// Same decorator instance chat runs through, so what you see here
									// is exactly what players would see.
									Component decorated = UnluckyGreenText.DECORATOR.decorate(null, line);
									ctx.getSource().sendSuccess(() -> decorated, false);
									return 1;
								})))
				.then(Commands.literal("reload")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.executes(ctx -> {
							UnluckyGreenText.CONFIG = GreenTextConfig.load();
							ctx.getSource().sendSuccess(
									() -> Component.literal("Greentext config reloaded.").withStyle(ChatFormatting.GREEN), true);
							return 1;
						}))
				.then(Commands.literal("toggle")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.executes(ctx -> {
							GreenTextConfig config = UnluckyGreenText.CONFIG;
							config.enabled = !config.enabled;
							config.save();
							ctx.getSource().sendSuccess(
									() -> Component.literal("Greentext " + (config.enabled ? "enabled." : "disabled."))
											.withStyle(config.enabled ? ChatFormatting.GREEN : ChatFormatting.RED), true);
							return 1;
						})));
	}

	private static Component info() {
		GreenTextConfig config = UnluckyGreenText.CONFIG;

		MutableComponent info = Component.literal("Unlucky GreenText").withStyle(ChatFormatting.WHITE)
				.append(config.enabled
						? Component.literal(" enabled").withStyle(ChatFormatting.GREEN)
						: Component.literal(" disabled").withStyle(ChatFormatting.RED))
				.append(rule(config.greentext_prefix, config.greentextColor()));

		if (config.orangetext_enabled) {
			info.append(rule(config.orangetext_prefix, config.orangetextColor()));
		}

		return info.append(Component.literal("\n" + GreenTextConfig.path()).withStyle(ChatFormatting.DARK_GRAY));
	}

	/** One "> sample line" rendered in the colour it would actually appear in. */
	private static Component rule(String prefix, TextColor color) {
		return Component.literal("\n  ").withStyle(ChatFormatting.GRAY)
				.append(Component.literal(prefix + " sample line").withStyle(style -> style.withColor(color)));
	}
}
