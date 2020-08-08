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
    private static OkHttpClient client;
    private static Gson gson;

    static {
        gson = new GsonBuilder().create();
        client = new OkHttpClient();
    }

    public static void recordResult(UUID winner, UUID loser) throws IOException {
        String baseUrl = DADuels.getInstance().getConfig().getString("webPath");

        Map<String, String> map = new HashMap<>();
        String recordUrl = String.format("%s/duel/private/record", baseUrl);

        map.put("winner", winner.toString());
        map.put("loser", loser.toString());

        String authString = getLogin(
                baseUrl,
                DADuels.getInstance().getConfig().getString("webUsername"),
                DADuels.getInstance().getConfig().getString("webPassword"));

        Request request = new Request.Builder()
                .url(recordUrl)
                .addHeader("Authorization", authString)
                .post(RequestBody.create(gson.toJson(map), MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, String.format("[DADuels] Could not add result for %s and %s, please contact developer.", winner, loser));
                Bukkit.getPlayer(winner).sendMessage(ChatColor.RED + "Please contact sirNik as match did not register");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() != 200) {
                    throw new IOException(String.format("Error Code: %d Could not record match. Please contact sirNik: winner: %s, loser %s", response.code(), winner, loser));
                }

                Bukkit.getLogger().log(Level.INFO, String.format("[DADuels] Recorded match between %s (winner) and %s (loser)", winner, loser));
            }
        });
    }

    private static String getLogin(String  baseUrl, String username, String password) {
        String loginUrl = String.format("%s/authenticate?username=%s&password=%s", baseUrl, username, password);
        Request request = new Request.Builder().url(loginUrl).build();
        Response response = null;
        String loginResponse = null;

        try {
            response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                loginResponse = response.headers().get("Authorization");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                if(response.body() != null) {
                    response.body().close();
                }

                response.close();
            }
        }

        return loginResponse;
    }
}
