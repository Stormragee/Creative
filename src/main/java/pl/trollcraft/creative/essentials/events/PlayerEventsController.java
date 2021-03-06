package pl.trollcraft.creative.essentials.events;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.trollcraft.creative.Creative;
import pl.trollcraft.creative.core.help.Colors;

import java.util.*;

public class PlayerEventsController {

    /**
     * Time is a value got by equation
     * current millis + player event delay
     */
    private HashMap<String, Long> recentEvents;

    /**
     * Existing and running events. After they are
     * finished, they become recent events.
     */
    private HashMap<PlayerEvent, Long> newEvents;

    /**
     * Events that ended, are marked as to remove.
     */
    private ArrayList<PlayerEvent> toRemove;

    public PlayerEventsController() {
        recentEvents = new HashMap<>();
        newEvents = new HashMap<>();
        toRemove = new ArrayList<>();
        observeEvents();
    }

    /**
     * @param player
     * @return time to be able to organize event
     * or 0 when player can create one.
     */
    public long canOrganize(Player player) {

        String name = player.getName();

        if (recentEvents.containsKey(name)) {

            long now = System.currentTimeMillis();
            long time = recentEvents.get(name);

            if (now >= time)
                return 0;

            return time - now;

        }

        return 0;

    }

    public void organize(Player player, String title) {
        PlayerEvent playerEvent = new PlayerEvent(player, title);

        TextComponent message = new TextComponent(ChatColor.GREEN + "\nNOWY EVENT!\n" +
                ChatColor.GREEN + "Organizator: " + ChatColor.YELLOW + player.getName() + "\n\n" +
                ChatColor.GREEN + "Nazwa: " + ChatColor.YELLOW + title + "\n\n" +
                ChatColor.YELLOW + "Kliknij, by dolaczyc!\n");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event dolacz " + player.getName()));
        Bukkit.getOnlinePlayers().forEach( p -> p.spigot().sendMessage(message) );

        newEvents.put(playerEvent, System.currentTimeMillis() + 1000 * 60 * 5);
    }

    public PlayerEvent findByOwner(Player player) {

        Set<Map.Entry<PlayerEvent, Long>> events = newEvents.entrySet();
        for (Map.Entry<PlayerEvent, Long> entry : events)
            if (entry.getKey().getOwner().equals(player))
                return entry.getKey();

        return null;

    }

    public PlayerEvent findByParticipator(Player player) {

        Set<Map.Entry<PlayerEvent, Long>> events = newEvents.entrySet();
        for (Map.Entry<PlayerEvent, Long> entry : events)
            if (entry.getKey().participates(player))
                return entry.getKey();

        return null;

    }

    public boolean leave(Player player) {
        Set<Map.Entry<PlayerEvent, Long>> events = newEvents.entrySet();
        for (Map.Entry<PlayerEvent, Long> entry : events) {

            if (entry.getKey().participates(player)) {
                entry.getKey().leave(player);
                return true;
            }
        }

        return false;
    }

    public boolean participates(Player player) {
        Set<Map.Entry<PlayerEvent, Long>> events = newEvents.entrySet();
        for (Map.Entry<PlayerEvent, Long> entry : events) {

            if (entry.getKey().getOwner().equals(player))
                return true;

            if (entry.getKey().participates(player))
                return true;
        }

        return false;
    }

    public void finish(PlayerEvent playerEvent) {

        Player player = playerEvent.getOwner();

        long delay = 1000 * 60 * 5; // 5 minutes

        if (player.hasPermission("creative.svip"))
            delay = 1000 * 60 * 2;
        else if (player.hasPermission("creative.vip"))
            delay = 1000 * 60 * 3;

        playerEvent.teleportBack();

        if (player != null && player.isOnline())
            playerEvent.getOwner().sendMessage(Colors.color("&7Twoj event zostal zakonczony automatycznie.\n" +
                "&eKolejny bedziesz mogl zorganizowac za " + (delay/ 1000 / 60) + " minut."));

        playerEvent.message("&cEvent zakonczyl sie!");

        toRemove.add(playerEvent);
        recentEvents.put(player.getName(), System.currentTimeMillis() + delay);

    }

    private void observeEvents() {

        new BukkitRunnable() {

            @Override
            public void run() {

                for (PlayerEvent event : toRemove)
                    newEvents.remove(event);

                toRemove.clear();

                newEvents.forEach( (event, time) -> {

                    if (System.currentTimeMillis() >= time)
                        finish(event);

                } );

            }

        }.runTaskTimer(Creative.getPlugin(), 20, 20 * 3);

    }

    public String debug() {

        StringBuilder builder = new StringBuilder("Nowe Event'y\n");

        newEvents.forEach( (ev, time) ->
            builder.append("- " + ev.getOwner().getName() + ", until " + time + "\n") );

        builder.append("\nRecent events:\n");

        recentEvents.forEach( (player, time) ->
                builder.append("- " + player + ", until " + time + "\n"));

        return builder.toString();

    }

}