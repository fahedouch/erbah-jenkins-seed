node(node_label) {
    try {
        stage('Preparation') {
            echo 'VM Status Checkup...'
            shell('/workspace/jenkins-seed/vm-checkup.sh')
            echo 'Get sources from GitHub...'
            checkout changelog: false,
                     poll: false,
                     scm: [
                         $class: 'GitSCM',
                         branches: [[name: branch]],
                         doGenerateSubmoduleConfigurations: false,
                         extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]],
                         submoduleCfg: [],
                         userRemoteConfigs: [[url: git_acct + '@' + git_url + ':fahedouch/' + repo_name + '.git']]]
            sh('chmod +x ./scripts/dc-help.sh')
            version = sh (
                script: './scripts/dc-help.sh version',
                returnStdout: true
            ).trim()
            echo "Tina 3 Manager version: ${version}"
        }
        stage('Build Environment') {
            sh("./scripts/dc-help.sh setup_sonar_properties ${sonar_branch}")
            echo 'Create the container from the UX container...'
            sh('./scripts/dc-help.sh create')
            sh('./scripts/dc-help.sh start')
            sh('./scripts/dc-help.sh setup_conf')
            echo 'The build environment:'
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make clean"')
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make prepare"')
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make check_environment"')
            sh('./scripts/dc-help.sh setup_db')
        }
        stage('Build Frontend') {
            echo 'Building Frontend...'
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make front_build"')
        }
        stage('Build Backend') {
            echo 'Building Backend...'
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make back_build"')
        }
        /* temp place for Sonar Qube */
        if (sonar_branch) {
            stage('Run SonarQube job') {
                echo 'Trigger SonarQube job...'
                build job: name + '_' + type + '_SonarQube'
            }
        }
        stage('Test Frontend') {
            echo 'Testing Frontend...'
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make front_test"')
        }
        stage('Test Backend') {
            echo 'Testing Backend...'
            sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make back_test"')
        }
        /* Here is the best place ever for SonarQube */
        /* Following steps are run only for releases */
        if (branch == 'release') {
            stage('Package Frontend') {
                echo 'Packaging...'
                sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make front_build"')
                sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make front_dist"')
            }
            stage('Package Backend') {
                echo 'Packaging...'
                sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make back_build"')
                sh('./scripts/dc-help.sh exec "/home/ux/bin/ux-make back_dist"')
            }
            stage('Create RPMs') {
                echo 'Creating the RPMs...'
                release = sh (
                    script: './scripts/dc-help.sh release',
                    returnStdout: true
                ).trim()
                sh("./scripts/dc-help.sh exec \"/home/ux/bin/ux-make release ${release} rpm_build\"")
            }
            stage('Publish') {
                sh('./scripts/dc-help.sh publish')
            }
        }
    }
    catch (e) {
        currentBuild.result = "FAILED"
        throw e
    }
    finally {
        //notifyBuild(currentBuild.result)
        echo 'Stop and delete the container...'
        sh('./scripts/dc-help.sh stop')
        sh('./scripts/dc-help.sh delete')
    }
}


def notifyBuild(String buildStatus) {
    // build status of null means successful
    buildStatus =  buildStatus ?: 'SUCCESSFUL'

    def subject = "${env.JOB_NAME} - Build ${env.BUILD_DISPLAY_NAME} - ${buildStatus}"
    def body = "${env.JOB_NAME} - Build ${env.BUILD_DISPLAY_NAME} - ${buildStatus}:\n\nThe log file is attached.\n\nCheck console output at ${env.BUILD_URL} to view the results."

    emailext attachLog: true, body: body , subject: subject, to: mail_list
}

runtime : nodejs
env : flex

