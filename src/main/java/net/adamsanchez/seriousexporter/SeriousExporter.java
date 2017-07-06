package net.adamsanchez.seriousexporter;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by adam_sanchez on 7/6/2017.
 */
@SuppressWarnings("unused")
@Plugin(id = "seriousexporter", name = "SeriousExporter", version = "1.0", description = "This plugin scrapes data and exports it in JSON format to be gathered by an External Monitor")
public class SeriousExporter {

    @Inject
    private Game game;
    private Game getGame(){
        return this.game;
    }

    @Inject private PluginContainer plugin;
    private PluginContainer getPlugin(){
        return this.plugin;
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;
    private CommentedConfigurationNode rootNode;

    private static SeriousExporter instance;

    private static SeriousExporter seriousExporterPlugin;

    @Inject
    Logger logger;
    public Logger getLogger()
    {
        return logger;
    }

    private static Optional<UserStorageService> userStorage;

    @Listener
    public void onInitialization(GamePreInitializationEvent event) {
        instance = this;
        userStorage = Sponge.getServiceManager().provide(UserStorageService.class);

        getLogger().info("Serious Exporter loading...");
        getLogger().info("Trying To setup Config Loader");

        Asset configAsset = plugin.getAsset("seriousexporter.conf").orElse(null);

        if (Files.notExists(defaultConfig)) {
            if (configAsset != null) {
                try {
                    getLogger().info("Copying Default Config");
                    getLogger().info(configAsset.readString());
                    getLogger().info(defaultConfig.toString());
                    configAsset.copyToFile(defaultConfig);
                } catch (IOException e) {
                    e.printStackTrace();
                    getLogger().error("Could not unpack the default config from the jar! Maybe your Minecraft server doesn't have write permissions?");
                    return;
                }
            } else {
                getLogger().error("Could not find the default config file in the jar! Did you open the jar and delete it?");
                return;
            }
        }

        reloadConfigs();


    }

    @Listener
    public void onPostInitalization(GamePostInitializationEvent event){
        instance = this;
    }


    @Listener
    public void onServerStart(GameInitializationEvent event)
    {
        seriousExporterPlugin = this;
        registerCommands();
        getLogger().info("Serious Export Has Loaded\n\n\n\n");

    }

///////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////COMMAND MANAGER//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void registerCommands() {

        //////////////////////COMMAND BUILDERS///////////////////////////////////////////////
        CommandSpec reload = CommandSpec.builder()
                .description(Text.of("Reload your configs for seriousexporter"))
                .permission("seriousexporter.commands.admin.reload")
                .executor(new SExportReload())
                .build();
        Sponge.getCommandManager().register(this, reload,"sxreload","seriousexportreload");
    }
    //////////////////////////////COMMAND EXECUTOR CLASSES/////////////////////////////////////
    public class SExportReload implements CommandExecutor {
        public CommandResult execute(CommandSource src, CommandContext args) throws
                CommandException {
            if (reloadConfigs()) {
                src.sendMessage(Text.of("Reloaded successfully!"));
            } else {
                src.sendMessage(Text.of("Could not reload properly :( did you break your config?").toBuilder().color(TextColors.RED).build());
            }
            return CommandResult.success();
        }
    }
    private boolean reloadConfigs() {

        return true;
    }


    public static SeriousExporter getInstance(){
        return instance;
    }


    public static Optional<UserStorageService> getUserStorage(){
        return userStorage;
    }

    public boolean broadCastMessage(String message, String username) {
        if (message.isEmpty()) return false;
        game.getServer().getBroadcastChannel().send(
                TextSerializers.FORMATTING_CODE.deserialize(message));
        return true;
    }




}
