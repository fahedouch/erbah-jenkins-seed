def erbah_config = load 'erbah_config.groovy'
/*def sonarqube_config(node, String branch = null, String properties_file) {
    conf = new Node(null, 'hudson.plugins.sonar.SonarRunnerBuilder', [plugin: 'sonar'])
    if (!properties_file) {
        properties_file = 'sonar-project.properties'
    }
    (conf / 'project').setValue(properties_file)

    properties_value = ""
    if (branch != null) {
        properties_value += "sonar.branch=${branch}"
    }
    (conf / 'properties').setValue(properties_value)
    node.append(conf)
}*/

erbah_config.projects.each { project ->
    pipelineJob(project.name + '_' + project.type) {
        displayName(project.name + ' - ' + project.type)
        customWorkspace('/data/jenkins/workspace/' + project.name + '_' + project.type)
        triggers {
            /*if (project.type == erbah_config.stage_nightly) {
                cron ("00 00 * * *")
            }*/
            if (project.type == erbah_config.stage_continuous) {
                // Executed when push event occurs on develop
                gitlabPush {
                    buildOnMergeRequestEvents(false)
                    buildOnPushEvents(true)
                    enableCiSkip(false)
                    setBuildDescription(true)
                    rebuildOpenMergeRequest('never')
                    //includeBranches('develop')
                    targetBranchRegex('develop')
                    //excludeBranches('')
                }
            }
            /*else if (project.type == erbah_config.stage_merge_request) {
                // Executed when a merge request from a feature branch to develop occurs
                gitlabPush {
                    buildOnMergeRequestEvents(true)
                    buildOnPushEvents(false)
                    enableCiSkip(false)
                    setBuildDescription(true)
                    rebuildOpenMergeRequest('never')  // Can be useful?
                    targetBranchRegex('develop')  // TODO To test - or 'feature'?
                }
            }
            else if (project.type == erbah_config.stage_release) {
                gitlabPush {
                    buildOnMergeRequestEvents(false)
                    buildOnPushEvents(true)
                    enableCiSkip(false)
                    setBuildDescription(true)
                    rebuildOpenMergeRequest('never')  // Can be useful?
                    targetBranchRegex('release')  // TODO To test
                }
            }*/
        }
        definition {
            cps {
                node_label = ''
                if (project.jenkins_label) {
                    node_label = project.jenkins_label
                } else {
                    node_label = erbah_config.jenkins_label
                }
                script(
                    "stage_nightly = '$erbah_config.stage_nightly'\n" +
                    "stage_continuous = '$erbah_config.stage_continuous'\n" +
                    "stage_merge_request = '$erbah_config.stage_merge_request'\n" +
                    "stage_release = '$erbah_config.stage_release'\n" +
                    "jenkins_credential = '$erbah_config.jenkins_credential'\n" +
                    "git_url = '$erbah_config.git_url'\n" +
                    "git_acct = '$erbah_config.git_acct'\n" +
                    "name = '$project.name'\n" +
                    "repo_name = '$project.repo_name'\n" +
                    "type = '$project.type'\n" +
                    "cron_schedule = '$project.cron_schedule'\n" +
                    "branch = '$project.branch'\n" +
                    "sonar_branch = '$project.sonar_branch'\n" +
                    "warnings_publisher = $project.warnings_publisher\n" +
                    "unstable_quality_gates = $project.unstable_quality_gates\n" +
                    "mail_list = '$project.mail_list'\n" +
                    "archive_artifacts = '$project.archive_artifacts'\n" +
                    "publish_rpm = $project.publish_rpm\n" +
                    "jenkins_options = '$project.jenkins_options'\n" +
                    "branch_filter_type = '$project.branch_filter_type'\n" +
                    "target_branch_regex = '$project.target_branch_regex'\n" +
                    "node_label = '$node_label'\n" +
                    "\n\n" +
                    readFileFromWorkspace("erbah_workflow.groovy")
                )
            }
        }
    }

    job(project.name + '_' + project.type + '_SonarQube') {
        displayName(project.name + ' - ' + project.type + ' - SonarQube')
        customWorkspace('/data/jenkins/workspace/' + project.name + '_' + project.type)
        parameters {
            nodeParam(node_label) {
                defaultNodes([node_label])
                allowedNodes([node_label])
            }
        }
        steps {
            shell("pwd && ls -l")
        }
        configure {
            sonarqube_config(it / 'builders', project.sonar_branch, project.sonar_file)
        }
        /*steps {
            shell("python tools/scripts/statusQualityGates.py " + project.sonar_key_name + " " + project.sonar_branch)
        }*/
    }
}

sectionedView('UX - TinMan3') {
    sections {
        listView {
            name("TinMan3")
            jobs {
                regex('TinMan3_.*')
            }
            columns {
                status()
                weather()
                coverageColumn {
                    type("Line coverage")
                }
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                lastBuildConsole()
                buildButton()
            }
        }
    }
}
