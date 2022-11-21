def jnlp_image = "registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jnlp-slave:latest"
def base_dir = "/home/jenkins/agent/workspace/hqy-parent-all"

podTemplate(
    label: 'jenkins-agent',
    cloud: 'kubernetes',
    containers: [
        containerTemplate(name: 'jnlp',
                          image: "${jnlp_image}",
                          alwaysPullImage: false,
                          privileged: true,
                          ttyEnabled: true)
    ],
    volumes: [
//         hostPathVolume(mountPath: '/usr/local/apache-maven-3.8.6/repo', hostPath: '/usr/local/apache-maven-3.8.6/repo')
           nfsVolume(
                mountPath: '/usr/local/apache-maven-3.8.6/repo',
                serverAddress: '172.30.0.10',
                serverPath: '/hongqy/share/data/maven-repository',
                readOnly: false)
    ]) {

    node('jenkins-agent'){
        stage('Git Clone') {
            dir("${base_dir}") {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/test']],
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
