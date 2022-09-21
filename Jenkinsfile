def jnlp_image = "registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jnlp-slave:latest"
def git_address = "https://github.com/nauyiq/hqy-parent-all.git"
def branch = "*/dev"
def base_dir = "/home/jenkins/agent/workspace/hqy-parent-all_dev"

podTemplate(
    label: 'jenkins-agent',
    cloud: 'kubernetes',
    containers: [
        containerTemplate(name: 'jnlp', image: "${jnlp_image}",  alwaysPullImage: true)
    ],
    volumes: [
        hostPathVolume(mountPath: '/usr/local/apache-maven-3.8.6/repo', hostPath: '/usr/local/apache-maven-3.8.6/repo')
    ]) {

    node('jenkins-agent'){
        stage('Git Clone') {
            checkout([
            $class: 'GitSCM',
            branches: [[name: '${branch}']],
            userRemoteConfigs: [[credentialsId: "", url: "${git_address}"]],
            extensions: [
            [$class: 'CloneOption', depth: 1, noTags: false, reference: '', shallow: true]
            ]
            ])
        }

         stage('Maven Build') {
            sh """
                cd /home/jenkins/agent/workspace/hqy-parent-all_dev
                ls
                mvn clean compile install -Dmaven.test.skip=true
            """
         }
    }
}
