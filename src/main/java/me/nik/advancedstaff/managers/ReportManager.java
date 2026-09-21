package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReportManager implements AbstractManager {

    private final AdvancedStaff plugin;

    private final Map<Integer, Report> reports = new HashMap<>();
    private final Map<UUID, Long> lastReportAt = new HashMap<>();
    private final Map<UUID, UUID> awaitingCustomReason = new HashMap<>();

    private File file;
    private YamlConfiguration storage;
    private int nextId = 1;

    public ReportManager(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        this.file = new File(plugin.getDataFolder(), "reports.yml");

        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException ignored) {
            }
        }

        this.storage = YamlConfiguration.loadConfiguration(this.file);

        load();
    }

    @Override
    public void shutdown() {
        save();
        this.reports.clear();
        this.lastReportAt.clear();
        this.awaitingCustomReason.clear();
    }

    private enum SubmitResult {
        SUCCESS, CANNOT_REPORT_SELF, ON_COOLDOWN, MAX_ACTIVE_REACHED, DUPLICATE
    }

    public void handleSubmit(Player reporter, Player target, String reason) {
        SubmitResult result = submit(reporter, target, reason);

        switch (result) {
            case SUCCESS:
                reporter.sendMessage(MsgType.REPORT_SUBMITTED.getMessage().replace("%player%", target.getName()));
                break;
            case CANNOT_REPORT_SELF:
                reporter.sendMessage(MsgType.REPORT_CANNOT_SELF.getMessage());
                break;
            case ON_COOLDOWN:
                reporter.sendMessage(MsgType.REPORT_COOLDOWN.getMessage());
                break;
            case MAX_ACTIVE_REACHED:
                reporter.sendMessage(MsgType.REPORT_MAX_ACTIVE.getMessage());
                break;
            case DUPLICATE:
                reporter.sendMessage(MsgType.REPORT_DUPLICATE.getMessage());
                break;
        }
    }

    private SubmitResult submit(Player reporter, Player target, String reason) {
        if (target.getUniqueId().equals(reporter.getUniqueId())) {
            return SubmitResult.CANNOT_REPORT_SELF;
        }

        long cooldownMillis = Config.Setting.REPORT_COOLDOWN.getLong() * 1000L;
        Long last = lastReportAt.get(reporter.getUniqueId());
        if (cooldownMillis > 0 && last != null && System.currentTimeMillis() - last < cooldownMillis) {
            return SubmitResult.ON_COOLDOWN;
        }

        int maxActive = Config.Setting.REPORT_MAX_ACTIVE_PER_PLAYER.getInt();
        if (maxActive > 0) {
            long activeFromReporter = reports.values().stream()
                    .filter(r -> r.getStatus() == Report.Status.OPEN)
                    .filter(r -> r.getReporterId().equals(reporter.getUniqueId()))
                    .count();
            if (activeFromReporter >= maxActive) {
                return SubmitResult.MAX_ACTIVE_REACHED;
            }
        }

        boolean duplicate = reports.values().stream().anyMatch(r ->
                r.getStatus() == Report.Status.OPEN
                        && r.getReporterId().equals(reporter.getUniqueId())
                        && r.getTargetId().equals(target.getUniqueId()));
        if (duplicate) {
            return SubmitResult.DUPLICATE;
        }

        int id = nextId++;
        Report report = new Report(id, reporter.getUniqueId(), reporter.getName(),
                target.getUniqueId(), target.getName(), reason, System.currentTimeMillis(),
                Report.Status.OPEN, "");

        reports.put(id, report);
        lastReportAt.put(reporter.getUniqueId(), System.currentTimeMillis());

        save();
        notifyStaff(report);

        return SubmitResult.SUCCESS;
    }

    private void notifyStaff(Report report) {
        if (!Config.Setting.REPORT_NOTIFY_STAFF.getBoolean()) return;

        String message = MsgType.REPORT_STAFF_NOTIFY.getMessage()
                .replace("%id%", String.valueOf(report.getId()))
                .replace("%reporter%", report.getReporterName())
                .replace("%target%", report.getTargetName())
                .replace("%reason%", report.getReason());

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission(Permissions.ADMIN.getPermission()))
                .forEach(p -> p.sendMessage(message));
    }

    public void resolve(Report report, Player staff) {
        report.setStatus(Report.Status.RESOLVED);
        report.setHandledBy(staff.getName());
        afterHandled(report);
    }

    public void reject(Report report, Player staff) {
        report.setStatus(Report.Status.REJECTED);
        report.setHandledBy(staff.getName());
        afterHandled(report);
    }

    private void afterHandled(Report report) {
        if ("DELETE".equalsIgnoreCase(Config.Setting.REPORT_COMPLETED_HANDLING.getString())) {
            reports.remove(report.getId());
        }
        save();
    }

    public Report getReport(int id) {
        return reports.get(id);
    }

    public List<Report> getActiveReports() {
        return reports.values().stream()
                .filter(r -> r.getStatus() == Report.Status.OPEN)
                .sorted(Comparator.comparingLong(Report::getTimestamp))
                .collect(Collectors.toList());
    }

    public void awaitCustomReason(Player reporter, Player target) {
        awaitingCustomReason.put(reporter.getUniqueId(), target.getUniqueId());
    }

    public boolean isAwaitingCustomReason(Player player) {
        return awaitingCustomReason.containsKey(player.getUniqueId());
    }

    public UUID getAwaitingTarget(Player player) {
        return awaitingCustomReason.get(player.getUniqueId());
    }

    public void clearAwaiting(Player player) {
        awaitingCustomReason.remove(player.getUniqueId());
    }

    private void load() {
        this.nextId = storage.getInt("next-id", 1);

        ConfigurationSection section = storage.getConfigurationSection("reports");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                String path = "reports." + key;
                int id = Integer.parseInt(key);

                UUID reporterId = UUID.fromString(storage.getString(path + ".reporter-uuid"));
                String reporterName = storage.getString(path + ".reporter-name");
                UUID targetId = UUID.fromString(storage.getString(path + ".target-uuid"));
                String targetName = storage.getString(path + ".target-name");
                String reason = storage.getString(path + ".reason");
                long timestamp = storage.getLong(path + ".timestamp");
                Report.Status status = Report.Status.valueOf(storage.getString(path + ".status", "OPEN"));
                String handledBy = storage.getString(path + ".handled-by", "");

                reports.put(id, new Report(id, reporterId, reporterName, targetId, targetName,
                        reason, timestamp, status, handledBy));
            } catch (Exception ignored) {
            }
        }
    }

    public void save() {
        storage.set("reports", null);
        storage.set("next-id", nextId);

        for (Report report : reports.values()) {
            String path = "reports." + report.getId();
            storage.set(path + ".reporter-uuid", report.getReporterId().toString());
            storage.set(path + ".reporter-name", report.getReporterName());
            storage.set(path + ".target-uuid", report.getTargetId().toString());
            storage.set(path + ".target-name", report.getTargetName());
            storage.set(path + ".reason", report.getReason());
            storage.set(path + ".timestamp", report.getTimestamp());
            storage.set(path + ".status", report.getStatus().name());
            storage.set(path + ".handled-by", report.getHandledBy());
        }

        try {
            storage.save(this.file);
        } catch (IOException ignored) {
        }
    }

    public static class Report {

        public enum Status {
            OPEN, RESOLVED, REJECTED
        }

        private final int id;
        private final UUID reporterId;
        private final String reporterName;
        private final UUID targetId;
        private final String targetName;
        private final String reason;
        private final long timestamp;

        private Status status;
        private String handledBy;

        public Report(int id, UUID reporterId, String reporterName, UUID targetId, String targetName,
                      String reason, long timestamp, Status status, String handledBy) {
            this.id = id;
            this.reporterId = reporterId;
            this.reporterName = reporterName;
            this.targetId = targetId;
            this.targetName = targetName;
            this.reason = reason;
            this.timestamp = timestamp;
            this.status = status;
            this.handledBy = handledBy;
        }

        public int getId() {
            return id;
        }

        public UUID getReporterId() {
            return reporterId;
        }

        public String getReporterName() {
            return reporterName;
        }

        public UUID getTargetId() {
            return targetId;
        }

        public String getTargetName() {
            return targetName;
        }

        public String getReason() {
            return reason;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getHandledBy() {
            return handledBy;
        }

        public void setHandledBy(String handledBy) {
            this.handledBy = handledBy;
        }
    }
}