pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''echo username=sk1erdeploy'\n'password=${22f85b5a-6300-48ee-b2f9-a04fbe40403f} > gradle.properties.private'''
        sh "./gradlew preprocessResources"
      }
    }
    stage('Build') {
      steps {
        sh "./gradlew build -PBUILD_ID=${env.BUILD_ID} --no-daemon"
      }
    }


    stage('Report') {
      steps {
        archiveArtifacts 'versions/1.8.9/build/libs/*.jar'
        archiveArtifacts 'versions/1.12.2/build/libs/*.jar'
//         archiveArtifacts 'versions/1.15.2/build/libs/*.jar'
      }
    }
  }
}
