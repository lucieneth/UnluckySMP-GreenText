# Unlucky GreenText

Server-side Fabric mod for the Unlucky SMP: 2b2t-style greentext. Any chat
message that starts with `>` is recoloured, so quoting and greentexting reads
the way it does everywhere else on the internet.

```
> be me
> join server
> immediately die to a creeper
```

The mod only changes the *colour* of the message — the text itself is passed
through byte for byte. That matters more than it sounds: chat messages are
signed by the sending client, and a mod that rewrites the contents makes
vanilla clients render the message with a "modified" warning. Recolouring
alone leaves the signature intact, so the line just shows up green.

Vanilla clients see it — nothing to install client-side. Works on a dedicated
server, in singleplayer and over LAN.

## Config

`config/unlucky-greentext.json` (created on first run). The generated file
starts with a `//` comment block explaining every option, so the reference is
always at hand — comments are preserved across reloads.

- `enabled` — master switch. When false, chat is left completely alone.
- `greentext_prefix` — what starts a greentext line. Default `>`.
- `greentext_color` — default `&#789922`, 4chan's greentext olive.
- `orangetext_enabled` — a second colour on its own prefix. Off by default.
- `orangetext_prefix` — default `<`.
- `orangetext_color` — default `&#ff7700`.
- `allow_leading_whitespace` — whether `  > like this` counts too. Default true.

Colours accept hex (`&#789922` or `#789922`), a classic `&`-code (`&a`), or a
vanilla name (`green`, `dark_green`). An unreadable value logs a warning once
at load and falls back to the default rather than breaking chat.

Setting a prefix to `""` disables that rule — an empty prefix would otherwise
match every message ever sent.

## Commands

- `/greentext` — status, plus a sample line in the configured colour.
- `/greentext preview <message>` — run a line through the decorator to check
  config edits without having to type it in chat.
- `/greentext reload` — reload the config (op level 2).
- `/greentext toggle` — flip `enabled` and write it to disk (op level 2).

## Building

`gradlew build` — the jar lands in `build/libs/`. Requires Java 25.

Needs [Fabric API](https://modrinth.com/mod/fabric-api) on the server.

## License

CC0-1.0. Do whatever you like with it.
