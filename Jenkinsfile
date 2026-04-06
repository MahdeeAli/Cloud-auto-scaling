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
                sshagent(credentials: ['project-server-key']) {
                    
                    // 1. Copy files
                    sh "scp -o StrictHostKeyChecking=no -r ./* ${TARGET_USER}@${PROJECT_SERVER_IP}:${APP_DIR}/"

                    // 2. SSH and run commands
                    sh """
                    ssh -o StrictHostKeyChecking=no ${TARGET_USER}@${PROJECT_SERVER_IP} '
                        cd ${APP_DIR}
                        
                        echo "📦 Installing Python Dependencies..."
                        pip3 install -r requirements.txt
                        
                        echo "🐳 Booting Docker Infrastructure..."
                        sudo docker compose down || true
                        sudo docker compose up -d --build --scale web_app=1
                        
                        echo "🧠 Launching AI Brain in Background..."
                        # Use sudo to ensure we can kill ANY old instance
                        sudo pkill -f "python3 brain.py" || true
                        
                        # Use < /dev/null to cleanly detach the SSH session
                        nohup python3 brain.py > brain.log 2>&1 < /dev/null &
                        
                        # Pause for 2 seconds to let the process detach before closing SSH
                        sleep 2
                        
                        echo "✅ System is Live!"
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