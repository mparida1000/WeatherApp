pipeline {
    agent any
    environment {
            CITY_NAME = ''
            MULESOFT_ENV = ''
            BRANCH = ''
            SUITES = ''
        }
    parameters {
        string(name: 'CITY_NAME', defaultValue: 'London', description: 'Enter the city name')
    }
    stages {


    stage('Parse JSON Input') {
                steps {
                    script {
                        // Read JSON input from params.JSON_INPUT
                        def requestBody = readJSON text: params.JSON_INPUT

                        // Iterate through JSON parameters and assign values dynamically
                        requestBody.parameter.each { param ->
                            if (param.name == 'CITY_NAME') {
                                env.CITY_NAME = param.value
                            } else if (param.name == 'MulesoftEnv') {
                                env.MULESOFT_ENV = param.value
                            } else if (param.name == 'Branch') {
                                env.BRANCH = param.value
                            } else if (param.name == 'Suites') {
                                env.SUITES = param.value
                            }
                        }

                        // Print extracted values for debugging
                        echo "CITY_NAME: ${env.CITY_NAME}"
                        echo "MulesoftEnv: ${env.MULESOFT_ENV}"
                        echo "Branch: ${env.BRANCH}"
                        echo "Suites: ${env.SUITES}"
                    }
                }
            }



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
                echo "Running Weather App for CITY_NAME: ${env.CITY_NAME}"
                sh "mvn exec:java -Dexec.mainClass=com.example.WeatherCSVGenerator -Dexec.args='${env.CITY_NAME}'"
            }
        }

        stage('Archive CSV') {
            steps {
                archiveArtifacts artifacts: 'weather_data.csv', fingerprint: true
            }
        }
    }
}
