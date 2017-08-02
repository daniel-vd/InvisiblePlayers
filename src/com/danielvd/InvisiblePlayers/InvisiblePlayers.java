package com.danielvd.InvisiblePlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class InvisiblePlayers extends JavaPlugin implements CommandExecutor, Listener {

    public Server server = Bukkit.getServer();
    public ConsoleCommandSender console = server.getConsoleSender();
    
    List<UUID> isVisible = new ArrayList<UUID>();
    
    //For the update checker
    PluginDescriptionFile pdf = this.getDescription();
	
	@Override
	public void onEnable() {
		
		//Register commands
		getCommand("invisibleplayers").setExecutor(this);
		getCommand("ip").setExecutor(this);
		
		getServer().getPluginManager().registerEvents(this, this);
		
		//BStats metrics
		Metrics metrics = new Metrics(this);
		
		//Show some text in console
        getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            public void run() {
            	console.sendMessage(ChatColor.BLUE + "============================================");
            	console.sendMessage(ChatColor.GREEN + "[InvisiblePlayers]");
            	console.sendMessage(ChatColor.GREEN + "InvisiblePlayers loaded succesfully!");
        	    console.sendMessage(ChatColor.BLUE + "============================================");
        	    checkUpdate();
            }
        }, 20L); //Wait for everything to finish
	}
	
	@EventHandler
	public void onPlayerJoin (PlayerJoinEvent e) {
		//Everybody should be visible on join
		
		Player player = e.getPlayer();
		
		if (isVisible.contains(player.getUniqueId())) {
			
			//Loop through all online players and enable visibility one by one
			for (Player players : Bukkit.getOnlinePlayers()) {
                players.showPlayer(player);
            }
			
			isVisible.remove(player.getUniqueId());
			
			player.sendMessage(ChatColor.BLUE + "You logged out while you were invisible, you are visible now!");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("ip") || cmd.getName().equalsIgnoreCase("invisibleplayers")) {
				if (args.length == 0) {
					if(sender.hasPermission("InvisiblePlayers.main") || sender.isOp()){
						//Show help
						player.sendMessage(ChatColor.BLUE + "Available IP commands:");
						player.sendMessage(ChatColor.BLUE + "/InvisiblePlayers: Main IP command");
						player.sendMessage(ChatColor.BLUE + "/ip: Main command shortcut");
						player.sendMessage(ChatColor.BLUE + "/ip on: Make yourself invisible");
						player.sendMessage(ChatColor.BLUE + "/ip off: Make yourself visible");
						player.sendMessage(ChatColor.BLUE + "/ip on/off <playername>: Make another player invisible/visible");
					}
					
				//Enable invisibility
				} else if (args[0].equalsIgnoreCase("on")) {
					if (args.length == 1) {
						if(sender.hasPermission("InvisiblePlayers.invisible") || sender.isOp()){
							if (!isVisible.contains(player.getUniqueId())) {
								
								//Loop through all online players and disable visibility one by one
								for (Player players : Bukkit.getOnlinePlayers()) {
			                        players.hidePlayer(player);
			                    }
								
								//Add to list to avoid vanishing twice
								isVisible.add(player.getUniqueId());
								
								player.sendMessage(ChatColor.BLUE + "You are invisible now!");
							} else {
								player.sendMessage(ChatColor.RED + "You are invisible already");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You are not allowed to do that" );
						}
					} else if (args.length == 2){
						if(sender.hasPermission("InvisiblePlayers.targetInvisible") || sender.isOp()){
							//Get target player
							Player target = Bukkit.getPlayer(args[1]);
								
							//Target must be valid
							if (target != null) {
									
								if (!isVisible.contains(target.getUniqueId())) {
									
									//Loop through all online players and disable visibility one by one
									for (Player players : Bukkit.getOnlinePlayers()) {
										players.hidePlayer(target);
									}
									
									isVisible.add(target.getUniqueId());
									
									//Notify both sender and target
									player.sendMessage(ChatColor.BLUE + "" + target.getDisplayName() + " is invisible now!");
									target.sendMessage(ChatColor.BLUE + "Player " + player.getDisplayName() + " made you invisible!");
								} else {
									player.sendMessage(ChatColor.RED + "Player " + target.getDisplayName() + " already is invisible!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "Invalid target player");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You are not allowed to do that!");
						}
					}
					
				//Disable invisibility
				} else if (args[0].equalsIgnoreCase("off")){
					if (args.length == 1) {
						if(sender.hasPermission("InvisiblePlayers.invisible") || sender.isOp()){
							if (isVisible.contains(player.getUniqueId())) {
								
								//Loop through all online players and enable visibility one by one
								for (Player players : Bukkit.getOnlinePlayers()) {
									players.showPlayer(player);
								}
							
								//Remove from list to avoid not being able to vanish
								isVisible.remove(player.getUniqueId());
							
								player.sendMessage(ChatColor.BLUE + "You are visible now!");
							} else {
							player.sendMessage(ChatColor.RED + "Make sure you are invisible first: /ip on");
							}
						}  else {
							player.sendMessage(ChatColor.RED + "You are not allowed to do that");
						}
					} else if (args.length == 2){
						if(sender.hasPermission("InvisiblePlayers.targetInvisible") || sender.isOp()){
							//Get target player
							Player target = Bukkit.getPlayer(args[1]);
								
							//Target must be valid
							if (target != null) {
									
								if (isVisible.contains(target.getUniqueId())) {
									
									//Loop through all online players and disable visibility one by one
									for (Player players : Bukkit.getOnlinePlayers()) {
										players.showPlayer(target);
									}
									
									isVisible.remove(target.getUniqueId());
									
									//Notify both sender and target
									player.sendMessage(ChatColor.BLUE + "" + target.getDisplayName() + " is visible now!");
									target.sendMessage(ChatColor.BLUE + "Player " + player.getDisplayName() + " made you visible again!");
								} else {
									player.sendMessage(ChatColor.RED + "Player " + target.getDisplayName() + " already is visible!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "Invalid target player");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You are not allowed to do that!");
						}
					}
				}
			}
		}
		return true;
	}
	
	
	//Update checker
	public void checkUpdate() {
		console.sendMessage(ChatColor.DARK_AQUA + "Checking for InvisiblePlayers updates...");
        final Updater updater = new Updater(this, 14072/*plugin id*/, false);
        final Updater.UpdateResult result = updater.getResult();
        switch (result) {
            case FAIL_SPIGOT: {
            	console.sendMessage(ChatColor.RED + "ERROR: The InvisiblePlayers update checker could not contact Spgitomc.org");
                break;
            }
            case NO_UPDATE: {
            	console.sendMessage(ChatColor.DARK_AQUA + "The InvisiblePlayers update checker works fine!");
            	console.sendMessage(ChatColor.GREEN + "You have the latest InvisiblePlayers version!");
            	console.sendMessage(ChatColor.DARK_AQUA + "Current version: " + pdf.getVersion());
                break;
            }
            case UPDATE_AVAILABLE: {
                String version = updater.getVersion();
            	console.sendMessage(ChatColor.DARK_AQUA + "The InvisiblePlayers updater works fine!");
                console.sendMessage(ChatColor.GREEN + "An InvisiblePlayers update is found!");
                console.sendMessage(ChatColor.DARK_AQUA + "Your version: " + pdf.getVersion() + ". Newest Version: " + version);
                @SuppressWarnings("unused")
				Boolean updateAvailable = true;
                break;
            }
            default: {
                console.sendMessage(result.toString());
                break;
            }
        }
}
	
}
