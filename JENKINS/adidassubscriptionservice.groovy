node {
    stage 'Strart Cloning the project'
    git 'https://github.com/eugenp/adidassubscriptionservice.git'
   
    dir('adidassubscriptionservice') {
        stage("Start cleaning installing") {
            parallel 'Compilation': {
                sh "./mvnw clean install -DskipTests"
            }
            
        }
         
        stage("Start testing and deployment") {
            parallel 'apply tests': {
                stage("Start runing the tests") {
                    try {
                        sh "./mvnw test -Punit"
                    } catch(err) {
                        step([$class: 'JUnitResultArchiver', testResults: 
                          '**/target/surefire-reports/*Test.xml'])
                        throw err
                    }
                   step([$class: 'JUnitResultArchiver', testResults: 
                     '**/target/surefire-reports/*Test.xml'])
                }
            }
             
            stage("Initializing") {
                sh "pid=\$(lsof -i:8092 -t); kill -TERM \$pid "
                  + "|| kill -KILL \$pid"
                withEnv(['JENKINS_NODE_COOKIE=dontkill']) {
                    sh 'nohup ./mvnw spring-boot:run &'
                }   
            }
        }
    }
}