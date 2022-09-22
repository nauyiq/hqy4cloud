def jnlp_image = "registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jnlp-slave:latest"
def branch = "*/dev"
def base_dir = "/home/service"

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
            dir("${base_dir}") {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/dev']],
                    userRemoteConfigs: [[credentialsId: "", url: "https://github.com/nauyiq/hqy-parent-all.git"]],
                    extensions: [
                    [$class: 'CloneOption', depth: 1, noTags: false, reference: '', shallow: true]
                    ]
                ])
            }

        }

         stage('Maven Build') {
            dir("${base_dir}") {
               sh """
                  mvn clean compile install -Dmaven.test.skip=true
                  """
            }
         }
    }
}
