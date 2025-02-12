pipeline {
    agent any
    parameters {
        string(name: 'CITY_NAME', defaultValue: 'London', description: 'Enter the city name')
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mparida1000/WeatherApp.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install' // Adjust path if needed
            }
        }
        stage('Check Class Files') {
            steps {
                sh 'find target/classes -name "*.class"'
            }
        }
        stage('Run Java Program') {
            steps {
                sh "mvn exec:java -Dexec.mainClass=com.example.WeatherCSVGenerator1 -Dexec.args='${CITY_NAME}'"
            }
        }

        stage('Archive CSV') {
            steps {
                archiveArtifacts artifacts: 'weather_data.csv', fingerprint: true
            }
        }
    }
}
