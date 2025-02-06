import groovy.json.JsonSlurper
pipeline {
    agent any
    environment {
            CITY_NAME = ''
            MULESOFT_ENV = ''
            BRANCH = ''
            SUITES = ''
        }
parameters {
        text(name: 'JSON_INPUT', defaultValue: '{"parameter":[]}', description: 'JSON Payload for Parameters')
    }
    stages {


   stage('Parse JSON Input') {
               steps {
                   script {
                       // Debug: Print received JSON
                       echo "Received JSON_INPUT: ${params.JSON_INPUT}"

                       // Ensure JSON_INPUT is not empty
                       if (!params.JSON_INPUT?.trim()) {
                           error "JSON_INPUT parameter is missing or empty!"
                       }

                       // Parse the JSON input
                       def jsonSlurper = new JsonSlurper()
                       def requestBody = jsonSlurper.parseText(params.JSON_INPUT)

                       // Validate JSON structure
                       if (!requestBody.containsKey('parameter') || !(requestBody.parameter instanceof List)) {
                           error "Invalid JSON structure: Missing 'parameter' array"
                       }

                       // Extract parameters dynamically
                       requestBody.parameter.each { param ->
                           switch (param.name) {
                               case 'CITY_NAME':
                                   env.CITY_NAME = param.value
                                   break
                               case 'MulesoftEnv':
                                   env.MULESOFT_ENV = param.value
                                   break
                               case 'Branch':
                                   env.BRANCH = param.value
                                   break
                               case 'Suites':
                                   env.SUITES = param.value
                                   break
                           }
                       }

                       // Debugging: Print extracted values
                       echo "Extracted Parameters:"
                       echo "CITY_NAME: ${env.CITY_NAME}"
                       echo "MULESOFT_ENV: ${env.MULESOFT_ENV}"
                       echo "BRANCH: ${env.BRANCH}"
                       echo "SUITES: ${env.SUITES}"
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
