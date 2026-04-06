pipeline {
    agent any

    environment {
        // REPLACE THIS with the Private IP (if in the same VPC) or Public IP of your Project Server
        PROJECT_SERVER_IP = "35.175.201.135" 
        TARGET_USER = "ec2-user"
        APP_DIR = "/home/ec2-user/Cloud-auto-scaling"
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Jenkins pulls the code from your Git repo into its own workspace
                checkout scm
            }
        }

        stage('Deploy to Remote Server') {
            steps {
                // The sshagent block uses the PEM key you uploaded to Jenkins
                sshagent(credentials: ['project-server-key']) {
                    
                    // 1. Copy all project files from Jenkins to the Project Server
                    sh "scp -o StrictHostKeyChecking=no -r ./* ${TARGET_USER}@${PROJECT_SERVER_IP}:${APP_DIR}/"

                    // 2. SSH into the Project Server to run the deployment commands
                    sh """
                    ssh -o StrictHostKeyChecking=no ${TARGET_USER}@${PROJECT_SERVER_IP} '
                        cd ${APP_DIR}
                        
                        echo "📦 Installing Python Dependencies..."
                        pip3 install -r requirements.txt
                        
                        echo "🐳 Booting Docker Infrastructure..."
                        docker compose down || true
                        docker compose up -d --build --scale web_app=1
                        
                        echo "🧠 Launching AI Brain in Background..."
                        # Kill any old instances of the brain to prevent conflicts
                        pkill -f "python3 brain.py" || true
                        
                        # Run the new brain using nohup so it survives the SSH disconnect
                        nohup python3 brain.py > brain.log 2>&1 &
                    '
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo "✅ Deployment Successful! Dashboard is live on port 8000."
        }
        failure {
            echo "❌ Deployment Failed. Review the Jenkins console output."
        }
    }
}