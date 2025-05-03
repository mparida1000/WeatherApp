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
        SF_ENDPOINT = 'https://mlcg-dev-ed.my.salesforce.com/services/apexrest/jenkins/tests/webook'
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
                // Run Python script instead of original HTTP calls
                sh """
                    python3 ${WORKSPACE}/salesforce_notify.py \
                    '${params.promotionTestId}' \
                    '${env.BUILD_NUMBER}' \
                    '${currentBuild.currentResult}'
                """
            }
        }
    }
}
