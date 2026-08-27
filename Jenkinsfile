pipeline{
	agent any
	stages{
		stage('Git Connection Check'){
			steps{
				echo "==============="
				echo "Git 연결 확인"
				echo "==============="
				git branch: 'main',
					url: 'https://github.com/kcw858/SpringPiniaProject_2.git'
				echo "==============="
				echo "Git 연결 완료"
				echo "==============="
			}
		}
	}
}