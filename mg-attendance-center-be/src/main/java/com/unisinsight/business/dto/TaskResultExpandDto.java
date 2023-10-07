package com.unisinsight.business.dto;

import com.unisinsight.business.bo.PersonListOfClassBO;
import com.unisinsight.business.dto.response.PracticeRecordDetailDTO;
import lombok.Data;


@Data
public class TaskResultExpandDto extends PersonListOfClassBO {

    private String location;

    private String placeName;

    private String picture;

    private Integer matchResult;

    private String absenceReason;

    private Long createTime;

    private PracticeRecordDetailDTO practice;

    private LeaveInfoDTO leave;

}
