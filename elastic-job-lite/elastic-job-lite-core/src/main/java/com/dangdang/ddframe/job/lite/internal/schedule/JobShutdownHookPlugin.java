package com.dangdang.ddframe.job.lite.internal.schedule;

import com.dangdang.ddframe.job.lite.internal.election.LeaderService;
import com.dangdang.ddframe.job.lite.internal.instance.InstanceService;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.plugins.management.ShutdownHookPlugin;
import org.quartz.spi.ClassLoadHelper;

import java.util.Date;

/**
 * 作业关闭钩子.
 *
 * @author zhangliang
 */
public final class JobShutdownHookPlugin extends ShutdownHookPlugin {
    
    private String jobName;
    
    @Override
    public void initialize(final String name, final Scheduler scheduler, final ClassLoadHelper classLoadHelper) throws SchedulerException {
        super.initialize(name, scheduler, classLoadHelper);
        jobName = scheduler.getSchedulerName();
    }
    
    @Override
    public void shutdown() {
        System.out.println("shutdown now");
        CoordinatorRegistryCenter regCenter = JobRegistry.getInstance().getRegCenter(jobName);
        if (null == regCenter) {
            return;
        }
        LeaderService leaderService = new LeaderService(regCenter, jobName);
        if (leaderService.isLeader()) {
            leaderService.removeLeader();
        }
        new InstanceService(regCenter, jobName).removeInstance();
        System.out.println("shutdown finish:" + new Date());
    }
}
