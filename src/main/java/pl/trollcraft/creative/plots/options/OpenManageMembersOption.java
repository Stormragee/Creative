package pl.trollcraft.creative.plots.options;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.trollcraft.creative.core.help.Colors;
import pl.trollcraft.creative.plots.pages.PlotPage;
import pl.trollcraft.creative.plots.pages.PlotPagesManager;

import java.util.ArrayList;
import java.util.List;

public class OpenManageMembersOption implements PlotOption {

    private PlotPage previous;
    private Player player;

    public OpenManageMembersOption (PlotPage previous, Player player) {
        this.previous = previous;
        this.player = player;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getName());
        itemMeta.setLore(getLore());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public String getName() {
        return Colors.color("&2&lCzlonkowie");
    }

    @Override
    public List<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(Colors.color("&aZobacz liste"));
        lore.add(Colors.color("&agraczy nalezacych"));
        lore.add(Colors.color("&ado tej dzialki."));
        return lore;
    }

    @Override
    public ClickResponse click(Plot plot) {
        PlotPagesManager.getMembersPage(plot, previous).open(player);
        return ClickResponse.DO_NOTHING;
    }
}
