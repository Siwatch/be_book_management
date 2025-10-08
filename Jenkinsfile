pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'cicd', url: 'https://github.com/Siwatch/be_book_management.git'
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }

        // stage('Integration Test') {
        //     steps {
        //         // ตัวอย่าง spin up database ด้วย Docker ก่อน test
        //         sh 'docker compose -f docker-compose-test.yml up -d'
        //         sh './mvnw verify -Pintegration-test'
        //         sh 'docker compose -f docker-compose-test.yml down'
        //     }
        // }

        stage('Deploy') {
            steps {
                echo '🚀 Deploy to local/staging server...'
                // sh 'scp target/app.jar user@server:/path/to/deploy'
            }
        }
    }
}
