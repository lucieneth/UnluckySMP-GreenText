package unlucky.greentext;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnluckyGreenText implements ModInitializer {
	public static final String MOD_ID = "unluckygreentext";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static GreenTextConfig CONFIG = GreenTextConfig.load();

	/** Shared so /greentext preview renders a line through exactly the code chat goes through. */
	public static final GreenTextDecorator DECORATOR = new GreenTextDecorator();

	@Override
	public void onInitialize() {
		// Styling phase: we only recolour the message, we never change its contents,
		// so we run after any mod that does rewrite them.
		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, DECORATOR);
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> GreenTextCommand.register(dispatcher));

		LOGGER.info("Unlucky GreenText loaded.");
	}
}
