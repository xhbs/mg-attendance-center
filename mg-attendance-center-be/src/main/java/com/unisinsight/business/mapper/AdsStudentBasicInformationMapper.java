package com.unisinsight.business.mapper;

import com.unisinsight.business.model.AdsStudentBasicInformation;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdsStudentBasicInformationMapper extends Mapper<AdsStudentBasicInformation> {

    List<AdsStudentBasicInformation> selectXszpPage(@Param(value = "limit") int limit, @Param(value = "offset") int offset);

    List<AdsStudentBasicInformation> selectPageByUpdateTime(@Param(value = "updateTime") String updateTime, @Param(value = "startNum") int startNum, @Param(value = "pageSize") int pageSize);

    void updateImage(@Param(value = "image") String image, @Param(value = "personNo") String personNo);

    void insertBatch(List<AdsStudentBasicInformation> req);

    void sync(@Param("host") String host,
              @Param("port") String port,
              @Param("username") String username,
              @Param("password") String password);
}
