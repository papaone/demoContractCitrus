pipeline {
    agent any
    tools {
        maven 'maven'
    }

    triggers {
        githubPush()
    }

    environment {
        LC_ALL = 'en_US.UTF-8'
        LANG    = 'en_US.UTF-8'
        LANGUAGE = 'en_US.UTF-8'
        EMAIL_TO = 'liliiashaikina3@gmail.com'
    }

    parameters {
        string(name: 'GIT_URL', defaultValue: 'https://github.com/papaone/demoContractCitrus.git', description: 'The target git url')
        string(name: 'GIT_BRANCH', defaultValue: 'jenkins', description: 'The target git branch')
        choice(name: 'BROWSER_NAME', choices: ['chrome', 'firefox'], description: 'Pick the target browser in Selenoid')
        choice(name: 'BROWSER_VERSION', choices: ['86.0', '85.0', '78.0'], description: 'Pick the target browser version in Selenoid')
    }

    stages {
        stage('Pull from GitHub') {
            steps {
                slackSend(message: "Notification from Jenkins Pipeline")
                git ([
                    url: "${params.GIT_URL}",
                    branch: "${params.GIT_BRANCH}"
                    ])
            }
        }
        stage('Run maven clean test') {
            steps {
		// sh меняем на bat, если операционная система Windows
                sh 'mvn clean test -Dbrowser_name=$BROWSER_NAME -Dbrowser_version=$BROWSER_VERSION'
            }
        }
        stage('Backup and Reports') {
            steps {
                archiveArtifacts artifacts: '**/target/', fingerprint: true
            }
            post {
                always {
                  script {

                    // Формирование отчета
                    allure([
                      includeProperties: false,
                      jdk: '',
                      properties: [],
                      reportBuildPolicy: 'ALWAYS',
                      results: [[path: 'target/allure-results']]
                    ])
                    println('allure report created')

                    // Узнаем ветку репозитория
                    def branch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD\n').trim().tokenize().last()
                    println("branch= " + branch)

                    // Достаем информацию по тестам из junit репорта
                    def summary = junit testResults: '**/target/surefire-reports/*.xml'
                    println("summary generated")

                    sendNotifications()

                    // Текст оповещения
                    def sendNotifications() {
		    def summary = junit testResults: '**/target/surefire-reports/*.xml'

		    def branch = bat(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD\n').trim().tokenize().last()
		    def emailMessage = "${currentBuild.currentResult}: Job '${env.JOB_NAME}', Build ${env.BUILD_NUMBER}, Branch ${branch}. \nPassed time: ${currentBuild.durationString}. \n\nTESTS:\nTotal = ${summary.totalCount},\nFailures = ${summary.failCount},\nSkipped = ${summary.skipCount},\nPassed = ${summary.passCount} \n\nMore info at: ${env.BUILD_URL}"

		    emailext (
		        subject: "Jenkins Report",
		        body: emailMessage,
		        to: "${EMAIL_TO}",
		        from: "jenkins@code-maven.com"
    		    )

		    def colorCode = '#FF0000'
		    def slackMessage = "${currentBuild.currentResult}: Job '${env.JOB_NAME}', Build ${env.BUILD_NUMBER}. \nTotal = ${summary.totalCount}, Failures = ${summary.failCount}, Skipped = ${summary.skipCount}, Passed = ${summary.passCount} \nMore info at: ${env.BUILD_URL}"

		    slackSend(color: colorCode, message: slackMessage)
		    }
                  }
                }
            }
        }
    }
}