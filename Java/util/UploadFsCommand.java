package uyun.show.server.domain.util;

import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uyun.show.server.domain.Constants;

import java.io.File;

public class UploadFsCommand extends HystrixCommand<String> {

    private final static Logger logger = LoggerFactory.getLogger(UploadFsCommand.class);

    private String msg;
    private String dir;
    private File file;


    public UploadFsCommand(String msg, String dir, File file) {

        super(Setter.withGroupKey(
                //服务分组
                HystrixCommandGroupKey.Factory.asKey("UploadFSGroup"))
                //线程分组
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("UploadFSPool"))

                //线程池配置
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(10)
                        .withKeepAliveTimeMinutes(5)
                        .withMaxQueueSize(100)
                        .withQueueSizeRejectionThreshold(10000))

                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
        );
        this.msg = msg;
        this.dir = dir;
        this.file = file;
    }

    @Override
    public String run() throws Exception {
        if (file == null || dir == null || dir.isEmpty()) {
            return "failed";
        }

        try {
            for (int i = 0; i < 5; i++) {
                String result = StoreFSUtil.UploadFile(Constants.STORE_FS_URL, dir, file.getName(), file.getPath(), "", "true", "");
                if (result != null && !result.isEmpty()) {
                    return "success";
                }
            }
        } catch (Exception e) {
            logger.error(msg + file.getName() + " 到 STORE-FS 异常：" + e.getMessage());
        }

        return "failed";
    }
}
