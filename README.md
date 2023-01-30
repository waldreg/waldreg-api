   
   
![Wald logo](https://user-images.githubusercontent.com/62425964/214485760-209749e1-fddd-44ea-9c21-a689a4add5cc.svg) __api__
---
[License](https://github.com/)      
[Waldreg home page](https://waldreg.org)   
      
![api-version](https://img.shields.io/badge/api--version-0.1.3-blue)    
[![made with love](https://camo.githubusercontent.com/c6c5b56fc051557203c6dffa4242b41b09ff22f6303da15e47162a5c1691e8a5/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d616465253230776974682d4c6f76652d2d2545322539442541342d726564)](https://camo.githubusercontent.com/c6c5b56fc051557203c6dffa4242b41b09ff22f6303da15e47162a5c1691e8a5/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d616465253230776974682d4c6f76652d2d2545322539442541342d726564)   
![acceptance test method coverage](https://img.shields.io/badge/Acceptance%20test%20method%20coverage-89%25-brightgreen) ![acceptance test line coverage](https://img.shields.io/badge/Acceptance%20test%20line%20coverage-89%25-brightgreen) ![acceptance test class coverage](https://img.shields.io/badge/Acceptance%20test%20class%20coverage-92%25-brightgreen)   

## Getting start
Waldreg api server is deployed using Docker.     
This document introduces how to run waldreg api server with Docker.

## Download api server 
1. Clone this repository
``` textile
git clone https://github.com/waldreg/waldreg-api.git
```

2. Go to the path where the Dockerfile is located
``` textile
cd waldreg-api
```

3. Build api server with docker
``` docker
docker build --tag waldreg-api:{api-version} .
```

4. Run server with docker
``` docker
docker run -p 9344:9344 -t waldreg-api:{api-version}
```
