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
                sh 'java -cp target/weatherapp-1.0-SNAPSHOT.jar com.example.WeatherCSVGenerator'
            }
        }
        stage('Archive CSV') {
            steps {
                archiveArtifacts artifacts: 'weather_data.csv', fingerprint: true
            }
        }
    }
}
