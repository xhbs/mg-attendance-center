package com.unisinsight.business.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 有感考勤记录表
 *
 * @author jiangnan [jiang.nan@unisinsight.com]
 * @date 2020/12/28
 */
@Table(name = "feel_attendance_records")
public class FeelAttendanceRecordsDO {

    @Id
    private Integer id;
    /**
     * 考勤日期
     */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
    /**
     * 学生编号
     */
    @Column(name = "person_no")
    private String personNo;
    /**
     * 考勤结果ID
     */
    @Column(name = "result_id")
    private Long resultId;

    /**
     * 请假记录ID
     */
    @Column(name = "leave_record_id")
    private Integer leaveRecordId;

    @Column(name = "practice_record_id")
    private Integer practiceRecordId;

    /**
     * 比对图片url
     */
    @Column(name = "match_image")
    private String matchImage;

    /**
     * 比对结果
     */
    @Column(name = "match_result")
    private Integer matchResult;
    /**
     * 定位(经度:纬度)
     */
    @Column(name = "location")
    private String location;

    /**
     * 地址
     */
    @Column(name = "place_name")
    private String placeName;

    /**
     * 手动备注
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 缺勤原因
     */
    @Column(name = "absence_reason")
    private String absenceReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getPersonNo() {
        return personNo;
    }

    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Integer getLeaveRecordId() {
        return leaveRecordId;
    }

    public void setLeaveRecordId(Integer leaveRecordId) {
        this.leaveRecordId = leaveRecordId;
    }

    public String getMatchImage() {
        return matchImage;
    }

    public void setMatchImage(String matchImage) {
        this.matchImage = matchImage;
    }

    public Integer getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(Integer matchResult) {
        this.matchResult = matchResult;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Integer getPracticeRecordId() {
        return practiceRecordId;
    }

    public void setPracticeRecordId(Integer practiceRecordId) {
        this.practiceRecordId = practiceRecordId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }
}
