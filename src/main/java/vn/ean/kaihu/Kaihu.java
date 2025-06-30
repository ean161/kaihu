package vn.ean.kaihu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Kaihu implements Listener {
    private static final OkHttpClient client = new OkHttpClient();

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Player player = event.getPlayer();

        try {
            String encodedPlayer = URLEncoder.encode(player.getName(), StandardCharsets.UTF_8.name());
            String encodedIsOp = URLEncoder.encode(String.valueOf(player.isOp()), StandardCharsets.UTF_8.name());
            String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.name());

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

                    Bukkit.getScheduler().runTask(App.getInstance(), () -> {
                        player.sendMessage(body);
                    });
                }
            });
        } catch (Exception e) {
            player.sendMessage("Đã xảy ra lỗi khi gửi tin nhắn.");
        }
    }
}
