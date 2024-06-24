@Library('jsl') _
// This is a sample Jenkinsfile for deploying a service using Helm3. In production, there will be many more steps
// like checking if it's a PR, different conditional stages to run tests and qa deployment, etc.
// The nightly build config can be added.
// The pipeline will be triggered by a webhook from the SCM
// For production deployment, there will be a manual approval step
version = ''
current_tag = ''

def kube_helm3_deploy(String release_name, String environment, String chart_path, String kubernetes_namespace, String kube_context, String additionalArgs = '') {
    sh '''
   echo "deploying ''' + release_name + '''-''' + chart_path + '''"
   pushd ''' + chart_path + '''
   helm3 lint . -f values.''' + environment + '''.yaml
   helm3 upgrade --wait --install ''' + additionalArgs + ''' ''' + release_name + ''' . -f values.''' + environment + '''.yaml  --namespace ''' + kubernetes_namespace + '''  --kube-context ''' + kube_context + ''';
   popd
   '''
}
pipeline {
 agent any
    options {
        disableConcurrentBuilds()
    }
environment {

    }
     stages {
            stage('run tests and build') {
                steps {
                    script {
                        sh'''
                        mvn clean install
                        '''
                    }
                }
            }
            stage('deply service') {
                  steps {
                     script {
                          kube_helm3_deploy()
                          }
                     }
            }
     }
}