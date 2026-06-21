package business.impl;

import dao.IAuditLogDAO;
import dao.impl.AuditLogDAOImpl;
import entity.AuditLog;
import java.util.List;

public class AuditLogServiceImpl {
    // Ép kiểu khai báo về Interface IAuditLogDAO thay vì dùng thẳng AuditLogDAOImpl
    private final IAuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public void log(String actorInfo, String actionType, String targetTable, int targetId, String actionDetails) {
        try {
            AuditLog logEntry = new AuditLog(actorInfo, actionType, targetTable, targetId, actionDetails);
            auditLogDAO.log(logEntry);
        } catch (Exception e) {
            System.err.println("⚠️ [Audit Log Error] Không thể ghi nhật ký hệ thống: " + e.getMessage());
        }
    }

    public List<AuditLog> getRecentSystemLogs(int limit) throws Exception {
        return auditLogDAO.getRecentLogs(limit);
    }
}