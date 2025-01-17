# NBE3-4-1-Team03

## 서비스 시작하기

### 1. 환경 변수 설정
루트 경로에 `.env` 파일을 생성해주세요. 해당 파일은 프로젝트 실행에 필요한 환경 변수를 포함해야 합니다.  
경로: `/NBE3-4-1-Team03-main/backend/.env`

#### 내용 예시:
```env
JWT_SECRET=your_cret
```

### 2. Redis 설정
이 프로젝트는 Redis를 사용하고 있습니다. Docker를 통해 Redis를 설치하고 실행해주세요.

#### 1. Redis 이미지 다운로드
아래 명령어로 Docker에서 Redis 이미지를 다운로드합니다.
```bash
docker pull redis:latest
```
#### 2. Redis 컨테이너 실행
아래 명령어로 Redis 컨테이너를 실행합니다.
```bash
docker run -d --name redis-server -p 6379:6379 redis:latest
```
#### 3. Redis 실행 확인 (선택 사항)
Redis 컨테이너가 정상적으로 실행 중인지 확인하려면 아래 명령어를 사용하세요.
```bash
docker ps
```
#### 4. Redis CLI 접속 (선택 사항)
Redis CLI에 접속하려면 아래 명령어를 사용하세요.
```bash
docker exec -it redis-server redis-cli
```
CLI에 접속 후, 간단한 명령어로 Redis가 정상 작동하는지 확인할 수 있습니다.
```bash
ping
```
응답으로 pong이 표시되면 정상 작동 중입니다.