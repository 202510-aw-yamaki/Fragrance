# Fregrance

フレグランスワークショップのポートフォリオ用 Web アプリケーションです。

## 概要

本プロジェクトは、来店前のユーザーに対して「診断」ではなく「香りづくりの準備体験」を提供することを目的としたフレグランスページです。トップページからアンケート、香りグラフ、予約、予約完了までの導線を持ち、最終的には Spring Boot を用いたバックエンド実装と予約管理機能を備えたポートフォリオ成果物とする想定です。

## 使用技術

- Java
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Security
- MyBatis
- Maven
- HTML / CSS / JavaScript

## 現在の進捗

- Spring Boot の基本骨格を追加
- 既存フロント画面を `src/main/resources/templates` へ移行
- CSS / 画像を `src/main/resources/static` へ移行
- 画面返却用 `FragrancePageController` を追加
- ヘルスチェック用 `/api/health` を追加
- 公開画面を匿名アクセス可能にする基本 Security 設定を追加

## 画面一覧

- `/index.html`
- `/questionnaire.html`
- `/questionnaire_step2.html`
- `/fragrance-graph.html`
- `/reservation.html`
- `/reservation-complete.html`

## 起動手順

1. Java と Maven を利用可能な状態にする
2. プロジェクトルートで依存関係を取得する
3. Spring Boot アプリケーションを起動する

```bash
mvn spring-boot:run
```

起動後、ブラウザで `http://localhost:8080/index.html` を開きます。

## 設計資料

設計資料は主に以下を参照します。

- `p-answer_0316/docs/07-specification.html`
- `p-answer_0316/docs/08-db-design.html`
- `p-answer_0316/docs/09-test-report.html`
- `p-answer_0316/design/class-diagram.html`
- `2026-03-16_1228_SpringBoot実装方針と依存関係.md`

## 注意事項

- 現在のローカル環境では `mvn` が未導入、Java は 17 系です。
- 方針書上の目標環境は `Java 21 + Maven` です。
- 予約 API、DB 接続、スタッフログイン、管理画面はこれから実装します。
