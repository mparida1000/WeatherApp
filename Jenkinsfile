pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mparida1000/WeatherApp.git'
            }
        }
        stage('Build') {
            steps {
                sh '/usr/bin/mvn clean install'  // Adjust path if needed
            }
        }
        stage('Run Java Program') {
            steps {
                sh 'java -cp target/classes com.example.WeatherCSVGenerator'
            }
        }
        stage('Archive CSV') {
            steps {
                archiveArtifacts artifacts: 'weather_data.csv', fingerprint: true
            }
        }
    }
}
