package dao.impl;

import dao.IAuditLogDAO;
import entity.AuditLog;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements IAuditLogDAO {

    @Override
    public void log(AuditLog log) throws Exception {
        String sql = "INSERT INTO audit_log (actor_info, action_type, target_table, target_id, action_details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, log.getActorInfo());
            pstmt.setString(2, log.getActionType());
            pstmt.setString(3, log.getTargetTable());
            pstmt.setInt(4, log.getTargetId());
            pstmt.setString(5, log.getActionDetails());

            pstmt.executeUpdate();
        }
    }

    @Override
    public List<AuditLog> getRecentLogs(int limit) throws Exception {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setId(rs.getInt("id"));
                    log.setActorInfo(rs.getString("actor_info"));
                    log.setActionType(rs.getString("action_type"));
                    log.setTargetTable(rs.getString("target_table"));
                    log.setTargetId(rs.getInt("target_id"));
                    log.setActionDetails(rs.getString("action_details"));
                    log.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(log);
                }
            }
        }
        return list;
    }
}