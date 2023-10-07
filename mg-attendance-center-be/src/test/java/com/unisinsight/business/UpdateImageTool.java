package com.unisinsight.business;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 快速更新项目到服务器的脚本，省去jenkins打整包再从微云更新的步骤，用于开发阶段快速更新到开发环境进行联调和测试。
 * <p>
 * 使用：
 * 1. 在pom.xml中添加依赖；
 * <pre>
 *     <dependency>
 *         <groupId>com.jcraft</groupId>
 *         <artifactId>jsch</artifactId>
 *         <version>0.1.55</version>
 *         <scope>test</scope>
 *     </dependency>
 * </pre>
 * 2. 拷贝当前java文件到 test/java/{package}/ 目录下；
 * <p>
 * 3. 根据实际情况修改脚本中的常量值；
 * <p>
 * 4. 执行main方法。
 * <p>
 * Note:
 * 1. 该脚本只是更新镜像而不是安装，所以目标服务器上必须有运行改服务；
 * 2. IDEA的working directory默认是项目根目录，
 * 如果当前项目是子module，需要在IDEA的 Edit Configurations 中指定 working directory。
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/9/18
 * @since 1.0
 */
public class UpdateImageTool {
    /**
     * 服务器地址
     */
    private static final String SERVER_HOST = "10.210.32.64";

    /**
     * 服务器ssh端口
     */
    private static final int SERVER_PORT = 22;

    /**
     * 服务器账号
     */
    private static final String USERNAME = "root";

    /**
     * 服务器密码
     */
    private static final String PASSWORD = "Unis2020@_";

    /**
     * 镜像名称
     */
    private static final String IMAGE_NAME = "mg-scene-attendancesys-be";

    /**
     * 镜像中jar文件绝对路径，从DockerFile中查看
     */
    private static final String APP_JAR = "/opt/unisinsight/app/java/app.jar";

    public static void main(String[] args) throws Exception {
        // 1. 编译项目
        compileProject();
        System.out.println("编译完成");

        // 2. 找到要发布的jar文件
        File targetJar = findTargetJar();
        if (targetJar == null) {
            throw new RuntimeException("未找到jar文件");
        }
        System.out.println("编译成功的jar文件: " + targetJar.getAbsolutePath());

        // 3. 和服务器建立ssh连接
        Session session = connectServer();
        System.out.println("登录服务器[" + SERVER_HOST + "]成功");

        // 4. 拷贝jar文件到服务器
        String dstFile = "/tmp/" + targetJar.getName();
        copyFileToServer(session, targetJar.getAbsolutePath(), dstFile);
        System.out.println("拷贝文件到服务器: " + dstFile);

        // 5. 修改服务器上对应的镜像，重启pod完成更新
        updateImage(session, dstFile);

        // 6. 断开连接
        session.disconnect();
        System.out.println("更新成功");
    }

    /**
     * 执行 "mvn clean install" 得到jar文件
     */
    private static void compileProject() throws IOException {
        Process process = Runtime.getRuntime().exec("cmd /c mvn clean package -Dmaven.test.skip=true -s E:\\Maven\\settings.xml -Dmaven.repo.local=E:\\Maven\\Repositroy");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
        process.destroy();
    }

    /**
     * 找到编译成功的jar文件
     */
    private static File findTargetJar() {
        File targetDir = new File("target/");
        File[] files = targetDir.listFiles();
        if (files == null) {
            return null;
        }

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                return file;
            }
        }
        return null;
    }

    /**
     * 和服务器建立ssh连接
     */
    private static Session connectServer() throws Exception {
        Session session = new JSch().getSession(USERNAME, SERVER_HOST, SERVER_PORT);
        session.setPassword(PASSWORD);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setOutputStream(System.out);
        session.connect();
        return session;
    }

    /**
     * 拷贝文件到服务器
     *
     * @param session SSH Session
     * @param src     本地文件路径
     * @param dst     目标文件路径
     */
    private static void copyFileToServer(Session session, String src, String dst) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.setOutputStream(System.out);
        channel.connect();
        channel.put(src, dst);
        channel.disconnect();
    }

    /**
     * 在服务器执行命令并返回输出结果
     *
     * @param session SSH Session
     * @param command 命令
     * @return 输出结果
     */
    private static String execRemote(Session session, String command) throws Exception {
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        exec.setErrStream(System.err);
        exec.setOutputStream(System.out);
        exec.setCommand(command);
        exec.connect();
        return IOUtils.toString(exec.getInputStream(), "UTF-8");
    }

    /**
     * 通过 "kubectl get pod" 获取服务器正在运行的Pod名称
     *
     * @param session SSH Session
     * @return Pod name
     */
    private static String getPodName(Session session) throws Exception {
        String ret = execRemote(session, "kubectl get pod |grep " + IMAGE_NAME);
        return ret.split(" ")[0];
    }

    /**
     * 通过 "kubectl describe pod" 获取docker容器id
     *
     * @param session SSH Session
     * @return Container id
     */
    private static String getContainerId(Session session) throws Exception {
        String ret = execRemote(session, "kubectl describe pod " + IMAGE_NAME + " |grep \"Container ID\"");
        String[] arr = ret.split("//");
        return arr[arr.length - 1].substring(0, 10);
    }

    /**
     * 通过 "kubectl describe pod" 获取POD使用的 Image tag
     *
     * @param session SSH Session
     * @return Image tag
     */
    private static String getImageTag(Session session) throws Exception {
        String ret = execRemote(session, "kubectl describe pod " + IMAGE_NAME + " |grep Image:");
        String[] arr = ret.split(" ");
        return arr[arr.length - 1];
    }

    /**
     * 修改docker镜像，然后重启pod完成更新操作
     *
     * @param session   SSH Session
     * @param targetJar 更新的jar文件在服务器的路径
     */
    private static void updateImage(Session session, String targetJar) throws Exception {
        // 1. 获取运行中的pod name
        String podName = getPodName(session);
        System.out.println("PodName: " + podName);
        if (podName == null || podName.length() == 0) {
            throw new RuntimeException("服务器上未找到对应pod: " + IMAGE_NAME);
        }

        // 2. 获取container id
        String containerId = getContainerId(session);
        System.out.println("ContainerId: " + containerId);

        // 3. 获取image tag
        String imageTag = getImageTag(session);
        System.out.println("ImageTag: " + imageTag);

        // 4. 拷贝jar文件到pod中，覆盖当前的jar文件
        execRemote(session, "kubectl cp " + targetJar + " " + podName + ":" + APP_JAR);
        System.out.println("拷贝文件[" + targetJar + "]到pod中");

        // 5. 修改镜像
        String ret = execRemote(session, "docker commit " + containerId + " " + imageTag);
        System.out.println("修改镜像: " + ret);

        // 6. 重启pod
        ret = execRemote(session, "kubectl delete pod " + podName);
        System.out.println("重启镜像: " + ret);
    }
}
