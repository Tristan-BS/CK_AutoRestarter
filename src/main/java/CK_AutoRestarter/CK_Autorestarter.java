package CK_AutoRestarter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Calendar;

public final class CK_Autorestarter extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CK_AutoRestarter Plugin aktiviert!");

        // Tägliche Neustarts um 00:00, 12:00 und 17:00 Uhr
        scheduleDailyRestarts();
    }

    @Override
    public void onDisable() {
        getLogger().info("CK_AutoRestarter Plugin deaktiviert!");
    }

    // Diese Methode plant die täglichen Neustarts
    private void scheduleDailyRestarts() {
        // Wiederhole die Prüfung alle 20 Sekunden, um die Zeiten zu überprüfen
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Prüfe, ob die aktuelle Zeit 00:00, 12:00 oder 17:00 ist
                if ((hour == 0 && minute == 0) || (hour == 12 && minute == 0) || (hour == 17 && minute == 0)) {
                    // Führe den Save-All Befehl aus, um den Server zu speichern
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");

                    // Sende Countdown-Nachrichten in den Minuten vor dem Restart
                    sendCountdownMessages();

                    // Warte 5 Sekunden und starte dann den Neustartprozess
                    Bukkit.getScheduler().runTaskLater(CK_Autorestarter.this, new Runnable() {
                        @Override
                        public void run() {
                            // Sende die Nachricht, dass der Server jetzt neu gestartet wird
                            Bukkit.broadcastMessage("Server will restart now!");
                            // Starte den Neustart
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                        }
                    }, 100L); // 100 Ticks entsprechen 5 Sekunden Verzögerung nach dem Speichern
                }
            }
        }, 0L, 400L); // 400 Ticks = 20 Sekunden (die Häufigkeit, mit der die Prüfung erfolgt)
    }

    // Diese Methode sendet Countdown-Nachrichten vor dem Neustart
    private void sendCountdownMessages() {
        // Countdown von 30 Minuten, 15 Minuten, 5 Minuten, 1 Minute und 30 Sekunden
        Bukkit.broadcastMessage("Server will restart in 30 minutes!");
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.broadcastMessage("Server will restart in 15 minutes!"), 600L); // 30 Minuten in Ticks
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.broadcastMessage("Server will restart in 5 minutes!"), 900L); // 15 Minuten in Ticks
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.broadcastMessage("Server will restart in 1 minute!"), 1040L); // 5 Minuten in Ticks
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.broadcastMessage("Server will restart in 30 seconds! \nPlease leave the Server now!"), 1060L); // 1 Minute in Ticks
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (int i = 29; i >= 0; i--) {  // Countdown von 30 bis 1 Sekunde
                final int secondsLeft = i;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (secondsLeft > 0) {
                        Bukkit.broadcastMessage("Server will restart in " + secondsLeft + " seconds!");
                    }
                }, 1060L + (30 - secondsLeft) * 20L); // Sekundenzähler
            }
        }, 1060L); // 30 Sekunden nach der 1-Minuten-Nachricht
    }
}
