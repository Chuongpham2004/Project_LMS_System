package dao;

import entity.AuditLog;

import java.util.List;

public interface IAuditLogDAO {
    void log(AuditLog log) throws Exception;
    List<AuditLog> getRecentLogs(int limit) throws Exception;
}
