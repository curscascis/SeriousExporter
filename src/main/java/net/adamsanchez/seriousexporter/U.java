package net.adamsanchez.seriousexporter;

import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;


/**
 * Created by adam_ on 01/22/17.
 */
public class U {
    public static void info(String info){
        SeriousExporter.getInstance().getLogger().info(info);
    }
    public static void debug(String debug){
        SeriousExporter.getInstance().getLogger().debug(debug);
    }
    public static void error(String error) {
        SeriousExporter.getInstance().getLogger().error(error);
    }
    public static void error(String error, Exception e){
        SeriousExporter.getInstance().getLogger().error(error,e);
    }
    public static void warn(String warn){
        SeriousExporter.getInstance().getLogger().warn(warn);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getName(UUID player){
        Optional<UserStorageService> userStorage =  SeriousExporter.getUserStorage();
        return userStorage.get().get(player).get().getName();
    }
    public static UUID getUUID(String name){
        Optional<UserStorageService> userStorage =  SeriousExporter.getUserStorage();
        return userStorage.get().get(name).get().getUniqueId();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void bcast(String msg, String username){
        SeriousExporter.getInstance().broadCastMessage(msg,username);
    }


}
