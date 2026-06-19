package it.bypasser.toolsfast.commands;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Colors;
import it.bypasser.toolsfast.utils.Keys;
import it.bypasser.toolsfast.utils.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ToolsFastCommand implements CommandExecutor, TabCompleter {

    private final ToolsFast plugin;
    public ToolsFastCommand(ToolsFast plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(plugin.messages().message("players-only"));
                return true;
            }
            if (!p.hasPermission("toolsfast.menu")) {
                sendNoPerm(sender);
                return true;
            }
            plugin.gui().openMain(p);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload" -> {
                if (!sender.hasPermission("toolsfast.admin")) { sendNoPerm(sender); return true; }
                plugin.reloadAll();
                sender.sendMessage(plugin.messages().prefix() + Colors.color("&aConfigurazione ricaricata."));
                return true;
            }
            case "selfdestruct", "sd" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(plugin.messages().message("players-only"));
                    return true;
                }
                if (!p.hasPermission("toolsfast.selfdestruct")) { sendNoPerm(sender); return true; }
                handleSelfDestruct(p, args);
                return true;
            }
            case "give" -> {
                if (!sender.hasPermission("toolsfast.admin")) { sendNoPerm(sender); return true; }
                handleGive(sender, args);
                return true;
            }
            case "list" -> {
                if (!sender.hasPermission("toolsfast.menu")) { sendNoPerm(sender); return true; }
                sender.sendMessage(Colors.color("&#C77DFF&lTOOLSFAST &7- Tools disponibili:"));
                for (var t : plugin.toolRegistry().all()) {
                    sender.sendMessage(Colors.color("&d▪ &f" + t.id()));
                }
                return true;
            }
            case "stats" -> {
                if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().message("players-only")); return true; }
                if (!p.hasPermission("toolsfast.menu")) { sendNoPerm(sender); return true; }
                showStats(sender, p.getUniqueId(), p.getName());
                return true;
            }
            case "leaderboard", "top" -> {
                if (!sender.hasPermission("toolsfast.menu")) { sendNoPerm(sender); return true; }
                if (sender instanceof Player p) plugin.gui().openLeaderboard(p);
                else handleLeaderboard(sender);
                return true;
            }
            case "help" -> {
                sendHelp(sender);
                return true;
            }
            default -> {
                sendHelp(sender);
                return true;
            }
        }
    }

    private void handleSelfDestruct(Player p, String[] args) {
        if (args.length < 2) {
            p.sendMessage(plugin.messages().prefix() + Colors.color("&cUso: /toolsfast selfdestruct <time|delayed <time>|remove>"));
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            p.sendMessage(plugin.messages().prefix() + Colors.color("&cDevi tenere un item in mano."));
            return;
        }
        if (args[1].equalsIgnoreCase("remove")) {
            plugin.selfDestruct().removeSelfDestruct(item);
            p.sendMessage(plugin.messages().prefix() + Colors.color("&aSelf destruct rimosso."));
            return;
        }
        if (args[1].equalsIgnoreCase("delayed")) {
            if (args.length < 3) {
                p.sendMessage(plugin.messages().prefix() + Colors.color("&cUso: /toolsfast selfdestruct delayed <time>"));
                return;
            }
            long duration = TimeParser.parseToMillis(args[2]);
            if (duration <= 0) {
                p.sendMessage(plugin.messages().prefix() + Colors.color("&cFormato tempo non valido."));
                return;
            }
            plugin.selfDestruct().applyDelayed(item, duration);
            for (String s : plugin.messages().messages("self-destruct-added")) {
                p.sendMessage(s.replace("%time%", TimeParser.formatRemaining(duration)));
            }
            return;
        }
        long duration = TimeParser.parseToMillis(args[1]);
        if (duration <= 0) {
            p.sendMessage(plugin.messages().prefix() + Colors.color("&cFormato tempo non valido. Esempi: 1d, 5h, 30m"));
            return;
        }
        plugin.selfDestruct().apply(item, duration);
        for (String s : plugin.messages().messages("self-destruct-added")) {
            p.sendMessage(s.replace("%time%", TimeParser.formatRemaining(duration)));
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.messages().prefix() + Colors.color("&cUso: /toolsfast give <player> <tool> [amount]"));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.messages().prefix() + Colors.color("&cGiocatore non trovato."));
            return;
        }
        String toolId = args[2].toLowerCase();
        var def = plugin.toolRegistry().get(toolId);
        if (def == null) {
            var custom = plugin.customItems().get(toolId);
            if (custom == null) {
                sender.sendMessage(plugin.messages().prefix() + Colors.color("&cTool non trovato: " + toolId));
                return;
            }
            int amount = args.length >= 4 ? parseInt(args[3], 1) : 1;
            ItemStack item = custom.build(target, amount);
            target.getInventory().addItem(item).values().forEach(left ->
                    target.getWorld().dropItemNaturally(target.getLocation(), left));
            for (String s : plugin.messages().messages("tool-received")) {
                target.sendMessage(s.replace("%tool%", custom.id()));
            }
            return;
        }
        int amount = args.length >= 4 ? parseInt(args[3], 1) : 1;
        plugin.api().giveTool(target, toolId, amount);
        for (String s : plugin.messages().messages("tool-received")) {
            target.sendMessage(s.replace("%tool%", def.id()));
        }
        String title = Colors.color(plugin.messages().message("tool-received-title.title"));
        String subtitle = Colors.color(plugin.messages().message("tool-received-title.subtitle").replace("%tool%", def.id()));
        if (!title.isEmpty() || !subtitle.isEmpty()) {
            if (plugin.nms() != null) plugin.nms().sendTitle(target, title, subtitle, 10, 60, 10);
            else target.sendTitle(title, subtitle, 10, 60, 10);
        }
    }

    private void handleLeaderboard(CommandSender sender) {
        var top = plugin.statistics().topBlocks(5);
        sender.sendMessage(Colors.color("&#C77DFF&lTOP BLOCKS"));
        int i = 1;
        for (var e : top.entrySet()) {
            String name = Bukkit.getOfflinePlayer(e.getKey()).getName();
            if (name == null) name = e.getKey().toString().substring(0, 8);
            sender.sendMessage(Colors.color("&d#" + i + " &f" + name + " &7- &d" + e.getValue()));
            i++;
        }
    }

    private void showStats(CommandSender sender, java.util.UUID uuid, String name) {
        sender.sendMessage(Colors.color("&#C77DFF&lSTATISTICHE &7- " + name));
        sender.sendMessage(Colors.color("&dBlocchi: &f" + plugin.statistics().getBlocks(uuid)));
        sender.sendMessage(Colors.color("&dSoldi: &a$" + plugin.statistics().getMoney(uuid)));
        sender.sendMessage(Colors.color("&dAlberi: &f" + plugin.statistics().getTrees(uuid)));
        sender.sendMessage(Colors.color("&dColture: &f" + plugin.statistics().getCrops(uuid)));
        sender.sendMessage(Colors.color("&dVenduti: &f" + plugin.statistics().getItemsSold(uuid)));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Colors.color("&#C77DFF&lTOOLSFAST &7- Comandi"));
        sender.sendMessage(Colors.color("&d/toolsfast &7- Apri il menu"));
        sender.sendMessage(Colors.color("&d/toolsfast give <player> <tool> &7- Dai un tool"));
        sender.sendMessage(Colors.color("&d/toolsfast list &7- Lista tools"));
        sender.sendMessage(Colors.color("&d/toolsfast selfdestruct <time> &7- Aggiungi scadenza"));
        sender.sendMessage(Colors.color("&d/toolsfast selfdestruct delayed <time> &7- Scadenza ritardata"));
        sender.sendMessage(Colors.color("&d/toolsfast selfdestruct remove &7- Rimuovi scadenza"));
        sender.sendMessage(Colors.color("&d/toolsfast stats &7- Statistiche"));
        sender.sendMessage(Colors.color("&d/toolsfast leaderboard &7- Classifica"));
        sender.sendMessage(Colors.color("&d/toolsfast reload &7- Ricarica"));
    }

    private void sendNoPerm(CommandSender sender) {
        sender.sendMessage(plugin.messages().prefix() + Colors.color("&cNon hai il permesso."));
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("reload", "give", "selfdestruct", "list", "stats", "leaderboard", "help"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return null;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> ids = new ArrayList<>();
            for (var t : plugin.toolRegistry().all()) ids.add(t.id());
            for (var c : plugin.customItems().all()) ids.add(c.id());
            return filter(ids, args[2]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("selfdestruct")) {
            return filter(Arrays.asList("delayed", "remove", "1m", "5m", "30m", "1h", "5h", "1d", "7d"), args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> options, String prefix) {
        List<String> out = new ArrayList<>();
        for (String o : options) if (o.toLowerCase().startsWith(prefix.toLowerCase())) out.add(o);
        return out;
    }
}
