pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    environment {
        IMAGE_NAME    = 'northwind-api-server'
        HELM_RELEASE  = 'northwind-api-service-dev'
        HELM_CHART    = 'charts/northwind'
        NAMESPACE     = 'default'
        KUBECONFIG    = credentials('northwind-kubeconfig')
    }

    stages {
       stage('Checkout') {
           steps {
               checkout([
                   $class: 'GitSCM',
                   branches: scm.branches,
                   extensions: [
                       [$class: 'CloneOption', shallow: false, noTags: false, depth: 0]
                   ],
                   userRemoteConfigs: scm.userRemoteConfigs
               ])
           }
       }

        stage('Resolve Version') {
            steps {
                dir("${env.WORKSPACE}") {
                    script {
                        env.VERSION = sh(
                            script: './gradlew -q printVersion',
                            returnStdout: true
                        ).trim()
                        echo "Building version: ${env.VERSION}"
                    }
                }
            }
        }

//         stage('Build & Test') {
//             steps {
//                 dir("${env.WORKSPACE}") {
//                     sh './gradlew clean build'
//                 }
//             }
//         }

        stage('Docker Build') {
            steps {
                dir("${env.WORKSPACE}") {
                    sh """
                        docker buildx build \\
                            --load \\
                            -t ${IMAGE_NAME}:${VERSION} \\
                            .
                    """
                }
            }
        }

        stage('Helm Deploy') {
            steps {
                dir("${env.WORKSPACE}") {
                    sh """
                        helm upgrade --install ${HELM_RELEASE} ${HELM_CHART} \\
                            --namespace ${NAMESPACE} \\
                            --set image.tag=${VERSION} \\
                            --wait \\
                            --timeout 3m
                    """
                }
            }
        }
    }

    post {
        failure {
            echo "Pipeline failed. Helm release was not upgraded."
        }
        success {
            echo "Deployed ${IMAGE_NAME}:${VERSION} to local Kubernetes successfully."
        }
    }
}