package com.Dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.Dbconnection.AdminDBconnection;
import com.dto.ScheduleAdminDto;
import com.dto.ServiceResponseDto;
@Repository
public class ScheduleAdminDao {
	
	public String ScheduleAdminServicepublich(ScheduleAdminDto dto) {
        Connection connection = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        String sql = null;
        String status = null;

        try {
            connection = AdminDBconnection.getConnection();

            sql = "{call schedule_deatils_insert_produ(?,?,?,?,?,?,?,?,?,?)}"; // Still 10 parameters based on your code

            ps = connection.prepareCall(
                sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );

            byte[] imageBytes = null;
            String base64ImageWithPrefix = dto.getUploadImage();

            if (base64ImageWithPrefix != null && !base64ImageWithPrefix.isEmpty()) {
                String base64Data = base64ImageWithPrefix;
                if (base64ImageWithPrefix.contains(",")) {
                    base64Data = base64ImageWithPrefix.split(",", 2)[1];
                }
                try {
                    imageBytes = Base64.getDecoder().decode(base64Data);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error decoding Base64 image in ScheduleAdminServicepublich: " + e.getMessage());
                    throw new RuntimeException("Invalid image data format. Please ensure it's a valid Base64 string.", e);
                }
            }

            // Set the byte array to the PreparedStatement (1st parameter)
            ps.setBytes(1, imageBytes);
            ps.setString(2, dto.getTitle());
            ps.setString(3, dto.getDatePublish());
            ps.setString(4, dto.getPublishTime());
            ps.setString(5, dto.getStartLocation());
            ps.setString(6, dto.getEndLocation());
            ps.setString(7, dto.getDistance());
            ps.setString(8, dto.getOrganizedBy());
            ps.setString(9, dto.getContactMobile());
            ps.setString(10, dto.getContactEmail());

            System.out.println("Executing SQL for insert: " + sql);

            rs = ps.executeQuery();

            if (rs.next()) {
                status = rs.getString(1);
            } else {
                status = "No status returned from stored procedure.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in ScheduleAdminServicepublich: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(connection);
        }

        return status;
    }

    // Helper method to close resources quietly
    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }

    // --- get API: getAllScheduleDetails (Fixed and added image retrieval, removed ID) ---
    public List<ScheduleAdminDto> getAllScheduleDetails() {
        List<ScheduleAdminDto> scheduleList = new ArrayList<>();
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = AdminDBconnection.getConnection();
            stmt = conn.prepareCall("{call get_all_schedule_details()}");

            rs = stmt.executeQuery();
            while (rs.next()) {
                ScheduleAdminDto dto = new ScheduleAdminDto();
                byte[] imageBytes = rs.getBytes(1);
                if (imageBytes != null) {
                    dto.setUploadImage(Base64.getEncoder().encodeToString(imageBytes));
                }
                dto.setTitle(rs.getString(2)); 
                dto.setDatePublish(rs.getString(3));
                dto.setPublishTime(rs.getString(4));
                dto.setStartLocation(rs.getString(5));
                dto.setEndLocation(rs.getString(6));
                dto.setDistance(rs.getString(7));
                dto.setOrganizedBy(rs.getString(8));
                dto.setContactMobile(rs.getString(9));
                dto.setContactEmail(rs.getString(10)); 
                dto.setCreate_at(rs.getString(11));

                scheduleList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }

        return scheduleList;
    }

}