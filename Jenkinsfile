pipeline {
    agent any
    parameters {
        string(name: 'CITY_NAME', defaultValue: 'London', description: 'Enter the city name')
        string(name: 'promotionTestId', defaultValue: '', description: 'Promotion Test ID for Salesforce tracking')
    }
    environment {
        // Salesforce credentials - store these in Jenkins credentials store
        SF_USERNAME = credentials('sf-username')
        SF_PASSWORD = credentials('sf-password')
        SF_SECURITY_TOKEN = credentials('sf-security-token')
        SF_CONSUMER_KEY = credentials('sf-consumer-key')
        SF_CONSUMER_SECRET = credentials('sf-consumer-secret')
        SF_ENDPOINT = 'https://yourinstance.salesforce.com/services/apexrest/jenkins/tests/webook'
        SF_LOGIN_URL = 'https://login.salesforce.com/services/oauth2/token'
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mparida1000/WeatherApp.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Check Class Files') {
            steps {
                sh 'find target/classes -name "*.class"'
            }
        }
        stage('Run Java Program') {
            steps {
                sh "mvn exec:java -Dexec.mainClass=com.example.WeatherCSVGenerator -Dexec.args='${params.CITY_NAME}'"
            }
        }
        stage('Archive CSV') {
            steps {
                archiveArtifacts artifacts: 'weather_data.csv', fingerprint: true
            }
        }
    }
    post {
        always {
            script {
                // Prepare the payload
                def payload = [
                    promotionTestId: params.promotionTestId,
                    BuildNumber: env.BUILD_NUMBER,
                    Status: currentBuild.currentResult
                ]

                // Convert to JSON
                def payloadJson = groovy.json.JsonOutput.toJson(payload)

                try {
                    // Get Salesforce access token
                    def authResponse = httpRequest(
                        acceptType: 'APPLICATION_JSON',
                        contentType: 'APPLICATION_FORM',
                        httpMode: 'POST',
                        requestBody: "grant_type=password&client_id=${SF_CONSUMER_KEY}&client_secret=${SF_CONSUMER_SECRET}&username=${SF_USERNAME}&password=${SF_PASSWORD}${SF_SECURITY_TOKEN}",
                        url: SF_LOGIN_URL,
                        quiet: true
                    )

                    def authData = readJSON text: authResponse.content
                    def accessToken = authData.access_token

                    // Send the webhook
                    httpRequest(
                        acceptType: 'APPLICATION_JSON',
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'POST',
                        requestBody: payloadJson,
                        url: SF_ENDPOINT,
                        customHeaders: [[name: 'Authorization', value: "Bearer ${accessToken}"]],
                        quiet: true
                    )

                    echo "Successfully sent build status to Salesforce"
                } catch (Exception e) {
                    echo "Failed to send build status to Salesforce: ${e.getMessage()}"
                }
            }
        }
    }
}