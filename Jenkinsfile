pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''echo username=sk1erdeploy'\n'password=${github-deploy} > gradle.properties.private'''
        sh "./gradlew preprocessResources"
      }
    }
    stage('Build') {
      steps {
        sh "./gradlew jar -PBUILD_ID=${env.BUILD_ID} --no-daemon"
      }
    }


    stage('Report') {
      steps {
        archiveArtifacts 'versions/1.8.9/build/libs/*.jar'
        archiveArtifacts 'versions/1.12.2/build/libs/*.jar'
//         archiveArtifacts 'versions/1.15.2/build/libs/*.jar'
      }
    }
    stage('Deploy') {
            steps {
                nexusPublisher nexusInstanceId: 'sk1errepo', nexusRepositoryId: 'maven-releases', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'versions/1.8.9/build/libs/Elementa1.8.9-${BUILD_ID}.jar']], mavenCoordinate: [artifactId: 'Elementa', groupId: 'club.sk1er', packaging: 'jar', version: '${BUILD_ID}-10809']]]
                nexusPublisher nexusInstanceId: 'sk1errepo', nexusRepositoryId: 'maven-releases', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'versions/1.12.2/build/libs/Elementa1.12.2-${BUILD_ID}.jar']], mavenCoordinate: [artifactId: 'Elementa', groupId: 'club.sk1er', packaging: 'jar', version: '${BUILD_ID}-11202']]]
 //               nexusPublisher nexusInstanceId: 'sk1errepo', nexusRepositoryId: 'maven-releases', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'versions/1.15.2/build/libs/Elementa1.15.2-${BUILD_ID}.jar']], mavenCoordinate: [artifactId: 'Elementa', groupId: 'club.sk1er', packaging: 'jar', version: '${BUILD_ID}-11502']]]
            }
        }
  }
}
