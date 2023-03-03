   
   
![Wald logo](https://user-images.githubusercontent.com/62425964/214485760-209749e1-fddd-44ea-9c21-a689a4add5cc.svg) __api__
---
Waldreg는 동아리와 스터디 관리를 도와주기위해 탄생했습니다.

[License](https://github.com/)      
[Architecture](https://github.com/)     
[Waldreg home page](https://waldreg.org)   
      
![api-version](https://img.shields.io/badge/api--version-0.5.0-92CE64)    
[![made with love](https://camo.githubusercontent.com/c6c5b56fc051557203c6dffa4242b41b09ff22f6303da15e47162a5c1691e8a5/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d616465253230776974682d4c6f76652d2d2545322539442541342d726564)](https://camo.githubusercontent.com/c6c5b56fc051557203c6dffa4242b41b09ff22f6303da15e47162a5c1691e8a5/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d616465253230776974682d4c6f76652d2d2545322539442541342d726564)   
![acceptance test method coverage](https://img.shields.io/badge/Acceptance%20test%20method%20coverage-93%25-brightgreen) ![acceptance test line coverage](https://img.shields.io/badge/Acceptance%20test%20line%20coverage-94%25-brightgreen) ![acceptance test class coverage](https://img.shields.io/badge/Acceptance%20test%20class%20coverage-97%25-brightgreen)   

## Getting start
Waldreg 는 Docker를 이용해서 배포되고 있으며, 이 문서는 Docker를 이용해 waldreg 애플리케이션을 구동하는 방법을 설명합니다.

## Download api server 
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
