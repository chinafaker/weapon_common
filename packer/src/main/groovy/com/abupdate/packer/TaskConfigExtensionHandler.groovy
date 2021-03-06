package com.abupdate.packer

import org.gradle.api.Project

class TaskConfigExtensionHandler {

    static void applyTaskConfig(Project project, def taskConfigExtension) {
        Log.D("applyTaskConfig() start")
        String[] disableProductFlavors = taskConfigExtension.disableProductFlavors as String[]
        String[] disableBuildTypes = taskConfigExtension.disableBuildTypes as String[]
        def targetTasks = project.tasks.findAll { task ->
            def taskName = task.name.toLowerCase()
            if (containInArray(taskName, disableProductFlavors)) {
                return true
            }
            if (containInArray(taskName, disableBuildTypes)) {
                return true
            }

            return false
        }
        targetTasks.each {
            Log.D("disable task ${it.name} by autoPackPlugin")
            it.setEnabled false
        }
    }

    static boolean containInArray(String taskName, String[] array) {
        for (key in array) {
            key = key.toLowerCase()
            if (taskName.contains(key)) {
                return true
            }
        }
        return false
    }

}