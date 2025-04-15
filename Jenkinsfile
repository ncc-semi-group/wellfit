pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
metadata:
  namespace: jenkins
  labels:
    jenkins/agent-type: kaniko
spec:
  nodeSelector:
    nodetype: agent
  containers:
  - name: jnlp
    image: chauid/jenkins-inbound-agent:1.0
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command:
      - /busybox/cat
    tty: true
    volumeMounts:
      - name: docker-secret
        mountPath: /kaniko/.docker
  volumes:
    - name: docker-secret
      secret:
        secretName: docker-config
            '''
        }
    }

    environment {
        IMAGE_NAME = 'wellfit-hub.kr.ncr.ntruss.com/wellfit'
        TAG = "${env.BUILD_NUMBER}"
        APPLICATION_PROPERTIES = credentials('wellfit_application_properties')
    }

    stages {
        stage('Copy Properties') {
            steps {
                sh 'cp -f ${APPLICATION_PROPERTIES} ./src/main/resources/application.properties'
            }
        }

        stage('Build with Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Spring Boot Version Check') {
            steps {
                sh """
                    PROJECT_NAME=\$(sed -nE "s/^rootProject.name *= *'([^']+)'/\\1/p" settings.gradle)
                    sed -i "s/{PROJECT_NAME}/\${PROJECT_NAME}/g" Dockerfile
                    VERSION=\$(sed -nE "s/^version *= *'([^']+)'/\\1/p" build.gradle)
                    sed -i "s/{VERSION}/\${VERSION}/g" Dockerfile
                """
            }
        }

        stage('Build Docker Image & Push to Registry') {
            steps {
                container('kaniko') {
                    sh "/kaniko/executor --context . --dockerfile Dockerfile --destination ${IMAGE_NAME}:${TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                    DEPLOYMENT_NAME='wellfit-app'
                    CONTAINER_NAME='wellfit-app'
                    NAMESPACE='wellfit-deploy'
                    IMAGE_TAG="${IMAGE_NAME}:${TAG}"
                    kubectl set image deployment/\${DEPLOYMENT_NAME} \${CONTAINER_NAME}=\${IMAGE_TAG} -n \${NAMESPACE}
                    kubectl rollout status deployment/\${DEPLOYMENT_NAME} -n \${NAMESPACE}
                """
            }
        }
    }

    post {
        always {
            echo 'The process is completed. '
        }
    }
}
