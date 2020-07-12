package io.github.sirnik.daduels.website;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.sirnik.daduels.DADuels;
import net.md_5.bungee.api.ChatColor;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ApiQuerier {
    private static final String BASE_URL = "https://sirnik.dev/dastats";
    private static OkHttpClient client;
    private static Gson gson;

    static {
        gson = new GsonBuilder().create();
        client = new OkHttpClient();
    }

    public static void recordResult(UUID winner, UUID loser) throws IOException {
        Map<String, String> map = new HashMap<>();
        String recordUrl = String.format("%s/duel/private/record", BASE_URL);

        map.put("winner", winner.toString());
        map.put("loser", loser.toString());

        String authString = getLogin(
                DADuels.getInstance().getConfig().getString("webUsername"),
                DADuels.getInstance().getConfig().getString("webPassword"));

        Request request = new Request.Builder()
                .url(recordUrl)
                .addHeader("Authorization", authString)
                .post(RequestBody.create(gson.toJson(map).getBytes()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, String.format("[DADuels] Could not add result for %s and %s, please contact developer.", winner, loser));
                Bukkit.getPlayer(winner).sendMessage(ChatColor.RED + "Please contact sirNik as match did not register");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Bukkit.getLogger().log(Level.INFO, String.format("[DADuels] Recorded match between %s (winner) and %s (loser)", winner, loser));
            }
        });
    }

    private static String getLogin(String username, String password) throws IOException {
        String loginUrl = String.format("%s/authenticate?username=%s&password=%s", BASE_URL, username, password);
        Request request = new Request.Builder().url(loginUrl).build();

        try (Response response = client.newCall(request).execute()) {
            return response.headers().get("Authorization");
        }
    }
}
