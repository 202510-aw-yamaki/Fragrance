# 引継ぎメモ: reservation.html 調整状況

## 対象

- `reservation.html`

## 1. 現在の位置づけ

- `questionnaire.html` は完成扱い
- `questionnaire_step2.html` は完成扱い
- `fragrance-graph.html` は仮完成扱い
- 次の主作業対象は `reservation.html`
- 現段階ではデモページとしての完成を優先し、Spring Boot + DB 連携は未実装のまま進める

## 2. reservation.html の現状

### 全体構成

- ヘッダー
  - ブランドリンク
  - `fragrance-graph.html` と `questionnaire.html` へのナビリンクあり
- 上部に `step-strip`
  - 現在は `予約確認` のみ表示
- 本体は3カラム構成
  - 左: `summary-panel`
  - 中央: `booking-panel`
  - 右: `side-panel`
- 下段に
  - `memo-panel`
  - `action-panel`
- 予約枠選択はモーダル表示

### 表示内容

- 左カラム
  - 現在の香り傾向見出し
  - 軸ごとのバランス表示
- 中央カラム
  - 選択状態表示
  - 予約枠の週タブ
  - 来店内容、人数選択
- 右カラム
  - ビジュアルカード
  - 予約前メモ
  - 安心事項
- 下段
  - スタッフ向け任意メモ
  - 予約確定ボタン
  - グラフへ戻るボタン

### データの扱い

- グラフページからの値は `sessionStorage` の `fragranceReservationDraft` を参照
- 予約確定時は `fragranceReservationConfirmation` を `sessionStorage` に保存
- 予約枠は固定配列と日付計算で仮生成している
- 実予約送信、DB保存、STEP間の本連携は未実装

## 3. 現時点の課題

### 導線面

- ヘッダーに `fragrance-graph.html` と `questionnaire.html` への戻り導線が残っている
- 下部アクションにも `グラフへ戻る` があり、予約確定導線が分散している
- デモ用の仮導線と、本番想定の予約完了導線が混在している

### 実装面

- `renderSlots()` は仮データ生成のまま
- 予約完了処理は `reservation-complete.html` への遷移のみ
- `handleConfirm()` の `alert` 仮実装が残っている

### アクセシビリティ面

- モーダルは `role="dialog"` と `aria-modal="true"` までは入っている
- ただし、開いた直後のフォーカス移動がない
- 閉じた時に元の操作位置へフォーカスを戻していない
- フォーカストラップ未対応
- そのためモーダル周りは改善余地がある

### 文字化けについて

- ユーザー環境では文字化けは再現していない
- 文字化けに見えるものは、原則として PowerShell 出力時のエンコーディング由来として扱う
- 実ファイル破損前提では扱わない

## 4. レイアウト面の見方

### PC

- 3カラム + 下段2エリア構成
- 要素数が多いため、縦方向の収まりと情報の優先順位確認が必要

### 中間帯

- 2カラム再配置が入っている
- `summary / booking / side / memo / actions` の配置バランス確認が必要

### スマホ

- ヘッダー折返し
- `main-nav` の横スクロール
- `slot-modal` の表示位置
- `time-grid` の1カラム化
- 下段アクションの見え方
  - このあたりが重点確認ポイント

## 5. 次にやること

### 優先度高

- `reservation.html` の実画面確認
- `reservation.html` の導線整理
- `reservation-complete.html` の表示と導線確認

### 最初に見るとよい項目

- ヘッダーの戻り導線をどう残すか
- 予約確定ボタン以外の導線をどこまで整理するか
- モーダルの見え方と閉じ方
- スマホ時の各パネルの優先順位
- `reservation-complete.html` へ遷移した後の戻り導線

## 6. 補足メモ

- `fragrance-graph.html` は仮完成扱いになったため、次の主戦場は `reservation.html`
- 現段階では「実データ連携」より「デモとして自然に見えること」を優先する
- まずは画面整理と導線整理を先に進め、その後に必要ならアクセシビリティ改善を詰める流れでよい
