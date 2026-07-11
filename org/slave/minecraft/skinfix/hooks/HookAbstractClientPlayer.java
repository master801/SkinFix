package org.slave.minecraft.skinfix.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.util.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Master on 6/30/2026 at 4:13 PM
 *
 * @author Master
 */
public final class HookAbstractClientPlayer {

    private static Logger logger;

    /**
     * Gets the player's profile based on username
     * @param username Player's username
     * @return Player Profile as a JSON object
     */
    public static JsonObject getPlayerProfileFromUsername(final String username) {
        JsonObject playerProfile = null;
        try {
            URL urlPlayerProfile = new URL(
                    String.format("https://api.mojang.com/users/profiles/minecraft/%s", StringUtils.stripControlCodes(username))
            );
            InputStream is = null;
            try {
                is = urlPlayerProfile.openStream();
                if (is != null) {
                    InputStreamReader isReader = null;
                    try {
                        isReader = new InputStreamReader(is);
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(isReader);
                            playerProfile = new JsonParser().parse(bufferedReader)
                                    .getAsJsonObject();
                        } finally {
                            if (bufferedReader != null) bufferedReader.close();
                        }
                    } finally {
                        if (isReader != null) isReader.close();
                    }
                }
            } catch(SSLException e) {
                HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught SSLException while opening stream to player profile! Please use Java 8 or higher!", e);
            } finally {
                if (is != null) is.close();
            }
        } catch(IOException e) {
            HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException getting the player's profile!", e);
        }
        return playerProfile;
    }

    /**
     * Actually gets the player's profile based on UUID
     * @param uuid Player's UUID
     * @return Player's profile as a JSON object
     */
    public static JsonObject getPlayerProfileFromUUID(final String uuid) {
        JsonObject playerProfile = null;
        try {
            URL urlPlayerProfile = new URL(
                    String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", StringUtils.stripControlCodes(uuid))
            );
            InputStream is = null;
            try {
                is = urlPlayerProfile.openStream();
                if (is != null) {
                    InputStreamReader isReader = null;
                    try {
                        isReader = new InputStreamReader(is);
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(isReader);
                            playerProfile = new JsonParser().parse(bufferedReader)
                                    .getAsJsonObject();
                        } finally {
                            if (bufferedReader != null) bufferedReader.close();
                        }
                    } finally {
                        if (isReader != null) isReader.close();
                    }
                }
            } catch(SSLException e) {
                HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught SSLException while opening stream to player UUID profile! Please use Java 8 or higher!", e);
            } finally {
                if (is != null) is.close();
            }
        } catch(IOException e) {
            HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException getting the player's UUID profile!", e);
        }
        return playerProfile;
    }

    /**
     * Gets the player's textures from JSON object containing the player's (UUID) profile
     * @param jsonObjectPlayerProfileUUID Player's UUID profile as a JSON object
     * @return Player's textures as JSON object
     */
    public static JsonObject getPlayerProfileUUIDTextures(final JsonObject jsonObjectPlayerProfileUUID) {
        if (jsonObjectPlayerProfileUUID != null) {
            String base64Textures = null;
            if (jsonObjectPlayerProfileUUID.has("properties")) {
                JsonArray properties = jsonObjectPlayerProfileUUID.getAsJsonArray("properties");
                for(int i = 0; i < properties.size(); i++) {
                    JsonObject property = properties.get(i).getAsJsonObject();
                    if (property.has("name") && property.get("name").getAsString().equals("textures")) {
                        base64Textures = property.get("value").getAsString();
                        break;
                    }
                }
            }
            if (base64Textures != null) {
                return new JsonParser().parse(
                                new String(Base64.decode(base64Textures)))//Dirty hack
                        .getAsJsonObject();
            }
        }
        return null;
    }

    /**
     * Called from {@link net.minecraft.client.entity.AbstractClientPlayer#getSkinUrl(String)}
     * @param username Player's username
     * @return String containing the URL, or null if none was found.
     */
    public static String getSkinURL(final String username) {
        JsonObject playerProfileUsername = HookAbstractClientPlayer.getPlayerProfileFromUsername(username);
        if (playerProfileUsername != null) {
            String uuid = playerProfileUsername.getAsJsonPrimitive("id").getAsString();
            if (uuid != null) {
                JsonObject jsonObjectPlayerProfileUUID = HookAbstractClientPlayer.getPlayerProfileFromUUID(uuid);
                JsonObject jsonObjectPlayerProfileTextures = HookAbstractClientPlayer.getPlayerProfileUUIDTextures(jsonObjectPlayerProfileUUID);
                if (jsonObjectPlayerProfileTextures != null && jsonObjectPlayerProfileTextures.has("textures")) {
                    JsonObject jsonObjectTextures = jsonObjectPlayerProfileTextures.getAsJsonObject("textures");
                    if (jsonObjectTextures.has("SKIN")) {
                        return jsonObjectTextures.getAsJsonObject("SKIN")
                                .get("url").getAsString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Called from {@link net.minecraft.client.entity.AbstractClientPlayer#getCapeUrl(String)}
     * @param username Player's username
     * @return String containing the URL, or null if none was found.
     */
    public static String getCapeURL(final String username) {
        JsonObject playerProfileUsername = HookAbstractClientPlayer.getPlayerProfileFromUsername(username);
        if (playerProfileUsername != null) {
            String uuid = playerProfileUsername.getAsJsonPrimitive("id").getAsString();
            if (uuid != null) {
                JsonObject jsonObjectPlayerProfileUUID = HookAbstractClientPlayer.getPlayerProfileFromUUID(uuid);
                JsonObject jsonObjectPlayerProfileTextures = HookAbstractClientPlayer.getPlayerProfileUUIDTextures(jsonObjectPlayerProfileUUID);
                if (jsonObjectPlayerProfileTextures != null && jsonObjectPlayerProfileTextures.has("textures")) {
                    JsonObject jsonObjectTextures = jsonObjectPlayerProfileTextures.getAsJsonObject("textures");
                    if (jsonObjectTextures.has("CAPE")) {
                        return jsonObjectTextures.getAsJsonObject("CAPE")
                                .get("url").getAsString();
                    }
                }
            }
        }
        return null;
    }

    static {
        //Set up the logger
        final String channel = "SkinFix";
        FMLLog.makeLog(channel);
        HookAbstractClientPlayer.logger = Logger.getLogger(channel);
    }

}
