package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class Break5x5 extends AreaBreakAbility {
    public Break5x5(ToolsFast plugin) { super(plugin, 2); }
    @Override public String id() { return "BREAK_5X5"; }
}
