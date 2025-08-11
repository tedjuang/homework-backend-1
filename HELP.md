# 🛠️ 使用說明（HELP.md）

---

## 1️⃣ RocketMQ IP 設定

請將 `broker.conf` 內的下列兩個 IP 替換為你本機的 IP：

```conf
namesrvAddr={YOUR_LOCAL_IP}:9876
brokerIP1={YOUR_LOCAL_IP}
```

> **請將 `{YOUR_LOCAL_IP}` 替換成你自己的電腦 IP，例如 `192.168.1.100`**

---

## 2️⃣ 啟動服務

在專案根目錄下依序執行：

```bash
docker-compose up -d
./mvnw spring-boot:run
```

- `docker-compose up -d`：啟動 MySQL、Redis、RocketMQ 等依賴服務
- `./mvnw spring-boot:run`：啟動 Spring Boot 應用程式

---

## 3️⃣ API 測試

你可以參考 `curls` 檔案，裡面有常用的 API 測試指令範例。

---

如有任何啟動或測試問題，請先確認 IP 設定正確，或參考 README.md 內的詳細說明。
