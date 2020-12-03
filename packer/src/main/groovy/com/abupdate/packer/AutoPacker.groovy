package com.abupdate.packer

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class AutoPacker implements Plugin<Project> {

    Project project
    PackOutputConfigExtensionHandler packOutputConfigExtensionHandler = new PackOutputConfigExtensionHandler()
    TaskConfigExtensionHandler taskConfigExtensionHandler = new TaskConfigExtensionHandler()
//    boolean paramsVerifyOK = false

    @Override
    void apply(Project project) {
        Log.D("apply project start..")
        this.project = project
        project.extensions.create('autoPack', AutoPackExtension)
        project.autoPack.extensions.create('packOutputConfig', PackOutputConfig)
        project.autoPack.extensions.create('taskConfig', TaskConfig)
        AutoPackTask autoPackTask = project.tasks.create(AutoPackTask.taskName(), AutoPackTask.class)
        Task buildTask = project.tasks.getByName('assemble')
        autoPackTask.setGroup('AutoPack')
        autoPackTask.dependsOn(buildTask)
        autoPackTask.setDescription('Automated packaging task')

        project.afterEvaluate {
            Log.D("afterEvaluate")
            if (hasAutoPackTask()) {
                Log.D("hasAutoPackTask")
                def autoPackExtension = project['autoPack']
                if (autoPackExtension == null) {
                    throw new GradleException('Please config the autoPack dsl')
                }

                def taskConfigExtension = autoPackExtension.taskConfig
                if (taskConfigExtension == null) {
                    throw new GradleException('Please config taskConfigExtension')
                } else {
                    taskConfigExtensionHandler.applyTaskConfig(project, taskConfigExtension)
                }

                def packOutputConfig = autoPackExtension.packOutputConfig
                if (packOutputConfig == null) {
                    throw new GradleException('Please config packOutputConfig')
                } else {
                    packOutputConfigExtensionHandler.renamePackFile(project, autoPackExtension)
                }
            } else {
                Log.D("not has AutoPackTask")
            }
            Log.D("apply project end..")
        }

//        def task = project.task('customTaskWithParams')
//        Map<Project, Set<Task>> maps = project.getRootProject().getAllTasks(false)
//        D("============maps:" + maps)
//
//        def task1 = project.tasks.getByName('assemble')
//        D("======task1:" + task1)
////        Set<Task> assembleTask = project.getTasksByName(":assembleRelease", false)
//        Set<Task> assembleTask = project.getTasksByName("assembleDebug", false)
//        D("============" + project.name + "  ,, " + assembleTask.size())
//        def iterator = assembleTask.iterator()
//        while (iterator.hasNext()) {
//            def next = iterator.next()
//            D("============" + next.getName())
//        }
//
//        task.doFirst {
//            D("-----------------doFirst-------------------")
//            def autoPackerFile = project.getProject().file("auto-packer.properties")
//            if (!autoPackerFile.canRead()) {
//                D("file which named auto-packer.properties was not found")
//                throw new RuntimeException("file which named auto-packer.properties was not found")
//            }
//            def autoPackerProperties = new Properties()
//            autoPackerProperties.load(new FileInputStream(autoPackerFile))
//
//            if (!autoPackerProperties.containsKey("OUTPUT_DIR")) {
//                throw new RuntimeException("file which named keystore.properties miss some entries:OUTPUT_DIR")
//            }
//
//            paramsVerifyOK = true
//        }
//
//        task.doLast {
//            D("-----------------doLast-------------------")
//
//        }

    }

    boolean hasAutoPackTask() {
        def taskNames = project.gradle.startParameter.taskNames
        List<String> targetTaskNames = [AutoPackTask.taskName(),
                                        AutoPackTask.taskShortName(),
                                        String.format(":%s:%s", project.name, AutoPackTask.taskName()),
                                        String.format(":%s:%s", project.name, AutoPackTask.taskShortName())]
        def hasAutoPackTask = false
        taskNames.each { name ->
            if (targetTaskNames.contains(name)) {
                hasAutoPackTask = true
            }
        }
        return hasAutoPackTask
    }
}