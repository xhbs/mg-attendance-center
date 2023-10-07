package com.unisinsight.business.service.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.*;
import com.unisinsight.business.client.MgClient;
import com.unisinsight.business.common.constants.Constants;
import com.unisinsight.business.dto.PersonDTO;
import com.unisinsight.business.dto.request.FindMdkReq;
import com.unisinsight.business.dto.request.TabListReq;
import com.unisinsight.business.dto.response.FindMdkRes;
import com.unisinsight.business.dto.response.TabListRes;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class StudentImageService {


    @Resource
    private MgClient mgClient;
    @Value("${student.image.ip}")
    private String ip;
    @Value("${student.image.port}")
    private Integer port;
    @Value("${student.image.userName}")
    private String userName;
    @Value("${student.image.password}")
    private String password;
    @Value("${student.image.path}")
    private String path;
    @Value("${student.image.splitStr}")
    private String splitImageStr;


    private final String MDK_NAME_KEY = "mdk_name_key";
    private final String SAVE_SUCCESS_KEY = "save_success_key";
    private final String REGISTER_SUCCESS_KEY = "register_success_key";
    private final String UPLOAD_SUCCESS_KEY = "upload_success_key";

    private final String REGISTER = "register";
    private final String IMPORT = "import";
    private final String DATABASE = "database";


    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Resource
    private JdbcTemplate ziguangMysqlJdbcTemplate;
    @Resource
    private JdbcTemplate objectManageJdbcTemplate;
    @Resource
    private MdkErrorMapper mdkErrorMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private PersonMapper personMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StutentImageStatisticsDao stutentImageStatisticsDao;
    @Resource
    private com.unisinsight.business.client.ImageClient imageClient;
    @Resource
    private AdsStudentBasicInformationMapper adsStudentBasicInformationMapper;
    private Session session;

    public void importStudentImage() throws Exception {
        Session session = initSession();
        foreach(path, session, Boolean.FALSE);
        session.disconnect();
        System.out.println("执行完了");
    }

    public Session initSession() throws JSchException {
        if (session != null && session.isConnected()) {
            return session;
        }
        Session session = new JSch().getSession(userName, ip, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        this.session = session;
        return this.session;
    }

    public void foreach(String src, Session session, Boolean canSave) throws JSchException, SftpException, InterruptedException {
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        Vector<ChannelSftp.LsEntry> vector = sftp.ls(src);
        sftp.disconnect();
        CountDownLatch latch = new CountDownLatch(vector.size());
        for (ChannelSftp.LsEntry entry : vector) {
            if (".".equals(entry.getFilename()) || "..".equals(entry.getFilename()) || "pd".equals(entry.getFilename()) || "2997".equals(entry.getFilename())) {
                latch.countDown();
                continue;
            }
            String localSrc = src + entry.getFilename();
            if (entry.getAttrs().isDir()) {
                String saved = (String) stringRedisTemplate.opsForHash().get("import_dir", localSrc);
                if (StrUtil.isBlank(saved)) {
                    foreach(localSrc + "/", session, canSave);
                    stringRedisTemplate.opsForHash().put("import_dir", localSrc, localSrc);
                }
                log.info("文件夹:{}已经导入完成", localSrc);
                latch.countDown();
            } else {
//                String img = stringRedisTemplate.opsForValue().get("mdk_save");
//                if (img == null) {
//                    canSave = true;
//                } else {
//                    if (localSrc.equals(img)) {
//                        canSave = true;
//                        latch.countDown();
//                        continue;
//                    }
//                }
                executorService.execute(() -> {
                    try {
                        String saved = (String) stringRedisTemplate.opsForHash().get(SAVE_SUCCESS_KEY, localSrc);
                        if (StrUtil.isNotBlank(saved)) {
                            log.info("{}已经导入", localSrc);
                            return;
                        }
                        String res = download(session, null, localSrc, IMPORT);
                        if (res != null) {
//                            stringRedisTemplate.opsForValue().set("mdk_save", localSrc);
                            stringRedisTemplate.opsForHash().put(SAVE_SUCCESS_KEY, localSrc, res);
                        }
                    } catch (JSchException | SftpException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        latch.await();
    }

    public String download(Session session, PersonDTO personNo, String path, String type) throws JSchException, SftpException, IOException {
//        String primaryCode = fileName.split("\\.")[0];
//        String sql = "select SFZJH as sfzjh,XM as xm,ID as id from xs where ID = ? or SFZJH = ?";
//        Xs xs = null;
//        PersonDTO personNo;
//        try {
//            xs = ziguangMysqlJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Xs.class), primaryCode, primaryCode);
//        } catch (Exception ignored) {
//
//        }
//        personNo = personMapper.findByPersonNo(ObjectUtil.isEmpty(xs) || StrUtil.isBlank(xs.getSfzjh()) ? primaryCode : xs.getSfzjh());
        if (ObjectUtil.isEmpty(personNo)) {
            saveErrorData(personNo.getPersonNo(), "未查询到学生信息", path, type);
            return null;
        }
        long time = System.currentTimeMillis();
        HashMap<String, String> map = img2Basee64AndUpload(session, path, personNo, type);
        log.info("从nfs服务器下载图片耗时：{}s", (double) (System.currentTimeMillis() - time) / 1000);
        if (ObjectUtil.isEmpty(map)) {
            return null;
        }
        String base64 = map.get("base64");
        String image = map.get("image");
        long time1 = System.currentTimeMillis();
        String res = saveToMdk(personNo, base64);
        log.info("创建-查询-插入名单库总耗时：{}s", (double) (System.currentTimeMillis() - time1) / 1000);
        if (StrUtil.isNotBlank(res) && res.startsWith("Error:")) {
            saveErrorData(personNo.getPersonNo(), res, path, type);
            return null;
        } else {
            //学生注册
            register(personNo, image);
            return image;
        }
    }

    private HashMap<String, String> img2Basee64AndUpload(Session session, String path, PersonDTO personNo, String type) throws JSchException, SftpException {
        try {
            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            InputStream inputStream;
            try {
                inputStream = sftp.get(path);
            } catch (Exception e) {
                saveErrorData(personNo.getPersonNo(), "图片路径不存在", path, type);
//                log.info("{}-{}-图片路径不存在{}", personNo.getPersonName(), personNo.getPersonNo(), path);
                sftp.disconnect();
                return null;
            }
            String base64 = Base64Encoder.encode(IoUtil.readBytes(inputStream, false));
            sftp.disconnect();
            String image = upload(base64, path);
            if (StrUtil.isBlank(base64)) {
                saveErrorData(personNo.getPersonNo(), "图片流转base64失败", path, type);
                return null;
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("base64", base64);
            map.put("image", image);

            return map;
        } catch (Exception e) {
            saveErrorData(personNo.getPersonNo(), "下载图片失败", path, type);
            log.info("{}-{}-下载图片{}失败", personNo.getPersonName(), personNo.getPersonNo(), path, e);
            return null;
        }
    }

    private String upload(String base64, String path) {
        String image = (String) stringRedisTemplate.opsForHash().get(UPLOAD_SUCCESS_KEY, path);
        log.info("获取upload缓存:{}", image);
        if (StrUtil.isBlank(image)) {
            image = imageClient.uploadImage(Constants.BASE64_PRE + base64);
            if (StrUtil.isBlank(image)) {
                log.info("上传图片失败");
                return null;
            }
            stringRedisTemplate.opsForHash().put(UPLOAD_SUCCESS_KEY, path, image);
        }
        return image;
    }

    public boolean register(PersonDTO personNo, String image) {
        log.info("学生:{}开始注册,image:{}", personNo.getPersonName(), image);
        Boolean key = stringRedisTemplate.opsForHash().hasKey(REGISTER_SUCCESS_KEY, personNo.getPersonNo());
        log.info("{}-{}-获取register缓存:{}", personNo.getPersonName(), personNo.getPersonUrl(), image);
        if (key) {
            log.info("学生:{}-所属学校:{}已经注册", personNo.getPersonName(), personNo.getOrgIndexPathName());
            return false;
        }
        long time = System.currentTimeMillis();
        String sql = "update public.tb_person set user_image = '" + image + "' where identity_no = '" + personNo.getPersonNo() + "'";
        int update = objectManageJdbcTemplate.update(sql);
        log.info("学生注册耗时：{}s", (double) (System.currentTimeMillis() - time) / 1000);
        if (update != 0) {
            log.info("学生:{}-所属学校:{}注册成功", personNo.getPersonName(), personNo.getTabName());
            stringRedisTemplate.opsForHash().put(REGISTER_SUCCESS_KEY, personNo.getPersonNo(), image);
            return true;
        } else {
            saveErrorData(personNo.getPersonNo(), "修改学生表失败", null, REGISTER);
            return false;
        }
    }

    private String saveToMdk(PersonDTO personNo, String base64) {
        OrganizationDO school = getSchool(personNo.getOrgIndex());
        if (ObjectUtil.isEmpty(school)) {
//            saveErrorData(personNo.getPersonNo(), "未查询到学校信息：" + personNo.getOrgIndexPathName(), null, type);
//            throw new BaseException("未查询到学校信息：" + personNo.getOrgIndexPathName());
            return "Error:未查询到学校信息:" + personNo.getOrgIndexPathName();
        }
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

        String tabName = school.getOrgName().replaceAll(regEx, "_") + "_自动创建";
        String tabId = (String) stringRedisTemplate.opsForHash().get(MDK_NAME_KEY, tabName);
        if (StrUtil.isBlank(tabId)) {
            RLock lock = redissonClient.getLock(tabName + "_lock");
            lock.lock();
            //线程抢占到锁后，再次检查redis缓存，防止上个线程已经创建名单库
            tabId = (String) stringRedisTemplate.opsForHash().get(MDK_NAME_KEY, tabName);
            try {
                if (StrUtil.isBlank(tabId)) {
                    try {
                        tabId = mgClient.createTab(tabName);
                        stringRedisTemplate.opsForHash().put(MDK_NAME_KEY, tabName, tabId);
                    } catch (Exception e) {
//                        saveErrorData(personNo.getPersonNo(), e.getMessage(), tabName, IMPORT);
                        tabId = null;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        personNo.setTabName(tabName);
        return mgClient.insert(personNo, tabId, base64);
    }

    private OrganizationDO getSchool(String index) {
        OrganizationDO orgIndex = organizationMapper.selectByOrgIndex(index);
        if (ObjectUtil.isEmpty(orgIndex)) {
            return null;
        }
        if (orgIndex.getSubType() != 4) {
            return getSchool(orgIndex.getOrgParentIndex());
        }
        return orgIndex;
    }

    private void saveErrorData(String code, String msg, String path, String type) {
        log.info("{}-导入失败，原因：{},图片来源:{}", code, msg, type);
        MdkError mdkError = new MdkError();
        mdkError.setIdNumber(code);
        mdkError.setType(type);
        mdkError.setFilePath(path);
        int count = mdkErrorMapper.selectCount(mdkError);
        if (count != 0) {
            return;
        }

        MdkError error = new MdkError();
        error.setCause(msg);
        error.setId(code);
        error.setIdNumber(code);
        error.setFilePath(path);
        error.setType(type);
        error.setErrorTime(System.currentTimeMillis());
        mdkErrorMapper.insert(error);
    }

    public void statistics() {

        //清空表
        stutentImageStatisticsDao.deleteByCondition(Example.builder(StutentImageStatistics.class).build());

        TabListReq req = new TabListReq();
        req.setPageSize(300);
        TabListRes res = mgClient.selectMdk(req);
        res.getData().getData().forEach(v -> {
            StutentImageStatistics statistics = new StutentImageStatistics();
            statistics.setSchool(v.getTabName().contains("_") ? v.getTabName().substring(0, v.getTabName().length() - 5) : v.getTabName());
            FindMdkReq mdkReq = new FindMdkReq();
            mdkReq.setTabId(v.getTabId());
            FindMdkRes.DataDTO dto = mgClient.find(mdkReq);
            statistics.setCount(dto.getPaging().getTotalNum());
            statistics.setUpdateTime(LocalDate.now());
            stutentImageStatisticsDao.insert(statistics);
        });
    }

    public void deleteAllMdk() {
        mgClient.deleteAll();
    }

    /**
     * 全量更新 在校学生 且未注册的
     *
     * @throws JSchException
     * @throws SftpException
     */
    public void downloadImageInDatabase() throws JSchException, SftpException {
        int offset = 0;
        int limit = 1000;
        Session session = initSession();
//        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
//        log.info("连接服务器成功");
        try {
            while (true) {
//                sftp.connect();
                List<AdsStudentBasicInformation> list = adsStudentBasicInformationMapper.selectXszpPage(offset, limit);
                if (CollUtil.isEmpty(list)) {
                    break;
                }
                List<List<AdsStudentBasicInformation>> split = CollUtil.split(list, 200);
                CountDownLatch latch = new CountDownLatch(split.size());
                for (List<AdsStudentBasicInformation> informations : split) {
                    executorService.execute(() -> {
                        try {
                            for (AdsStudentBasicInformation information : informations) {
                                downloadAndRegister(information);
                            }
                        } finally {
                            latch.countDown();
                        }
                    });
                }
                latch.await();
                offset += limit;
            }
        } catch (InterruptedException ignored) {

        } finally {
            session.disconnect();
        }
    }

    public String downloadAndRegister(AdsStudentBasicInformation information) {
        if (StrUtil.isBlank(information.getOrgIndex()) && StrUtil.isBlank(information.getXxIndex())) {
            return null;
        }
        String imgUrl = path + information.getXszp();
        String saved = (String) stringRedisTemplate.opsForHash().get(SAVE_SUCCESS_KEY, imgUrl);
        if (StrUtil.isNotBlank(saved)) {
            log.info("database||{}-{}-{}已经导入", information.getXm(), information.getSfzjh(), saved);
            return saved;
        }
        PersonDTO personDTO = new PersonDTO();
        personDTO.setPersonNo(information.getSfzjh());
        personDTO.setPersonName(information.getXm());
        personDTO.setOrgIndex(information.getOrgIndex());
        personDTO.setOrgIndexPathName(information.getBjmc());
        try {
            String res = download(session, personDTO, imgUrl, DATABASE);
            if (res != null) {
                stringRedisTemplate.opsForHash().put(SAVE_SUCCESS_KEY, imgUrl, res);
                return res;
            }
        } catch (JSchException | SftpException | IOException e) {
            log.info("{}-{}-下载图片失败", personDTO.getPersonName(), personDTO.getPersonNo(), e);
        }
        return null;
    }

    public static void main(String[] args) throws JSchException, SftpException {
        Session session = new JSch().getSession("root", "10.209.8.14", 22);
        session.setPassword("Unis2020@_");
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        InputStream inputStream = sftp.get("/data/a.jpg");
        String base64 = Base64Encoder.encode(IoUtil.readBytes(inputStream, false));
    }
}
