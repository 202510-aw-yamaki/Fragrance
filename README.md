# Fregrance

フレグランスワークショップのポートフォリオ用 Web アプリケーションです。

## 概要

本プロジェクトは、来店前のユーザーに対して「診断」ではなく「香りづくりの準備体験」を提供することを目的としたフレグランスページです。トップページからアンケート、香りグラフ、予約、予約完了までの導線を持ち、現在は Spring Boot + MyBatis + MySQL を用いた予約導線のバックエンド実装まで進んでいます。

## 使用技術

- Java 21
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Security
- MyBatis
- Maven Wrapper
- MySQL
- HTML / CSS / JavaScript

## 現在の進捗

- Spring Boot の基本骨格、画面返却、静的資産移行を完了
- 既存フロント画面を `src/main/resources/templates` へ同期
- CSS / 画像を `src/main/resources/static` へ同期
- `FragrancePageController` による各公開画面の返却を実装
- `/api/health` を実装
- `/api/reservation-slots` を実装
- `/api/reservations` を実装
- `/api/questionnaire-results` を実装
- 予約枠の DB 実データ返却、予約登録 DB 永続化、`reservationCode` 再取得を確認
- `reservations.questionnaire_result_code` による予約とアンケート結果の内部紐付けを実装
- 公開画面と公開 API を `permitAll` にした基本 Security 設定を実装
- `./mvnw.cmd test` で 12 件のテスト成功を確認

## 画面一覧

- `/`
- `/index.html`
- `/questionnaire`
- `/questionnaire.html`
- `/questionnaire/step2`
- `/questionnaire_step2.html`
- `/graph`
- `/fragrance-graph.html`
- `/reservation`
- `/reservation.html`
- `/reservation/complete`
- `/reservation-complete.html`

## 起動手順

1. Java 21 と MySQL を利用可能な状態にする
2. 必要に応じて `src/main/resources/application.yml` の接続設定を確認する
3. プロジェクトルートで Spring Boot アプリケーションを起動する

```bash
.\mvnw.cmd spring-boot:run
```

テスト実行:

```bash
.\mvnw.cmd test
```

起動後、ブラウザで `http://localhost:8080/` または `http://localhost:8080/index.html` を開きます。

## 設計資料

設計資料は主に以下を参照します。

- `p-answer_0316/docs/07-specification.html`
- `p-answer_0316/docs/08-db-design.html`
- `p-answer_0316/docs/09-test-report.html`
- `p-answer_0316/design/class-diagram.html`

## 注意事項

- ルート直下の静的 HTML / CSS / 画像と、Spring Boot 側の `src/main/resources` は並行管理になっています。
- 3/26 時点では `index.html` など主要ファイルの同期は概ね完了していますが、`reservation-complete.html` は整形差分の残存前提です。
- 現在の未実装は主にスタッフ用認証、ログイン画面、予約一覧、予約詳細です。
- 完了画面へのアンケート要約表示は見送りとし、内部保存と予約紐付けまでを実装済みです。
- 作業開始前は `作業md/2026-03-26_1230_重大インシデント_チェックリスト運用逸脱と再発防止.md` の運用ルールを確認してください。
