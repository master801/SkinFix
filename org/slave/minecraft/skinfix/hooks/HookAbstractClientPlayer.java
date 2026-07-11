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

    public static JsonObject getPlayerProfileFromUsername(final String username) {
        JsonObject playerProfile = null;
        try {
            URL urlPlayerProfile = new URL(
                    String.format("https://api.mojang.com/users/profiles/minecraft/%s", StringUtils.stripControlCodes(username))
            );

            try {
                InputStream is = urlPlayerProfile.openStream();
                if (is != null) {
                    try {
                        InputStreamReader isReader = new InputStreamReader(is);
                        try {
                            BufferedReader bufferedReader = new BufferedReader(isReader);
                            playerProfile = new JsonParser().parse(bufferedReader)
                                    .getAsJsonObject();
                            bufferedReader.close();
                        } catch(IOException e) {
                            HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException while reading and parsing player profile!", e);

                        }
                        isReader.close();
                    } catch(IOException e) {
                        HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException while reading!", e);
                    }
                }
            } catch(SSLException e) {
                HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught SSLException while opening stream to player profile! Please use Java 8 or higher!", e);
            } catch(IOException e) {
                HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException opening a stream to the player's profile!", e);
            }
        } catch(IOException e) {
            HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException getting the player's profile!", e);
        }
        return playerProfile;
    }

    public static JsonObject getPlayerProfileFromUUID(final String uuid) {
        JsonObject playerProfile = null;
        try {
            URL urlPlayerProfile = new URL(
                    String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", StringUtils.stripControlCodes(uuid))
            );

            try {
                InputStream is = urlPlayerProfile.openStream();
                if (is != null) {
                    try {
                        InputStreamReader isReader = new InputStreamReader(is);
                        try {
                            BufferedReader bufferedReader = new BufferedReader(isReader);
                            playerProfile = new JsonParser().parse(bufferedReader)
                                    .getAsJsonObject();
                            bufferedReader.close();
                        } catch(IOException e) {
                            HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException while reading and parsing UUID player profile!", e);

                        }
                        isReader.close();
                    } catch(IOException e) {
                        HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException while reading!", e);
                    }
                }
            } catch(SSLException e) {
                HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught SSLException while opening stream to player UUID profile! Please use Java 8 or higher!", e);
            } catch(IOException e) {
                HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException opening a stream to the player's UUID profile!", e);
            }
        } catch(IOException e) {
            HookAbstractClientPlayer.logger.log(Level.SEVERE, "Caught IOException getting the player's UUID profile!", e);
        }
        return playerProfile;
    }

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
        final String channel = "SkinFix";
        FMLLog.makeLog(channel);
        HookAbstractClientPlayer.logger = Logger.getLogger(channel);
    }

}
