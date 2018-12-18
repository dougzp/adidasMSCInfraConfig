node {
    stage 'Clone the project'
    git 'https://github.com/dougzp/adidasapi.git'
   
    dir('adidasapi') {
            stage("Compilation and Analysis") {
                parallel 'Compilation': {
                    sh "./mvnw clean install -DskipTests"
                }
            }  
             
            stage("Staging") {
                sh "pid=\$(lsof -i:8091 -t); kill -TERM \$pid "
                  + "|| kill -KILL \$pid"
                withEnv(['JENKINS_NODE_COOKIE=dontkill']) {
                    sh 'nohup ./mvnw spring-boot:run &'
                }   
            }
        }
    }
}