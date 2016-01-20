package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;

import java.util.Map;

/**
 * Created by BradeaC on 15/12/2015.
 */
public class DockerTaskExecutor extends TaskExecutor
{
    public DockerTaskExecutor(JobConsoleLogger console, Context context, Map config)
    {
        super(console, context, config);
    }

    public Result execute (Config config, Context context)
    {
        try
        {
            return runCommand (context, config);
        }
        catch (Exception e)
        {
            return new Result(false, "Failed while running the task", e);
        }
    }

    public Result runCommand (Context taskContext, Config taskConfig) throws Exception
    {
        try
        {
            DockerBuildCommand build = new DockerBuildCommand(taskContext, taskConfig);
            log("Build command: " + build.getCommand());
            build.run();

            DockerTagCommand tag = new DockerTagCommand(taskContext, taskConfig);
            log("Tag command: " + tag.getCommand());
            tag.run();

            DockerLoginCommand login = new DockerLoginCommand(taskContext, taskConfig);
            log("Login command: " + login.getCommand());
            login.run();

            DockerPushCommand push = new DockerPushCommand(taskContext, taskConfig);
            log("Push command: " + push.getCommand());
            push.run();

            return new Result(true, "Finished");
        }
        catch (Exception e)
        {
            return new Result(false, "Failed", e);
        }
        finally
        {
            new DockerCleanCommand(taskContext, taskConfig).run();
        }
    }

    @Override
    protected String getPluginLogPrefix()
    {
        return "Docker pipeline plugin: ";
    }
}
