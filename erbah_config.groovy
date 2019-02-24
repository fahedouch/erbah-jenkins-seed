class tinman3_config {
    public static jenkins_credential = '4600f7bf-1853-49fb-b69b-f1c8bc1091ee'
    public static repo_name = 'erbah'
    public static git_url = 'github.com/fahedouch'
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
        [name: 'TinMan3',
         repo_name: 'tina-manager-3',
         type: this.stage_merge_request,
         cron_schedule: 'H 2 * * *',
         branch: 'origin/${gitlabSourceBranch}',
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
        [name: 'TinMan3',
         repo_name: 'tina-manager-3',
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
