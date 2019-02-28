


class erbah_config {
    public static repo_name = 'erbah'
    public static git_url = 'github.com'
    public static git_acct = 'git'
    public static jenkins_label = 'erbah'

    // Variables to help always use the same name for the stages
    public static stage_nightly = 'Nightly'
    public static stage_continuous = 'Continuous'
    public static stage_merge_request = 'Merge Request'
    public static stage_release = 'Release'

    /*
     * A project have the following elements:
     * - name:                      The name of the project
     * - repo_name:                 The name of the git repository
     * - display_name:              The display name (without Continuous/Nightly/...)
     * - type:                      The type of the build (Continuous/Merge_Request/Nightly/Release)
     * - branch:                    The git branch to checkout
     * - sonar_branch:              The sonar branch
     * - doxyfile_path:             The path of the doxyfile (Optional)
     * - warnings_publisher:        Flag to publish warning (compilation/doxygen...)
     * - unstable_quality_gates:    Flag to mark unstable build if quality gates is WARN
     * - is_library:                Flag indicating if the job is a library
     * - mail_list:                 Mail list to send report (optional)
     * - packaging:                 Flag to package the build
     * - publishing:                Flag to publish the build
     * - cron_schedule:             Cron schedule (for Nightly)
     * - archive_artifacts:         Files to be archived
     * - jenkins_options:           Options to to launch the Jenkins script
     * - disable_cobertura_report:  Disable the publishing of cobertura report
     * - jenkins_label:             Label to decide on which jenkins slave the job will be run
     * - note_event:                Sentence send to Jenkins to rebuild the MR
     */
    public static projects = [
            // TinMan3 - Nightly
            [name: 'erbah',
             repo_name: 'erbah',
             type: this.stage_nightly,
             cron_schedule: 'H 2 * * *',
             branch: 'develop',
             sonar_branch: 'develop',
             warnings_publisher: true,
             unstable_quality_gates: true,
             mail_list: 'fahed.dorgaa@gmail.com',
             archive_artifacts: 'reports/**/*.xml',
             publish_rpm: true,
             failedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             skippedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             jenkins_options: '--nightly',
             jenkins_label: 'erbah',
             branch_filter_type: 'All',
             target_branch_regex: ''],

            // TinMan3 - Merge Request
            [name: 'erbah',
             repo_name: 'erbah',
             type: this.stage_merge_request,
             cron_schedule: 'H 2 * * *',
             branch: 'origin/${githubSourceBranch}',
             sonar_branch: 'merge_request',
             warnings_publisher: true,
             unstable_quality_gates: true,
             mail_list: 'fahed.dorgaa@gmail.coms',
             archive_artifacts: 'reports/**/*.xml',
             publish_rpm: true,
             failedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             skippedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             jenkins_options: '--merge-request',
             jenkins_label: 'tinman3',
             branch_filter_type: 'All',
             target_branch_regex: ''],

            // TinMan3 - Continuous
            [name: 'erbah',
             repo_name: 'erbah',
             type: this.stage_continuous,
             cron_schedule: 'H 2 * * *',
             branch: 'develop',
             sonar_branch: 'continuous',
             warnings_publisher: true,
             unstable_quality_gates: true,
             mail_list: 'fahed.dorgaa@gmail.coms',
             archive_artifacts: 'reports/**/*.xml',
             publish_rpm: true,
             failedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             skippedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             jenkins_options: '--continuous',
             jenkins_label: 'erbah',
             branch_filter_type: 'All',
             target_branch_regex: ''],

            // TinMan3 - Release
            [name: 'erbah',
             repo_name: 'erbah',
             type: this.stage_release,
             cron_schedule: 'H 2 * * *',
             branch: '*/master,*/release',
             sonar_branch: 'release',
             warnings_publisher: true,
             unstable_quality_gates: true,
             mail_list: 'fahed.dorgaa@gmail.com',
             archive_artifacts: 'reports/**/*.xml',
             publish_rpm: true,
             failedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             skippedThresholds: [unstable: 0, unstableNew: 0, failure: 15, failureNew: 15],
             jenkins_options: '--release',
             jenkins_label: 'erbah',
             branch_filter_type: 'All',
             target_branch_regex: '']
    ]
}

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
                githubPush()
            }
            /*else if (project.type == erbah_config.stage_merge_request) {
                // Executed when a merge request from a feature branch to develop occurs
                githubPush {
                    buildOnMergeRequestEvents(true)
                    buildOnPushEvents(false)
                    enableCiSkip(false)
                    setBuildDescription(true)
                    rebuildOpenMergeRequest('never')  // Can be useful?
                    targetBranchRegex('develop')  // TODO To test - or 'feature'?
                }
            }
            else if (project.type == erbah_config.stage_release) {
                githubPush {
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

    /*job(project.name + '_' + project.type + '_SonarQube') {
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
        steps {
            shell("python tools/scripts/statusQualityGates.py " + project.sonar_key_name + " " + project.sonar_branch)
        }
    }*/
}



