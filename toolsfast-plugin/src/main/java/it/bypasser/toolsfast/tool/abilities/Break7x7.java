package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class Break7x7 extends AreaBreakAbility {
    public Break7x7(ToolsFast plugin) { super(plugin, 3); }
    @Override public String id() { return "BREAK_7X7"; }
}
