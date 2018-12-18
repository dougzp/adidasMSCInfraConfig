#!groovy

node {
    stage 'Strart Cloning the project'
    git 'https://github.com/dougzp/adidaseurekaserver.git'
   
    dir('adidaseurekaserver') {
            stage("Compilation and Analysis") {
                parallel 'Compilation': {
                    sh "./mvnw clean install -DskipTests"
                }
            }   
            
            stage("Initialize") {
                sh "pid=\$(lsof -i:8761 -t); kill -TERM \$pid "
                    + "|| kill -KILL \$pid"
                withEnv(['JENKINS_NODE_COOKIE=dontkill']) {
                    sh 'nohup ./mvnw spring-boot:run'
                }   
            }
        }
    }
}
