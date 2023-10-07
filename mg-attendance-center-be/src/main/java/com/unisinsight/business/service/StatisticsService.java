package com.unisinsight.business.service;

import com.unisinsight.business.dto.*;
import com.unisinsight.business.dto.request.StatisticsProvinceReq;
import com.unisinsight.business.dto.request.StatisticsReq;

import java.util.List;

/**
 * @ClassName : mg-attendance-center
 * @Description :
 * @Author : xiehb
 * @Date: 2022/11/04 15:03
 * @Version 1.0.0
 */
public interface StatisticsService {
    StatisticsProvince provinceStatistics(StatisticsProvinceReq req);

    StatisticsProvince schoolStatistics(StatisticsReq req);

    StatisticsProvince cityStatistics(StatisticsReq req);

    List<StatisticsAllSchool> allSchoolStatistics(StatisticsReq req);

    List<StatisticsAllSchool> citySchoolTop5Statistics(StatisticsReq req);

    SchoolNumStatisticsRes schoolNumberStatistics();

    List<TermStatisticsProvince> statisticsTerm(StatisticsReq req) throws InterruptedException;

    List<TermStatisticsProvince> cityStatisticsTerm(StatisticsReq req) throws InterruptedException;

    List<TermStatisticsProvince> schoolStatisticsTerm(StatisticsReq req) throws InterruptedException;

    List<StatisticsAllSchool> classTop5(StatisticsReq req);

    List<StatisticsEvery> statisticsProvince2City(StatisticsReq req) throws InterruptedException;

    List<StatisticsEvery> statisticsCity2School(StatisticsReq req) throws InterruptedException;

    UserDetail getUser();
}
