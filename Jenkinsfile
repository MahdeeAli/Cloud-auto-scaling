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
                    
                    // 1. Copy the files over
                    sh "scp -o StrictHostKeyChecking=no -r ./* ${TARGET_USER}@${PROJECT_SERVER_IP}:${APP_DIR}/"

                    // 2. Setup Docker and Kill the old Brain (Standard SSH)
                    sh """
                    ssh -o StrictHostKeyChecking=no ${TARGET_USER}@${PROJECT_SERVER_IP} '
                        cd ${APP_DIR}
                        
                        echo "📦 Installing Python Dependencies..."
                        pip3 install -r requirements.txt
                        
                        echo "🐳 Booting Docker Infrastructure..."
                        sudo docker compose down || true
                        sudo docker compose up -d --build --scale web_app=1
                        
                        echo "🧹 Wiping out old AI Brain..."
                        # Adding -9 forces immediate termination
                        sudo pkill -9 -f "python3 brain.py" || true
                    '
                    """

                    // 3. Launch the new Brain (Fire-and-Forget SSH)
                    // The '-f' flag forces SSH to go to the background and disconnect immediately
                    sh """
                    ssh -f -o StrictHostKeyChecking=no ${TARGET_USER}@${PROJECT_SERVER_IP} '
                        cd ${APP_DIR}
                        echo "🧠 Launching AI Brain..."
                        nohup python3 brain.py > brain.log 2>&1 < /dev/null &
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