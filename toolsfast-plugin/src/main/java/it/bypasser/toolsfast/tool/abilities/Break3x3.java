package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class Break3x3 extends AreaBreakAbility {
    public Break3x3(ToolsFast plugin) { super(plugin, 1); }
    @Override public String id() { return "BREAK_3X3"; }
}
