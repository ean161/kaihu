package vn.ean.kaihu;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.md_5.bungee.api.ChatColor;

public class Kaihu implements Listener {
    private static final OkHttpClient client = new OkHttpClient();

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (!message.toLowerCase().contains("kaihu"))
            return;

        Player player = event.getPlayer();

        try {
            String encodedPlayer = URLEncoder.encode(player.getName(), StandardCharsets.UTF_8.name());
            String encodedIsOp = URLEncoder.encode(String.valueOf(player.isOp()), StandardCharsets.UTF_8.name());
            String encodedMsg = URLEncoder.encode(message.replace("@kaihu", ""), StandardCharsets.UTF_8.name());

            String query = String.format("player=%s&isOp=%s&msg=%s", encodedPlayer, encodedIsOp, encodedMsg);

            Request request = new Request.Builder()
                .url("https://ean.vn/models/project/kaihu/unauth_ask.php?" + query)
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    final String[] answer = {""};
                    final String[] command = {""};

                    if (body.contains("[split_param]")) {
                        String[] parts = body.split("\\[split_param\\]", 2);
                        answer[0] = parts[0].trim();
                        command[0] = parts[1].trim();
                    } else {
                        answer[0] = body.trim();
                    }

                    Bukkit.getScheduler().runTask(App.getInstance(), () -> {
                        if (!answer[0].isEmpty()) {
                            Bukkit.broadcast(Component.text(ChatColor.translateAlternateColorCodes('&', String.format(
                                "%s %s",
                                "&7[&b&lKaihu&r&7]&f",
                                answer[0]
                            ))));
                            // Bukkit.broadcast(Component.text(answer[0]));
                        }

                        if (!command[0].isEmpty()) {
                            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                            Bukkit.dispatchCommand(console, command[0].startsWith("/") ? command[0].substring(1) : command[0]);
                        }
                    });
                }
            });
        } catch (Exception e) { }
    }
}
