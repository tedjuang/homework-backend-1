## 1. 設定環境變數

broker.conf 內下面這兩個 ip 換成自己的 ip
namesrvAddr={YOUT_LOCAL_IP}:9876
brokerIP1={YOUT_LOCAL_IP}

## 2. 啟動應用程式

```bash
docker-compose up -d
./mvnw spring-boot:run
```

## 3. curls

參考 curls 有之前使用紀錄
