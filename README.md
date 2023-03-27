   
   
![Wald logo](https://user-images.githubusercontent.com/62425964/214485760-209749e1-fddd-44ea-9c21-a689a4add5cc.svg) __api__
---
[License](https://github.com/waldreg/waldreg-api/blob/main/LICENSE)      
[Architecture](https://waldreg.notion.site/Architecture-7a8ff1b597464d468a692f150ca3f755)     
      
![api-version](https://img.shields.io/badge/api--version-0.5.3-92CE64)    
[![made with love](https://camo.githubusercontent.com/c6c5b56fc051557203c6dffa4242b41b09ff22f6303da15e47162a5c1691e8a5/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d616465253230776974682d4c6f76652d2d2545322539442541342d726564)](https://camo.githubusercontent.com/c6c5b56fc051557203c6dffa4242b41b09ff22f6303da15e47162a5c1691e8a5/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d616465253230776974682d4c6f76652d2d2545322539442541342d726564)   
![acceptance test method coverage](https://img.shields.io/badge/Acceptance%20test%20method%20coverage-92%25-brightgreen) ![acceptance test line coverage](https://img.shields.io/badge/Acceptance%20test%20line%20coverage-93%25-brightgreen) ![acceptance test class coverage](https://img.shields.io/badge/Acceptance%20test%20class%20coverage-96%25-brightgreen)   

# Overview
***Waldreg는 동아리를 관리하는데 특화된 서비스로***, 동아리를 운영하며 필요하다고 생각한 기능인 `구성원의 출석 여부 확인 기능`, `내부 데이터를 저장하는 기능`, `역할 기반 구성원의 접근 가능 정보 지정 기능` `구성원들의 가중치를 기반으로 팀을 맺어주는 기능`... 등이 한곳에 모인 프로그램을 찾을 수 없어서 개발을 하게 되었습니다.    
     
Waldreg 를 사용하면 다음과 같은 서비스를 통해 동아리를 효과적으로 관리할 수 있습니다.
1. 출석 시스템을 이용해 동아리에 참석한 동아리원을 판별할 수 있습니다.
2. 상 벌점 시스템을 이용해 동아리원의 기여도를 측정할 수 있습니다.
3. 일정 관리 시스템을 이용해 동아리의 전체적인 일정을 관리 할 수 있습니다.
4. 구성원들의 가중치를 기반으로 자동으로 팀을 만드는 기능을 이용해 팀 구성을 자동화 할 수 있습니다.
5. 구성원들에게 역할을 주어 접근가능한 정보를 조절할 수 있습니다.
6. 저장소와 게시판 기능을 이용해 동아리 내부 데이터를 보관할 수 있습니다.   


# Getting start
Waldreg 는 Docker를 이용해서 배포되고 있으며, 이 문서는 Docker를 이용해 waldreg 애플리케이션을 각자의 서버에 세팅해 구동하는 방법을 설명합니다.

# Download api server 
1. 이 레포지토리를 클론합니다.
``` shell
git clone https://github.com/waldreg/waldreg-api.git
```

2. Dockerfile이 위치한 폴더로 이동합니다.
``` shell
cd waldreg-api
```

3. 서버를 구동하기 위해 Dockerfile 과 같은 경로에 위치한 .env파일을 편집합니다.   
   .env 파일은 다음과 같습니다.
```shell
DB_ROOT_PASSWORD=${db root 유저의 password} 
DB_PASSWORD=${db waldreg 유저의 password} 

DB_URL=jdbc:... # 수정금지 
DDL_AUTO_RULE=update # 만약, DDL생성 기능을 정의합니다. 이미 table을 생성한경우, 혹은 운영환경인경우 none으로 설정하세요
```

4. .env 파일 편집을 마쳤다면, 다음 명령어를 입력해 애플리케이션을 실행합니다.
```shell
docker-compose up
```
