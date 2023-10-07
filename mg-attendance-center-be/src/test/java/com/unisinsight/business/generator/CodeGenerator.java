package com.unisinsight.business.generator;

import com.google.common.base.CaseFormat;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: create by EthanCheng
 * @description: generator
 * @date: 2019/7/17
 */
public class CodeGenerator {
    //生成代码所在的基础包名称，可根据自己公司的项目修改（注意：这个配置修改之后需要手工修改src目录项目默认的包路径，使其保持一致，不然会找不到类）
    private static final String BASE_PACKAGE = "com.unisinsight.business";

    //生成的Model所在包
    private static final String MODEL_PACKAGE = BASE_PACKAGE + ".model";

    //生成的Mapper所在包
    private static final String MAPPER_PACKAGE = BASE_PACKAGE + ".mapper";

    //生成的Service所在包
    private static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";

    //生成的ServiceImpl所在包
    private static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";

    //生成的Controller所在包
    private static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller";

    //生成的dto request所在包
    private static final String DTO_REQ_PACKAGE = BASE_PACKAGE + ".dto.request";

    //生成的dto response所在包
    private static final String DTO_RES_PACKAGE = BASE_PACKAGE + ".dto.response";

    //生成的Convert所在包
    private static final String MODELCONVERT_PACKAGE = SERVICE_PACKAGE + ".convert";

    //Mapper插件基础接口的完全限定名
    private static final String MAPPER_INTERFACE_REFERENCE = "com.unisinsight.framework.common.base.Mapper";

    //生成的do 文件名后缀
    private static final String DO_SUFFIX = "DO";

    //生成的dto response 文件名后缀
    private static final String DTO_RES_SUFFIX = "ResDTO";

    //生成的dto request 文件名后缀
    private static final String DTO_REQ_SUFFIX = "ReqDTO";

    //JDBC配置，请修改为你项目的实际配置
    private static final String JDBC_URL = "jdbc:postgresql://10.210.33.58:40093/biz_attendance";
    private static final String JDBC_USERNAME = "pgsql";
    private static final String JDBC_PASSWORD = "pgsql";
    private static final String JDBC_DIVER_CLASS_NAME = "org.postgresql.Driver";

    //项目在硬盘上的基础路径
    private static final String PROJECT_PATH = System.getProperty("user.dir");

    //模板位置
    private static final String TEMPLATE_FILE_PATH = PROJECT_PATH + "/src/test/resources/generator/template";

    //java文件路径
    private static final String JAVA_PATH = "/src/main/java";
    //资源文件路径
    private static final String RESOURCES_PATH = "/src/main/resources";

    //生成的DTO_REQ实现存放路径
    private static final String PACKAGE_PATH_DTO_REQ = packageConvertPath(DTO_REQ_PACKAGE);

    //生成的DTO_RES实现存放路径
    private static final String PACKAGE_PATH_DTO_RES = packageConvertPath(DTO_RES_PACKAGE);

    //生成的Service存放路径
    private static final String PACKAGE_PATH_SERVICE = packageConvertPath(SERVICE_PACKAGE);

    //生成的Service实现存放路径
    private static final String PACKAGE_PATH_SERVICE_IMPL = packageConvertPath(SERVICE_IMPL_PACKAGE);


    //生成的Controller存放路径
    private static final String PACKAGE_PATH_CONTROLLER = packageConvertPath(CONTROLLER_PACKAGE);

    //生成的Convert存放路径
    private static final String PACKAGE_PATH_MODELCONVERT = packageConvertPath(MODELCONVERT_PACKAGE);

    //@author
    private static final String AUTHOR = "wangyi01 [wang.yi@unisinsight.com]";

    //@date
    private static final String DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

    //API 前缀
    private static final String BASEREQUESTMAPPING_PREFIX = "/api/biz-scene/v1/attendance";

    public static void main(String[] args) {
        Assert.hasLength(AUTHOR, "AUTHOR不能为空");
        genCodeByCustomModelName("", "sms_records","SmsRecord");
        // genCodeByCustomModelName("", "task_target_orgs","TaskTargetOrg");
//        genCode("","eg_device_person_down_params");
        //genCodeByCustomModelName("输入表名","输入自定义Model名称");
    }

    /**
     * 通过数据表名称生成代码，Model 名称通过解析数据表名称获得，下划线转大驼峰的形式。
     * 如输入表名称 "t_user_detail" 将生成 TUserDetail、TUserDetailMapper、TUserDetailService ...
     *
     * @param tableNames 数据表名称...
     */
    public static void genCode(String schema, String... tableNames) {
        for (String tableName : tableNames) {
            genCodeByCustomModelName(schema, tableName, null);
        }
    }

    /**
     * 通过数据表名称，和自定义的 Model 名称生成代码
     * 如输入表名称 "t_user_detail" 和自定义的 Model 名称 "User" 将生成 User、UserMapper、UserService ...
     *
     * @param tableName 数据表名称
     * @param modelName 自定义的 Model 名称
     */
    public static void genCodeByCustomModelName(String schema, String tableName, String modelName) {
        genModelAndMapper(schema, tableName, modelName);
        // genEmptyDTO(tableName, modelName);
        //genConvert(tableName, modelName);
        // genService(tableName, modelName);
        // genController(tableName, modelName);
    }


    public static void genModelAndMapper(String schema, String tableName, String modelName) {
        Context context = new Context(ModelType.FLAT);
        context.setId("Potato");
        context.setTargetRuntime("MyBatis3Simple");
/*        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");*/

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
        jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
        jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
        jdbcConnectionConfiguration.setDriverClass(JDBC_DIVER_CLASS_NAME);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
        pluginConfiguration.addProperty("mappers", MAPPER_INTERFACE_REFERENCE);
        context.addPluginConfiguration(pluginConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
        javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(PROJECT_PATH + RESOURCES_PATH);
        sqlMapGeneratorConfiguration.setTargetPackage("mybatis");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
        javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE);
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        if (StringUtils.isNotEmpty(schema)) tableConfiguration.setSchema(schema);
        if (StringUtils.isNotEmpty(modelName)) {
            tableConfiguration.setDomainObjectName(modelName + DO_SUFFIX);
        } else {
            tableConfiguration.setDomainObjectName(tableNameConvertUpperCamel(tableName) + DO_SUFFIX);

        }
        //tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
        context.addTableConfiguration(tableConfiguration);

        List<String> warnings;
        MyBatisGenerator generator;
        try {
            Configuration config = new Configuration();
            config.addContext(context);
            config.validate();

            boolean overwrite = true;
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            warnings = new ArrayList<>();
            generator = new MyBatisGenerator(config, callback, warnings);
            generator.generate(null);
        } catch (Exception e) {
            throw new RuntimeException("生成Model和Mapper失败", e);
        }

        if (generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
            throw new RuntimeException("生成Model和Mapper失败：" + warnings);
        }
        if (StringUtils.isEmpty(modelName)) modelName = tableNameConvertUpperCamel(tableName);
        System.out.println(modelName + ".java 生成成功");
        System.out.println(modelName + "Mapper.java 生成成功");
        System.out.println(modelName + "Mapper.xml 生成成功");
    }


    public static void genService(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", tableNameConvertLowerCamel(tableName));

            data.put("basePackage", BASE_PACKAGE);

            File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + modelNameUpperCamel + "Service.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("service.ftl").process(data,
                    new FileWriter(file));
            System.out.println(modelNameUpperCamel + "service.java 生成成功");

            File file1 = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE_IMPL + modelNameUpperCamel + "ServiceImpl.java");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            cfg.getTemplate("service-impl.ftl").process(data,
                    new FileWriter(file1));
            System.out.println(modelNameUpperCamel + "ServiceImpl.java 生成成功");
        } catch (Exception e) {
            throw new RuntimeException("生成Service失败", e);
        }
    }

    public static void genEmptyDTO(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;

            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", tableNameConvertLowerCamel(tableName));

            data.put("basePackage", BASE_PACKAGE);

            File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_DTO_REQ + modelNameUpperCamel + DTO_REQ_SUFFIX + ".java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("dto-req.ftl").process(data,
                    new FileWriter(file));
            System.out.println(modelNameUpperCamel + DTO_REQ_SUFFIX + ".java 生成成功 该文件是空文件，根据实际需求，增加变量定义！");

            File file1 = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_DTO_RES + modelNameUpperCamel + DTO_RES_SUFFIX + ".java");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            cfg.getTemplate("dto-res.ftl").process(data,
                    new FileWriter(file1));
            System.out.println(modelNameUpperCamel + DTO_RES_SUFFIX + ".java 生成成功 该文件是空文件，根据实际需求，增加变量定义！");
        } catch (Exception e) {
            throw new RuntimeException("生成DTO失败", e);
        }
    }

    public static void genController(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            data.put("baseRequestMapping", modelNameConvertMappingPath(modelNameUpperCamel));
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
            data.put("basePackage", BASE_PACKAGE);

            File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_CONTROLLER + modelNameUpperCamel + "Controller.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("controller-restful.ftl").process(data, new FileWriter(file));

            System.out.println(modelNameUpperCamel + "Controller.java 生成成功");
        } catch (Exception e) {
            throw new RuntimeException("生成Controller失败", e);
        }

    }

    public static void genConvert(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
            data.put("basePackage", BASE_PACKAGE);

            File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_MODELCONVERT + modelNameUpperCamel + "Convert.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //cfg.getTemplate("controller-restful.ftl").process(data, new FileWriter(file));
            cfg.getTemplate("convert.ftl").process(data, new FileWriter(file));

            System.out.println(modelNameUpperCamel + "Convert.java 生成成功");
        } catch (Exception e) {
            throw new RuntimeException("生成Convert失败", e);
        }

    }

    private static freemarker.template.Configuration getConfiguration() throws IOException {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return cfg;
    }

    private static String tableNameConvertLowerCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
    }

    private static String tableNameConvertUpperCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.substring(3).toLowerCase());

    }

    private static String tableNameConvertMappingPath(String tableName) {
        tableName = tableName.toLowerCase();//兼容使用大写的表名
        return "/" + (tableName.contains("_") ? tableName.replaceAll("_", "/") : tableName);
    }

    private static String modelNameConvertMappingPath(String modelName) {
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
        return BASEREQUESTMAPPING_PREFIX + tableNameConvertMappingPath(tableName);
    }

    private static String packageConvertPath(String packageName) {
        return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
    }
}
