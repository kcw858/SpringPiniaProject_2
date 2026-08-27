package com.sist.web.aop;
/*
 * minikube 사용 목적
 *  1) 비용이 안든다
 *  2) 자신 컴퓨터에서 쿠바네티스 사용이 가능 (테스트)
 *  				------ 컨테이너 (도커)
 *  				------ 도커 허브
 *  
 *   도커 이미지를 만든다 -> docker build -t 이미지명 .
 *   도커 태그 생성 -> dokcer tag 이미지명 허브명/이미지명
 *   도커 허브 연결 -> docker login -u 허브명
 *   push -> docker push 허브명/이미지명
 *   pull -> docker pull 허브명/이미지명
 *   pull로 가져온 데이터를 deployment.yaml에 등록
 *   minikube 실행
 *    1. minikube start
 *    2. kubectl apply -f ~/k8s/deployment.yaml
 *    3. kubectl get pods
 *    	=> running 상태가 아닌경우 kubectl logs pod_name으로 확인
 *    4. kubectl get svc (service 확인)
 *    5. minikube service_name 
 *    
 *    
 *    용어 
 *     - 클러스트 : 쿠버네티스가 관리하는 전체 컴퓨터
 *     			=> 1개만 사용 (minikube)
 *     - 노드 : 각 컴퓨터에 있느 서버
 *     - 파드 : 실행하는 가장 작은 단위
 *     - 크루 : 쿠바네티스에 명령을 내리는 도구 kubectl
 *     
 *    동작 순서
 *     컴퓨터
 *     	|
 *     쿠바네티스 클러스트
 *      |
 *     노드 : 각 컴퓨터 서버
 *      |
 *     파드 : 실행이 가능한 파일 모음
 *      |
 *     컨테이너 : 실행 단위
 *     
 *     명령어 기억
 *      => minikube start
 *      => minikube status
 *      => kubectl get nodes
 *      => minikube dashboard
 *      => kubectl get pods
 *      => kubectl get svc
 *      => minikube service 서비스명   => 바로실행
 *      
 *      실제 환경
 *       서버 1 (게시판)
 *       서버 2 (상품)
 *       서버 3 (회원 ..)
 *       
 *       컴퓨터
 *        |
 *        ------- minikube
 *        	|
 *          ------ k8s
 */
public class Minikube {

}
